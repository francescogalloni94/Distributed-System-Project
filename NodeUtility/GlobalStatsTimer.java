package NodeUtility;

import beans.GlobalStats;

public class GlobalStatsTimer extends Thread {

    public GlobalStatsTimer(){

    }

    @Override
    public void run(){
        while(true){
            try {
                Thread.sleep(5000);
                CoordinatorStatsBuffer coordinatorBuffer=CoordinatorStatsBuffer.getInstance();
                coordinatorBuffer.computeGlobalStats();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
