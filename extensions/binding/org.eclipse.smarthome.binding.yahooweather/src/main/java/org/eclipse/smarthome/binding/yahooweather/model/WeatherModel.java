package org.eclipse.smarthome.binding.yahooweather.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import org.eclipse.smarthome.binding.yahooweather.model.YahooWeatherAPIModel.Astronomy;
import org.eclipse.smarthome.binding.yahooweather.model.YahooWeatherAPIModel.Atmosphere;
import org.eclipse.smarthome.binding.yahooweather.model.YahooWeatherAPIModel.Condition;
import org.eclipse.smarthome.binding.yahooweather.model.YahooWeatherAPIModel.Forecast;
import org.eclipse.smarthome.binding.yahooweather.model.YahooWeatherAPIModel.Location;
import org.eclipse.smarthome.binding.yahooweather.model.YahooWeatherAPIModel.Units;
import org.eclipse.smarthome.binding.yahooweather.model.YahooWeatherAPIModel.Wind;

public interface WeatherModel {

    ZonedDateTime getCreated();

    String getLanguage();

    Atmosphere getAtmosphere();

    Wind getWind();

    Astronomy getAstronomy();

    Location getLocation();

    Units getUnits();

    BigDecimal getLatitude();

    BigDecimal getLongitude();

    ZonedDateTime getPublicationDate();

    Condition getCondition();

    List<Forecast> getForecasts();

}
