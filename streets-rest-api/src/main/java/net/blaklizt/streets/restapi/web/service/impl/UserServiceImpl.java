package net.blaklizt.streets.restapi.web.service.impl;

import net.blaklizt.streets.persistence.user_location_history;
import net.blaklizt.streets.restapi.contract.UserLocationHistory;
import net.blaklizt.streets.restapi.web.service.UserService;
import net.blaklizt.symbiosis.sym_persistence.dao.super_class.AbstractDao;
import net.blaklizt.symbiosis.sym_persistence.entity.enumeration.symbiosis_response_code;
import net.blaklizt.symbiosis.sym_persistence.structure.ResponseObject;

import org.springframework.stereotype.Service;

import static net.blaklizt.symbiosis.sym_persistence.dao.super_class.SymbiosisEntityManager.DaoDataManager.using;

@Service
public class UserServiceImpl implements UserService {

	@Override
    public UserLocationHistory postUserLocation(UserLocationHistory userLocationHistory) {
        AbstractDao.using(user_location_history.class).save(userLocationHistory);
        return userLocationHistory;
    }

	@Override
	public ResponseObject<symbiosis_response_code> test() {
		return using(symbiosis_response_code.class).findUniqueWhere(null);
	}
}
