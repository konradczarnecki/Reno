package konra.reno.cli;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Cli {

    private static Map<String, String> config = new HashMap<>();

    static {

        // different host than in P2P service; api host for rest
        config.put("apihost", "http://localhost");
        config.put("port", "8031");
    }

    public static void main(String[] args) throws Exception{

        Scanner scan = new Scanner(System.in);

        String input = "";

        while(!input.equals("quit")){

            input = scan.nextLine();

            String[] inputArr = input.split(" ");
            Map<String, String> params;
            String response;

            switch (inputArr[0]){

                case "bootstrap":

                    params = new HashMap<>();
                    if(inputArr.length > 1) params.put("host", inputArr[1]);

                    response = getRequest("/p2p/bootstrap", params);
                    System.out.println(response);
                    break;

                case "cli-set":

                    if(inputArr.length == 3){

                        config.put(inputArr[1], inputArr[2]);

                    } else System.out.println("error");

                    break;

                case "blockchain":

                    params = new HashMap<>();

                    if(inputArr.length >= 2 && inputArr[1].equals("sync")){

                        if(inputArr.length == 3 && inputArr[2].equals("--debug"))
                            params.put("debug", "yes");

                        response = getRequest("/blockchain-sync", params);
                        System.out.println(response);

                    } else if(inputArr.length == 2 && inputArr[1].equals("start")){

                        response = getRequest("/blockchain-start", params);
                        System.out.println(response);

                    }

                    break;

                case "mine":

                    if(inputArr.length < 2) break;

                    params = new HashMap<>();

                    if(inputArr[1].equals("start")){
                        response = getRequest("/mine", params);
                        System.out.println(response);

                    } else if (inputArr[1].equals("stop")){
                        response = getRequest("/stop-mining", params);
                        System.out.println(response);
                    }

                    break;

                case "login":

                    params = new HashMap<>();

                    if(inputArr.length == 1){
                        response = getRequest("/p2p/login-com", params);
                        System.out.println(response);
                    }

                case "crypto":

                    if(inputArr.length < 2) break;

                    if(inputArr[1].equals("hashHex")){

                        String result = getRequest("/crypto-hashHex", new HashMap<>());
                        System.out.println(result);
                    }
            }
        }
    }

    public static String getRequest(String urlString, Map<String, String > params) throws Exception {

        String fullHost = config.get("apihost") + ":" + config.get("port");
        URL url = new URL(fullHost + urlString + "?" + ParameterStringBuilder.getParamsString(params));
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

//
//        con.setDoOutput(true);
//        DataOutputStream out = new DataOutputStream(con.getOutputStream());
//        out.writeBytes(ParameterStringBuilder.getParamsString(params));
//        out.flush();
//        out.close();

        int status = con.getResponseCode();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        if(content.toString().equals("")) return status == 200 ? "success" : "failure";
        else return content.toString();
    }

    public static class ParameterStringBuilder {

        public static String getParamsString(Map<String, String> params) {

            StringBuilder result = new StringBuilder();

            for (Map.Entry<String, String> entry : params.entrySet()) {
                try {
                    result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                result.append("=");
                try {
                    result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                result.append("&");
            }

            String resultString = result.toString();
            return resultString.length() > 0
                    ? resultString.substring(0, resultString.length() - 1)
                    : resultString;
        }
    }
}
