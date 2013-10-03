package net.blaklizt.streets.persistence;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 9/8/13
 * Time: 2:58 PM
 */
@Entity
public class Store {
	private Long storeID;

	@javax.persistence.Column(name = "StoreID", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
	@javax.persistence.Id
	public Long getStoreID() {
		return storeID;
	}

	public void setStoreID(Long storeID) {
		this.storeID = storeID;
	}

	private Long locationID;

	@javax.persistence.Column(name = "LocationID", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
	@javax.persistence.Basic
	public Long getLocationID() {
		return locationID;
	}

	public void setLocationID(Long locationID) {
		this.locationID = locationID;
	}

	private String storeName;

	@Column(name = "StoreName")
	@Basic
	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Store store = (Store) o;

		if (locationID != null ? !locationID.equals(store.locationID) : store.locationID != null) return false;
		if (storeID != null ? !storeID.equals(store.storeID) : store.storeID != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = storeID != null ? storeID.hashCode() : 0;
		result = 31 * result + (locationID != null ? locationID.hashCode() : 0);
		return result;
	}
}
