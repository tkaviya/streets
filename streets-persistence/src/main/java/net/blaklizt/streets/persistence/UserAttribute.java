package net.blaklizt.streets.persistence;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 8/29/13
 * Time: 12:21 AM
 */
@Entity
@Table (name = "UserAttribute")
public class UserAttribute {
	private Long userID;
	private Double bankBalance;
	private Integer healthPoints;
	private String gangName;
	private Long locationID;

	@ManyToOne
	@JoinTable(name="Location")
	@JoinColumn(name="LocationID", referencedColumnName="LocationID")
	private Location location;

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@ManyToOne(optional = true)
	@JoinTable(name="Gang")
	@JoinColumn(name="GangName", referencedColumnName="GangName")
	private Gang gang;

	public Gang getGang() {
		return gang;
	}

	public void setGang(Gang gang) {
		this.gang = gang;
	}

	@Column(name = "UserID", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
	@Id
	public Long getUserID() {
		return userID;
	}

	public void setUserID(Long userId) {
		this.userID = userId;
	}

	@Column(name = "GangName", nullable = true, insertable = true, updatable = true, length = 30, precision = 0)
	@Basic
	public String getGangName() {
		return gangName;
	}

	public void setGangName(String gangName) {
		this.gangName = gangName;
	}

	@Column(name = "BankBalance", nullable = false, insertable = true, updatable = true, length = 22, precision = 2)
	@Basic
	public Double getBankBalance() {
		return bankBalance;
	}

	public void setBankBalance(Double bankBalance) {
		this.bankBalance = bankBalance;
	}

	@Column(name = "HealthPoints", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
	@Basic
	public Integer getHealthPoints() {
		return healthPoints;
	}

	public void setHealthPoints(Integer healthPoints) {
		this.healthPoints = healthPoints;
	}

	@Column(name = "LocationID", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
	@Basic
	public Long getLocationID() {
		return locationID;
	}

	public void setLocationID(Long locationID) {
		this.locationID = locationID;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		UserAttribute that = (UserAttribute) o;

		if (bankBalance != null ? !bankBalance.equals(that.bankBalance) : that.bankBalance != null) return false;
		if (gangName != null ? !gangName.equals(that.gangName) : that.gangName != null) return false;
		if (healthPoints != null ? !healthPoints.equals(that.healthPoints) : that.healthPoints != null) return false;
		if (location != null ? !location.equals(that.location) : that.location != null) return false;
		if (userID != null ? !userID.equals(that.userID) : that.userID != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = userID != null ? userID.hashCode() : 0;
		result = 31 * result + (gangName != null ? gangName.hashCode() : 0);
		result = 31 * result + (bankBalance != null ? bankBalance.hashCode() : 0);
		result = 31 * result + (healthPoints != null ? healthPoints.hashCode() : 0);
		result = 31 * result + (location != null ? location.hashCode() : 0);
		return result;
	}
}
