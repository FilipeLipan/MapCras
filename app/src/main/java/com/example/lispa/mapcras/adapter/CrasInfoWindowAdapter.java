package com.example.lispa.mapcras.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.lispa.mapcras.R;
import com.example.lispa.mapcras.modelRetrofit.Cras;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.zip.Inflater;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lispa on 21/12/2016.
 */

public class CrasInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context mContext;
    private Cras mCras;

    private TextView mName;
    private TextView mAdress;
    private TextView mCity;
    private TextView mPhone;

    public CrasInfoWindowAdapter(Context context, Cras cras) {
        mContext = context;
        mCras = cras;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.info_window, null);


        mName = (TextView) view.findViewById(R.id.infoViewName);
        mAdress = (TextView) view.findViewById(R.id.infoViewAdress);
        mCity = (TextView) view.findViewById(R.id.infoViewCity);
        mPhone = (TextView) view.findViewById(R.id.infoViewPhone);

        mName.setText(mCras.getNome());
        mAdress.setText(mCras.getEndereco());
        mCity.setText(mCras.getCidade());
        mPhone.setText(mCras.getTelefone());

        return view;
    }
}
