package com.example.lispa.mapcras.modelRetrofit;

import java.util.List;

/**
 * Created by lispa on 10/12/2016.
 */

public class Response {

    List<Cras> docs;

    public List<Cras> getDocs() {
        return docs;
    }

    public void setDocs(List<Cras> docs) {
        this.docs = docs;
    }
}
