package net.blaklizt.streets.restapi.web.service.impl;

import net.blaklizt.streets.persistence.user_location_history;
import net.blaklizt.streets.restapi.contract.UserLocationHistory;
import net.blaklizt.streets.restapi.web.service.UserService;
import net.blaklizt.symbiosis.sym_persistence.dao.super_class.AbstractDao;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    public UserLocationHistory postUserLocation(UserLocationHistory userLocationHistory) {
        AbstractDao.using(user_location_history.class).save(userLocationHistory);
        return userLocationHistory;
    }
}
