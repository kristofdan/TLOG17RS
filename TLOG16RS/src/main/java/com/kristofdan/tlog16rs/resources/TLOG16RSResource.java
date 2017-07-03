package com.kristofdan.tlog16rs.resources;

import com.kristofdan.tlog16rs.core.Greeting;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class TLOG16RSResource {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getGreeting() {
        return "Hello world!";
    }
    
    //Ez a path hozzáadódik a class szintűhöz!
    @Path("/path_param/{name}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getNamedGreeting(@PathParam(value = "name") String name) {
        return "Hello " + name;
    }
    
    @Path("/query_param")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getNamedStringWithParam(@DefaultValue("world") @QueryParam("name") String name) {
        return "Hello " + name;
    }
    
    
    @Path("/hello_json")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Greeting getJSONGreeting() {
        return new Greeting("Hello world!");
    }
}
