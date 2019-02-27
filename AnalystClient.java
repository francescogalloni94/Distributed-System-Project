import beans.GlobalStats;
import beans.InternalState;
import beans.LocalStats;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class AnalystClient {

    public static void caseManagement(int selectedCase,BufferedReader in){

        final String URIServerCloud="http://localhost:1337";
        ClientConfig config = new DefaultClientConfig();
        config.getClasses().add(JacksonJsonProvider.class);
        Client client = Client.create(config);

        switch(selectedCase){


            case 1:{
                WebResource webResource=client.resource(URIServerCloud+"/analystservice/citystate");
                ClientResponse response = webResource.type("application/json").
                        get(ClientResponse.class);
                if(response.getStatus()==404)
                    System.out.println("No nodes connected\n\n\n");
                else if(response.getStatus()==200) {
                    InternalState internalState = response.getEntity(new GenericType<InternalState>(){});
                    System.out.println(internalState.toString());
                    System.out.println("\n\n\n");
                }

                break;
            }


            case 2:{
                System.out.println("insert a node ID:");
                int nodeID = readNumber(in,2);

                System.out.println("insert number of stats:");
                int numberOfStats = readNumber(in,2);

                WebResource webResource=client.resource(URIServerCloud+"/analystservice/nodestats");
                ClientResponse response = webResource.queryParam("nodeid",""+nodeID).queryParam("n",""+numberOfStats).type("application/json").
                        get(ClientResponse.class);
                if(response.getStatus()==200){
                    ArrayList<LocalStats> localStats = response.getEntity(new GenericType<ArrayList<LocalStats>>(){});
                    for (int i=0;i<localStats.size();i++){
                        System.out.println(localStats.get(i).toString());
                    }
                }else if(response.getStatus()==404)
                    System.out.println("No stats for this node");

                System.out.println("\n\n\n");

                break;
            }


            case 3:{

                System.out.println("insert number of stats:");
                int numberOfStats = readNumber(in,3);

                WebResource webResource=client.resource(URIServerCloud+"/analystservice/globalstats");
                ClientResponse response = webResource.queryParam("n",""+numberOfStats).type("application/json").
                        get(ClientResponse.class);
                if(response.getStatus()==200){
                    ArrayList<GlobalStats> globalStats = response.getEntity(new GenericType<ArrayList<GlobalStats>>(){});
                    for (int i=0;i<globalStats.size();i++){
                        System.out.println(globalStats.get(i).toString());
                    }

                }else if(response.getStatus()==404){
                    System.out.println("No global stats");
                }
                System.out.println("\n\n\n");

                break;
            }


            case 4:{

                System.out.println("insert a node ID:");
                int nodeID = readNumber(in,4);

                System.out.println("insert number of stats to consider in std:");
                int numberOfStats = readNumber(in,4);

                WebResource webResource=client.resource(URIServerCloud+"/analystservice/nodestd");
                ClientResponse response = webResource.queryParam("nodeid",""+nodeID).queryParam("n",""+numberOfStats).type("application/json").
                        get(ClientResponse.class);

                if(response.getStatus()==200){
                    double std = response.getEntity(new GenericType<Double>(){});
                    System.out.println("Local Standard deviation is : "+std);
                }else if(response.getStatus()==404){
                    System.out.println("No data to compute local standar deviation");
                }
                System.out.println("\n\n\n");
                break;

            }
            case 5:{

                System.out.println("insert a node ID:");
                int nodeID = readNumber(in,5);

                System.out.println("insert number of stats to consider in mean:");
                int numberOfStats = readNumber(in,5);

                WebResource webResource=client.resource(URIServerCloud+"/analystservice/nodemean");
                ClientResponse response = webResource.queryParam("nodeid",""+nodeID).queryParam("n",""+numberOfStats).type("application/json").
                        get(ClientResponse.class);
                if(response.getStatus()==200){
                    double mean = response.getEntity(new GenericType<Double>(){});
                    System.out.println("Local Mean is : "+mean);
                }else if(response.getStatus()==404){
                    System.out.println("No data to compute local mean");
                }
                System.out.println("\n\n\n");

                break;
            }
            case 6:{

                System.out.println("insert number of stats to consider in std:");
                int numberOfStats = readNumber(in,6);

                WebResource webResource=client.resource(URIServerCloud+"/analystservice/globalstd");
                ClientResponse response = webResource.queryParam("n",""+numberOfStats).type("application/json").
                        get(ClientResponse.class);

                if(response.getStatus()==200){
                    double std = response.getEntity(new GenericType<Double>(){});
                    System.out.println("Global Standard deviation is : "+std);
                }else if(response.getStatus()==404){
                    System.out.println("No data to compute global standar deviation");
                }
                System.out.println("\n\n\n");

                break;
            }
            case 7:{

                System.out.println("insert number of stats to consider in mean:");
                int numberOfStats = readNumber(in,7);

                WebResource webResource=client.resource(URIServerCloud+"/analystservice/globalmean");
                ClientResponse response = webResource.queryParam("n",""+numberOfStats).type("application/json").
                        get(ClientResponse.class);
                if(response.getStatus()==200){
                    double mean = response.getEntity(new GenericType<Double>(){});
                    System.out.println("Global Mean is: "+mean);
                }else if(response.getStatus()==404){
                    System.out.println("No data to compute global mean");
                }
                System.out.println("\n\n\n");

                break;
            }
            case 8:{
                System.exit(0);
                break;
            }
            default:{
                System.out.println("Select a valid option!!\n\n");


            }

        }

    }

    public static int readNumber(BufferedReader in,int selectedCase){
        int number = 0;
        try {
            number =Integer.parseInt(in.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }catch (NumberFormatException e) {
            System.out.println("insert a valid number!!");
            caseManagement(selectedCase, in);
        }

        return number;
    }

    public static void main(String[] args){

        BufferedReader inFromUser= new BufferedReader(new InputStreamReader(System.in));

        while(true) {

            System.out.println("Analyst Client prompt");
            System.out.println("Insert desired action's number:");
            System.out.println("1 City state");
            System.out.println("2 Last N stats of desired node");
            System.out.println("3 Last N global city stats");
            System.out.println("4 Standard deviation of last N stats of desired node");
            System.out.println("5 Mean of last N stats of desired node");
            System.out.println("6 Standard deviation of last N global city stats");
            System.out.println("7 Mean of last N global city stats");
            System.out.println("8 Exit");

            try {
                String selection=inFromUser.readLine();
                try {
                    caseManagement(Integer.parseInt(selection),inFromUser);
                }catch (NumberFormatException ex){
                    caseManagement(0,inFromUser);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }




    }
}
