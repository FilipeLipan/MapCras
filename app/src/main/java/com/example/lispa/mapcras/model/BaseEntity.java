package com.example.lispa.mapcras.model;

/**
 * Created by lispa on 09/12/2016.
 */

public class BaseEntity {
    private String mIbge;
    private String mUf;
    private String mNome;
    private String mArea;

    public String getIbge() {
        return mIbge;
    }

    public void setIbge(String ibge) {
        mIbge = ibge;
    }

    public String getUf() {
        return mUf;
    }

    public void setUf(String uf) {
        mUf = uf;
    }

    public String getNome() {
        return mNome;
    }

    public void setNome(String nome) {
        mNome = nome;
    }

    public String getArea() {
        return mArea;
    }

    public void setArea(String area) {
        mArea = area;
    }
}
