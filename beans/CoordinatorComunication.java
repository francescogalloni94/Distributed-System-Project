package beans;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;

@XmlRootElement
public class CoordinatorComunication {

    private GlobalStats globalStats;
    private HashMap<Integer,ArrayList<LocalStats>> statsMap;

    public CoordinatorComunication(){

    }

    public CoordinatorComunication(GlobalStats globalStats,HashMap<Integer,ArrayList<LocalStats>> statsMap){
        this.globalStats = globalStats;
        this.statsMap = statsMap;
    }

    public GlobalStats getGlobalStats() {
        return this.globalStats;
    }

    public HashMap<Integer, ArrayList<LocalStats>> getStatsMap() {
        return this.statsMap;
    }
}
