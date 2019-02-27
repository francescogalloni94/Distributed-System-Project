import NodeUtility.*;
import beans.EdgeData;
import beans.Node;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Random;


public class EdgeNode {
    public static void main(String [] args){

        final String URIServerCloud="http://localhost:1337";
        final String ipAdress="localhost";
        ArrayList<EdgeData> nodesData=null;
        int nodeSocket=0;
        int sensorSocket=0;
        UtilityData utility=UtilityData.getInstance();
        Node testNode=null;

        ServerSocket nodeServerSocket= null;
        ServerSocket sensorServerSocket=null;
        ConnectionThread sensorConnection=null;
        ConnectionThread nodeConnection=null;


        try {
            nodeServerSocket = new ServerSocket(0);
            sensorServerSocket=new ServerSocket(0);
        } catch (IOException e) {
            System.out.println("error while creating node and sensor server sockets");
        }
        nodeSocket=nodeServerSocket.getLocalPort();
        sensorSocket=sensorServerSocket.getLocalPort();


        ClientConfig config = new DefaultClientConfig();
        config.getClasses().add(JacksonJsonProvider.class);
        Client client = Client.create(config);
        WebResource webResource=client.resource(URIServerCloud+"/edgenodeservice/postnode");
        ClientResponse response=null;

        int attempsCounter=0;
        int status=409;

        while(status!=201 && attempsCounter<9){
            testNode = new Node();
            testNode.setEdgePort(nodeSocket);
            testNode.setIpAdress(ipAdress);
            testNode.setSensorPort(sensorSocket);
            Random random = new Random();
            int id=random.nextInt(1000000000);
            testNode.setId(id);
            int x = 1 + random.nextInt(100 - 1 + 1);
            int y = 1 + random.nextInt(100 - 1 + 1);
            testNode.setX(x);
            testNode.setY(y);

            response = webResource.type("application/json")
                    .post(ClientResponse.class, testNode);
            attempsCounter++;
            status=response.getStatus();

        }

        if(response.getStatus()==201) {
            utility.setMyNode(testNode);
            EdgeData myData=new EdgeData();
            myData.setId(utility.getMyNode().getId());
            myData.setComunicationPort(utility.getMyNode().getEdgePort());
            myData.setIpAdress(utility.getMyNode().getIpAdress());
            utility.setMyData(myData);
            nodesData = response.getEntity(new GenericType<ArrayList<EdgeData>>() {
            });
            sensorConnection=new ConnectionThread(sensorServerSocket,"sensor");
            nodeConnection=new ConnectionThread(nodeServerSocket,"node");
            sensorConnection.start();
            nodeConnection.start();
            UtilityData.getInstance().setNodeList(nodesData);
            UtilityData.getInstance().removeNode(myData);
            if(nodesData.size()>1){
                CoordinatorResponseTimeOut timeOut = new CoordinatorResponseTimeOut();
                for (int i=0;i<nodesData.size();i++){
                    if(nodesData.get(i).getId()!=utility.getMyNode().getId()){
                        Message message=new Message(null,"Presentation",myData);
                        SendMessage.sendMessageInThread(message,nodesData.get(i));

                    }
                }
                timeOut.start();

            }else {
                utility.setCoordinator(true);
                utility.setCoordinatorData(utility.getMyData());
            }

            System.out.println("ID: "+utility.getMyData().getId());


            System.out.println("Press return to exit");
            BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
            try {
                in.readLine();
                WebResource webResourceDelete=client.resource(URIServerCloud+"/edgenodeservice/deletenode");
                ClientResponse deleteResponse=webResourceDelete.type("application/json")
                        .delete(ClientResponse.class,utility.getMyNode());
                nodeServerSocket.close();
                sensorServerSocket.close();
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else{
            System.out.println("10 attemps of insert failed");
        }



    }
}
