package com.example.lispa.mapcras.model;

import java.util.ArrayList;

/**
 * Created by lispa on 09/12/2016.
 */

public class Estado extends BaseEntity {
    private ArrayList<Municipio> mMunicipios;

    public Estado(){
        mMunicipios = new ArrayList<>();

    }

    public ArrayList<Municipio> getMunicipios() {
        return mMunicipios;
    }

    public void setMunicipios(ArrayList<Municipio> municipios) {
        mMunicipios = municipios;
    }
}
