package dev.ebullient.gameontext.junkyplace;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/hello")
public class RestApplication {

    @GET
    public String hello() {
        return "hello";
    }

}
