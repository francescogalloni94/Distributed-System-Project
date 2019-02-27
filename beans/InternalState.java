package beans;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement
public class InternalState {

    private ArrayList<Node> internalState;

    public InternalState(){

    }

    public InternalState(ArrayList<Node> internalState){
        this.internalState = internalState;
    }

    @Override
    public String toString(){
        String toString = "";
        for (int i=0;i<this.internalState.size();i++){
            toString+="Node ID: "+this.internalState.get(i).getId()+
                    "  Position: x = "+this.internalState.get(i).getX()+"  y = "+this.internalState.get(i).getY()+"\n";
        }

        return toString;

    }


    public ArrayList<Node> getInternalState() {
        return this.internalState;
    }
}
