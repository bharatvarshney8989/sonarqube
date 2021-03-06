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
package org.sonarqube.ws.client.projects;

import java.util.List;
import javax.annotation.Generated;

/**
 * Bulk update a project or module key and all its sub-components keys. The bulk update allows to replace a part of the current key by another string on the current project and all its sub-modules.<br>It's possible to simulate the bulk update by setting the parameter 'dryRun' at true. No key is updated with a dry run.<br>Ex: to rename a project with key 'my_project' to 'my_new_project' and all its sub-components keys, call the WS with parameters:<ul>  <li>project: my_project</li>  <li>from: my_</li>  <li>to: my_new_</li></ul>Either 'projectId' or 'project' must be provided.<br> Requires one of the following permissions: <ul><li>'Administer System'</li><li>'Administer' rights on the specified project</li></ul>
 *
 * This is part of the internal API.
 * This is a POST request.
 * @see <a href="https://next.sonarqube.com/sonarqube/web_api/api/projects/bulk_update_key">Further information about this action online (including a response example)</a>
 * @since 6.1
 */
@Generated("https://github.com/SonarSource/sonar-ws-generator")
public class BulkUpdateKeyRequest {

  private String dryRun;
  private String from;
  private String project;
  private String projectId;
  private String to;

  /**
   * Simulate bulk update. No component key is updated.
   *
   * Possible values:
   * <ul>
   *   <li>"true"</li>
   *   <li>"false"</li>
   *   <li>"yes"</li>
   *   <li>"no"</li>
   * </ul>
   */
  public BulkUpdateKeyRequest setDryRun(String dryRun) {
    this.dryRun = dryRun;
    return this;
  }

  public String getDryRun() {
    return dryRun;
  }

  /**
   * String to match in components keys
   *
   * This is a mandatory parameter.
   * Example value: "_old"
   */
  public BulkUpdateKeyRequest setFrom(String from) {
    this.from = from;
    return this;
  }

  public String getFrom() {
    return from;
  }

  /**
   * Project or module key
   *
   * Example value: "my_old_project"
   */
  public BulkUpdateKeyRequest setProject(String project) {
    this.project = project;
    return this;
  }

  public String getProject() {
    return project;
  }

  /**
   * Project or module ID
   *
   * Example value: "AU-Tpxb--iU5OvuD2FLy"
   * @deprecated since 6.4
   */
  @Deprecated
  public BulkUpdateKeyRequest setProjectId(String projectId) {
    this.projectId = projectId;
    return this;
  }

  public String getProjectId() {
    return projectId;
  }

  /**
   * String replacement in components keys
   *
   * This is a mandatory parameter.
   * Example value: "_new"
   */
  public BulkUpdateKeyRequest setTo(String to) {
    this.to = to;
    return this;
  }

  public String getTo() {
    return to;
  }
}
