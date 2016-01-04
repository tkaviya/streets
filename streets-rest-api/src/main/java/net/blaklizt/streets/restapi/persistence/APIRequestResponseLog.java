package net.blaklizt.streets.restapi.persistence;

/******************************************************************************
 * *
 * Created:     27 / 12 / 2015                                             *
 * Platform:    Red Hat Linux 9                                            *
 * Author:      Tich de Blak (Tsungai Kaviya)                              *
 * Copyright:   Blaklizt Entertainment                                     *
 * Website:     http://www.blaklizt.net                                    *
 * Contact:     blaklizt@gmail.com                                         *
 * *
 * This program is free software; you can redistribute it and/or modify    *
 * it under the terms of the GNU General Public License as published by    *
 * the Free Software Foundation; either version 2 of the License, or       *
 * (at your option) any later version.                                     *
 * *
 * This program is distributed in the hope that it will be useful,         *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of          *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.    See the         *
 * GNU General Public License for more details.                            *
 * *
 ******************************************************************************/

import net.blaklizt.symbiosis.sym_persistence.entity.super_class.symbiosis_entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@Entity
public class APIRequestResponseLog extends symbiosis_entity {

//	RequestMethod.GET
    String request_type;

    @Column(nullable = false)
    String request_path;

    @Column(nullable = false)
    Long symbiosis_user_id;

    @Column(nullable = false)
    Long device_id;

    @Column(nullable = true)
    Long device_transaction_id;

    @Column(nullable = false, length = 1024)
    String request_body;

    @Column(nullable = false, length = 1024)
    String response_body;

    @Column(nullable = false)
    Integer http_response_code;

    @Column(nullable = false)
    Date request_date;

    @Column(nullable = false)
    Date response_date;

    @Column(nullable = false)
    Integer attempt_count;

    @Column(nullable = false)
    Integer reporting_time_delay;

    public APIRequestResponseLog(String request_type, String request_path, Long symbiosis_user_id,
                                 Long device_id, Long device_transaction_id, String request_body,
                                 String response_body, Integer http_response_code, Date request_date,
                                 Date response_date, Integer attempt_count, Integer reporting_time_delay) {
        this.request_type = request_type;
        this.request_path = request_path;
        this.symbiosis_user_id = symbiosis_user_id;
        this.device_id = device_id;
        this.device_transaction_id = device_transaction_id;
        this.request_body = request_body;
        this.response_body = response_body;
        this.http_response_code = http_response_code;
        this.request_date = request_date;
        this.response_date = response_date;
        this.attempt_count = attempt_count;
        this.reporting_time_delay = reporting_time_delay;
    }

    public String getRequest_path() {
        return request_path;
    }

    public void setRequest_path(String request_path) {
        this.request_path = request_path;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public Long getSymbiosis_user_id() {
        return symbiosis_user_id;
    }

    public void setSymbiosis_user_id(Long symbiosis_user_id) {
        this.symbiosis_user_id = symbiosis_user_id;
    }

    public Long getDevice_transaction_id() {
        return device_transaction_id;
    }

    public void setDevice_transaction_id(Long device_transaction_id) {
        this.device_transaction_id = device_transaction_id;
    }

    public Long getDevice_id() {
        return device_id;
    }

    public void setDevice_id(Long device_id) {
        this.device_id = device_id;
    }

    public String getRequest_body() {
        return request_body;
    }

    public void setRequest_body(String request_body) {
        this.request_body = request_body;
    }

    public String getResponse_body() {
        return response_body;
    }

    public void setResponse_body(String response_body) {
        this.response_body = response_body;
    }

    public Integer getHttp_response_code() {
        return http_response_code;
    }

    public void setHttp_response_code(Integer http_response_code) {
        this.http_response_code = http_response_code;
    }

    public Date getRequest_date() {
        return request_date;
    }

    public void setRequest_date(Date request_date) {
        this.request_date = request_date;
    }

    public Date getResponse_date() {
        return response_date;
    }

    public void setResponse_date(Date response_date) {
        this.response_date = response_date;
    }

    public Integer getAttempt_count() {
        return attempt_count;
    }

    public void setAttempt_count(Integer attempt_count) {
        this.attempt_count = attempt_count;
    }

    public Integer getReporting_time_delay() {
        return reporting_time_delay;
    }

    public void setReporting_time_delay(Integer reporting_time_delay) {
        this.reporting_time_delay = reporting_time_delay;
    }
}
