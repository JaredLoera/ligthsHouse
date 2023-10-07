package com.example.lucescasa;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
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

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.view.animation.DecelerateInterpolator;

public class MainActivity extends AppCompatActivity {
    private ImageView dayNightImageView, dayNightImageViewBG;
    private Button toggleButton;
    private boolean isDay = true;
    private RequestQueue rQueue;
    protected String IoKey ;
    String adafruitURL = "https://io.adafruit.com/api/v2/JaredLoera/feeds/iluminacion/data";
    boolean status = true;
    String estadoFoco;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ApiKeyManager keysData = new ApiKeyManager();

        IoKey = keysData.getApiKey();

        dayNightImageView = findViewById(R.id.dayNightImageView);
        toggleButton = findViewById(R.id.toggleButton);
        dayNightImageViewBG = findViewById(R.id.backgroundImageView);

        rQueue = Volley.newRequestQueue(this);
        getLastDataFeedIluminacion();
    
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
                        dayNightImageViewBG.setImageResource(R.drawable._761719);
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
                headers.put("X-AIO-Key",IoKey);
                return headers;
            }
        };
        rQueue.add(request);
    }
    public  void dayNaghtBG(View view){
        float currentAlphaBG = dayNightImageViewBG.getAlpha();
        float targetAlphaBG = (currentAlphaBG == 1) ? 0 : 1;
        Animation animationBG = new AlphaAnimation(currentAlphaBG, targetAlphaBG);
        animationBG.setDuration(2000);
        animationBG.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(!isDay){
                    dayNightImageViewBG.setImageResource(R.drawable._761719);
                }
                else{
                    dayNightImageViewBG.setImageResource(R.drawable.cartoon_cloud_sky_background_1_cover);
                }
                Animation fadeInAnimationBG = new AlphaAnimation(0.0f, 1.0f);
                fadeInAnimationBG.setDuration(1500); // Duración de la animación de aparición
                fadeInAnimationBG.setInterpolator(new DecelerateInterpolator());
                dayNightImageView.startAnimation(fadeInAnimationBG);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        dayNightImageViewBG.startAnimation(animationBG);
    }
    public void  toggleDayNight (View view){
        float currentAlpha = dayNightImageView.getAlpha();
        float targetAlpha = (currentAlpha == 1) ? 0 : 1;

        Animation animation = new AlphaAnimation(currentAlpha, targetAlpha);
        animation.setDuration(2000);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                dayNaghtBG(view);
                requestQue(status);
                toggleButton.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isDay){
                    dayNightImageView.setImageResource(R.drawable.moon);
                    toggleButton.setText("Apagado");
                }else {
                    dayNightImageView.setImageResource(R.drawable.sun);
                    toggleButton.setText("Encendido");

                }
                isDay=!isDay;
                Animation fadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
                fadeInAnimation.setDuration(1500); // Duración de la animación de aparición
                fadeInAnimation.setInterpolator(new DecelerateInterpolator());
                dayNightImageView.startAnimation(fadeInAnimation);
                toggleButton.setEnabled(true);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        dayNightImageView.startAnimation(animation);

        ScaleAnimation buttonAnimation = new ScaleAnimation(1, 1.1f, 1, 1.1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        buttonAnimation.setDuration(300);
        buttonAnimation.setRepeatCount(1);
        buttonAnimation.setRepeatMode(Animation.REVERSE);
        toggleButton.startAnimation(buttonAnimation);

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
                headers.put("X-AIO-Key", IoKey);
                return headers;
            }
        };
        rQueue.add(request);
    }
}