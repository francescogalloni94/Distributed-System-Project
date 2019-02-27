package NodeUtility;

import beans.EdgeData;

public class Message {

    private String messageType;
    private EdgeData sender;
    private String obj;

    public Message(String obj,String messageType,EdgeData sender){
        this.messageType=messageType;
        this.obj=obj;
        this.sender=sender;


    }


    public String getMessageObject(){
        return this.obj;
    }

    public String getMessageType(){
        return this.messageType;
    }

    public EdgeData getMessageSender(){
        return this.sender;
    }


}
