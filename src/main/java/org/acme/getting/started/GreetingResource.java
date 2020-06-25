package org.acme.getting.started;

import org.acme.getting.started.crd.Foo;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.stream.Collectors;

@Path("/hello")
public class GreetingResource {

    @Inject
    MyService service;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "hello: " + service.getFoos().stream().map(Foo::toString).collect(Collectors.joining(", "));
    }
}