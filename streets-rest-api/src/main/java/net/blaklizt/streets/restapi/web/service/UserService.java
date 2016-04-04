package net.blaklizt.streets.restapi.web.service;

import net.blaklizt.streets.restapi.contract.UserLocationHistory;
import net.blaklizt.symbiosis.sym_persistence.entity.enumeration.symbiosis_response_code;
import net.blaklizt.symbiosis.sym_persistence.structure.ResponseObject;

public interface UserService {
    UserLocationHistory postUserLocation(UserLocationHistory userLocationHistory);
    ResponseObject<symbiosis_response_code> test();
}
