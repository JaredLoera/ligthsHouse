package com.example.lucescasa;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
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
import android.widget.TextView;
import android.widget.Toast;


import java.util.Calendar;
import java.util.Locale;
import java.text.SimpleDateFormat;

import android.os.Handler;
import android.Manifest;


public class MainActivity extends AppCompatActivity {
    private ImageView dayNightImageView, dayNightImageViewBG;
    private TextView textViewHora;

    private Button toggleButton;
    private boolean isDay = true;
    private RequestQueue rQueue;
    protected String IoKey;
    String adafruitURL = "https://io.adafruit.com/api/v2/JaredLoera/feeds/iluminacion/data";
    boolean status = true;
    String estadoFoco;

    private static final String HORA_SALIDA_SOL = "6:48";
    private static final String HORA_PUESTA_SOL = "6:33";
    private boolean esDeNoche;
    private Handler handler = new Handler();
    private Runnable updateTimeAuto;

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "alarm_channel";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        ApiKeyManager keysData = new ApiKeyManager();

        IoKey = keysData.getApiKey();

        dayNightImageView = findViewById(R.id.dayNightImageView);
        toggleButton = findViewById(R.id.toggleButton);
        dayNightImageViewBG = findViewById(R.id.backgroundImageView);
        textViewHora = findViewById(R.id.textViewHora);


        //area pruebas
        setAlarm();



        rQueue = Volley.newRequestQueue(this);
        getLastDataFeedIluminacion();
        setHourTexView();
        if (esDeNoche) {
            Toast.makeText(this, "es de noche we", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Es de dia we", Toast.LENGTH_SHORT).show();
        }
        setAlarm();
        updateTimeAuto = new Runnable() {
            @Override
            public void run() {
                setHourTexView();
                handler.postDelayed(this, 1000);
            }
        };

        handler.post(updateTimeAuto);

    }





    public  void setAlarm(){
        NotificationChannel channel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID, "Alarma", NotificationManager.IMPORTANCE_DEFAULT);
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 03);
        calendar.set(Calendar.MINUTE, 45);
        long millis = calendar.getTimeInMillis();

        alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 10000,pendingIntent);

    }

    public  void setHourTexView(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatoHora = new SimpleDateFormat("hh:mm", Locale.getDefault());
        String horaActual = formatoHora.format(calendar.getTime());
        esDeNoche = esDeNoche(horaActual);
        textViewHora.setText(horaActual);

    }

    private boolean esDeNoche(String horaActual) {
        try {
            SimpleDateFormat formatoHora = new SimpleDateFormat("hh:mm", Locale.getDefault());
            Calendar calHoraActual = Calendar.getInstance();
            Calendar calSalidaSol = Calendar.getInstance();
            Calendar calPuestaSol = Calendar.getInstance();

            calHoraActual.setTime(formatoHora.parse(horaActual));
            calSalidaSol.setTime(formatoHora.parse(HORA_SALIDA_SOL));
            calPuestaSol.setTime(formatoHora.parse(HORA_PUESTA_SOL));
            return calHoraActual.after(calPuestaSol) || calHoraActual.before(calSalidaSol);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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
                        dayNightImageView.setImageResource(R.drawable.moon);
                        dayNightImageViewBG.setImageResource(R.drawable._761719);
                        toggleButton.setText("Encendido");
                    }else{
                        status = false;
                        toggleButton.setText("Apagado");

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
                    dayNightImageViewBG.setImageResource(R.drawable.cartoon_cloud_sky_background_1_cover);
                }
                else{
                    dayNightImageViewBG.setImageResource(R.drawable._761719);
                }
                Animation fadeInAnimationBG = new AlphaAnimation(0.0f, 1.0f);
                fadeInAnimationBG.setDuration(2000); // Duración de la animación de aparición
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
                  dayNightImageView.setImageResource(R.drawable.sun);
                  toggleButton.setText("Apagado");

                }else {
                    dayNightImageView.setImageResource(R.drawable.moon);
                    toggleButton.setText("Encendido");
                }
                isDay=!isDay;
                Animation fadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
                fadeInAnimation.setDuration(2000);
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