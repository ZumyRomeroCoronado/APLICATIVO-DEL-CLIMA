package com.example.zumi.mapa;


import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LatLng lima=new LatLng(-8.3751034,-74.6517595);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        String Ciudades[] ={"Espinar","camaná","nazca"
                ,"chachapoyas","huaraz","abancay","arequipa","ayacucho","cajamarca","callao","andahuaylas","cusco","huancavelica",
                "huanuco","ica","huancayo","trujillo","chiclayo","lima","iquitos","puerto%20maldonado",
                "moquegua","cerro%20de%20pasco","piura","puno","moyobamba","tacna","tumbes","pucallpa","puquio","bagua"};
        // Add a marker in Sydney and move the camera
       // LatLng sydney = new LatLng(-34, 151);//.................


        for( int i=0; i<31; i++)
        {
            new ReadJSONFeedTask().execute(
                    "http://api.openweathermap.org/data/2.5/weather?q="+Ciudades[i]+",PE&units=metric&APPID=9c390ebbe3276a74e74814962661b624");

        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lima,5));
    }
/**llamamos el url para q  lea la lon lan*/
public String readJSONFeed(String URL ){
    StringBuilder stringBuilder = new StringBuilder();

    HttpClient client = new DefaultHttpClient();
    HttpGet httpGet = new HttpGet(URL);
    try {
        HttpResponse response = client.execute(httpGet);
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        if (statusCode == 200) {
            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(content));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } else {
            Log.e("JSON", "Failed to download file");
        }
    } catch (ClientProtocolException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return stringBuilder.toString();
}

    private class ReadJSONFeedTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }
        protected void onPostExecute(String result) {

            try {
                JSONObject obj = new JSONObject(result);
                JSONObject obj1 = obj.getJSONObject("main");
                JSONObject objcoor = obj.getJSONObject("coord");

                Float latitud=Float.parseFloat(objcoor.getString("lat"));
                Float longitud=Float.parseFloat(objcoor.getString("lon"));
                String temperatura = obj1.getString("temp");
                String ciudad = obj.getString("name");
                String humedad = obj1.getString("humidity");
                LatLng coordenada= new LatLng(latitud,longitud);

                Marker ubicacion=mMap.addMarker(new MarkerOptions().position(coordenada).title("Ciudad : "+ciudad)
                        .snippet("T : "+temperatura+" C° ; H :"+humedad+"%"));

                ubicacion.showInfoWindow();

            }
            catch (Exception e) {
                e.printStackTrace();
            }



        }

    }

}
