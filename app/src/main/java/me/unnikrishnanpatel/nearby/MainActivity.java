package me.unnikrishnanpatel.nearby;
import android.Manifest;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmResults;
import me.unnikrishnanpatel.nearby.data.Place;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, LocationListener {

    GoogleApiClient googleApiClient;
    LocationRequest locationRequest;
    final static String CLIENT_ID = "QT3LMN1SQKXPVKTSNGZLRWNYUGNJ0UP5KYXE2GYI551CICRI";
    final static String CLIENT_SECRET= "PKJZJBIDBZ4LFHYZQYY40KEGMM42BT1RNG4AHMK1V3JCJ3HH";
    Realm realm;
    ArrayList<HashMap<String,String>> ids;
    ArrayList<Place> pl = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realm = Realm.getDefaultInstance();
        RealmResults<Place> places = realm.where(Place.class).findAll();
        googleApiClient = new GoogleApiClient.Builder(this).
                addOnConnectionFailedListener(this).
                addConnectionCallbacks(this).
                addApi(LocationServices.API).
                build();
    }

    protected void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        createLocationRequest();

        startLocationUpdates();
    }



    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    void startLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (googleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient, this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startLocationUpdates();

                } else {}
                return;
            }
        }
    }

    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {
        final TextView t = (TextView)findViewById(R.id.textview);
        t.setText(location.toString());
        String url = "https://api.foursquare.com/v2/venues/search" +
                "?client_id="+CLIENT_ID +
                "&client_secret="+CLIENT_SECRET +
                "&v=20160517" +
                "&ll="+location.getLatitude()+","+location.getLongitude()+"&query=business";
        Log.d("url",url);

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String data = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    JSONObject listObject = new JSONObject(jsonObject.get("response").toString());
                    JSONArray venuesArray = listObject.getJSONArray("venues");
                    ids = new ArrayList<>();
                    for (int i = 0; i < venuesArray.length(); i++) {
                        JSONObject venue = venuesArray.getJSONObject(i);
                        HashMap<String,String> store= new HashMap<String, String>();
                        store.put("id",venue.getString("id"));
                        store.put("distance",venue.getJSONObject("location").getString("distance"));
                        ids.add(store);
                    }

                    Log.d("ids", String.valueOf(ids));
                    loadVenues();


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }



    public void loadVenues(){
        for(int i = 0;i<ids.size();i++){
            final String venue_id = ids.get(i).get("id");
            final int venue_distance = Integer.parseInt(ids.get(i).get("distance"));
            String url = "https://api.foursquare.com/v2/venues/"+ venue_id +"?oauth_token=2QKJ3SXYVY5MLRI5IKSLRZVB5E301CAPRVQ05EJK45GMAM0L&v=20160520";
            OkHttpClient client = new OkHttpClient();

            Log.d("fucking url ",url);
            final Request request = new Request.Builder().url(url).build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String data = response.body().string();

                            try{
                                realm.beginTransaction();
                                JSONObject jsonObject = new JSONObject(data);
                                JSONObject venueObject = jsonObject.getJSONObject("response").getJSONObject("venue");
                                Place place = new Place();
                                place.setId(venue_id);
                                place.setDistance(venue_distance);
                                place.setCategory(new JSONObject(venueObject.getJSONArray("categories").get(0).toString()).getString("name"));
                                place.setNow(Boolean.parseBoolean(venueObject.getJSONObject("hours").getString("isOpen")));
                                place.setName(venueObject.getString("name"));
                                place.setIcon_url("zzzzzz");
                                pl.add(place);
                                realm.copyToRealmOrUpdate(place);
                                realm.commitTransaction();


                            } catch (JSONException e){}






                }
            });
        }

    }
}
