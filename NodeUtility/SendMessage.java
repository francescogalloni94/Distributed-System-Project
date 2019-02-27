package NodeUtility;

import beans.EdgeData;
import com.google.gson.Gson;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class SendMessage {

    private static Gson gson = new Gson();

    private SendMessage() {

    }

    public static void sendMessage(Message message, EdgeData nodeToContact) throws IOException {

        Socket socket = new Socket(nodeToContact.getIpAdress(), nodeToContact.getComunicationPort());
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        String jsonData = gson.toJson(message);
        out.writeBytes(jsonData);
        socket.close();


    }

    public static void sendMessageInThread(Message message, EdgeData nodeToContact) {
        SendMessageThread send = new SendMessageThread(message, nodeToContact);
        send.start();

    }


}
