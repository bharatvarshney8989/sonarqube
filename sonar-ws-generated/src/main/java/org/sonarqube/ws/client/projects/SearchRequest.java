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
 * Search for projects or views to administrate them.<br>Requires 'System Administrator' permission
 *
 * This is part of the internal API.
 * This is a POST request.
 * @see <a href="https://next.sonarqube.com/sonarqube/web_api/api/projects/search">Further information about this action online (including a response example)</a>
 * @since 6.3
 */
@Generated("https://github.com/SonarSource/sonar-ws-generator")
public class SearchRequest {

  private String analyzedBefore;
  private String onProvisionedOnly;
  private String organization;
  private String p;
  private List<String> projectIds;
  private List<String> projects;
  private String ps;
  private String q;
  private List<String> qualifiers;
  private String visibility;

  /**
   * Filter the projects for which last analysis is older than the given date (exclusive).<br> Either a date (server timezone) or datetime can be provided.
   *
   * Example value: "2017-10-19 or 2017-10-19T13:00:00+0200"
   */
  public SearchRequest setAnalyzedBefore(String analyzedBefore) {
    this.analyzedBefore = analyzedBefore;
    return this;
  }

  public String getAnalyzedBefore() {
    return analyzedBefore;
  }

  /**
   * Filter the projects that are provisioned
   *
   * Possible values:
   * <ul>
   *   <li>"true"</li>
   *   <li>"false"</li>
   *   <li>"yes"</li>
   *   <li>"no"</li>
   * </ul>
   */
  public SearchRequest setOnProvisionedOnly(String onProvisionedOnly) {
    this.onProvisionedOnly = onProvisionedOnly;
    return this;
  }

  public String getOnProvisionedOnly() {
    return onProvisionedOnly;
  }

  /**
   * The key of the organization
   *
   * This is part of the internal API.
   */
  public SearchRequest setOrganization(String organization) {
    this.organization = organization;
    return this;
  }

  public String getOrganization() {
    return organization;
  }

  /**
   * 1-based page number
   *
   * Example value: "42"
   */
  public SearchRequest setP(String p) {
    this.p = p;
    return this;
  }

  public String getP() {
    return p;
  }

  /**
   * Comma-separated list of project ids
   *
   * Example value: "AU-Tpxb--iU5OvuD2FLy,AU-TpxcA-iU5OvuD2FLz"
   * @deprecated since 6.6
   */
  @Deprecated
  public SearchRequest setProjectIds(List<String> projectIds) {
    this.projectIds = projectIds;
    return this;
  }

  public List<String> getProjectIds() {
    return projectIds;
  }

  /**
   * Comma-separated list of project keys
   *
   * Example value: "my_project,another_project"
   */
  public SearchRequest setProjects(List<String> projects) {
    this.projects = projects;
    return this;
  }

  public List<String> getProjects() {
    return projects;
  }

  /**
   * Page size. Must be greater than 0 and less than 500
   *
   * Example value: "20"
   */
  public SearchRequest setPs(String ps) {
    this.ps = ps;
    return this;
  }

  public String getPs() {
    return ps;
  }

  /**
   * Limit search to: <ul><li>component names that contain the supplied string</li><li>component keys that contain the supplied string</li></ul>
   *
   * Example value: "sonar"
   */
  public SearchRequest setQ(String q) {
    this.q = q;
    return this;
  }

  public String getQ() {
    return q;
  }

  /**
   * Comma-separated list of component qualifiers. Filter the results with the specified qualifiers
   *
   * Possible values:
   * <ul>
   *   <li>"TRK"</li>
   *   <li>"VW"</li>
   *   <li>"APP"</li>
   * </ul>
   */
  public SearchRequest setQualifiers(List<String> qualifiers) {
    this.qualifiers = qualifiers;
    return this;
  }

  public List<String> getQualifiers() {
    return qualifiers;
  }

  /**
   * Filter the projects that should be visible to everyone (public), or only specific user/groups (private).<br/>If no visibility is specified, the default project visibility of the organization will be used.
   *
   * This is part of the internal API.
   * Possible values:
   * <ul>
   *   <li>"private"</li>
   *   <li>"public"</li>
   * </ul>
   */
  public SearchRequest setVisibility(String visibility) {
    this.visibility = visibility;
    return this;
  }

  public String getVisibility() {
    return visibility;
  }
}
