package net.blaklizt.streets.persistence;

import javax.persistence.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 9/13/13
 * Time: 3:33 PM
 */
@javax.persistence.IdClass(net.blaklizt.streets.persistence.UserItemPK.class)
@Entity
public class UserItem {
	private Long userId;
	private Long itemId;

	public UserItem() {}

	public UserItem(Long userId, Long itemId)
	{
		this.userId = userId;
		this.itemId = itemId;
	}

	@javax.persistence.Column(name = "UserID", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
	@javax.persistence.Id
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@javax.persistence.Column(name = "ItemID", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
	@javax.persistence.Id
	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		UserItem userItem = (UserItem) o;

		if (itemId != null ? !itemId.equals(userItem.itemId) : userItem.itemId != null) return false;
		if (userId != null ? !userId.equals(userItem.userId) : userItem.userId != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = userId != null ? userId.hashCode() : 0;
		result = 31 * result + (itemId != null ? itemId.hashCode() : 0);
		return result;
	}
}
