package SensorUtility;

import beans.EdgeData;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import simulation_src_2018.PM10Simulator;
import java.util.Random;

public class Sensor {

    private PM10Simulator simulator;
    private SensorStreamImpl sensorStream;
    private EdgeData nodeData=null;
    private int x=0;
    private int y=0;
    private String UriServerCloud="";

    public Sensor(String UriServerCloud){
        this.UriServerCloud=UriServerCloud;
        this.sensorStream=new SensorStreamImpl(this.nodeData,this);
        positionGenerator();
        getNearestNode(); simulator=new PM10Simulator(this.sensorStream);
        simulator.start();
        TimerNearestNode timer=new TimerNearestNode(this);
        timer.start();
    }

    private void positionGenerator(){
        Random random=new Random();
        int x = 1 + random.nextInt(100 - 1 + 1);
        int y = 1 + random.nextInt(100 - 1 + 1);
        this.x=x;
        this.y=y;

    }

    public synchronized void getNearestNode(){
        ClientConfig config = new DefaultClientConfig();
        config.getClasses().add(JacksonJsonProvider.class);
        Client client = Client.create(config);
        WebResource webResource=client.resource(UriServerCloud+"/sensorservice/getnearestnode");
        ClientResponse response = webResource.queryParam("x",""+this.x).queryParam("y",""+this.y).type("application/json").
                get(ClientResponse.class);
        if(response.getStatus()!=404) {
            this.nodeData = response.getEntity(new GenericType<EdgeData>() {
            });
        }else{
            this.nodeData=null;
        }

        this.sensorStream.setEdgeData(this.nodeData);

    }

    public synchronized void setNodeData(EdgeData nodeData){
        this.nodeData=nodeData;
    }

    public synchronized EdgeData getNodeData() {
        return this.nodeData;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}
