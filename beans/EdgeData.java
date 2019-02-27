package beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class EdgeData {

    private int id=0;
    private int comunicationPort=0;
    private String ipAdress="";

    public EdgeData(){

    }

    public void setId(int id){
        this.id=id;
    }

    public int getId(){
        return this.id;
    }

    public void setComunicationPort(int comunicationPort){
        this.comunicationPort=comunicationPort;
    }

    public int getComunicationPort(){
        return this.comunicationPort;
    }

    public void setIpAdress(String ipAdress){
        this.ipAdress=ipAdress;
    }

    public String getIpAdress(){
        return this.ipAdress;
    }


}
