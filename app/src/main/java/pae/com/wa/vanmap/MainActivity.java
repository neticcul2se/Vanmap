package pae.com.wa.vanmap;

import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class MainActivity extends FragmentActivity implements LocationListener {
    GoogleMap googleMap;
    Circle gCircle;
    Marker mMarker;
    double mylat,mylng,srlat,srlng;
    TextView curlocation;
    TextView duration;
    TextView distan;
    String lo;
    String resultgle;
    String msgs;
    Marker mMarker2;
    Button relovan,usevan;
    int zoomfirst=0;
    Marker thismaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

            StrictMode.setThreadPolicy(policy);

        }
        if (!isGooglePlayServicesAvailable())
        {
            finish();
        }
        distan = (TextView)findViewById(R.id.distan);
        duration = (TextView)findViewById(R.id.duration);
        curlocation = (TextView)findViewById(R.id.latlongLocation);
        relovan = (Button)findViewById(R.id.reLovan);
        usevan = (Button)findViewById(R.id.btnuse);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        googleMap = supportMapFragment.getMap();
        googleMap.setMyLocationEnabled(true);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            onLocationChanged(location);
        }

        locationManager.requestLocationUpdates(bestProvider, 5000, 0, this);

        usevan.setVisibility(View.GONE);

        relovan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread vanlo = new Thread(new requestLocation());
                vanlo.start();




            }
        });


       /* googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            public boolean onMarkerClick(Marker arg0) {
                distan_duration di_du = new distan_duration();

                resultdis = di_du.distan("13.846435,100.85833", "13.842559,100.856334");
                distan.setText(resultdis);

                return true;
            }
        });
        */
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                if(marker.equals(mMarker)){
                    duration.setText("");
                    distan.setText("");
                    usevan.setVisibility(View.GONE);
                }
                else {


                    distan_duration di_du = new distan_duration();
                    resultgle = di_du.distan(lo, String.valueOf(srlat) + "," + String.valueOf(srlng));
                    String[] all2 =resultgle.split(",");

                    duration.setText(all2[0]);
                    distan.setText(all2[1]);
                    usevan.setVisibility(View.VISIBLE);

                }
                return true;
            }
        });
    }


    class mylo implements Runnable {
        final Handler handler = new Handler();
        public void run() {
            // TODO Auto-generated method stub
            try {

                int server_port = 4444;
                DatagramSocket s = new DatagramSocket();
                InetAddress local = InetAddress.getByName("129.199.230.75");

                int msg_length = lo.length();
                byte[] message = lo.getBytes();
                DatagramPacket p = new DatagramPacket(message, msg_length, local,server_port);
                s.send(p);

            } catch (Exception e) {

            }
        }
    }

    class  requestLocation implements Runnable{
        final Handler handler = new Handler();
        public void run() {


            int server_port = 4444;
            DatagramSocket s = null;



            try {
                s = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            InetAddress local = null;


            try {
                local = InetAddress.getByName("192.168.56.1");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            String data = "Code:04,idroad:001";
            int msg_length = data.length();
            byte[] message = data.getBytes();
            DatagramPacket p = new DatagramPacket(message, msg_length, local, server_port);
            try {
                s.send(p);
            } catch (IOException e) {
                e.printStackTrace();
            }



            while (true){
                byte[] message2 = new byte[1500];
                DatagramPacket packet = new DatagramPacket(message2, message2.length);
                try {
                    s.receive(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }


                msgs = new String(packet.getData(), 0, packet.getLength());

                String[] all = msgs.split(",");
                final double la = Double.parseDouble(all[0]);
                final double lo = Double.parseDouble(all[1]);
                handler.post(new Runnable() {
                    public void run() {
                        addMaker(la, lo);


                    }
                });
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public  void makerMy(double latitude,double longitude) { //ปักหมุดแล้วซูมกล้องตำแหน่งปัจจุบัน
        LatLng latLng = new LatLng(latitude, longitude);
        if (mMarker != null)
        {
            mMarker.remove();
            gCircle.remove();
        }
        mMarker =  googleMap.addMarker(new MarkerOptions()
                .position(latLng));
        gCircle =  googleMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(700)
                .strokeWidth(5)
                .strokeColor(Color.argb(100, 11, 195, 255))
                .fillColor(Color.argb(10, 11, 195, 255)));


            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));




    }


    private void addMaker(double l1,double l2){
        LatLng car1= new LatLng( l1, l2);
        if(mMarker2 != null)
            mMarker2.remove();

        mMarker2 = googleMap.addMarker(new MarkerOptions()
                .position(car1)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
        srlat=l1;
        srlng=l2;


    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }





    @Override
    public void onLocationChanged(Location location) {

        double latitude= location.getLatitude();
        double longitude = location.getLongitude();
        mylat=latitude;
        mylng=longitude;
        zoomfirst++;
        makerMy(latitude, longitude);

        lo=String.valueOf(latitude)+","+String.valueOf(longitude);
        curlocation.setText(lo);

       // Thread t = new Thread(new mylo());///ส่งตำแหน่งไปให้ server ทุกครั้งที่เปลี่ยนตำแหน่ง
        //t.start();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }




}
