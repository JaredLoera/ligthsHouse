package com.example.lucescasa;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.os.Bundle;
import android.view.View;
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

public class MainActivity extends AppCompatActivity {
    private RequestQueue rQueue;
    private Button btn;
    String adafruitURL = "https://io.adafruit.com/api/v2/JaredLoera/feeds/iluminacion/data";
    boolean status = true;
    String estadoFoco;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        rQueue = Volley.newRequestQueue(this);
        btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestQue(status,btn);
            }
        });


    }
    public  void requestQue(boolean estado,Button btn){
        if(estado){
            estadoFoco ="0";
            status = false;
            btn.setText("Apagado");
        }else{
            estadoFoco ="1";
            status = true;
           btn.setText("Encendido");
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
                headers.put("X-AIO-Key", "aio_PzLI65y4r47JYAOL0xxSTlubHtog");
                return headers;
            }
        };
        rQueue.add(request);
    }
}