import SensorUtility.Sensor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class SensorSimulator {

    public static void main(String[] args){
        final String URIServerCloud="http://localhost:1337";
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Insert number of sensor: ");
        int sensorNumber = 0;
        boolean control = true;
        while(control) {
            try {
                String number = in.readLine();
                sensorNumber = Integer.parseInt(number);
                control = false;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                System.out.println("Insert a valid number!");
            }
        }

        for(int i=0;i<sensorNumber;i++) {
            Sensor sensor = new Sensor(URIServerCloud);
        }



    }
}
