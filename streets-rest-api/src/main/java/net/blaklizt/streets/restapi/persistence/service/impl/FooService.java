//package net.blaklizt.streets.restapi.persistence.service.impl;
//
//import java.util.List;
//
//import net.blaklizt.streets.restapi.persistence.service.IFooService;
//import net.blaklizt.streets.restapi.persistence.dao.IFooDao;
//import net.blaklizt.streets.restapi.persistence.model.Foo;
//import net.blaklizt.streets.restapi.persistence.service.common.AbstractService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.repository.PagingAndSortingRepository;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.google.common.collect.Lists;
//
//@Service
//@Transactional
//public class FooService extends AbstractService<Foo> implements IFooService {
//
//    @Autowired
//    private IFooDao dao;
//
//    public FooService() {
//        super();
//    }
//
//    // API
//
//    @Override
//    protected PagingAndSortingRepository<Foo, Long> getDao() {
//        return dao;
//    }
//
//    // custom methods
//
//    public Foo retrieveByName(final String name) {
//        return dao.retrieveByName(name);
//    }
//
//    // overridden to be secured
//
//    @Override
//    @Transactional(readOnly = true)
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    public List<Foo> findAll() {
//        return Lists.newArrayList(getDao().findAll());
//    }
//
//}
