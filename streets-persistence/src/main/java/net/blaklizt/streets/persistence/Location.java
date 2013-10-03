package net.blaklizt.streets.persistence;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 9/8/13
 * Time: 2:58 PM
 */
@Entity
@Table(name = "Location")
public class Location {
	private Long locationId;

	@javax.persistence.Column(name = "LocationID", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
	@Id
	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}

	private String locationName;

	@javax.persistence.Column(name = "LocationName", nullable = false, insertable = true, updatable = true, length = 30, precision = 0)
	@Basic
	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	private Long northLocationId;

	@javax.persistence.Column(name = "NorthLocationID", nullable = true, insertable = true, updatable = true, length = 19, precision = 0)
	@Basic
	public Long getNorthLocationId() {
		return northLocationId;
	}

	public void setNorthLocationId(Long northLocationId) {
		this.northLocationId = northLocationId;
	}

	private Long southLocationId;

	@javax.persistence.Column(name = "SouthLocationID", nullable = true, insertable = true, updatable = true, length = 19, precision = 0)
	@Basic
	public Long getSouthLocationId() {
		return southLocationId;
	}

	public void setSouthLocationId(Long southLocationId) {
		this.southLocationId = southLocationId;
	}

	private Long eastLocationId;

	@javax.persistence.Column(name = "EastLocationID", nullable = true, insertable = true, updatable = true, length = 19, precision = 0)
	@Basic
	public Long getEastLocationId() {
		return eastLocationId;
	}

	public void setEastLocationId(Long eastLocationId) {
		this.eastLocationId = eastLocationId;
	}

	private Long westLocationId;

	@javax.persistence.Column(name = "WestLocationID", nullable = true, insertable = true, updatable = true, length = 19, precision = 0)
	@Basic
	public Long getWestLocationId() {
		return westLocationId;
	}

	public void setWestLocationId(Long westLocationId) {
		this.westLocationId = westLocationId;
	}

	private String controllingGangName;

	@ManyToOne(optional = true)
	@JoinTable(name="Gang")
	@JoinColumn(name="ControllingGangName", referencedColumnName="GangName")
	private Gang controllingGang;

	public Gang getControllingGang() {
		return controllingGang;
	}

	public void setControllingGang(Gang controllingGang) {
		this.controllingGang = controllingGang;
	}

	@javax.persistence.Column(name = "ControllingGangName", nullable = true, insertable = true, updatable = true, length = 30, precision = 0)
	@Basic
	public String getControllingGangName() {
		return controllingGangName;
	}

	public void setControllingGangName(String controllingGangName) {
		this.controllingGangName = controllingGangName;
	}

	private String bestBusinessType;

	@javax.persistence.Column(name = "BestBusinessType", nullable = true, insertable = true, updatable = true, length = 30, precision = 0)
	@Basic
	public String getBestBusinessType() {
		return bestBusinessType;
	}

	public void setBestBusinessType(String bestBusinessType) {
		this.bestBusinessType = bestBusinessType;
	}

	@ManyToOne(optional = true)
	@JoinTable(name="Business")
	@JoinColumn(name="CurrentBusinessType", referencedColumnName="BusinessType")
	private Business currentBusiness;

	public Business getCurrentBusiness() {
		return currentBusiness;
	}

	public void setCurrentBusiness(Business currentBusiness) {
		this.currentBusiness = currentBusiness;
	}

	private String currentBusinessType;

	@javax.persistence.Column(name = "CurrentBusinessType", nullable = true, insertable = true, updatable = true, length = 30, precision = 0)
	@Basic
	public String getCurrentBusinessType() {
		return currentBusinessType;
	}

	public void setCurrentBusinessType(String currentBusinessType) {
		this.currentBusinessType = currentBusinessType;
	}

	private Long businessProblemID;

	@javax.persistence.Column(name = "BusinessProblemID", nullable = true, insertable = true, updatable = true, length = 19, precision = 0)
	@Basic
	public Long getBusinessProblemID() {
		return businessProblemID;
	}

	public void setBusinessProblemID(Long businessProblemID) {
		this.businessProblemID = businessProblemID;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Location location = (Location) o;

		if (bestBusinessType != null ? !bestBusinessType.equals(location.bestBusinessType) : location.bestBusinessType != null)
			return false;
		if (controllingGangName != null ? !controllingGangName.equals(location.controllingGangName) : location.controllingGangName != null)
			return false;
		if (currentBusinessType != null ? !currentBusinessType.equals(location.currentBusinessType) : location.currentBusinessType != null)
			return false;
		if (eastLocationId != null ? !eastLocationId.equals(location.eastLocationId) : location.eastLocationId != null)
			return false;
		if (locationId != null ? !locationId.equals(location.locationId) : location.locationId != null) return false;
		if (locationName != null ? !locationName.equals(location.locationName) : location.locationName != null)
			return false;
		if (northLocationId != null ? !northLocationId.equals(location.northLocationId) : location.northLocationId != null)
			return false;
		if (southLocationId != null ? !southLocationId.equals(location.southLocationId) : location.southLocationId != null)
			return false;
		if (westLocationId != null ? !westLocationId.equals(location.westLocationId) : location.westLocationId != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = locationId != null ? locationId.hashCode() : 0;
		result = 31 * result + (locationName != null ? locationName.hashCode() : 0);
		result = 31 * result + (northLocationId != null ? northLocationId.hashCode() : 0);
		result = 31 * result + (southLocationId != null ? southLocationId.hashCode() : 0);
		result = 31 * result + (eastLocationId != null ? eastLocationId.hashCode() : 0);
		result = 31 * result + (westLocationId != null ? westLocationId.hashCode() : 0);
		result = 31 * result + (controllingGangName != null ? controllingGangName.hashCode() : 0);
		result = 31 * result + (bestBusinessType != null ? bestBusinessType.hashCode() : 0);
		result = 31 * result + (currentBusinessType != null ? currentBusinessType.hashCode() : 0);
		return result;
	}
}
