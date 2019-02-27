package services;


import beans.EdgeData;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

@Path("/sensorservice")
public class SensorService {

    @GET
    @Path("getnearestnode")
    @Produces({"application/json"})
    @Consumes({"application/json"})
    public Response getNearNode(@QueryParam("x") int x, @QueryParam("y") int y){
        City city=City.getInstance();
        EdgeData data=city.getNearestNode(x,y);
        if(data!=null){

            return Response.status(200).entity(data).build();

        }else{
            return Response.status(404).build();
        }
    }
}
