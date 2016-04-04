package net.blaklizt.streets.restapi.web.controller;

import io.swagger.annotations.ApiOperation;
import net.blaklizt.streets.restapi.contract.UserLocationHistory;
import net.blaklizt.streets.restapi.web.hateoas.event.SingleResourceRetrievedEvent;
import net.blaklizt.streets.restapi.web.service.UserService;
import net.blaklizt.symbiosis.sym_persistence.entity.enumeration.symbiosis_response_code;
import net.blaklizt.symbiosis.sym_persistence.structure.ResponseObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/api/users")
public class StreetsAPIController {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private UserService userService;

	@ApiOperation(value = "Post user's current location")
    @RequestMapping(value="/{user_id}/location", method= RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public UserLocationHistory postUserLocation(@PathVariable("user_id") Long userId,
                                 UserLocationHistory userLocationHistory, HttpServletResponse response)
    {
        UserLocationHistory postResponse = userService.postUserLocation(userLocationHistory);
        String locationURL = ServletUriComponentsBuilder.fromCurrentRequest()
                .pathSegment("{id}").buildAndExpand(userLocationHistory.getId())
                .toUriString();

        response.setHeader("Location", locationURL);
        return postResponse;
    }

	@ApiOperation(value = "API test")
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
	@ResponseStatus(HttpStatus.OK)
    public ResponseObject<symbiosis_response_code> findById(final HttpServletResponse response) {
		ResponseObject<symbiosis_response_code> responseObject = userService.test();
		eventPublisher.publishEvent(new SingleResourceRetrievedEvent(this, response));
        return responseObject;
    }

//
//    @RequestMapping(params = { "page", "size" }, method = RequestMethod.GET)
//    @ResponseBody
//    public List<Foo> findPaginated(@RequestParam("page") final int page, @RequestParam("size") final int size, final UriComponentsBuilder uriBuilder, final HttpServletResponse response) {
////        final Page<Foo> resultPage = service.findPaginated(page, size);
////        if (page > resultPage.getTotalPages()) {
////            throw new MyResourceNotFoundException();
////        }
////        eventPublisher.publishEvent(new PaginatedResultsRetrievedEvent<Foo>(Foo.class, uriBuilder, response, page, resultPage.getTotalPages(), size));
////
////        return resultPage.getContent();
//		return null;
//    }
//
//    // write
//
//    @RequestMapping(method = RequestMethod.POST)
//    @ResponseStatus(HttpStatus.CREATED)
//    public void create(@RequestBody final Foo resource, final HttpServletResponse response) {
////        Preconditions.checkNotNull(resource);
////        final Long idOfCreatedResource = service.create(resource).getId();
////
////        eventPublisher.publishEvent(new ResourceCreatedEvent(this, response, idOfCreatedResource));
//    }
//
//    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
//    @ResponseStatus(HttpStatus.OK)
//    public void update(@PathVariable("id") final Long id, @RequestBody final Foo resource) {
////        Preconditions.checkNotNull(resource);
////        RestPreconditions.checkFound(service.findOne(resource.getId()));
////        service.update(resource);
//    }
//
//    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
//    @ResponseStatus(HttpStatus.OK)
//    public void delete(@PathVariable("id") final Long id) {
////        service.deleteById(id);
//    }

}
