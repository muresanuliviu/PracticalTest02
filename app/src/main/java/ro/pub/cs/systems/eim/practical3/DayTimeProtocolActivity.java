package ro.pub.cs.systems.eim.practical3;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class DayTimeProtocolActivity extends AppCompatActivity {
    private Button getInformationButton;
    private TextView daytimeProtocolTextView;

    private class NISTCommunicationAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String dayTimeProtocol = null;
            try {
                Socket socket = new Socket(Constants.NIST_SERVER_HOST, Constants.NIST_SERVER_PORT);
                BufferedReader bufferedReader = Utilities.getReader(socket);
                bufferedReader.readLine();
                dayTimeProtocol = bufferedReader.readLine();
                Log.d(Constants.TAG, "The server returned: " + dayTimeProtocol);
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
        protected void onPostExecute(String result) {
            daytimeProtocolTextView.setText(result);
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
//        setContentView(R.layout.activity_day_time_protocol);

//        daytimeProtocolTextView = (TextView)findViewById(R.id.daytime_protocol_text_view);

//        getInformationButton = (Button)findViewById(R.id.get_information_button);
        getInformationButton.setOnClickListener(timebuttonClickListener);
    }
}
