package NodeUtility;


import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

public class TemporaryElectionBuffer {

    private static TemporaryElectionBuffer instance;
    private ArrayList<Message> buffer;
    private Gson gson;

    private TemporaryElectionBuffer(){
        this.buffer=new ArrayList<>();
        this.gson = new Gson();

    }

    public static synchronized TemporaryElectionBuffer getInstance(){
        if(instance==null)
            instance = new TemporaryElectionBuffer();
        return instance;
    }

    public synchronized void putMessage(Message message){

        this.buffer.add(message);


    }

    public synchronized void flushMessagesToNewCoordinator(){

        boolean failure = false;

        for(int i=0;i<this.buffer.size();i++){
            try {
                SendMessage.sendMessage(this.buffer.get(i),UtilityData.getInstance().getCoordinatorData());
            } catch (IOException e) {
                failure = true;
                break;
            }
        }
        if(!failure)
            this.buffer.clear();

    }



}
