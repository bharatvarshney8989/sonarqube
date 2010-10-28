/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.plugins.core.purges;

import org.sonar.api.batch.PurgeContext;
import org.sonar.api.database.DatabaseSession;
import org.sonar.api.database.model.MeasureModel;
import org.sonar.api.database.model.Snapshot;
import org.sonar.core.purge.AbstractPurge;

import java.util.List;

import javax.persistence.Query;

/**
 * see SONAR-522
 * 
 * @since 1.11
 */
public class PurgeRuleMeasures extends AbstractPurge {

  public PurgeRuleMeasures(DatabaseSession session) {
    super(session);
  }

  public void purge(PurgeContext context) {
    if (context.getPreviousSnapshotId() != null) {
      purge(context.getPreviousSnapshotId());
    }
  }

  private void purge(Integer sid) {
    Query query = getSession().createQuery("SELECT m.id FROM " + MeasureModel.class.getSimpleName() + " m, " + Snapshot.class.getSimpleName() + " s WHERE s.id = m.snapshotId and " +
        "(s.rootId=:rootSid OR s.id=:rootSid) and (m.rule is not null or m.rulesCategoryId is not null or m.rulePriority is not null)");
    query.setParameter("rootSid", sid);
    List<Integer> measureIds = selectIds(query);

    deleteMeasuresById(measureIds);
  }
}
