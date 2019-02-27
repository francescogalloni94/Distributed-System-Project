package NodeUtility;

import beans.EdgeData;

import java.util.ArrayList;

public class CoordinatorResponseTimeOut extends Thread {

    public CoordinatorResponseTimeOut(){

    }

    public void run(){
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (UtilityData.getInstance().getCoordinatorData()==null){
           ArrayList<EdgeData> nodeList = UtilityData.getInstance().getNodeList();
           Message message = new Message(null,"Presentation",UtilityData.getInstance().getMyData());
           for (int i=0;i<nodeList.size();i++){
               SendMessage.sendMessageInThread(message,nodeList.get(i));
           }
           UtilityData.getInstance().setElection(true);
           ElectionTimeOut timeOut = new ElectionTimeOut();
           timeOut.start();
        }
    }
}
