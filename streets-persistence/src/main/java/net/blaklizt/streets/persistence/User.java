package net.blaklizt.streets.persistence;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "User")
public class User implements Serializable
{
	public enum UserStatus
	{
		ACTIVE(1),
		SUSPENDED(2),
		BLOCKED(3),
		UNKNOWN(10);

		private int value;

		UserStatus(int value)
		{
			this.value = value;
		}

		public int getValue()
		{
			return value;
		}

		public static UserStatus fromValue(int status)
		{
			for (UserStatus userStatus : UserStatus.values())
			{
				if (userStatus.getValue() == status)
					return userStatus;
			}
			return UNKNOWN;
		}
	}

	private Long userID;
	private String name;
	private String userGroupID;
	private String username;
	private String password;
	private UserStatus status;
	private String salt;

	@OneToOne
	@JoinTable(name="UserAttribute",
			joinColumns = {@JoinColumn(name="UserID", referencedColumnName="UserID")},
	 inverseJoinColumns = {@JoinColumn(name="UserID", referencedColumnName="UserID")})
	private UserAttribute userAttribute;

	Long getUserID() {
		return userID;
	}

	void setUserID(Long userID) {
		this.userID = userID;
	}

	@Column(name = "Name")
	@Basic
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "Username")
	@Basic
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "Password")
	@Basic
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(name = "Status")
	@Basic
	public int getStatus() {
		return status.getValue();
	}

	public void setStatus(int status) {
		this.status = UserStatus.fromValue(status);
	}

	@Column(name = "Salt")
	@Basic
	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	@Column(name = "UserID")
	@Id
	public Long getUserId() {
		return userID;
	}

	public void setUserId(Long userID) {
		this.userID = userID;
	}

	@Column(name = "UserGroupID")
	@Basic
	public String getUserGroupID() {
		return userGroupID;
	}

	public void setUserGroupID(String userGroupID) {
		this.userGroupID = userGroupID;
	}

	public UserAttribute getUserAttribute() {
		return userAttribute;
	}

	public void setUserAttribute(UserAttribute userAttribute) {
		this.userAttribute = userAttribute;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		User user = (User) o;

		if (userID != user.userID) return false;
		if (name != null ? !name.equals(user.name) : user.name != null) return false;
		if (password != null ? !password.equals(user.password) : user.password != null) return false;
		if (salt != null ? !salt.equals(user.salt) : user.salt != null) return false;
		if (userGroupID != null ? !userGroupID.equals(user.userGroupID) : user.userGroupID != null) return false;
		if (username != null ? !username.equals(user.username) : user.username != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		Long result = userID;
		result = 31 * result + (name != null ? name.hashCode() : 0);
		result = 31 * result + (userGroupID != null ? userGroupID.hashCode() : 0);
		result = 31 * result + (username != null ? username.hashCode() : 0);
		result = 31 * result + (password != null ? password.hashCode() : 0);
		result = 31 * result + (salt != null ? salt.hashCode() : 0);
		return result.intValue();
	}
}