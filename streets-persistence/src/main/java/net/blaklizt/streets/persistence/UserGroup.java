package net.blaklizt.streets.persistence;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 8/7/13
 * Time: 9:02 PM
 */
@Entity
@Table(name = "UserGroup")
public class UserGroup implements Serializable{
	private String userGroupID;

	@javax.persistence.Column(name = "UserGroupID")
	@javax.persistence.Id
	public String getUserGroupID() {
		return userGroupID;
	}

	public void setUserGroupID(String userGroupID) {
		this.userGroupID = userGroupID;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		UserGroup userGroup = (UserGroup) o;

		if (userGroupID != null ? !userGroupID.equals(userGroup.userGroupID) : userGroup.userGroupID != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return userGroupID != null ? userGroupID.hashCode() : 0;
	}
}
