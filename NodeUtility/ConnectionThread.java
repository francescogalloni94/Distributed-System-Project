package NodeUtility;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ConnectionThread extends Thread {

    private ServerSocket serverSocket;
    private String connectionType="";

    public ConnectionThread(ServerSocket serverSocket,String connectionType){
        this.serverSocket=serverSocket;
        this.connectionType=connectionType;
    }

    @Override
    public void run(){
        while(true){
            try {
                Socket connection = this.serverSocket.accept();
                MessageManagementThread managementThread = new MessageManagementThread(connection);
                managementThread.start();
             } catch (SocketException e){

             }catch(IOException e){
                System.out.println("IOException");
            }

        }
    }





}
