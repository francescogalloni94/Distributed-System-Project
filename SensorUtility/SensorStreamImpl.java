package SensorUtility;

import NodeUtility.Message;
import NodeUtility.SendMessage;
import beans.EdgeData;
import com.google.gson.Gson;
import simulation_src_2018.Measurement;
import simulation_src_2018.SensorStream;
import java.io.IOException;



public class SensorStreamImpl implements SensorStream {

    EdgeData nodeData;
    Sensor sensor;


    public SensorStreamImpl(EdgeData nodeData,Sensor sensor){
        this.nodeData=nodeData;
        this.sensor=sensor;
    }

    @Override
    public synchronized void sendMeasurement(Measurement m) {
        if(nodeData!=null){
            Gson gson=new Gson();
            String jsonMeasurement=gson.toJson(m,Measurement.class);
            Message message=new Message(jsonMeasurement,"sensorMeasurement",null);
            try {
                SendMessage.sendMessage(message,this.nodeData);
                System.out.println("Message sent to: "+this.nodeData.getId());
            } catch (IOException e) {
                this.sensor.getNearestNode();
                sendMeasurement(m);
                System.out.println("Catched exception and resend to new node");
            }

        }else{
            System.out.println("message delete");
        }
    }

    public synchronized void setEdgeData(EdgeData nodeData){
        this.nodeData=nodeData;
    }
}
