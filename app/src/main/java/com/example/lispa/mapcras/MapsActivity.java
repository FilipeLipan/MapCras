package com.example.lispa.mapcras;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.lispa.mapcras.adapter.CrasInfoWindowAdapter;
import com.example.lispa.mapcras.model.BaseEntity;
import com.example.lispa.mapcras.modelRetrofit.Body;
import com.example.lispa.mapcras.modelRetrofit.Cras;
import com.example.lispa.mapcras.retrofitService.CrasService;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    @BindView(R.id.spinnerEstados)    Spinner mSpinnerEstados;
    @BindView(R.id.spinnerMunicipios) Spinner mSpinnerMunicipios;
    @BindView(R.id.progressBar) ProgressBar mProgressBar;

    private ArrayList<BaseEntity> mEstados;
    private ArrayList<String> mEstadosString;
    private ArrayList<BaseEntity> mMunicipios;
    private ArrayList<String> mMunicipiosString;
    private List<Cras> mCrasList;

    private GoogleMap mMap;

    private static final String KEY_FIRST_TIME_OPÈN = "first_time_open";
    private boolean mIsFirstTimeOpen = true;

    private static final String IBGE_CODE_CURITIBA = "410690";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        if(savedInstanceState != null){
            if(savedInstanceState.containsKey(KEY_FIRST_TIME_OPÈN)){
                mIsFirstTimeOpen = savedInstanceState.getBoolean(KEY_FIRST_TIME_OPÈN);
            }
        }

        if(mIsFirstTimeOpen){
            getAllCrasByIBGE(IBGE_CODE_CURITIBA);
        }

        if(isGoogleMapsInstalled()) {
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Please install Google Maps");
            builder.setCancelable(false);
            builder.setPositiveButton("Install", getGoogleMapsListener());
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        mProgressBar.setVisibility(View.INVISIBLE);

        //Setting Up a blank spinner
        ArrayList<String> blankSpinner = new ArrayList<String>(Arrays.asList("-"));
        setUpSpinner(blankSpinner, mSpinnerEstados);
        setUpSpinner(blankSpinner, mSpinnerMunicipios);

        //calling the task to set up the spinner state
        recoverStatesTask recoverStates = new recoverStatesTask();
        recoverStates.execute();

        mSpinnerEstados.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mEstados != null) {
                    recoverCityTask municipiosTask = new recoverCityTask();
                    municipiosTask.execute(mEstados.get(position).getUf());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mSpinnerMunicipios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mMunicipios != null) {
                    getAllCrasByIBGE(mMunicipios.get(position).getIbge().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mIsFirstTimeOpen = false;
        outState.putBoolean(KEY_FIRST_TIME_OPÈN, mIsFirstTimeOpen);
    }

    public boolean isGoogleMapsInstalled()
    {
        try
        {
            ApplicationInfo info = getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0 );
            return true;
        }
        catch(PackageManager.NameNotFoundException e)
        {
            return false;
        }
    }

    public DialogInterface.OnClickListener getGoogleMapsListener()
    {
        return new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.maps"));
                startActivity(intent);

                //Finish the activity so they can't circumvent the check
                finish();
            }
        };
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

        if (mCrasList != null) {
            boolean isFirstItem = true;

            //clear th map from previous markers
            mMap.clear();

            //set up the markers
            for (Cras cras : mCrasList) {

                //this piece of code is going to verify if the object has a geographic location,
                //if don't he will find one with the object address
                if(cras.getGeoref_location() == null){
                    cras.setGeoref_location(getLatLongByAdress(cras.getEndereco(),cras.getCidade(),mSpinnerEstados.getSelectedItem().toString()));
                }

                //verify if the object cras has a geographic location and if it has both latitude and longitude
                if (cras.getGeoref_location() != null && cras.getGeoref_location().contains(",")) {
                    String[] latlng = cras.getGeoref_location().split(Pattern.quote(","));

                    //setting up the latitude and longitude
                    LatLng municipioLatLng = new LatLng(Float.parseFloat(latlng[0]), Float.parseFloat(latlng[1]));
                    //setting up the custom adapter
                    Marker marker = mMap.addMarker(new MarkerOptions().position(municipioLatLng));

                    //TODO: ask android teacher about custom infowindows!
//                    CrasInfoWindowAdapter adapter = new CrasInfoWindowAdapter(getBaseContext(), cras);
//                    mMap.setInfoWindowAdapter(adapter);

                    //setting up the marker
                    mMap.addMarker(new MarkerOptions().position(municipioLatLng).title(cras.getNome()).snippet(cras.getTelefone()));

                    //setting up the camera
                    if (isFirstItem) {
                        CameraUpdate center = CameraUpdateFactory.newLatLng(municipioLatLng);
                        CameraUpdate zoom = CameraUpdateFactory.zoomTo(11);

                        mMap.moveCamera(center);
                        mMap.animateCamera(zoom);
                    }
                }
                isFirstItem = false;
            }
        }
    }


    // get lat lng from an adress
    public String getLatLongByAdress(String adress, String city, String state) {
        String adressString = adress + "," + city + "," + state;
        Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());

        try {
            List<Address> addresses = geoCoder.getFromLocationName(adressString, 1);
            if (addresses.size() > 0) {
                return addresses.get(0).getLatitude() + "," + addresses.get(0).getLongitude();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    ;

    class recoverStatesTask extends AsyncTask<Void, Void, Void> {

        public static final String SOAP_ACTION = "http://pct/retornaTodosEstados";
        public static final String METHOD_NAME = "retornaTodosEstados";
        public static final String NAMESPACE = "http://pct/";
        public static final String URL = "http://192.168.25.9:8080/ClienteServletWS/alo?WSDL";

        String result;

        @Override
        protected Void doInBackground(Void... params) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            result = makeKsoapCall(URL, SOAP_ACTION, envelope);
            if (!result.equals("anyType{}")) {
                mEstados = Convertion.convertStringIntoArrayOfObjects(result);
                mEstadosString = Convertion.convertStringIntoArrayOfStringOjects(result);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setUpSpinner(mEstadosString, mSpinnerEstados);
        }

    }

    private String makeKsoapCall(String URL, String SOAP_ACTION, SoapSerializationEnvelope envelope) {
        String result = "anyType{}";
        HttpTransportSE transporter = new HttpTransportSE(URL);

        try {
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

            //this is the actual part that will call the webservice
            androidHttpTransport.call(SOAP_ACTION, envelope);

            // Get the SoapResult from the envelope body.
            SoapObject soapObject = (SoapObject) envelope.bodyIn;

            if (soapObject != null) {
                //Get the first property and change the label text
                result = soapObject.getProperty(0).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    class recoverCityTask extends AsyncTask<String, Void, Void> {

        public static final String SOAP_ACTION = "http://pct/procuraTodasCidadePorUF/";
        public static final String METHOD_NAME = "procuraTodasCidadePorUF";
        public static final String NAMESPACE = "http://pct/";
        public static final String URL = "http://192.168.25.9:8080/ClienteServletWS/alo?WSDL";

        String result;


        @Override
        protected Void doInBackground(String... params) {

            if (params != null) {
                String uf = params[0];

                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                request.addProperty("uf", uf);
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

                envelope.setOutputSoapObject(request);
                envelope.dotNet = false;

                result = makeKsoapCall(URL, SOAP_ACTION, envelope);
                if (!result.equals("anyType{}")) {
                    mMunicipios = Convertion.convertStringIntoArrayOfObjects(result);
                    mMunicipiosString = Convertion.convertStringIntoArrayOfStringOjects(result);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void Void) {
            setUpSpinner(mMunicipiosString, mSpinnerMunicipios);
        }

    }

    private void setUpSpinner(ArrayList<String> strings, Spinner spinner) {
        if (strings != null) {
            //Get the first property and change the label text
            ArrayAdapter adapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_spinner_item, strings);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
        }
    }

    private void getAllCrasByIBGE(String ibge) {
        mProgressBar.setVisibility(View.VISIBLE);

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(CrasService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //create service from the interface
        CrasService service = retrofit.create(CrasService.class);

        //prepare the call, invoke the method from the interface Crashservice passing the proper values
        Call<Body> call = service.getEveryCrass("tipo_equipamento:CRAS", "ibge:" + ibge, "json",
                "id_equipamento,ibge,uf,cidade,nome,responsavel,telefone,endereco,numero,complemento,referencia,bairro,cep,georef_location,data_atualizacao", "999999999");

        //enqueue the call in order to not let the ui stop
        call.enqueue(new Callback<Body>() {
            @Override
            public void onResponse(Call<Body> call, retrofit2.Response<Body> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getBaseContext(), getBaseContext().getResources().getString(R.string.recover_state_error), Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    Body body = response.body();
                    mCrasList = body.getResponse().getDocs();
                    Toast.makeText(getBaseContext(), getBaseContext().getResources().getString(R.string.recover_state_sucess), Toast.LENGTH_SHORT).show();
                    mProgressBar.setVisibility(View.INVISIBLE);

                    onMapReady(mMap);
                }
            }

            @Override
            public void onFailure(Call<Body> call, Throwable t) {
                Toast.makeText(getBaseContext(), getBaseContext().getResources().getString(R.string.recover_state_error), Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @OnClick(R.id.loadState)
    public void loadStateOnClick(){
        recoverStatesTask recoverStates = new recoverStatesTask();
        recoverStates.execute();
    }
}
