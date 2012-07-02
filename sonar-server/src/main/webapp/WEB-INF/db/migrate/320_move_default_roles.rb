#
# Sonar, open source software quality management tool.
# Copyright (C) 2008-2012 SonarSource
# mailto:contact AT sonarsource DOT com
#
# Sonar is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 3 of the License, or (at your option) any later version.
#
# Sonar is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with Sonar; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
#

#
# Sonar 3.2
#
class MoveDefaultRoles < ActiveRecord::Migration

  class Group < ActiveRecord::Base
  end

  class GroupRole < ActiveRecord::Base
  end

  class User < ActiveRecord::Base
  end
  class UserRole < ActiveRecord::Base
  end

  class Property < ActiveRecord::Base
    set_table_name 'properties'
  end

  def self.up
    if GroupRole.count==0
      # fresh install
      Property.delete_all(['prop_key like ?', 'sonar.role.%'])
      Property.create(:prop_key => 'sonar.role.admin.project.defaultGroups', :text_value => 'sonar-administrators')
      Property.create(:prop_key => 'sonar.role.user.project.defaultGroups', :text_value => 'sonar-users,Anyone')
      Property.create(:prop_key => 'sonar.role.codeviewer.project.defaultGroups', :text_value => 'sonar-users,Anyone')
    else
      # upgrade from version < 3.2.
      move_groups
      move_users
    end
  end

  private

  def self.move_groups
    groups_per_role={}
    group_roles = GroupRole.find(:all, :conditions => ['resource_id is null and role like ?', 'default-%'])

    group_roles.each do |group_role|
      role = group_role.role[8..-1]
      group_name = nil
      if group_role.group_id
        group = Group.find(group_role.group_id)
        group_name = group.name if group
      else
        group_name = 'Anyone'
      end
      if group_name
        groups_per_role[role]||=[]
        groups_per_role[role]<<group_name
      end
    end

    groups_per_role.each_pair do |role, groups|
      Property.create(:prop_key => "sonar.role.#{role}.project.defaultGroups", :text_value => groups.join(','))
    end

    #GroupRole.delete_all ['role like ?', 'default-%']
  end

  def self.move_users
    users_per_role={}
    user_roles = UserRole.find(:all, :conditions => ['user_id is not null and resource_id is null and role like ?', 'default-%'])

    user_roles.each do |user_role|
      role = user_role.role[8..-1]
      user = User.find(user_role.user_id)
      if user
        users_per_role[role]||=[]
        users_per_role[role]<<user.login
      end
    end

    users_per_role.each_pair do |role, users|
      Property.create(:prop_key => "sonar.role.#{role}.project.defaultUsers", :text_value => users.join(','))
    end

    #UserRole.delete_all ['role like ?', 'default-%']
  end
end
