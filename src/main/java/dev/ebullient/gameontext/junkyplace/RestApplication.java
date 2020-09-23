package dev.ebullient.gameontext.junkyplace;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class RestApplication {

    @GET
    public String hello() {
        return "hello";
    }

}
