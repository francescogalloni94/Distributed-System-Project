package services;


import beans.CoordinatorComunication;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/statsservice")
public class StatsService {


    @POST
    @Path("poststats")
    @Consumes({"application/json"})
    public Response postStats(CoordinatorComunication coordinatorComunication){
        StatsRepo repo = StatsRepo.getInstance();
        repo.putGlobalStat(coordinatorComunication.getGlobalStats());
        repo.putLocalStats(coordinatorComunication.getStatsMap());
        return Response.status(201).build();

    }

}
