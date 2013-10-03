package net.blaklizt.streets.persistence;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 9/13/13
 * Time: 3:33 PM
 */
public class UserItemPK implements Serializable {
	private Long userId;
	private Long itemId;

	public UserItemPK(Long userId, Long itemId)
	{
		this.userId = userId;
		this.itemId = itemId;
	}

	public UserItemPK() {}

	@Id@Column(name = "UserID", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Id@Column(name = "ItemID", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
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

		UserItemPK that = (UserItemPK) o;

		if (itemId != null ? !itemId.equals(that.itemId) : that.itemId != null) return false;
		if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = userId != null ? userId.hashCode() : 0;
		result = 31 * result + (itemId != null ? itemId.hashCode() : 0);
		return result;
}}
