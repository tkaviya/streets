package net.blaklizt.streets.persistence;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 9/8/13
 * Time: 2:58 PM
 */
@Entity
@Table (name = "Item")
public class Item {
	private Long itemId;

	@Column(name = "ItemID", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
	@Id
	public Long getItemId() {
		return itemId;
	}

	public void setItemId(Long itemId) {
		this.itemId = itemId;
	}

	private Integer itemValue;

	@Column(name = "ItemValue")
	@Basic
	public Integer getItemValue() {
		return itemValue;
	}

	public void setItemValue(Integer itemValue) {
		this.itemValue = itemValue;
	}

	private String itemName;

	@Column(name = "ItemName")
	@Basic
	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	private String itemDescription;

	@Column(name = "ItemDescription")
	@Basic
	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	private Boolean allowMultiple;

	@Column(name = "AllowMultiple")
	@Basic
	public Boolean getAllowMultiple() {
		return allowMultiple;
	}

	public void setAllowMultiple(Boolean allowMultiple) {
		this.allowMultiple = allowMultiple;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Item item = (Item) o;

		if (itemId != null ? !itemId.equals(item.itemId) : item.itemId != null) return false;
		if (itemValue != null ? !itemValue.equals(item.itemValue) : item.itemValue != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = itemId != null ? itemId.hashCode() : 0;
		result = 31 * result + (itemValue != null ? itemValue.hashCode() : 0);
		return result;
	}
}
