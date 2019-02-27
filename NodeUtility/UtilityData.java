package NodeUtility;


import beans.EdgeData;
import beans.Node;
import services.Semaphore;

import java.util.ArrayList;

public class UtilityData {

    public static UtilityData instance;
    private boolean coordinator=false;
    private Object coordinatorLock;
    private boolean election = false;
    private Object electionLock;
    private EdgeData coordinatorData=null;
    private Object coordinatorDataLock;
    private EdgeData myData=null;
    private Object myDataLock;
    private Node myNode=null;
    private Object myNodeLock;
    private ArrayList<EdgeData> nodeList;
    private Semaphore listSemaphore;

    private UtilityData(){

        this.listSemaphore = new Semaphore();
        this.coordinatorLock = new Object();
        this.electionLock = new Object();
        this.coordinatorDataLock = new Object();
        this.myDataLock = new Object();
        this.myNodeLock = new Object();
    }

    public static synchronized UtilityData getInstance(){
        if(instance==null)
            instance = new UtilityData();
        return instance;
    }

    public  boolean getCoordinator(){
        synchronized (coordinatorLock) {
            return this.coordinator;
        }
    }

    public  void setCoordinator(boolean coordinator){
        synchronized (coordinatorLock) {
            this.coordinator = coordinator;
        }
    }

    public  EdgeData getCoordinatorData(){
        synchronized (coordinatorDataLock) {
            return this.coordinatorData;
        }
    }

    public  void setCoordinatorData(EdgeData coordinatorData){
        synchronized (coordinatorDataLock) {
            this.coordinatorData = coordinatorData;
        }
    }

    public  EdgeData getMyData(){
        synchronized (myDataLock) {
            return this.myData;
        }
    }

    public  void setMyData(EdgeData myData){
        synchronized (myDataLock) {
            this.myData = myData;
        }
    }

    public  Node getMyNode(){
        synchronized (myNodeLock) {
            return this.myNode;
        }
    }

    public void setMyNode(Node myNode){
        synchronized (myNodeLock) {
            this.myNode = myNode;
        }
    }

    public  void setElection(boolean election) {
        synchronized (electionLock) {
            this.election = election;
        }
    }

    public  boolean isElection() {
        synchronized (electionLock) {
            return election;
        }
    }

    public void setNodeList(ArrayList<EdgeData> list){
        listSemaphore.acquireWriteLock();
        this.nodeList = new ArrayList<>(list);
        listSemaphore.releaseWriteLock();
    }

    public ArrayList<EdgeData> getNodeList(){
        listSemaphore.acquireReadLock();
        ArrayList<EdgeData> copy = new ArrayList<>(this.nodeList);
        listSemaphore.releaseReadLock();
        return copy;
    }

    public ArrayList<EdgeData> getGreaterNodeList(){
        synchronized (myDataLock) {
            listSemaphore.acquireReadLock();
            ArrayList<EdgeData> greaterList = new ArrayList<>();
            for (int i = 0; i < nodeList.size(); i++) {
                if (nodeList.get(i).getId() > myData.getId())
                    greaterList.add(nodeList.get(i));
            }
            listSemaphore.releaseReadLock();
            return greaterList;
        }
    }

    public void addNode(EdgeData node){
        listSemaphore.acquireReadLock();
        boolean addControl = true;
        for(int i=0;i<this.nodeList.size();i++){
            if(this.nodeList.get(i).getId()==node.getId()){
                addControl = false;
                break;
            }
        }
        listSemaphore.releaseReadLock();
        if(addControl) {
            listSemaphore.acquireWriteLock();
            this.nodeList.add(node);
            listSemaphore.releaseWriteLock();
        }
    }

    public void removeNode(EdgeData node){
        listSemaphore.acquireWriteLock();
        for(int i=0;i<this.nodeList.size();i++){
            if(this.nodeList.get(i).getId()==node.getId()){
                this.nodeList.remove(i);
                break;
            }
        }
        listSemaphore.releaseWriteLock();
    }

    public  boolean isGreaterID() {

            int greaterID = 0;
            listSemaphore.acquireReadLock();
            for (int i = 0; i < this.nodeList.size(); i++) {
                if (this.nodeList.get(i).getId() > greaterID)
                    greaterID = this.nodeList.get(i).getId();
            }
            listSemaphore.releaseReadLock();
        synchronized (myDataLock) {
            if (greaterID < this.myData.getId())
                return true;
            else
                return false;
        }
    }
}
