package net.blaklizt.streets.persistence;

import javax.persistence.*;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 12/15/13
 * Time: 12:28 PM
 */
@Entity
@Table (name = "EventLog")
public class EventLog {
	private Long eventLogId;
	Date eventDate;
	Long userID;
	String description;

	public EventLog() {}

	public EventLog(Long eventLogId, Date eventDate, Long userID, String description)
	{
		this.eventLogId = eventLogId;
		this.eventDate = eventDate;
		this.userID = userID;
		this.description = description;
	}

	@Column(name = "EventLogID")
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	public Long getEventLogId() {
		return eventLogId;
	}

	public void setEventLogId(Long eventLogId) {
		this.eventLogId = eventLogId;
	}

	@Column(name = "EventDate")
	@Basic
	public Date getEventDate() {
		return eventDate;
	}

	public void setEventDate(Date eventDate) {
		this.eventDate = eventDate;
	}

	@Column(name = "UserID")
	@Basic
	public Long getUserID() {
		return userID;
	}

	public void setUserID(Long userID) {
		this.userID = userID;
	}

	@Column(name = "Description")
	@Basic
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
