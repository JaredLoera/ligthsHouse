package com.example.lucescasa;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.animation.ObjectAnimator;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private ImageView dayNightImageView;
    private Button toggleButton;
    private boolean isDay = true;
    private RequestQueue rQueue;
    private Button btn;
    String adafruitURL = "https://io.adafruit.com/api/v2/JaredLoera/feeds/iluminacion/data";
    boolean status = true;
    String estadoFoco;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        dayNightImageView = findViewById(R.id.dayNightImageView);
        toggleButton = findViewById(R.id.toggleButton);
        rQueue = Volley.newRequestQueue(this);
        getLastDataFeedIluminacion();
        /*
        btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestQue(status,btn);
            }
        });
        */
    }

    public void getLastDataFeedIluminacion(){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "https://io.adafruit.com/api/v2/JaredLoera/feeds/iluminacion", null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String  value = response.getString("last_value");
                    int lastData= Integer.parseInt(value);
                    if(lastData == 1){
                        status = true;
                        toggleButton.setText("Encendido");
                    }else{
                        status = false;
                        toggleButton.setText("Apagado");
                        dayNightImageView.setImageResource(R.drawable.moon);
                        isDay = false;
                    }
                }catch (JSONException error){
                    error.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               error.printStackTrace();
            }
        }){
            @Override
            public Map<String,String> getHeaders(){
                Map<String,String> headers = new HashMap<>();
                headers.put("X-AIO-Key","aio_OmFm05QNZnpqTH5DjLVLOwC1LoMz");
                return headers;
            }
        };
        rQueue.add(request);
    }
    public void  toggleDayNight (View view){
        float currentAlpha = dayNightImageView.getAlpha();
        float targetAlpha = (currentAlpha == 1) ? 0 : 1;

        Animation animation = new AlphaAnimation(currentAlpha, targetAlpha);
        animation.setDuration(2000);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                requestQue(status);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isDay){
                    dayNightImageView.setImageResource(R.drawable.moon);
                    isDay =false;
                    toggleButton.setText("Apagado");
                }else {
                    dayNightImageView.setImageResource(R.drawable.sun);
                    isDay = true;
                    toggleButton.setText("Encendido");
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        dayNightImageView.startAnimation(animation);

    }

    public  void requestQue(boolean estado){
        if(estado){
            estadoFoco ="0";
            status = false;
        }else{
            estadoFoco ="1";
            status = true;
        }
        JSONObject objeto = new JSONObject();
        try {
                objeto.put("value",estadoFoco);
        }catch (JSONException error)
        {
            error.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, adafruitURL, objeto, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("X-AIO-Key", "aio_OmFm05QNZnpqTH5DjLVLOwC1LoMz");
                return headers;
            }
        };
        rQueue.add(request);
    }
}