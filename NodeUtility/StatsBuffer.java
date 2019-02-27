package NodeUtility;

import beans.EdgeData;
import beans.LocalStats;
import com.google.gson.Gson;
import services.Semaphore;
import simulation_src_2018.Measurement;

import java.io.IOException;
import java.util.ArrayList;

public class StatsBuffer {

    private static StatsBuffer instance;
    private Semaphore bufferSemaphore;
    private ArrayList<Measurement> buffer;

    private StatsBuffer(){
       this.buffer=new ArrayList<>();
       this.bufferSemaphore=new Semaphore();
    }

    public static synchronized StatsBuffer getInstance(){
        if(instance==null)
            instance = new StatsBuffer();
        return instance;
    }

    public void putMeasurement(Measurement measurement){
        bufferSemaphore.acquireWriteLock();
        buffer.add(measurement);
        bufferSemaphore.releaseWriteLock();
        if(buffer.size()==40)
            computeMean();

    }

    public void computeMean(){
        double total=0.0;
        long timeStamp=System.currentTimeMillis();
        bufferSemaphore.acquireReadLock();
        for (int i=0;i<=39;i++){
            total+=this.buffer.get(i).getValue();
        }
        bufferSemaphore.releaseReadLock();
        double mean=total/40.0;
        LocalStats localStats=new LocalStats(mean,timeStamp);
        Message message = null;
        if(!UtilityData.getInstance().getCoordinator() && UtilityData.getInstance().getCoordinatorData()!=null){
            System.out.println("LocalStat  Timestamp: "+localStats.getTimestamp()+"  Value: "+localStats.getValue()+"\n");
            Gson gson=new Gson();
            String jsonStats=gson.toJson(localStats,LocalStats.class);
            message=new Message(jsonStats,"localStats",UtilityData.getInstance().getMyData());
            try {
                SendMessage.sendMessage(message,UtilityData.getInstance().getCoordinatorData());
            } catch (IOException e) {
                if(UtilityData.getInstance().isElection()){
                    TemporaryElectionBuffer buffer = TemporaryElectionBuffer.getInstance();
                    buffer.putMessage(message);
                }else{
                       boolean isElected = Election.election();
                        if(isElected) {
                            CoordinatorStatsBuffer coordinatorBuffer = CoordinatorStatsBuffer.getInstance();
                            coordinatorBuffer.putLocalStats(localStats, UtilityData.getInstance().getMyData());
                        }else {

                            TemporaryElectionBuffer buffer = TemporaryElectionBuffer.getInstance();
                            buffer.putMessage(message);
                        }



                }
            }
        }else{
           if(UtilityData.getInstance().getCoordinator()) {
               CoordinatorStatsBuffer coordinatorBuffer = CoordinatorStatsBuffer.getInstance();
               coordinatorBuffer.putLocalStats(localStats, UtilityData.getInstance().getMyData());
           }else if(!UtilityData.getInstance().getCoordinator() && UtilityData.getInstance().getCoordinatorData()==null){
               TemporaryElectionBuffer.getInstance().putMessage(message);
           }
        }
        bufferSemaphore.acquireWriteLock();
        for (int i=0;i<=19;i++){
            this.buffer.remove(0);
        }
        bufferSemaphore.releaseWriteLock();

    }
}
