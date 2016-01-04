package net.baeldung.persistence.service;

import net.baeldung.persistence.IOperations;
import net.baeldung.persistence.model.Foo;

public interface IFooService extends IOperations<Foo> {

    Foo retrieveByName(String name);

}
