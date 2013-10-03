package net.blaklizt.streets.persistence;

import javax.persistence.Column;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 9/8/13
 * Time: 3:45 PM
 */
public class StoreItemPK implements Serializable {
	private Long storeId;
	private Long itemId;

@Id@Column(name = "StoreID", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
public Long getStoreID() {
	return storeId;
}

	public void setStoreID(Long storeId) {
		this.storeId = storeId;
	}

	@Id@Column(name = "ItemID", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
	public Long getItemID() {
		return itemId;
	}

	public void setItemID(Long itemId) {
		this.itemId = itemId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		StoreItemPK that = (StoreItemPK) o;

		if (itemId != null ? !itemId.equals(that.itemId) : that.itemId != null) return false;
		if (storeId != null ? !storeId.equals(that.storeId) : that.storeId != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = storeId != null ? storeId.hashCode() : 0;
		result = 31 * result + (itemId != null ? itemId.hashCode() : 0);
		return result;
}}
