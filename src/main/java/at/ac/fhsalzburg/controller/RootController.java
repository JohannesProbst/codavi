package at.ac.fhsalzburg.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public HttpEntity<Menu> rootMenu(){
        Link selfLink = ControllerLinkBuilder.linkTo(RootController.class).withSelfRel();
        Link chartsLink = ControllerLinkBuilder.linkTo(ChartsController.class).withRel("Charts");
        Link uuidsLink = ControllerLinkBuilder.linkTo(ActiveChartsController.class).withRel("Active UUIDs");
        Menu rootMenu = new Menu();
        rootMenu.add(selfLink, chartsLink, uuidsLink);
        return new ResponseEntity<Menu>(rootMenu, HttpStatus.OK);
    }

    class Menu extends ResourceSupport {
    }
}
