package beans;


import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Node {

    private int id=0;
    private String ipAdress="";
    private int sensorPort=0;
    private int edgePort=0;
    private int x=0;
    private int y=0;


    public Node(){

    }

    public void setId(int id){
        this.id=id;
    }

    public int getId(){
        return this.id;
    }

    public void setIpAdress(String ipAdress){
        this.ipAdress=ipAdress;
    }

    public String getIpAdress(){
        return this.ipAdress;
    }

    public void setSensorPort(int sensorPort){
        this.sensorPort=sensorPort;
    }

    public int getSensorPort(){
        return this.sensorPort;
    }

    public void setEdgePort(int edgePort){
        this.edgePort=edgePort;
    }

    public int getEdgePort(){
        return this.edgePort;
    }

    public void setX(int x){
        this.x=x;
    }

    public int getX(){
        return this.x;
    }

    public void setY(int y){
        this.y=y;
    }

    public int getY(){
        return this.y;
    }


}
