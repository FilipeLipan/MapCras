package com.example.lispa.mapcras.retrofitService;



import com.example.lispa.mapcras.modelRetrofit.Body;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by lispa on 10/12/2016.
 */

public interface CrasService {
    public static final String BASE_URL = "http://aplicacoes.mds.gov.br/";

    //http://aplicacoes.mds.gov.br/sagi/servicos/equipamentos?
    // q=tipo_equipamento:CRAS&fq=ibge:410180&
    // wt=json&fl=id_equipamento,ibge,uf,cidade,nome,responsavel,telefone,endereco,numero,complemento,referencia,bairro,cep,georef_location,data_atualizacao
    // &rows=999999999
    @GET("sagi/servicos/equipamentos")
    Call<Body> getEveryCrass(@Query("q") String q,
                             @Query("fq") String ibge,
                             @Query("wt") String wt,
                             @Query("fl") String fl,
                             @Query("rows") String rows);
}
