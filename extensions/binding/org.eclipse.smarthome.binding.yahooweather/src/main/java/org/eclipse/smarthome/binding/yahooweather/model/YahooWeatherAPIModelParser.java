package org.eclipse.smarthome.binding.yahooweather.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class YahooWeatherAPIModelParser {

    public YahooWeatherAPIModel parseFromJson(final String json) {
        if (json == null) {
            return null;
        }
        final Gson gson = new GsonBuilder().create();
        final YahooWeatherAPIModel apiResult = gson.fromJson(json, YahooWeatherAPIModel.class);
        return apiResult;
    }

}
