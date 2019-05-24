package ro.pub.cs.systems.eim.practical3;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;

public class CommunicationThread extends Thread {
    private Socket socket;
    private ServerThread serverThread;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }
        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type!");
            String set = bufferedReader.readLine();
            serverThread.setSet(set);
            String hour = bufferedReader.readLine();
//            serverThread.setHour(hour);
            String minute = bufferedReader.readLine();
//            serverThread.setMinute(minute);
            /*
            if (city == null || city.isEmpty() || informationType == null || informationType.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type!");
                return;
            }
            */
//            HashMap<String, ServerData> data = serverThread.getData();
//            ServerData weatherForecastInformation = null;
//            if (data.containsKey(city)) {
//                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
//                weatherForecastInformation = data.get(city);
//            } else {

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the webservice...");
            /*
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("https://autocomplete.wunderground.com/aq");
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair(Constants.QUERY_ATTRIBUTE, city));
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            httpPost.setEntity(urlEncodedFormEntity);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String pageSourceCode = httpClient.execute(httpPost, responseHandler);
            if (pageSourceCode == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                return;
            }
            final String output = pageSourceCode;
            */
            String result= set + " " + hour + " " + minute;

            String pageSourceCode="";
            switch (set) {
                case "set":
                    serverThread.setHour(hour);
                    serverThread.setMinute(minute);
                    result = "hour: " + serverThread.getHour() + "  minute: " +serverThread.getMinute();
                    break;
                case "poll":
                    result = "hour: " + serverThread.getHour() + "  minute: " +serverThread.getMinute();
                    if(serverThread.getHour().equals("0") && serverThread.getMinute().equals("0"))
                        result +=" (None)";
                    result += "\n" + serverThread.getTime();
                    break;
                case "reset":
                    serverThread.setHour("0");
                    serverThread.setMinute("0");
                    result = "None (Reset)";
                    break;
            }



            /*
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet("localhost");
//                HttpResponse httpGetResponse = httpClient.execute(httpGet);
//                HttpEntity httpGetEntity = httpGetResponse.getEntity();
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                pageSourceCode = httpClient.execute(httpGet, responseHandler);
//                if (httpGetEntity != null) {
                result = pageSourceCode;
//                    Log.i(Constants.TAG, EntityUtils.toString(httpGetEntity));
//                }
            } catch (Exception exception) {
                Log.e(Constants.TAG, exception.getMessage());
                if (Constants.DEBUG) {
                    exception.printStackTrace();
                }
            }
            */



            /*Log.d("WOW","CLIENT" + output);
            JSONObject jsonObj = new JSONObject(pageSourceCode);
            JSONArray results = jsonObj.getJSONArray("RESULTS");
            String country = results.getJSONObject(0).getString("name");
            String timeZone = results.getJSONObject(0).getString("tz");
            */
//
//  weatherForecastInformation = new ServerData(country, timeZone);
//            serverThread.setData(city, weatherForecastInformation);

//            }
//            if (weatherForecastInformation == null) {
//                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Weather Forecast Information is null!");
//                return;
//            }
            /*
            switch(informationType) {
                case "All":
                    result = weatherForecastInformation.toString();
                    break;
                case "country":
                    result = weatherForecastInformation.getCountry();
                    Log.i(Constants.TAG, "[COMMUNICATION THREAD] results" + result);
                    break;
                case "tz":
                    result = weatherForecastInformation.getTimeZone();
                    break;
                default:
                    result = "[COMMUNICATION THREAD] Wrong information type (all / temperature / wind_speed / condition / humidity / pressure)!";
            }
            */

            printWriter.println(result);
            printWriter.flush();
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

}
