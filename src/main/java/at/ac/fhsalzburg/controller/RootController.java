package at.ac.fhsalzburg.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public HttpEntity<String> rootMenu(){
        String menu = "menu";
        return new ResponseEntity<String>(menu, HttpStatus.OK);
    }
}
