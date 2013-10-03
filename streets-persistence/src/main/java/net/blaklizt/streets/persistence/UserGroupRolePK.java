package net.blaklizt.streets.persistence;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 8/7/13
 * Time: 9:02 PM
 */
public class UserGroupRolePK implements Serializable {
	private String userGroupID;
	private String roleID;

@Id@Column(name = "UserGroupID")
public String getUserGroupID() {
	return userGroupID;
}

	public void setUserGroupID(String userGroupID) {
		this.userGroupID = userGroupID;
	}

	@Id@Column(name = "RoleID")
	public String getRoleID() {
		return roleID;
	}

	public void setRoleID(String roleID) {
		this.roleID = roleID;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		UserGroupRolePK that = (UserGroupRolePK) o;

		if (roleID != null ? !roleID.equals(that.roleID) : that.roleID != null) return false;
		if (userGroupID != null ? !userGroupID.equals(that.userGroupID) : that.userGroupID != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = userGroupID != null ? userGroupID.hashCode() : 0;
		result = 31 * result + (roleID != null ? roleID.hashCode() : 0);
		return result;
}}
