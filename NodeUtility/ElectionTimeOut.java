package NodeUtility;

public class ElectionTimeOut extends  Thread {

    public ElectionTimeOut(){

    }

    @Override
    public void run(){
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(UtilityData.getInstance().isElection()){
            Election.election();
        }

    }
}
