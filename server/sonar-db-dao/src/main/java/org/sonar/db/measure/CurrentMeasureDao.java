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
package org.sonar.db.measure;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.sonar.api.utils.System2;
import org.sonar.core.util.Uuids;
import org.sonar.db.Dao;
import org.sonar.db.DbSession;

import static org.sonar.db.DatabaseUtils.executeLargeInputs;

public class CurrentMeasureDao implements Dao {

  private final System2 system2;

  public CurrentMeasureDao(System2 system2) {
    this.system2 = system2;
  }

  public void insert(DbSession dbSession, CurrentMeasureDto dto) {
    dto.setUuid(Uuids.create());
    mapper(dbSession).insert(dto, system2.now());
  }

  public List<CurrentMeasureDto> selectByComponentUuids(DbSession dbSession, Collection<String> largeComponentUuids, Collection<Integer> metricIds) {
    if (largeComponentUuids.isEmpty() || metricIds.isEmpty()) {
      return Collections.emptyList();
    }

    return executeLargeInputs(
      largeComponentUuids,
      componentUuids ->
        mapper(dbSession).selectByComponentUuids(componentUuids, metricIds));
  }

  public void deleteByProjectUuid(DbSession dbSession, String projectUuid) {
    mapper(dbSession).deleteByProjectUuid(projectUuid);
  }

  private static CurrentMeasureMapper mapper(DbSession dbSession) {
    return dbSession.getMapper(CurrentMeasureMapper.class);
  }

}
