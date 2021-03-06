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
package org.sonarqube.ws.client.custommeasures;

import java.util.stream.Collectors;
import javax.annotation.Generated;
import org.sonarqube.ws.MediaTypes;
import org.sonarqube.ws.client.BaseService;
import org.sonarqube.ws.client.GetRequest;
import org.sonarqube.ws.client.PostRequest;
import org.sonarqube.ws.client.WsConnector;

/**
 * Manage custom measures for a project. See also api/metrics.
 * @see <a href="https://next.sonarqube.com/sonarqube/web_api/api/custom_measures">Further information about this web service online</a>
 */
@Generated("https://github.com/SonarSource/sonar-ws-generator")
public class CustomMeasuresService extends BaseService {

  public CustomMeasuresService(WsConnector wsConnector) {
    super(wsConnector, "api/custom_measures");
  }

  /**
   * Create a custom measure.<br /> The project id or the project key must be provided (only project and module custom measures can be created). The metric id or the metric key must be provided.<br/>Requires 'Administer System' permission or 'Administer' permission on the project.
   *
   * This is part of the internal API.
   * This is a POST request.
   * @see <a href="https://next.sonarqube.com/sonarqube/web_api/api/custom_measures/create">Further information about this action online (including a response example)</a>
   * @since 5.2
   */
  public void create(CreateRequest request) {
    call(
      new PostRequest(path("create"))
        .setParam("description", request.getDescription())
        .setParam("metricId", request.getMetricId())
        .setParam("metricKey", request.getMetricKey())
        .setParam("projectId", request.getProjectId())
        .setParam("projectKey", request.getProjectKey())
        .setParam("value", request.getValue())
        .setMediaType(MediaTypes.JSON)
      ).content();
  }

  /**
   * Delete a custom measure.<br /> Requires 'Administer System' permission or 'Administer' permission on the project.
   *
   * This is part of the internal API.
   * This is a POST request.
   * @see <a href="https://next.sonarqube.com/sonarqube/web_api/api/custom_measures/delete">Further information about this action online (including a response example)</a>
   * @since 5.2
   */
  public void delete(DeleteRequest request) {
    call(
      new PostRequest(path("delete"))
        .setParam("id", request.getId())
        .setMediaType(MediaTypes.JSON)
      ).content();
  }

  /**
   * List all custom metrics for which no custom measure already exists on a given project.<br /> The project id or project key must be provided.<br />Requires 'Administer System' permission or 'Administer' permission on the project.
   *
   * This is part of the internal API.
   * This is a GET request.
   * @see <a href="https://next.sonarqube.com/sonarqube/web_api/api/custom_measures/metrics">Further information about this action online (including a response example)</a>
   * @since 5.2
   */
  public String metrics(MetricsRequest request) {
    return call(
      new GetRequest(path("metrics"))
        .setParam("projectId", request.getProjectId())
        .setParam("projectKey", request.getProjectKey())
        .setMediaType(MediaTypes.JSON)
      ).content();
  }

  /**
   * List custom measures. The project id or project key must be provided.<br />Requires 'Administer System' permission or 'Administer' permission on the project.
   *
   * This is part of the internal API.
   * This is a GET request.
   * @see <a href="https://next.sonarqube.com/sonarqube/web_api/api/custom_measures/search">Further information about this action online (including a response example)</a>
   * @since 5.2
   */
  public String search(SearchRequest request) {
    return call(
      new GetRequest(path("search"))
        .setParam("f", request.getF() == null ? null : request.getF().stream().collect(Collectors.joining(",")))
        .setParam("p", request.getP())
        .setParam("projectId", request.getProjectId())
        .setParam("projectKey", request.getProjectKey())
        .setParam("ps", request.getPs())
        .setMediaType(MediaTypes.JSON)
      ).content();
  }

  /**
   * Update a custom measure. Value and/or description must be provided<br />Requires 'Administer System' permission or 'Administer' permission on the project.
   *
   * This is part of the internal API.
   * This is a POST request.
   * @see <a href="https://next.sonarqube.com/sonarqube/web_api/api/custom_measures/update">Further information about this action online (including a response example)</a>
   * @since 5.2
   */
  public void update(UpdateRequest request) {
    call(
      new PostRequest(path("update"))
        .setParam("description", request.getDescription())
        .setParam("id", request.getId())
        .setParam("value", request.getValue())
        .setMediaType(MediaTypes.JSON)
      ).content();
  }
}
