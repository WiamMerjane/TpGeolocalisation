package ma.ensa.tpmap;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private double latitude;
    private double longitude;
    private double altitude;
    private float accuracy;
    RequestQueue requestQueue;
    Button showMap;
    String insertUrl = "http://192.168.0.112/localisation/createPosition.php";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        showMap = findViewById(R.id.showMap);
        showMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lancer une autre activité (intent) si nécessaire
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        // Vérifier si l'application a l'autorisation d'accéder à la localisation
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            // Demander les mises à jour de localisation depuis le GPS_PROVIDER
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 150, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    // Récupérer les données de localisation
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    altitude = location.getAltitude();
                    accuracy = location.getAccuracy();
                    String msg = String.format(getResources().getString(R.string.new_location), latitude, longitude, altitude, accuracy);
                    // Envoyer les données de localisation au serveur
                    addPosition(latitude, longitude);
                    // Afficher un message à l'utilisateur
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    // Gérer les changements d'état du fournisseur de localisation
                    String newStatus = "";
                    switch (status) {
                        case LocationProvider.OUT_OF_SERVICE:
                            newStatus = "OUT_OF_SERVICE";
                            break;
                        case LocationProvider.TEMPORARILY_UNAVAILABLE:
                            newStatus = "TEMPORARILY_UNAVAILABLE";
                            break;
                        case LocationProvider.AVAILABLE:
                            newStatus = "AVAILABLE";
                            break;
                    }
                    String msg = String.format(getResources().getString(R.string.provider_new_status), provider, newStatus);
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProviderEnabled(String provider) {
                    // Gérer le cas où le fournisseur de localisation est activé
                    String msg = String.format(getResources().getString(R.string.provider_enabled), provider);
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProviderDisabled(String provider) {
                    // Gérer le cas où le fournisseur de localisation est désactivé
                    String msg = String.format(getResources().getString(R.string.provider_disabled), provider);
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    void addPosition(final double lat, final double lon) {
        StringRequest request = new StringRequest(Request.Method.POST, insertUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Gérer la réponse du serveur ici
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Gérer les erreurs ici
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Obtenir le numéro IMEI du téléphone
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                // Préparer les données à envoyer
                HashMap<String, String> params = new HashMap<>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                params.put("latitude", String.valueOf(lat));
                params.put("longitude", String.valueOf(lon));
                params.put("date", sdf.format(new Date()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    params.put("imei", telephonyManager.getImei());
                }
                return params;
            }
        };
        requestQueue.add(request);
    }
}
