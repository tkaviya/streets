package net.blaklizt.streets.restapi.web.service;

import net.blaklizt.streets.restapi.contract.UserLocationHistory;

public interface UserService {
    UserLocationHistory postUserLocation(UserLocationHistory userLocationHistory);
}
