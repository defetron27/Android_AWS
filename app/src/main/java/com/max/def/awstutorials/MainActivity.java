package com.max.def.awstutorials;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity
{
    private AppCompatEditText textArea;
    private AppCompatTextView response;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textArea = findViewById(R.id.text_area);
        AppCompatButton getResult = findViewById(R.id.get_result);
        response = findViewById(R.id.response);

        getResult.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String text = textArea.getText().toString();

                if (text.isEmpty() || text.equals(" "))
                {
                    Toast.makeText(MainActivity.this, "Enter any text", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    textArea.setText("");

                    String finalText = text.replaceAll(" ","+");

                    new getApiResponse(MainActivity.this).execute(finalText);
                }
            }
        });
    }

    private static class getApiResponse extends AsyncTask<String,String,String>
    {
        WeakReference<MainActivity> mainActivityWeakReference;

        getApiResponse(MainActivity mainActivity)
        {
            this.mainActivityWeakReference = new WeakReference<>(mainActivity);
        }

        @Override
        protected String doInBackground(String... params)
        {
            try
            {
                URL apiUrl = new URL("https://2hq22zaeab.execute-api.us-east-1.amazonaws.com/alpha/awsApiFunction?text=" + params[0]);

                HttpsURLConnection connection = (HttpsURLConnection) apiUrl.openConnection();

                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestMethod("POST");

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuilder stringBuilder = new StringBuilder();

                String jsonOutput;

                while ((jsonOutput = bufferedReader.readLine()) != null)
                {
                    stringBuilder.append(jsonOutput);
                }

                return stringBuilder.toString();
            }
            catch (IOException e)
            {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String apiResponseResult)
        {
            super.onPostExecute(apiResponseResult);

            if (apiResponseResult == null || apiResponseResult.equals("null"))
            {
                mainActivityWeakReference.get().response.setText("Any results not found");
            }
            else
            {
                ApiResponse apiResponse = new Gson().fromJson(apiResponseResult,ApiResponse.class);

                if (apiResponse != null)
                {
                    if (apiResponse.getText() != null)
                    {
                        mainActivityWeakReference.get().response.setText(apiResponse.getText());
                    }
                    else
                    {
                        mainActivityWeakReference.get().response.setText("Any results not found");
                    }
                }
                else
                {
                    mainActivityWeakReference.get().response.setText("Any results not found");
                }
            }
        }
    }
}







