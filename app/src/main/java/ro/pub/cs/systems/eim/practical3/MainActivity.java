package ro.pub.cs.systems.eim.practical3;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    private EditText serverPort;
    private EditText clientPort;
    private EditText clientAddress;
    private EditText hour;
    private EditText minute;
    private EditText set;
    private Button startServer;
    private Button connect;
    private TextView result;
    private ServerThread serverThread;
    private ClientThread clientThread;

    private Button getInformationButton;

    private String time;

    private ConnectButtonClickListener buttonClickListener = new ConnectButtonClickListener();
    private class ConnectButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            Log.d(Constants.TAG, serverPort.getText().toString());
            String serverPortString = serverPort.getText().toString();
            if (serverPortString == null || serverPortString.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                serverThread = new ServerThread(Integer.parseInt(serverPortString));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }
    }

    private ClientButtonClickListener connectClickListener = new ClientButtonClickListener();
    private class ClientButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String clientAddressString = clientAddress.getText().toString();
            String clientPortString = clientPort.getText().toString();
            if (clientAddressString == null || clientAddressString.isEmpty()
                    || clientPortString == null || clientPortString.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }
            String setString = set.getText().toString();
            String hourString = hour.getText().toString();
            String minuteString = hour.getText().toString();
//            String informationType = spinner.getSelectedItem().toString();
//            if (ho == null || city.isEmpty()
//                    || informationType == null || informationType.isEmpty()) {
//                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (city / information type) should be filled", Toast.LENGTH_SHORT).show();
//                return;
//            }

            result.setText(Constants.EMPTY_STRING);

            clientThread = new ClientThread(
                    clientAddressString, Integer.parseInt(clientPortString), setString,hourString, minuteString, result
            );
            clientThread.start();
        }

    }



    private class NISTCommunicationAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String dayTimeProtocol = null;
            try {
                Socket socket = new Socket(Constants.NIST_SERVER_HOST, Constants.NIST_SERVER_PORT);
                BufferedReader bufferedReader = Utilities.getReader(socket);
                bufferedReader.readLine();
                dayTimeProtocol = bufferedReader.readLine();
                time = dayTimeProtocol;
                String []data = dayTimeProtocol.split(":");
                Log.d(Constants.TAG, "The server returned: " + dayTimeProtocol);
                for(String s : data) {
                    Log.d(Constants.TAG, "The server returned: " + s);
                }
                String setString = set.getText().toString();
                String hourString = hour.getText().toString();
                String minuteString = hour.getText().toString();
                serverThread.setTime(dayTimeProtocol);
                switch (setString) {
                    case "set":
                        Log.d(Constants.TAG, "Set option picked.");
                        break;
                    case "poll":
                        Log.d(Constants.TAG, "Poll option picked.");
                        dayTimeProtocol += "\nNone Hour: "  + serverThread.getHour() + " Minute:" + serverThread.getMinute();
                        break;
                    default:
                        break;
                }
            } catch (UnknownHostException unknownHostException) {
                Log.d(Constants.TAG, unknownHostException.getMessage());
                if (Constants.DEBUG) {
                    unknownHostException.printStackTrace();
                }
            } catch (IOException ioException) {
                Log.d(Constants.TAG, ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
            }
            return dayTimeProtocol;
        }

        @Override
        protected void onPostExecute(String res) {
            result.setText(res);
        }
    }

    private TimeButtonClickListener timebuttonClickListener = new TimeButtonClickListener();
    private class TimeButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            NISTCommunicationAsyncTask nistCommunicationAsyncTask = new NISTCommunicationAsyncTask();
            nistCommunicationAsyncTask.execute();
        }

    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serverPort = findViewById(R.id.server_port);
        clientPort = findViewById(R.id.client_port);
        clientAddress = findViewById(R.id.address);
        hour = findViewById(R.id.hour);
        minute = findViewById(R.id.minute);
        set = findViewById(R.id.set);
        clientPort = findViewById(R.id.client_port);
        startServer = findViewById(R.id.start_server);
        connect = findViewById(R.id.connect);
        result = findViewById(R.id.result);

        getInformationButton = (Button)findViewById(R.id.get_information_button);
        getInformationButton.setOnClickListener(timebuttonClickListener);

        startServer.setOnClickListener(buttonClickListener);
        connect.setOnClickListener(connectClickListener);
    }
}
