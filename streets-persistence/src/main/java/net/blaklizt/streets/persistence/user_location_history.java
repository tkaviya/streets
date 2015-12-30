package net.blaklizt.streets.persistence;

import net.blaklizt.symbiosis.sym_persistence.entity.super_class.symbiosis_entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 8/10/13
 * Time: 11:25 AM
 */

@Entity
public class user_location_history extends symbiosis_entity

{
    private Long symbiosis_user_id;
    private Date event_date;
    private Long latitude;
    private Long longitude;

    @Column(nullable = false)
    public Long getSymbiosis_user_id() {
        return symbiosis_user_id;
    }

    public void setSymbiosis_user_id(Long symbiosis_user_id) {
        this.symbiosis_user_id = symbiosis_user_id;
    }

	@Column(nullable = false)
	public Date getEvent_date() {
		return event_date;
	}

	public void setEvent_date(Date event_date) { this.event_date = event_date; }

    @Column(nullable = false)
    public Long getLatitude() {
        return latitude;
    }

    public void setLatitude(Long latitude) {
        this.latitude = latitude;
    }

    @Column(nullable = false)
    public Long getLongitude() {
        return longitude;
    }

    public void setLongitude(Long longitude) {
        this.longitude = longitude;
    }
}
