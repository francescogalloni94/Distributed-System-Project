package NodeUtility;

import beans.CoordinatorComunication;
import beans.EdgeData;
import beans.GlobalStats;
import beans.LocalStats;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import services.Semaphore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CoordinatorStatsBuffer {

    private static CoordinatorStatsBuffer instance;
    private GlobalStats recentGlobalStats=null;
    private Object recentStatsLock;
    private HashMap<Integer,ArrayList<LocalStats>> statsMap;
    private Semaphore mapSemaphore;
    private GlobalStatsTimer globalStatsTimer;
    final String URIServerCloud="http://localhost:1337";

    private CoordinatorStatsBuffer(){

        this.recentStatsLock=new Object();
        this.mapSemaphore=new Semaphore();
        this.statsMap=new HashMap<>();
        this.globalStatsTimer = new GlobalStatsTimer();
        this.globalStatsTimer.start();

    }

    public static synchronized CoordinatorStatsBuffer getInstance(){
        if(instance==null)
            instance = new CoordinatorStatsBuffer();
        return instance;
    }

    public void putLocalStats(LocalStats stats,EdgeData sender){
        mapSemaphore.acquireWriteLock();
        ArrayList<LocalStats> list=statsMap.get(sender.getId());
        if(list!=null){
            list.add(stats);
        }else{
            list=new ArrayList<>();
            list.add(stats);
        }

        statsMap.put(sender.getId(),list);

        mapSemaphore.releaseWriteLock();
    }

    public void computeGlobalStats(){
        mapSemaphore.acquireWriteLock();
        HashMap<Integer,ArrayList<LocalStats>> copyMap=new HashMap<>(this.statsMap);
        this.statsMap.clear();
        mapSemaphore.releaseWriteLock();

        int numberOfNode=0;
        double totalValue=0.0;
        double globalMean=0.0;
        if(!copyMap.isEmpty()){
            for(Map.Entry<Integer,ArrayList<LocalStats>> entry : copyMap.entrySet()){
                Integer key = entry.getKey();
                ArrayList<LocalStats> listValue = entry.getValue();
                numberOfNode++;
                for (int i=0;i<listValue.size();i++){
                    totalValue+=listValue.get(i).getValue();
                }
            }
            globalMean=totalValue/numberOfNode;
        }

        GlobalStats globalStats = new GlobalStats(globalMean,System.currentTimeMillis());
        synchronized (recentStatsLock){
            this.recentGlobalStats = globalStats;
        }
        CoordinatorComunication coordinatorComunication = new CoordinatorComunication(globalStats,copyMap);
        ClientConfig config = new DefaultClientConfig();
        config.getClasses().add(JacksonJsonProvider.class);
        Client client = Client.create(config);
        WebResource webResource=client.resource(URIServerCloud+"/statsservice/poststats");
        ClientResponse response = webResource.type("application/json")
                .post(ClientResponse.class, coordinatorComunication);
        System.out.println(this.toString(copyMap));
    }

    public GlobalStats getRecentGlobalStats(){
        synchronized (recentStatsLock) {
            return this.recentGlobalStats;
        }
    }


    public String toString(HashMap<Integer,ArrayList<LocalStats>> copyMap){
        String statsToPrint = "";
        synchronized (recentStatsLock) {
            statsToPrint += "Coordinator Panel:\n";
            statsToPrint += "Recent Global Stat: Timestamp: " +recentGlobalStats.getTimestamp()+"  value: "+recentGlobalStats.getValue()+"\n";
        }
        if(!copyMap.isEmpty()) {
            for (Map.Entry<Integer, ArrayList<LocalStats>> entry : copyMap.entrySet()) {
                Integer key = entry.getKey();
                ArrayList<LocalStats> listValue = entry.getValue();
                if (listValue.size() != 0)
                    statsToPrint += "node: " + key + " TimeStamp: " + listValue.get(listValue.size() - 1).getTimestamp()+"  Value: "+ listValue.get(listValue.size() - 1).getValue()+"\n";
            }
        }
        statsToPrint+="\n\n\n";

        return statsToPrint;

    }
}
