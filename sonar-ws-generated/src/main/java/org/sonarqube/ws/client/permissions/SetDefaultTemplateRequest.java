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
package org.sonarqube.ws.client.permissions;

import java.util.List;
import javax.annotation.Generated;

/**
 * Set a permission template as default.<br />Requires the following permission: 'Administer System'.
 *
 * This is part of the internal API.
 * This is a POST request.
 * @see <a href="https://next.sonarqube.com/sonarqube/web_api/api/permissions/set_default_template">Further information about this action online (including a response example)</a>
 * @since 5.2
 */
@Generated("https://github.com/SonarSource/sonar-ws-generator")
public class SetDefaultTemplateRequest {

  private String organization;
  private String qualifier;
  private String templateId;
  private String templateName;

  /**
   * Key of organization, used when group name is set
   *
   * This is part of the internal API.
   * Example value: "my-org"
   */
  public SetDefaultTemplateRequest setOrganization(String organization) {
    this.organization = organization;
    return this;
  }

  public String getOrganization() {
    return organization;
  }

  /**
   * Project qualifier. Filter the results with the specified qualifier. Possible values are:<ul><li>TRK - Projects</li></ul>
   *
   * Possible values:
   * <ul>
   *   <li>"TRK"</li>
   * </ul>
   */
  public SetDefaultTemplateRequest setQualifier(String qualifier) {
    this.qualifier = qualifier;
    return this;
  }

  public String getQualifier() {
    return qualifier;
  }

  /**
   * Template id
   *
   * Example value: "AU-Tpxb--iU5OvuD2FLy"
   */
  public SetDefaultTemplateRequest setTemplateId(String templateId) {
    this.templateId = templateId;
    return this;
  }

  public String getTemplateId() {
    return templateId;
  }

  /**
   * Template name
   *
   * Example value: "Default Permission Template for Projects"
   */
  public SetDefaultTemplateRequest setTemplateName(String templateName) {
    this.templateName = templateName;
    return this;
  }

  public String getTemplateName() {
    return templateName;
  }
}
