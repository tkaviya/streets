package net.blaklizt.streets.persistence;

import javax.persistence.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 9/8/13
 * Time: 3:45 PM
 */
@Entity
public class BusinessProblem {
	private Long businessProblemID;

	@javax.persistence.Column(name = "BusinessProblemID", nullable = false, insertable = true, updatable = true, length = 19, precision = 0)
	@javax.persistence.Id
	public Long getBusinessProblemID() {
		return businessProblemID;
	}

	public void setBusinessProblemID(Long businessProblemID) {
		this.businessProblemID = businessProblemID;
	}

	private String businessType;

	@javax.persistence.Column(name = "BusinessType", nullable = true, insertable = true, updatable = true, length = 30, precision = 0)
	@javax.persistence.Basic
	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}

	private String problemMenuName;

	@javax.persistence.Column(name = "ProblemMenuName", nullable = false, insertable = true, updatable = true, length = 30)
	@javax.persistence.Basic
	public String getProblemMenuName() {
		return problemMenuName;
	}

	public void setProblemMenuName(String problemMenuName) {
		this.problemMenuName = problemMenuName;
	}

	private String problemDescription;

	@javax.persistence.Column(name = "ProblemDescription", nullable = false, insertable = true, updatable = true, length = 256, precision = 0)
	@javax.persistence.Basic
	public String getProblemDescription() {
		return problemDescription;
	}

	public void setProblemDescription(String problemDescription) {
		this.problemDescription = problemDescription;
	}

	private Double cost;

	@javax.persistence.Column(name = "Cost", nullable = false, insertable = true, updatable = true, length = 22, precision = 0)
	@javax.persistence.Basic
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

		BusinessProblem that = (BusinessProblem) o;

		if (businessProblemID != null ? !businessProblemID.equals(that.businessProblemID) : that.businessProblemID != null)
			return false;
		if (businessType != null ? !businessType.equals(that.businessType) : that.businessType != null) return false;
		if (cost != null ? !cost.equals(that.cost) : that.cost != null) return false;
		if (problemDescription != null ? !problemDescription.equals(that.problemDescription) : that.problemDescription != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = businessProblemID != null ? businessProblemID.hashCode() : 0;
		result = 31 * result + (businessType != null ? businessType.hashCode() : 0);
		result = 31 * result + (problemDescription != null ? problemDescription.hashCode() : 0);
		result = 31 * result + (cost != null ? cost.hashCode() : 0);
		return result;
	}
}
