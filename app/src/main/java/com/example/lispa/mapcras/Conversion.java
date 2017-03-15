package com.example.lispa.mapcras;



import com.example.lispa.mapcras.model.BaseEntity;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by lispa on 09/12/2016.
 */

public class Conversion {

    public static ArrayList<BaseEntity> convertStringIntoArrayOfObjects(String objString){

        ArrayList<BaseEntity> objs = new ArrayList<>();
        String[] strings = new String[4];

        for(String str : objString.split("---")){

            BaseEntity obj = new BaseEntity();
            int i = 0;

            for(String item : str.split(Pattern.quote(","))){
                strings[i] = item;
                i++;
            }

            obj.setIbge(strings[0]);
            obj.setUf(strings[1]);
            obj.setNome(strings[2]);
            obj.setArea(strings[3]);

            objs.add(obj);
        }

        return objs;
    }

    public static ArrayList<String> convertStringIntoArrayOfStringOjects(String objString){

        ArrayList<String> objs = new ArrayList<>();
        String[] strings = new String[4];

        for(String str : objString.split("---")){

            BaseEntity obj = new BaseEntity();
            int i = 0;

            for(String item : str.split(Pattern.quote(","))){
                strings[i] = item;
                i++;
            }


            objs.add(strings[2]);
        }

        return objs;
    }
}
