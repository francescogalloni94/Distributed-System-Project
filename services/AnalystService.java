package services;


import beans.GlobalStats;
import beans.InternalState;
import beans.LocalStats;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.ArrayList;


@Path("/analystservice")
public class AnalystService {

    @GET
    @Path("citystate")
    @Produces({"application/json"})
    public Response getCityState(){
        City city = City.getInstance();
        InternalState internalState = city.getInternalState();
        if (internalState.getInternalState().size()==0)
            return Response.status(404).build();
        else
            return Response.status(200).entity(internalState).build();
    }

    @GET
    @Path("nodestats")
    @Produces({"application/json"})
    public Response getNodeStats(@QueryParam("nodeid") int nodeID,@QueryParam("n") int n){
        StatsRepo repo = StatsRepo.getInstance();
        ArrayList<LocalStats> localStats = repo.getNodeStats(nodeID,n);
        if(localStats!=null)
        return Response.status(200).entity(localStats).build();
        else
            return Response.status(404).build();
    }


    @GET
    @Path("globalstats")
    @Produces({"application/json"})
    public Response getGlobalStats(@QueryParam("n") int n){

        StatsRepo repo = StatsRepo.getInstance();
        ArrayList<GlobalStats> globalStats = repo.getGlobalStats(n);
        if(globalStats!=null)
            return Response.status(200).entity(globalStats).build();
        else
            return Response.status(404).build();
    }

    @GET
    @Path("nodestd")
    @Produces({"application/json"})
    public Response getLocalSTD(@QueryParam("nodeid") int nodeID,@QueryParam("n") int n){

        StatsRepo repo = StatsRepo.getInstance();
        Double std = repo.getLocalSTD(nodeID,n);
        if(std!=null)
            return Response.status(200).entity(std).build();
        else
            return Response.status(404).build();
    }

    @GET
    @Path("nodemean")
    @Produces({"application/json"})
    public Response getLocalMean(@QueryParam("nodeid") int nodeID,@QueryParam("n") int n){

        StatsRepo repo = StatsRepo.getInstance();
        Double mean = repo.getLocalMean(nodeID,n);
        if(mean!=null)
            return Response.status(200).entity(mean).build();
        else
            return Response.status(404).build();
    }

    @GET
    @Path("globalstd")
    @Produces({"application/json"})
    public Response getGlobalSTD(@QueryParam("n") int n){

        StatsRepo repo = StatsRepo.getInstance();
        Double std = repo.getGlobalSTD(n);
        if(std!=null)
            return Response.status(200).entity(std).build();
        else
            return Response.status(404).build();
    }

    @GET
    @Path("globalmean")
    @Produces({"application/json"})
    public Response getGlobalMean(@QueryParam("n") int n){

        StatsRepo repo = StatsRepo.getInstance();
        Double mean = repo.getGlobalMean(n);
        if (mean!=null)
            return Response.status(200).entity(mean).build();
        else
            return Response.status(404).build();
    }


}
