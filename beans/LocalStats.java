package beans;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class LocalStats {

    double value=0.0;
    long timestamp=0;

    public LocalStats(){

    }

    public LocalStats(double mean,long timestamp){
        this.value=mean;
        this.timestamp=timestamp;
    }

    public double getValue() {
        return this.value;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setMean(double mean) {
        this.value = mean;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString(){
        return timestamp+"  value: "+value;
    }
}
