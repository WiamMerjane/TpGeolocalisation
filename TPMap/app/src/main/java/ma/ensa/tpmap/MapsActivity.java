package ma.ensa.tpmap;

import android.os.Bundle;
import android.util.Log; // Import Log for logging

import androidx.fragment.app.FragmentActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private String showUrl = "https://192.168.0.112/localisation/showPositions.php";
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initialize the request queue
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void fetchAndDisplayMarkers() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                showUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray positions = response.getJSONArray("positions");
                    for (int i = 0; i < positions.length(); i++) {
                        JSONObject position = positions.getJSONObject(i);
                        double latitude = position.getDouble("latitude");
                        double longitude = position.getDouble("longitude");
                        LatLng positionLatLng = new LatLng(latitude, longitude);
                        gMap.addMarker(new MarkerOptions().position(positionLatLng).title("Marker"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSONError", "Error parsing JSON: " + e.getMessage()); // Log JSON parsing errors
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.e("VolleyError", "Volley error: " + error.getMessage()); // Log Volley errors
            }
        });

        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.gMap = googleMap;



        // Call the method to fetch and display markers
        fetchAndDisplayMarkers();
    }
}
