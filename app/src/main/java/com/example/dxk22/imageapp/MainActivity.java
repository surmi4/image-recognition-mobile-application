package com.example.dxk22.imageapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Caption;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
//import android.R;

public class MainActivity extends AppCompatActivity {
    public VisionServiceClient visionServiceClient= new VisionServiceRestClient("6c762a0bdddc4aa5a67fc54881233058");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.cat);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        //TextView textView = (TextView) findViewById(R.id.txtDescription);
        Button btnProcess = (Button) findViewById(R.id.btnProcess);

        imageView.setImageBitmap(mBitmap);

        //Convert Image into Stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        btnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               final AsyncTask<InputStream,String,String> visionTask = new AsyncTask<InputStream, String, String>() {
                   ProgressDialog mDailog = new ProgressDialog(MainActivity.this);
                   @Override
                    protected String doInBackground(InputStream... params) {
                        try{
                            publishProgress("Recognizing....");
                            String[] features = {"Description"};
                            String[] details = {};

                            AnalysisResult result= visionServiceClient.analyzeImage(params[0],features,details);

                            String strResult = new Gson().toJson(result);
                            return (strResult);
                        }
                        catch (Exception e)
                        {
                            return null;
                        }

                    }

                    @Override
                    protected void onPreExecute() {
                       mDailog.show();
                    }

                    @Override
                    protected void onPostExecute(String s) {
                       mDailog.dismiss();

                       AnalysisResult result = new Gson().fromJson(s,AnalysisResult.class);
                        TextView textView = (TextView) findViewById(R.id.txtDescription);
                        StringBuilder stringBuilder = new StringBuilder();
                        for(Caption caption:result.description.captions)
                        {
                            stringBuilder.append(caption.text);
                        }
                        textView.setText(stringBuilder);
                    }

                    @Override
                    protected void onProgressUpdate(String... values) {
                       mDailog.setMessage(values[0]);
                    }
                };

               visionTask.execute(inputStream);
            }
        });

    }
}
