package services;

public class Semaphore extends Thread {


    private int writing=1;
    private int reading=1;
    private int numLettori=0;

    public Semaphore(){

    }

    public synchronized void acquireReadLock(){
        while(writing==0){
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        if(reading==1) {
            reading--;
        }

        numLettori++;

    }

    public synchronized void releaseReadLock(){
        numLettori--;
        if(numLettori==0){
            reading++;
        }
        this.notify();


    }

    public synchronized void acquireWriteLock(){
        while(reading==0 || writing==0){
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        writing--;

    }

    public synchronized void releaseWriteLock(){
        writing++;
        this.notify();

    }
}
