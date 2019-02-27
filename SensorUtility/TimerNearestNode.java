package SensorUtility;

public class TimerNearestNode extends Thread {

    private Sensor sensor;

    public TimerNearestNode(Sensor sensor){
        this.sensor=sensor;

    }

    @Override
    public void run(){
        while(true){
            try {
                Thread.sleep(10000);
                sensor.getNearestNode();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
