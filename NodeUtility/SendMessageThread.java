package NodeUtility;

import beans.EdgeData;
import com.google.gson.Gson;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;


public class SendMessageThread extends Thread {

    private Message message;
    private EdgeData nodeToContact;
    private Gson gson;

    public SendMessageThread(Message message, EdgeData nodeToContact){
        this.message=message;
        this.nodeToContact=nodeToContact;
        this.gson=new Gson();

    }

    @Override
    public void run(){
        try {
            Socket socket=new Socket(nodeToContact.getIpAdress(),nodeToContact.getComunicationPort());
            DataOutputStream out=new DataOutputStream(socket.getOutputStream());
            String jsonData=gson.toJson(this.message);
            out.writeBytes(jsonData);
            socket.close();

        } catch (IOException e) {
           UtilityData.getInstance().removeNode(nodeToContact);
        }
    }
}
