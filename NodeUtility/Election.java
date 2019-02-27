package NodeUtility;

import beans.EdgeData;

import java.io.IOException;
import java.util.ArrayList;

public class Election {

    private Election(){

    }

    public static boolean  election(){
        boolean isElected = false;
        ArrayList<EdgeData> list = UtilityData.getInstance().getNodeList();
        if (UtilityData.getInstance().isGreaterID()) {
            Message elected = new Message(null, "elected", UtilityData.getInstance().getMyData());
            for (int i = 0; i < list.size(); i++) {
                SendMessage.sendMessageInThread(elected, list.get(i));
            }
            UtilityData.getInstance().setCoordinator(true);
            UtilityData.getInstance().setCoordinatorData(UtilityData.getInstance().getMyData());
            UtilityData.getInstance().setElection(false);
            isElected = true;
        } else {
            ElectionTimeOut timeOut = new ElectionTimeOut();
            UtilityData.getInstance().setElection(true);
            ArrayList<EdgeData> greaterList = UtilityData.getInstance().getGreaterNodeList();
            Message startElection = new Message(null, "electionStart", UtilityData.getInstance().getMyData());
            int failure = 0;
            for (int i = 0; i < greaterList.size(); i++) {
                try {
                    SendMessage.sendMessage(startElection, greaterList.get(i));
                } catch (IOException e) {
                    UtilityData.getInstance().removeNode(greaterList.get(i));
                    failure++;

                }
            }
            timeOut.start();

            if(failure==greaterList.size() && failure!=0){
                UtilityData.getInstance().setElection(false);
                UtilityData.getInstance().setCoordinator(true);
                UtilityData.getInstance().setCoordinatorData(UtilityData.getInstance().getMyData());
                Message elected = new Message(null, "elected", UtilityData.getInstance().getMyData());
                for (int i = 0; i < list.size(); i++) {
                    SendMessage.sendMessageInThread(elected, list.get(i));
                }
                isElected = true;
            }


        }
        return isElected;

    }
}
