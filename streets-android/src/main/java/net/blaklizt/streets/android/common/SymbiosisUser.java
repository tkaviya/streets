package net.blaklizt.streets.android.common;

/******************************************************************************
 * *
 * Created:     01 / 11 / 2015                                             *
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


public class SymbiosisUser {

    public Long symbiosisUserID;
    public String username;
    public String imei;
    public String imsi;
    public String password;
    public Long lastLocationID;
    public Long homePlaceID;
    public String type;

    public SymbiosisUser(Long symbiosisUserID, String username, String imei, String imsi, String password,
                         Long lastLocationID, Long homePlaceID, String type) {
        this.symbiosisUserID = symbiosisUserID;
        this.imei = imei;
        this.username = username;
        this.imsi = imsi;
        this.password = password;
        this.lastLocationID = lastLocationID;
        this.homePlaceID = homePlaceID;
        this.type = type;
    }
}
