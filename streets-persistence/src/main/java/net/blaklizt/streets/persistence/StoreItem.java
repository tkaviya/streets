package net.blaklizt.streets.persistence;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 9/8/13
 * Time: 3:45 PM
 */
@javax.persistence.IdClass(net.blaklizt.streets.persistence.StoreItemPK.class)
@Entity
public class StoreItem {
	private Long storeID;

	@javax.persistence.Column(name = "StoreID", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
	@Id
	public Long getStoreID() {
		return storeID;
	}

	public void setStoreID(Long storeID) {
		this.storeID = storeID;
	}

	private Long itemID;

	@javax.persistence.Column(name = "ItemID", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
	@Id
	public Long getItemID() {
		return itemID;
	}

	public void setItemID(Long itemID) {
		this.itemID = itemID;
	}

	@ManyToOne(optional = true)
	@JoinTable(name="Item")
	@JoinColumn(name="ItemID", referencedColumnName="ItemID")
	private Item item;

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}


	private Double cost;

	@javax.persistence.Column(name = "Cost", nullable = false, insertable = true, updatable = true, length = 22, precision = 0)
	@Basic
	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		StoreItem storeItem = (StoreItem) o;

		if (cost != null ? !cost.equals(storeItem.cost) : storeItem.cost != null) return false;
		if (itemID != null ? !itemID.equals(storeItem.itemID) : storeItem.itemID != null) return false;
		if (storeID != null ? !storeID.equals(storeItem.storeID) : storeItem.storeID != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = storeID != null ? storeID.hashCode() : 0;
		result = 31 * result + (itemID != null ? itemID.hashCode() : 0);
		result = 31 * result + (cost != null ? cost.hashCode() : 0);
		return result;
	}
}
