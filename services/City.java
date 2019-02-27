package services;

import beans.EdgeData;
import beans.InternalState;
import beans.Node;
import java.util.ArrayList;


public class City {

    private static City instance;
    private Semaphore semaphoreNode;
    private Semaphore semaphoreData;
    private ArrayList<Node> nodes;
    private ArrayList<EdgeData> dataList;


    private City(){
        nodes=new ArrayList<>();
        dataList=new ArrayList<>();
        semaphoreNode=new Semaphore();
        semaphoreData=new Semaphore();
    }

    public static synchronized City getInstance(){
        if(instance==null)
            instance = new City();
        return instance;
    }

    public ArrayList<EdgeData> addNode(Node node) throws PositionException, IDException {

        if((node.getX()<1)||(node.getX()>100)||(node.getY()<1)||(node.getY()>100)){
            throw new PositionException();

        }
           semaphoreNode.acquireReadLock();

            for (int i = 0; i < this.nodes.size(); i++) {
                if (node.getId() == this.nodes.get(i).getId()) {
                    semaphoreNode.releaseReadLock();
                    throw new IDException();

                }

                int distance = (Math.abs(node.getX() - this.nodes.get(i).getX())) + (Math.abs(node.getY() - this.nodes.get(i).getY()));
                if (distance < 20) {
                    semaphoreNode.releaseReadLock();
                    throw new PositionException();
                }
            }
           semaphoreNode.releaseReadLock();


            semaphoreNode.acquireWriteLock();
            this.nodes.add(node);
            semaphoreNode.releaseWriteLock();

            EdgeData data=new EdgeData();
            data.setId(node.getId());
            data.setComunicationPort(node.getEdgePort());
            data.setIpAdress(node.getIpAdress());

            semaphoreData.acquireWriteLock();

            this.dataList.add(data);
            ArrayList<EdgeData> dataCopy= new ArrayList<>(this.dataList);

            semaphoreData.releaseWriteLock();

            return dataCopy;





    }

    public void removeNode(Node node){

        Node nodeToRemove=null;
        EdgeData dataToRemove=null;

        semaphoreData.acquireReadLock();
        for(int i=0;i<this.dataList.size();i++){
            if(this.dataList.get(i).getId()==node.getId()){
                dataToRemove=this.dataList.get(i);
            }
        }
        semaphoreData.releaseReadLock();

        semaphoreNode.acquireReadLock();
        for (int i=0;i<this.nodes.size();i++){
            if(this.nodes.get(i).getId()==node.getId()){
                nodeToRemove=this.nodes.get(i);
            }
        }
        semaphoreNode.releaseReadLock();

        semaphoreData.acquireWriteLock();
        this.dataList.remove(dataToRemove);
        semaphoreData.releaseWriteLock();

        semaphoreNode.acquireWriteLock();
        this.nodes.remove(nodeToRemove);
        semaphoreNode.releaseWriteLock();




    }


    public EdgeData getNearestNode(int x,int y){
        Node nearestNode=null;
        int distance=1000000000;
        EdgeData nearestData=null;

        semaphoreNode.acquireReadLock();
        for (int i=0;i<this.nodes.size();i++){
            int distanceTemp = (Math.abs(this.nodes.get(i).getX() - x)) + (Math.abs(this.nodes.get(i).getY() - y));
            if(distanceTemp<distance){
                distance=distanceTemp;
                nearestNode=this.nodes.get(i);
            }
        }
        semaphoreNode.releaseReadLock();


        if(nearestNode!=null){
            nearestData=new EdgeData();
            nearestData.setId(nearestNode.getId());
            nearestData.setIpAdress((nearestNode.getIpAdress()));
            nearestData.setComunicationPort(nearestNode.getSensorPort());
        }

        return nearestData;

    }

    public InternalState getInternalState(){
        semaphoreNode.acquireReadLock();
        InternalState internalState = new InternalState(new ArrayList<>(this.nodes));
        semaphoreNode.releaseReadLock();

        return internalState;
    }


}
