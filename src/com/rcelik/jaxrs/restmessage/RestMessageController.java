package com.rcelik.jaxrs.restmessage;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/messages")
public class RestMessageController {

    @GET
    @Path("/message")
    public String getMessage() {
        return "Hello World!";
    }
}
