package services;


import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;


import beans.EdgeData;
import beans.Node;

import java.util.List;

@Path("/edgenodeservice")
public class EdgeNodeService {

    @POST
    @Path("postnode")
    @Produces({"application/json", "application/xml"})
    @Consumes({"application/json", "application/xml"})
    public Response addEdgeNode(Node node){
        City city=City.getInstance();
        List<EdgeData> data= null;
        try {
            data = city.addNode(node);
            return Response.status(201).entity(data).build();
        } catch (PositionException e) {
            return Response.status(409).entity("PositionException").build();
        } catch (IDException e) {
            return Response.status(409).entity("IDException").build();
        }

    }

    @DELETE
    @Path("deletenode")
    @Consumes({"application/json"})
    public Response removeEdgeNode(Node node){
        City city=City.getInstance();
        city.removeNode(node);
        return Response.status(204).build();

    }





}
