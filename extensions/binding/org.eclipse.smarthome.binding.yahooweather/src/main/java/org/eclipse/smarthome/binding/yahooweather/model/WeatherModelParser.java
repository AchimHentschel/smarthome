package org.eclipse.smarthome.binding.yahooweather.model;

public interface WeatherModelParser {

    WeatherModel parseFromJson(String json);

}
