package NodeUtility;

import beans.EdgeData;
import beans.GlobalStats;
import beans.LocalStats;
import com.google.gson.Gson;
import simulation_src_2018.Measurement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MessageManagementThread extends Thread {

    private Socket connection;
    private BufferedReader in;
    private Gson gson;

    public MessageManagementThread(Socket connection){
        this.connection=connection;
        try {
            this.in=new BufferedReader(new InputStreamReader(this.connection.getInputStream()));
            this.gson=new Gson();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        try {
            String jsonMessage=in.readLine();
            Message message=gson.fromJson(jsonMessage,Message.class);
            if(message.getMessageType().equals("Presentation"))
                handlePresentation(message);
            else if(message.getMessageType().equals("CoordinatorResponse"))
                handleCoordinatorResponse(message);
            else if(message.getMessageType().equals("sensorMeasurement"))
                handleSensorMeasurement(message);
            else if(message.getMessageType().equals("localStats"))
                handleLocalStats(message);
            else if(message.getMessageType().equals("globalStatsResponse"))
                handleGlobalStatsResponse(message);
            else if(message.getMessageType().equals("elected"))
                handleElected(message);
            else if(message.getMessageType().equals("electionStart"))
                handleElectionStart(message);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void handlePresentation(Message message){

        UtilityData.getInstance().addNode(message.getMessageSender());
        if(UtilityData.getInstance().getCoordinator()){
            Message coordinatorResponse=new Message(null,"CoordinatorResponse",UtilityData.getInstance().getMyData());
            try {
                SendMessage.sendMessage(coordinatorResponse,message.getMessageSender());
            } catch (IOException e) {
                UtilityData.getInstance().removeNode(message.getMessageSender());
            }
        }


    }


    private void handleCoordinatorResponse(Message message){
        UtilityData.getInstance().setElection(false);
        UtilityData.getInstance().setCoordinatorData(message.getMessageSender());
        TemporaryElectionBuffer.getInstance().flushMessagesToNewCoordinator();

    }

    private void handleSensorMeasurement(Message message){
        Measurement measurement=gson.fromJson(message.getMessageObject(),Measurement.class);
        StatsBuffer.getInstance().putMeasurement(measurement);

    }


    private void handleLocalStats(Message message){

        CoordinatorStatsBuffer buffer=CoordinatorStatsBuffer.getInstance();
        try {
            GlobalStats globalStats = buffer.getRecentGlobalStats();
            String jsonStats = gson.toJson(globalStats,GlobalStats.class);
            Message response = new Message(jsonStats,"globalStatsResponse",UtilityData.getInstance().getMyData());
            SendMessage.sendMessage(response,message.getMessageSender());
            LocalStats localStats=gson.fromJson(message.getMessageObject(),LocalStats.class);
            buffer.putLocalStats(localStats,message.getMessageSender());
        } catch (IOException e) {
            UtilityData.getInstance().removeNode(message.getMessageSender());
        }

    }

    private void handleGlobalStatsResponse(Message message){
            GlobalStats globalStats = gson.fromJson(message.getMessageObject(),GlobalStats.class);
            if(globalStats!=null && !UtilityData.getInstance().getCoordinator()) {
                System.out.println("GlobalStat from Coordinator node:" + UtilityData.getInstance().getCoordinatorData().getId());
                System.out.println("TimeStamp: " + globalStats.getTimestamp() + "  Value: " + globalStats.getValue()+"\n");
            }
    }

    private void handleElected(Message message){
        UtilityData.getInstance().setCoordinatorData(message.getMessageSender());
        UtilityData.getInstance().setCoordinator(false);
        UtilityData.getInstance().setElection(false);
        TemporaryElectionBuffer.getInstance().flushMessagesToNewCoordinator();

    }

    private void handleElectionStart(Message message){
        if(!UtilityData.getInstance().isElection()) {
            Election.election();
        }
    }



}
