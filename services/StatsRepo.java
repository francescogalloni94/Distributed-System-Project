package services;

import beans.GlobalStats;
import beans.LocalStats;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StatsRepo {

    private static StatsRepo instance;
    private ArrayList<GlobalStats> globalStats;
    private Semaphore globalStatsSemaphore;
    private HashMap<Integer, ArrayList<LocalStats>> localMap;
    private Semaphore localMapSemaphore;

    private StatsRepo(){
        this.globalStats = new ArrayList<>();
        this.globalStatsSemaphore = new Semaphore();
        this.localMap = new HashMap<>();
        this.localMapSemaphore = new Semaphore();

    }

    public static synchronized StatsRepo getInstance(){
        if (instance==null)
            instance = new StatsRepo();
        return instance;
    }

    public void putGlobalStat(GlobalStats globalStats){
        globalStatsSemaphore.acquireWriteLock();
        this.globalStats.add(globalStats);
        globalStatsSemaphore.releaseWriteLock();
    }

    public void putLocalStats(HashMap<Integer,ArrayList<LocalStats>> localStats){
        localMapSemaphore.acquireWriteLock();
        for(Map.Entry<Integer,ArrayList<LocalStats>> entry : localStats.entrySet()){
            Integer key = entry.getKey();
            ArrayList<LocalStats> list = entry.getValue();
            ArrayList<LocalStats> repoList = this.localMap.get(key);
            if(repoList==null){
                this.localMap.put(key,list);
            }else{
                repoList.addAll(list);
                this.localMap.put(key,repoList);
            }

        }
        localMapSemaphore.releaseWriteLock();
    }

    public ArrayList<LocalStats> getNodeStats(int nodeID,int n){
        localMapSemaphore.acquireReadLock();
        ArrayList<LocalStats> subList=getLocalSubList(new ArrayList<>(this.localMap.get(nodeID)),n);
        localMapSemaphore.releaseReadLock();


        return subList;


    }

    public ArrayList<GlobalStats> getGlobalStats(int n){

        globalStatsSemaphore.acquireReadLock();
        ArrayList<GlobalStats> subList=getGlobalSubList(new ArrayList<>(this.globalStats),n);
        globalStatsSemaphore.releaseReadLock();

        return subList;
    }

    public Double getLocalSTD(int nodeID,int n){

        localMapSemaphore.acquireReadLock();
        ArrayList<LocalStats> subList = getLocalSubList(new ArrayList<>(this.localMap.get(nodeID)),n);
        localMapSemaphore.releaseReadLock();
        if(subList!=null)
            return Math.sqrt(getVariance(subList));
        else
            return null;
    }

    public Double getLocalMean(int nodeID,int n){

        localMapSemaphore.acquireReadLock();
        ArrayList<LocalStats> subList = getLocalSubList(new ArrayList<>(this.localMap.get(nodeID)),n);
        localMapSemaphore.releaseReadLock();
        if(subList!=null)
            return getMean(subList);
        else
            return null;
    }

    public Double getGlobalSTD(int n){
        globalStatsSemaphore.acquireReadLock();
        ArrayList<GlobalStats> subList=getGlobalSubList(new ArrayList<>(this.globalStats),n);
        globalStatsSemaphore.releaseReadLock();

        if(subList!=null)
            return Math.sqrt((getVariance(subList)));
        else
            return null;
    }

    public Double getGlobalMean(int n){
        globalStatsSemaphore.acquireReadLock();
        ArrayList<GlobalStats> subList=getGlobalSubList(new ArrayList<>(this.globalStats),n);
        globalStatsSemaphore.releaseReadLock();

        if(subList!=null)
            return getMean(subList);
        else
            return null;

    }

    private double getVariance(ArrayList list){
        Method m = null;
        double mean = getMean(list);
        if(list.get(0).getClass().getName().equals("LocalStats"))
            list = (ArrayList<LocalStats>) list;
        else if(list.get(0).getClass().getName().equals("GlobalStats"))
            list = (ArrayList<GlobalStats>) list;
        try {
            m = list.get(0).getClass().getMethod("getValue");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        double total = 0.0;
        for (int i=0;i<list.size();i++){
            double value = 0.0;
            try {
                value = (double) m.invoke(list.get(i));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            total +=(value-mean)*(value-mean);
        }
        return total/(list.size()-1);

    }

    private double getMean(ArrayList list){
        Method m = null;
        if(list.get(0).getClass().getName().equals("LocalStats"))
            list = (ArrayList<LocalStats>) list;

        else if(list.get(0).getClass().getName().equals("GlobalStats"))
            list = (ArrayList<GlobalStats>) list;
        try {
            m = list.get(0).getClass().getMethod("getValue");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        double total = 0.0;
        for(int i=0;i<list.size();i++){
            try {
                total+=(double) m.invoke(list.get(i));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

        }
        return total/(list.size()-1);
    }

    private ArrayList<LocalStats> getLocalSubList(ArrayList<LocalStats> list,int n){
        ArrayList<LocalStats> subList;
        if(list!=null) {
            try {
                subList = new ArrayList<>(list.subList(list.size() - n, list.size()));
                return subList;
            }catch (IndexOutOfBoundsException e){
                subList = new ArrayList<>(list);
                return subList;
            }
        }else
            return null;
    }

    private ArrayList<GlobalStats> getGlobalSubList(ArrayList<GlobalStats> list,int n){
        ArrayList<GlobalStats> subList;
        if(list.size()!=0) {
            try {
                subList = new ArrayList<>(this.globalStats.subList(this.globalStats.size() - n, this.globalStats.size()));
                return subList;
            } catch (IndexOutOfBoundsException e) {
                subList = new ArrayList<>(this.globalStats);
                return subList;
            }
        }else
            return null;
    }



}
