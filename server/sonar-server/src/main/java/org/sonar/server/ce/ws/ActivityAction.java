/*
 * SonarQube
 * Copyright (C) 2009-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.ce.ws;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.server.ws.Change;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.Response;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.server.ws.WebService.Param;
import org.sonar.api.utils.DateUtils;
import org.sonar.api.web.UserRole;
import org.sonar.ce.taskprocessor.CeTaskProcessor;
import org.sonar.core.util.Uuids;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.ce.CeActivityDto;
import org.sonar.db.ce.CeQueueDto;
import org.sonar.db.ce.CeTaskQuery;
import org.sonar.db.ce.CeTaskTypes;
import org.sonar.db.component.ComponentDto;
import org.sonar.db.component.ComponentQuery;
import org.sonar.server.user.UserSession;
import org.sonarqube.ws.Ce;
import org.sonarqube.ws.Ce.ActivityResponse;
import org.sonarqube.ws.client.ce.ActivityRequest;

import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static org.apache.commons.lang.StringUtils.defaultString;
import static org.sonar.api.utils.DateUtils.parseEndingDateOrDateTime;
import static org.sonar.api.utils.DateUtils.parseStartingDateOrDateTime;
import static org.sonar.core.util.stream.MoreCollectors.toList;
import static org.sonar.db.Pagination.forPage;
import static org.sonar.server.ws.WsUtils.checkFoundWithOptional;
import static org.sonar.server.ws.WsUtils.checkRequest;
import static org.sonar.server.ws.WsUtils.writeProtobuf;
import static org.sonarqube.ws.client.ce.CeWsParameters.PARAM_COMPONENT_ID;
import static org.sonarqube.ws.client.ce.CeWsParameters.PARAM_COMPONENT_QUERY;
import static org.sonarqube.ws.client.ce.CeWsParameters.PARAM_MAX_EXECUTED_AT;
import static org.sonarqube.ws.client.ce.CeWsParameters.PARAM_MIN_SUBMITTED_AT;
import static org.sonarqube.ws.client.ce.CeWsParameters.PARAM_ONLY_CURRENTS;
import static org.sonarqube.ws.client.ce.CeWsParameters.PARAM_STATUS;
import static org.sonarqube.ws.client.ce.CeWsParameters.PARAM_TYPE;

public class ActivityAction implements CeWsAction {
  private static final int MAX_PAGE_SIZE = 1000;
  private static final String[] POSSIBLE_QUALIFIERS = new String[] {Qualifiers.PROJECT, Qualifiers.APP, Qualifiers.VIEW, "DEV", Qualifiers.MODULE};

  private final UserSession userSession;
  private final DbClient dbClient;
  private final TaskFormatter formatter;
  private final Set<String> taskTypes;

  public ActivityAction(UserSession userSession, DbClient dbClient, TaskFormatter formatter, CeTaskProcessor[] taskProcessors) {
    this.userSession = userSession;
    this.dbClient = dbClient;
    this.formatter = formatter;

    this.taskTypes = new LinkedHashSet<>();
    for (CeTaskProcessor taskProcessor : taskProcessors) {
      taskTypes.addAll(taskProcessor.getHandledCeTaskTypes());
    }
  }

  @Override
  public void define(WebService.NewController controller) {
    WebService.NewAction action = controller.createAction("activity")
      .setDescription(format("Search for tasks.<br> " +
        "Requires the system administration permission, " +
        "or project administration permission if %s is set.", PARAM_COMPONENT_ID))
      .setResponseExample(getClass().getResource("activity-example.json"))
      .setHandler(this)
      .setChangelog(
        new Change("5.5", "it's no more possible to specify the page parameter."),
        new Change("6.1", "field \"logs\" is deprecated and its value is always false"),
        new Change("6.6", "fields \"branch\" and \"branchType\" added"))
      .setSince("5.2");

    action.createParam(PARAM_COMPONENT_ID)
      .setDescription("Id of the component (project) to filter on")
      .setExampleValue(Uuids.UUID_EXAMPLE_03);
    action.createParam(PARAM_COMPONENT_QUERY)
      .setDescription(format("Limit search to: <ul>" +
        "<li>component names that contain the supplied string</li>" +
        "<li>component keys that are exactly the same as the supplied string</li>" +
        "</ul>" +
        "Must not be set together with %s.<br />" +
        "Deprecated and replaced by '%s'", PARAM_COMPONENT_ID, Param.TEXT_QUERY))
      .setExampleValue("Apache")
      .setDeprecatedSince("5.5");
    action.createParam(Param.TEXT_QUERY)
      .setDescription(format("Limit search to: <ul>" +
        "<li>component names that contain the supplied string</li>" +
        "<li>component keys that are exactly the same as the supplied string</li>" +
        "<li>task ids that are exactly the same as the supplied string</li>" +
        "</ul>" +
        "Must not be set together with %s", PARAM_COMPONENT_ID))
      .setExampleValue("Apache")
      .setSince("5.5");
    action.createParam(PARAM_STATUS)
      .setDescription("Comma separated list of task statuses")
      .setPossibleValues(ImmutableList.builder()
        .add(CeActivityDto.Status.values())
        .add(CeQueueDto.Status.values()).build())
      .setExampleValue(Joiner.on(",").join(CeQueueDto.Status.IN_PROGRESS, CeActivityDto.Status.SUCCESS))
      // activity statuses by default to be backward compatible
      // queued tasks have been added in 5.5
      .setDefaultValue(Joiner.on(",").join(CeActivityDto.Status.values()));
    action.createParam(PARAM_ONLY_CURRENTS)
      .setDescription("Filter on the last tasks (only the most recent finished task by project)")
      .setBooleanPossibleValues()
      .setDefaultValue("false");
    action.createParam(PARAM_TYPE)
      .setDescription("Task type")
      .setExampleValue(CeTaskTypes.REPORT)
      .setPossibleValues(taskTypes);
    action.createParam(PARAM_MIN_SUBMITTED_AT)
      .setDescription("Minimum date of task submission (inclusive)")
      .setExampleValue(DateUtils.formatDateTime(new Date()));
    action.createParam(PARAM_MAX_EXECUTED_AT)
      .setDescription("Maximum date of end of task processing (inclusive)")
      .setExampleValue(DateUtils.formatDateTime(new Date()));
    action.createParam(Param.PAGE)
      .setDescription("Deprecated parameter")
      .setDeprecatedSince("5.5")
      .setDeprecatedKey("pageIndex", "5.4");
    action.createPageSize(100, MAX_PAGE_SIZE);
  }

  @Override
  public void handle(Request wsRequest, Response wsResponse) throws Exception {
    ActivityResponse activityResponse = doHandle(toSearchWsRequest(wsRequest));
    writeProtobuf(activityResponse, wsRequest, wsResponse);
  }

  private ActivityResponse doHandle(ActivityRequest request) {

    try (DbSession dbSession = dbClient.openSession(false)) {
      ComponentDto component = loadComponent(dbSession, request);
      checkPermission(component);
      // if a task searched by uuid is found all other parameters are ignored
      Optional<Ce.Task> taskSearchedById = searchTaskByUuid(dbSession, request);
      if (taskSearchedById.isPresent()) {
        return buildResponse(
          singletonList(taskSearchedById.get()),
          Collections.emptyList(),
          parseInt(request.getPs()));
      }

      CeTaskQuery query = buildQuery(dbSession, request, component);
      List<Ce.Task> queuedTasks = loadQueuedTasks(dbSession, request, query);
      List<Ce.Task> pastTasks = loadPastTasks(dbSession, request, query);
      return buildResponse(
        queuedTasks,
        pastTasks,
        parseInt(request.getPs()));
    }
  }

  @CheckForNull
  private ComponentDto loadComponent(DbSession dbSession, ActivityRequest request) {
    String componentId = request.getComponentId();
    if (componentId == null) {
      return null;
    }
    return checkFoundWithOptional(dbClient.componentDao().selectByUuid(dbSession, componentId), "Component '%s' does not exist", componentId);
  }

  private void checkPermission(@Nullable ComponentDto component) {
    // fail fast if not logged in
    userSession.checkLoggedIn();

    if (component == null) {
      userSession.checkIsSystemAdministrator();
    } else {
      userSession.checkComponentPermission(UserRole.ADMIN, component);
    }
  }

  private Optional<Ce.Task> searchTaskByUuid(DbSession dbSession, ActivityRequest request) {
    String textQuery = request.getQ();
    if (textQuery == null) {
      return Optional.absent();
    }

    java.util.Optional<CeQueueDto> queue = dbClient.ceQueueDao().selectByUuid(dbSession, textQuery);
    if (queue.isPresent()) {
      return Optional.of(formatter.formatQueue(dbSession, queue.get()));
    }

    java.util.Optional<CeActivityDto> activity = dbClient.ceActivityDao().selectByUuid(dbSession, textQuery);
    return activity.map(ceActivityDto -> Optional.of(formatter.formatActivity(dbSession, ceActivityDto, null))).orElseGet(Optional::absent);
  }

  private CeTaskQuery buildQuery(DbSession dbSession, ActivityRequest request, @Nullable ComponentDto component) {
    CeTaskQuery query = new CeTaskQuery();
    query.setType(request.getType());
    query.setOnlyCurrents(parseBoolean(request.getOnlyCurrents()));
    Date minSubmittedAt = parseStartingDateOrDateTime(request.getMinSubmittedAt());
    query.setMinSubmittedAt(minSubmittedAt == null ? null : minSubmittedAt.getTime());
    Date maxExecutedAt = parseEndingDateOrDateTime(request.getMaxExecutedAt());
    query.setMaxExecutedAt(maxExecutedAt == null ? null : maxExecutedAt.getTime());

    List<String> statuses = request.getStatus();
    if (statuses != null && !statuses.isEmpty()) {
      query.setStatuses(request.getStatus());
    }

    String componentQuery = request.getQ();
    if (component != null) {
      query.setComponentUuid(component.uuid());
    } else if (componentQuery != null) {
      query.setComponentUuids(loadComponents(dbSession, componentQuery).stream().map(ComponentDto::uuid).collect(toList()));
    }
    return query;
  }

  private List<ComponentDto> loadComponents(DbSession dbSession, String componentQuery) {
    ComponentQuery componentDtoQuery = ComponentQuery.builder()
      .setNameOrKeyQuery(componentQuery)
      .setQualifiers(POSSIBLE_QUALIFIERS)
      .build();
    return dbClient.componentDao().selectByQuery(dbSession, componentDtoQuery, 0, CeTaskQuery.MAX_COMPONENT_UUIDS);
  }

  private List<Ce.Task> loadQueuedTasks(DbSession dbSession, ActivityRequest request, CeTaskQuery query) {
    List<CeQueueDto> dtos = dbClient.ceQueueDao().selectByQueryInDescOrder(dbSession, query, parseInt(request.getPs()));
    return formatter.formatQueue(dbSession, dtos);
  }

  private List<Ce.Task> loadPastTasks(DbSession dbSession, ActivityRequest request, CeTaskQuery query) {
    List<CeActivityDto> dtos = dbClient.ceActivityDao().selectByQuery(dbSession, query, forPage(1).andSize(parseInt(request.getPs())));
    return formatter.formatActivity(dbSession, dtos);
  }

  private static ActivityResponse buildResponse(Iterable<Ce.Task> queuedTasks, Iterable<Ce.Task> pastTasks, int pageSize) {
    Ce.ActivityResponse.Builder wsResponseBuilder = Ce.ActivityResponse.newBuilder();

    int nbInsertedTasks = 0;
    for (Ce.Task queuedTask : queuedTasks) {
      if (nbInsertedTasks < pageSize) {
        wsResponseBuilder.addTasks(queuedTask);
        nbInsertedTasks++;
      }
    }

    for (Ce.Task pastTask : pastTasks) {
      if (nbInsertedTasks < pageSize) {
        wsResponseBuilder.addTasks(pastTask);
        nbInsertedTasks++;
      }
    }
    return wsResponseBuilder.build();
  }

  private static ActivityRequest toSearchWsRequest(Request request) {
    ActivityRequest activityWsRequest = new ActivityRequest()
      .setComponentId(request.param(PARAM_COMPONENT_ID))
      .setQ(defaultString(request.param(Param.TEXT_QUERY), request.param(PARAM_COMPONENT_QUERY)))
      .setStatus(request.paramAsStrings(PARAM_STATUS))
      .setType(request.param(PARAM_TYPE))
      .setMinSubmittedAt(request.param(PARAM_MIN_SUBMITTED_AT))
      .setMaxExecutedAt(request.param(PARAM_MAX_EXECUTED_AT))
      .setOnlyCurrents(String.valueOf(request.paramAsBoolean(PARAM_ONLY_CURRENTS)))
      .setPs(String.valueOf(request.mandatoryParamAsInt(Param.PAGE_SIZE)));

    checkRequest(activityWsRequest.getComponentId() == null || activityWsRequest.getQ() == null, "%s and %s must not be set at the same time",
      PARAM_COMPONENT_ID, PARAM_COMPONENT_QUERY);
    return activityWsRequest;
  }
}
