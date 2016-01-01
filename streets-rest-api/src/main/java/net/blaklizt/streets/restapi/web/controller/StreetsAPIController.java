package net.blaklizt.streets.restapi.web.controller;

import net.blaklizt.streets.restapi.persistence.model.Foo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping(value = "/foos")
public class StreetsAPIController {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

//    @Autowired
//    private IFooService service;

    public StreetsAPIController() {
        super();
    }

    // API

    // read - one

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Foo findById(@PathVariable("id") final Long id, final HttpServletResponse response) {
//        final Foo resourceById = RestPreconditions.checkFound(service.findOne(id));
//
//        eventPublisher.publishEvent(new SingleResourceRetrievedEvent(this, response));
//        return resourceById;
		return null;
    }

    // read - all

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Foo> findAll() {
//        return service.findAll();
		return null;
    }

    @RequestMapping(params = { "page", "size" }, method = RequestMethod.GET)
    @ResponseBody
    public List<Foo> findPaginated(@RequestParam("page") final int page, @RequestParam("size") final int size, final UriComponentsBuilder uriBuilder, final HttpServletResponse response) {
//        final Page<Foo> resultPage = service.findPaginated(page, size);
//        if (page > resultPage.getTotalPages()) {
//            throw new MyResourceNotFoundException();
//        }
//        eventPublisher.publishEvent(new PaginatedResultsRetrievedEvent<Foo>(Foo.class, uriBuilder, response, page, resultPage.getTotalPages(), size));
//
//        return resultPage.getContent();
		return null;
    }

    // write

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody final Foo resource, final HttpServletResponse response) {
//        Preconditions.checkNotNull(resource);
//        final Long idOfCreatedResource = service.create(resource).getId();
//
//        eventPublisher.publishEvent(new ResourceCreatedEvent(this, response, idOfCreatedResource));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void update(@PathVariable("id") final Long id, @RequestBody final Foo resource) {
//        Preconditions.checkNotNull(resource);
//        RestPreconditions.checkFound(service.findOne(resource.getId()));
//        service.update(resource);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") final Long id) {
//        service.deleteById(id);
    }

}
