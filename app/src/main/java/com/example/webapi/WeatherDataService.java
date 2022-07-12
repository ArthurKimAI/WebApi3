package com.example.webapi;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class WeatherDataService {

    public static final String API_KEY = "2423b5db0ac4cfc6c484a81c1e2950a8";
    public static final String WEATHER_Q = "https://api.openweathermap.org/data/2.5/weather?q=";
    public static final String QUERY_FOR_FORECAST_BY_ID = "https://api.openweathermap.org/data/2.5/weather?id=";
    public static final String APPID = "&appid=";

    Context context;
    String cityID;

    public WeatherDataService(Context context) {
        this.context = context;
    }

    public interface VolleyResponseListener {
        void onError(String message);

        void onResponse(String cityID);
    }


    public void getCityID(String cityName, VolleyResponseListener volleyResponseListener){
        String url = WEATHER_Q + cityName + APPID + API_KEY;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                cityID = "";
                try {
                    JSONObject cityInfo = response.getJSONObject(0);
                    cityID = cityInfo.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                Toast.makeText(context, "CityID " + cityID, Toast.LENGTH_SHORT).show();
                volleyResponseListener.onResponse(cityID);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(context, "Error Occurred",Toast.LENGTH_SHORT).show();
                volleyResponseListener.onError("Something Wrong");
            }
        });
        RequestSingleton.getInstance(context).addToRequestQueue(request);
//        return cityID;
    }

    public void getForecastByID(String cityID){

        List<WeatherReportModel> report = new ArrayList<>();

        String url = QUERY_FOR_FORECAST_BY_ID + cityID + APPID + API_KEY;
        //get the json object
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(context, response.toString(), Toast.LENGTH_SHORT).show();


                try {
                    JSONArray consolidated_weather_list = response.getJSONArray("consolidated_weather");
                    WeatherReportModel first_Day = new WeatherReportModel();

                    JSONObject first_day_forecast = (JSONObject) consolidated_weather_list.get(0);
                    first_Day.setId(first_day_forecast.getInt("id"));
                    first_Day.setHumidity(first_day_forecast.getInt("Humidity"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){

            }
        });
        //get the property called "consolidated_weather" which is an array

        //get each item in the array and assign it to a new WeatherReportModel object
        RequestSingleton.getInstance(context).addToRequestQueue(request);
    }
}
