package net.blaklizt.streets.persistence;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 9/8/13
 * Time: 2:58 PM
 */
@Entity
public class Business {
	private String businessType;

	@javax.persistence.Column(name = "BusinessType", nullable = false, insertable = true, updatable = true, length = 30, precision = 0)
	@Id
	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	private Double startupCost;

	@javax.persistence.Column(name = "StartupCost", nullable = false, insertable = true, updatable = true, length = 22, precision = 0)
	@Basic
	public Double getStartupCost() {
		return startupCost;
	}

	public void setStartupCost(Double startupCost) {
		this.startupCost = startupCost;
	}

	private Integer riskFactor;

	@javax.persistence.Column(name = "RiskFactor", nullable = false, insertable = true, updatable = true, length = 10, precision = 0)
	@Basic
	public Integer getRiskFactor() {
		return riskFactor;
	}

	public void setRiskFactor(Integer riskFactor) {
		this.riskFactor = riskFactor;
	}

	private Double payout;

	@javax.persistence.Column(name = "Payout", nullable = false, insertable = true, updatable = true, length = 22, precision = 0)
	@Basic
	public Double getPayout() {
		return payout;
	}

	public void setPayout(Double payout) {
		this.payout = payout;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Business business = (Business) o;

		if (businessType != null ? !businessType.equals(business.businessType) : business.businessType != null)
			return false;
		if (payout != null ? !payout.equals(business.payout) : business.payout != null) return false;
		if (riskFactor != null ? !riskFactor.equals(business.riskFactor) : business.riskFactor != null) return false;
		if (startupCost != null ? !startupCost.equals(business.startupCost) : business.startupCost != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = businessType != null ? businessType.hashCode() : 0;
		result = 31 * result + (startupCost != null ? startupCost.hashCode() : 0);
		result = 31 * result + (riskFactor != null ? riskFactor.hashCode() : 0);
		result = 31 * result + (payout != null ? payout.hashCode() : 0);
		return result;
	}
}
