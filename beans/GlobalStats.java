package beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GlobalStats {

    private double value=0.0;
    private long timestamp=0;

    public GlobalStats(){

    }

    public GlobalStats(double value,long timestamp){
        this.value=value;
        this.timestamp=timestamp;
    }

    public double getValue() {
        return this.value;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String toString(){
        return timestamp+"  value: "+value;
    }
}
