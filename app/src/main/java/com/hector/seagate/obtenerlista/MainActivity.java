package com.hector.seagate.obtenerlista;
/*
tutorial buenisimo que me inspiró:
http://www.hermosaprogramacion.com/2015/01/android-httpurlconnection/
 */


//hay que guardar estado, pork al girar pantalla se cambian los iconos de los autobuses
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    Tarea tarea;
        URL url;
    String ruta;
    ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);






        lista = (ListView) findViewById(R.id.vistaLista);
        //conectarse asyncronamente a la url

         ruta= "http://www.motosmieres.com/mostrarVehiculos.php";
        try {
            url = new URL(ruta);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        tarea = new Tarea(MainActivity.this);
        tarea.execute(url);

    }
}


class Tarea extends AsyncTask<URL,Void,String>{
    HttpURLConnection con;
    String data="";
    String idGestor="1";
    Activity actividadUI;
    ArrayList<String> vehiculos=null;
    JSONArray arrayJSON;
    OutputStream os = null;
    JSONObject linea;
    BufferedWriter bw = null;
    InputStreamReader bis;
    StringBuffer result=null;
    BufferedReader lectorEntrada=null;
    String line="";


    public Tarea(MainActivity mainActivity) {
    this.actividadUI= mainActivity;
    }
    ArrayList<Vehiculo> vehiculos_data;
    @Override

    protected String doInBackground(URL... params) {

        try {
             con = (HttpURLConnection) params[0].openConnection();




        } catch (IOException e) {
            e.printStackTrace();
        }
        con.setReadTimeout(10000);
        con.setConnectTimeout(15000);
        con.setDoOutput(true);
        con.setDoInput(true);
        try {
            //en el php del servidor, debe estar tambien en POST
            con.setRequestMethod("POST");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        try {
            con.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }





        try {
            os = con.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));

            data = URLEncoder.encode("a", "UTF-8") + "=" + URLEncoder.encode(idGestor, "UTF-8") ;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        try {

            bw.write(data);
            bw.flush();

            bw.close();

            os.close();

/*
                Parsear el flujo con formato JSON a una lista de Strings
                que permitan crean un adaptador
                 */
             bis = new InputStreamReader(con.getInputStream());
             lectorEntrada = new BufferedReader(bis);



            //mientras el lectorEntrada encuentre algo lo añado ;
            result= new StringBuffer();
            while ((line = lectorEntrada.readLine()) != null) {
                result.append(line);
            }



            con.getInputStream().close();
            con.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d("RESULTADO", result.toString());
        return result.toString();



    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            arrayJSON = new JSONArray(s);




            ////
             //vehiculos_data = new Vehiculo[]
                 //   {
               //             new Vehiculo(R.drawable.busamarillo, "Linea sama-tuilla"),
                   //         new Vehiculo(R.drawable.busazul, "Linea laviana-villa"),
                     ////     new Vehiculo(R.drawable.busnegro, "La Felguera - Mieres")
                    //};






            ////

            vehiculos_data = new ArrayList<>();

            for (int i=0;i<arrayJSON.length();i++){
                linea =arrayJSON.getJSONObject(i);
                Log.d("RESULTADO", "Se recogio correctamente id_vehiculo: " + linea.get("id_vehiculo"));
            //    vehiculos.add(linea.getString("id_vehiculo"));
                Random genAlea = new Random();
                switch (genAlea.nextInt(4)){
                    case 1:
                        vehiculos_data.add(new Vehiculo(R.drawable.busnegro,linea.getString("nombre_linea"),linea.getString("tipo")));
                        break;
                    case 2:
                        vehiculos_data.add(new Vehiculo(R.drawable.busblanco,linea.getString("nombre_linea"),linea.getString("tipo")));
                        break;
                    case 3:
                        vehiculos_data.add(new Vehiculo(R.drawable.busazul,linea.getString("nombre_linea"),linea.getString("tipo")));
                        break;
                    default:
                        vehiculos_data.add(new Vehiculo(R.drawable.busamarillo,linea.getString("nombre_linea"),linea.getString("tipo")));
                        break;

                }




            }

            Log.d("RESULTADO", "Se recogio correctamente el JSON, AHORA A CARGAR LA LISTVIEW");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListView lista = (ListView) actividadUI.findViewById(R.id.vistaLista);

                /* Se crea un adaptador con el el resultado del parsing
        que se realizó al arreglo JSON
         */



        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.actividadUI.getBaseContext(),android.R.layout.simple_list_item_1,vehiculos);

        AdaptadorVehiculo adapter = new AdaptadorVehiculo(actividadUI,R.layout.listview_item_row, vehiculos_data);
        // Relacionar adaptador a la lista
        lista.setAdapter(adapter);


    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}
