package net.blaklizt.streets.api.service;

import net.blaklizt.streets.api.contract.UserLocationHistory;

public interface UserService {
    UserLocationHistory postUserLocation(UserLocationHistory userLocationHistory);
}
