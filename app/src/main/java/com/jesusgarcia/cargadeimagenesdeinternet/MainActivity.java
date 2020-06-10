package com.jesusgarcia.cargadeimagenesdeinternet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    RecyclerViewAdaptador recyclerViewAdaptador;
    RecyclerView recyclerView;

    public static final String TAG = "MyTag";

    /**
     * En el metodo onCreate se realizá el JsonObjectRequest que permite extraer los datos
     * contenidos en el JSON desde la dirección URL utilizada. Los datos obtenidos del JSON son
     * parseados para obtener los valores de los atributos que lo conforman (nombre y URL). Esos
     * datos son enviados al método "recibir".
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* TODO: Obtenemos los datos tipo JSON de la URL que solicitamos. Aquí se utiliza volley. */
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                "https://simplifiedcoding.net/demos/view-flipper/heroes.php", // URL solicitada.
                null,
                new Response.Listener<JSONObject>() {
                    /**
                     * De acuerdo a la respuesta del JSON obtenido parseamos los datos.
                     * @param response Contiene el Array de los datos en estructura de JSON.
                     */
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            /* Obtenemos los atributos que hacen a un objeto heroes. */
                            JSONArray jsonArray = response.getJSONArray("heroes");

                            datoNombre = new String[jsonArray.length()]; // Cantidad de nombres.
                            datoUrls = new String[jsonArray.length()]; // Cantidad de URLs en "heroes".

                            /* Extraemos los atributos de cada "hero" existente */
                            for (int i = 0; i < jsonArray.length(); i++) {
                                /* Extracción de los atributos "hero" en la posición i. */
                                JSONObject hero = jsonArray.getJSONObject(i);

                                String nombre = hero.getString("name"); // Extrae nombre.
                                String imageUrl = hero.getString("imageurl"); // Extrae URL.

                                /* Imprime los datos extraidos en la posición i en el LOG. */
                                Log.i("MOSTRAR", nombre + " - " + imageUrl);

                                /* Envía los datos extraidos (nombre, url) de la posición i a
                                 *  el método recibir. */
                                recibir(nombre, imageUrl, jsonArray.length());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );

        /* TODO: utiliza Singleton para realizar la operación con Volley del JsonRequest. */
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    String[] datoNombre, datoUrls;
    int bandera = 0;

    /**
     * EL método se encarga de agregar los datos de los heroes: nombre y URL a un ArrayList
     * que pasa al adaptador del RecyclerView.
     * @param names Nombre extraido del heroe.
     * @param imgUrls URL de la imagen extraido del heroe.
     * @param limite Cantidad de heroes encontrados en el JSON.
     */
    public void recibir(String names, String imgUrls, int limite) {
        datoNombre[bandera] = names;
        datoUrls[bandera] = imgUrls;
        bandera++;
        if (bandera == limite) {
            recyclerView = findViewById(R.id.rcvListado);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            ArrayList<Card> list = new ArrayList<>();

            /* Se añade al ArrayList los atributos del hero según la posición i. */
            for (int i = 0; i < datoNombre.length; i++) {
                list.add(new Card(datoUrls[i], datoNombre[i]));
            }

            recyclerViewAdaptador = new RecyclerViewAdaptador(list);
            recyclerView.setAdapter(recyclerViewAdaptador);
            bandera = 0;
        }
    }
}
