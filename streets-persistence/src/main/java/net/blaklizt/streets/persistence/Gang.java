package net.blaklizt.streets.persistence;

import javax.persistence.*;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 8/29/13
 * Time: 12:21 AM
 */
@Entity
@Table (name = "Gang")
public class Gang {
	private String gangName;
	private Boolean aiControlled;
	private Double currentBalance;
	private Double payout;
	private Long gangLeaderID;

	@Column(name = "GangName", nullable = false, insertable = true, updatable = true, length = 30, precision = 0)
	@Id
	public String getGangName() {
		return gangName;
	}

	public void setGangName(String gangName) {
		this.gangName = gangName;
	}

	@Column(name = "GangLeaderID", nullable = true, insertable = true, updatable = true, length = 10, precision = 0)
	@Basic
	public Long getGangLeaderID() {
		return gangLeaderID;
	}

	public void setGangLeaderID(Long gangLeaderId) {
		this.gangLeaderID = gangLeaderId;
	}

	@Column(name = "AIControlled", nullable = false, insertable = true, updatable = true, length = 0, precision = 0)
	@Basic
	public Boolean getAiControlled() {
		return aiControlled;
	}

	public void setAiControlled(Boolean aiControlled) {
		this.aiControlled = aiControlled;
	}

	@Column(name = "CurrentBalance", nullable = false, insertable = true, updatable = true)
	@Basic
	public Double getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(Double currentBalance) {
		this.currentBalance = currentBalance;
	}

	@Column(name = "Payout", nullable = false, insertable = true, updatable = true)
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

		Gang gang = (Gang) o;

		if (payout != null ? !payout.equals(gang.payout) : gang.payout != null) return false;
		if (currentBalance != null ? !currentBalance.equals(gang.currentBalance) : gang.currentBalance != null) return false;
		if (aiControlled != null ? !aiControlled.equals(gang.aiControlled) : gang.aiControlled != null) return false;
		if (gangLeaderID != null ? !gangLeaderID.equals(gang.gangLeaderID) : gang.gangLeaderID != null) return false;
		if (gangName != null ? !gangName.equals(gang.gangName) : gang.gangName != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = gangName != null ? gangName.hashCode() : 0;
		result = 31 * result + (payout != null ? payout.hashCode() : 0);
		result = 31 * result + (currentBalance != null ? currentBalance.hashCode() : 0);
		result = 31 * result + (gangLeaderID != null ? gangLeaderID.hashCode() : 0);
		result = 31 * result + (aiControlled != null ? aiControlled.hashCode() : 0);
		return result;
	}
}
