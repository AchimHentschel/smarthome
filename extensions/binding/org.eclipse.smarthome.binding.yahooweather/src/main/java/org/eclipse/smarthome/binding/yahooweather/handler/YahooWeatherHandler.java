/**
 * Copyright (c) 2014-2017 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.yahooweather.handler;

import static org.eclipse.smarthome.binding.yahooweather.YahooWeatherBindingConstants.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.smarthome.binding.yahooweather.internal.connection.YahooWeatherConnection;
import org.eclipse.smarthome.binding.yahooweather.model.WeatherModel;
import org.eclipse.smarthome.binding.yahooweather.model.YahooWeatherAPIModel.Forecast;
import org.eclipse.smarthome.binding.yahooweather.model.YahooWeatherAPIModelParser;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.config.core.status.ConfigStatusMessage;
import org.eclipse.smarthome.core.cache.ExpiringCacheMap;
import org.eclipse.smarthome.core.library.types.DateTimeType;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.ConfigStatusThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.RefreshType;
import org.eclipse.smarthome.core.types.State;
import org.eclipse.smarthome.core.types.UnDefType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link YahooWeatherHandler} is responsible for handling commands, which are
 * sent to one of the channels.
 *
 * @author Kai Kreuzer - Initial contribution
 * @author Stefan Bußweiler - Integrate new thing status handling
 * @author Thomas Höfer - Added config status provider
 * @author Christoph Weitkamp - Changed use of caching utils to ESH ExpiringCacheMap
 * @author Achim Hentschel - added further channels provided by the yahoo weather API
 *
 */
public class YahooWeatherHandler extends ConfigStatusThingHandler {

    private static final String LOCATION_PARAM = "location";

    private final Logger logger = LoggerFactory.getLogger(YahooWeatherHandler.class);

    private static final int MAX_DATA_AGE = 3 * 60 * 60 * 1000; // 3h
    private static final int CACHE_EXPIRY = 10 * 1000; // 10s
    private static final String CACHE_KEY_CONFIG = "CONFIG_STATUS";
    private static final String CACHE_KEY_WEATHER = "WEATHER";

    private final ExpiringCacheMap<String, String> cache = new ExpiringCacheMap<>(CACHE_EXPIRY);

    private final YahooWeatherConnection connection = new YahooWeatherConnection();

    private long lastUpdateTime;

    private BigDecimal location;
    private BigDecimal refresh;

    private WeatherModel weatherData = null;

    ScheduledFuture<?> refreshJob;

    public YahooWeatherHandler(@NonNull Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        logger.debug("Initializing YahooWeather handler.");

        Configuration config = getThing().getConfiguration();

        location = (BigDecimal) config.get(LOCATION_PARAM);

        try {
            refresh = (BigDecimal) config.get("refresh");
        } catch (Exception e) {
            logger.debug("Cannot set refresh parameter.", e);
        }

        if (refresh == null) {
            // let's go for the default
            refresh = new BigDecimal(60);
        }

        cache.put(CACHE_KEY_CONFIG, () -> connection.getResponseFromQuery(
                "SELECT location FROM weather.forecast WHERE woeid = " + location.toPlainString()));
        cache.put(CACHE_KEY_WEATHER, () -> connection.getResponseFromQuery(
                "SELECT * FROM weather.forecast WHERE u = 'c' AND woeid = " + location.toPlainString()));

        startAutomaticRefresh();
    }

    @Override
    public void dispose() {
        refreshJob.cancel(true);
    }

    @Override
    public Collection<ConfigStatusMessage> getConfigStatus() {
        Collection<ConfigStatusMessage> configStatus = new ArrayList<>();

        final String locationData = cache.get(CACHE_KEY_CONFIG);
        if (locationData != null) {
            String city = getValue(locationData, "location", "city");
            if (city == null) {
                configStatus.add(ConfigStatusMessage.Builder.error(LOCATION_PARAM)
                        .withMessageKeySuffix("location-not-found").withArguments(location.toPlainString()).build());
            }
        }

        return configStatus;
    }

    private synchronized boolean updateWeatherData() {
        final String data = cache.get(CACHE_KEY_WEATHER);
        if (data != null) {
            if (data.contains("\"results\":null")) {
                if (isCurrentDataExpired()) {
                    logger.trace(
                            "The Yahoo Weather API did not return any data. Omiting the old result because it became too old.");
                    weatherData = null;
                    updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.COMMUNICATION_ERROR,
                            "@text/offline.no-data");
                    return false;
                } else {
                    // simply keep the old data
                    logger.trace("The Yahoo Weather API did not return any data. Keeping the old result.");
                    return false;
                }
            } else {
                lastUpdateTime = System.currentTimeMillis();
                weatherData = (new YahooWeatherAPIModelParser()).parseFromJson(data);
            }
            updateStatus(ThingStatus.ONLINE);
            return true;
        }
        weatherData = null;
        updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.COMMUNICATION_ERROR,
                "@text/offline.location [\"" + location.toPlainString() + "\"");
        return false;
    }

    private boolean isCurrentDataExpired() {
        return lastUpdateTime + MAX_DATA_AGE < System.currentTimeMillis();
    }

    private void startAutomaticRefresh() {
        refreshJob = scheduler.scheduleWithFixedDelay(() -> {
            try {
                boolean success = updateWeatherData();
                if (success) {
                    // classic channels
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_TEMPERATURE), getConditionTemperature());
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_HUMIDITY), getAtmosphereHumidity());
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_PRESSURE), getAtmospherePressure());

                    // unit channels
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_UNITS_DISTANCE), getUnitsDistance());
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_UNITS_PRESSURE), getUnitsPressure());
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_UNITS_SPEED), getUnitsSpeed());
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_UNITS_TEMPERATURE), getUnitsTemperature());

                    // wind channels
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_WIND_CHILL), getWindChill());
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_WIND_DIRECTION), getWindDirection());
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_WIND_SPEED), getWindSpeed());

                    // astronomy channels
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_ASTRONOMY_SUNRISE), getAstronomySunrise());
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_ASTRONOMY_SUNSET), getAstronomySunset());

                    // atmosphere channels
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_ATMOSPHERE_RISING), getAtmosphereRising());
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_ATMOSPHERE_HUMIDITY),
                            getAtmosphereHumidity());
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_ATMOSPHERE_PRESSURE),
                            getAtmospherePressure());
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_ATMOSPHERE_VISIBILITY),
                            getAtmosphereVisibility());

                    // location channels
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_LOCATION_CITY), getLocationCity());
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_LOCATION_COUNTRY), getLocationCountry());
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_LOCATION_REGION), getLocationRegion());

                    // misc channels
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_MISC_PUBLICATION_DATE),
                            getMiscPublicationDate());
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_MISC_LANGUAGE_CODE), getMiscLanguageCode());

                    // condition channels
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_CONDITION_CODE), getConditionCode());
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_CONDITION_DATE), getConditionDate());
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_CONDITION_TEMPERATURE),
                            getConditionTemperature());
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_CONDITION_TEXT), getConditionText());

                    for (int forecastId = 1; forecastId <= 10; ++forecastId) {
                        // forecast channels
                        // forecast channel constants do not contain the forecast ID - so it has to be reinserted into
                        // the string via replacement of # to #1-#10
                        // IDEA: it would be possible to have a constant for every single channel and map all 10
                        // constants to one case below... would not be that readable though
                        updateState(
                                new ChannelUID(getThing().getUID(),
                                        CHANNEL_FORECAST_CODE.replace("#", String.valueOf(forecastId) + "#")),
                                getForecastCode(forecastId));
                        updateState(
                                new ChannelUID(getThing().getUID(),
                                        CHANNEL_FORECAST_DATE.replace("#", String.valueOf(forecastId) + "#")),
                                getForecastDate(forecastId));
                        updateState(
                                new ChannelUID(getThing().getUID(),
                                        CHANNEL_FORECAST_WEEKDAY.replace("#", String.valueOf(forecastId) + "#")),
                                getForecastWeekday(forecastId));
                        updateState(
                                new ChannelUID(getThing().getUID(),
                                        CHANNEL_FORECAST_MIN.replace("#", String.valueOf(forecastId) + "#")),
                                getForecastMin(forecastId));
                        updateState(
                                new ChannelUID(getThing().getUID(),
                                        CHANNEL_FORECAST_MAX.replace("#", String.valueOf(forecastId) + "#")),
                                getForecastMax(forecastId));
                        updateState(
                                new ChannelUID(getThing().getUID(),
                                        CHANNEL_FORECAST_TEXT.replace("#", String.valueOf(forecastId) + "#")),
                                getForecastText(forecastId));
                    }

                }
            } catch (Exception e) {
                logger.debug("Exception occurred during execution: {}", e.getMessage(), e);
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.COMMUNICATION_ERROR, e.getMessage());
            }
        }, 0, refresh.intValue(), TimeUnit.SECONDS);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        if (command instanceof RefreshType) {
            boolean success = updateWeatherData();
            if (success) {
                // check if channel ID is a forecast group channel and extract forecast index and field
                final Matcher forecastMatcher = PATTERN_FORECAST.matcher(channelUID.getId());

                final Integer forecastId;
                final String channelId;
                if (forecastMatcher.matches() && forecastMatcher.groupCount() == 3) {
                    forecastId = Integer.valueOf(forecastMatcher.group(PATTERN_INDEX_FORECAST_INDEX));
                    channelId = forecastMatcher.group(PATTERN_INDEX_CHANNEL_GROUP_NAME) + "#"
                            + forecastMatcher.group(PATTERN_INDEX_FORECAST_FIELD);
                } else {
                    forecastId = null;
                    channelId = channelUID.getId();
                }

                switch (channelId) {
                    // classic channels
                    case CHANNEL_TEMPERATURE:
                        updateState(channelUID, getConditionTemperature());
                        break;
                    case CHANNEL_HUMIDITY:
                        updateState(channelUID, getAtmosphereHumidity());
                        break;
                    case CHANNEL_PRESSURE:
                        updateState(channelUID, getAtmospherePressure());
                        break;

                    // unit channels
                    case CHANNEL_UNITS_DISTANCE:
                        updateState(channelUID, getUnitsDistance());
                        break;
                    case CHANNEL_UNITS_PRESSURE:
                        updateState(channelUID, getUnitsPressure());
                        break;
                    case CHANNEL_UNITS_SPEED:
                        updateState(channelUID, getUnitsSpeed());
                        break;
                    case CHANNEL_UNITS_TEMPERATURE:
                        updateState(channelUID, getUnitsTemperature());
                        break;

                    // wind channels
                    case CHANNEL_WIND_CHILL:
                        updateState(channelUID, getWindChill());
                        break;
                    case CHANNEL_WIND_DIRECTION:
                        updateState(channelUID, getWindDirection());
                        break;
                    case CHANNEL_WIND_SPEED:
                        updateState(channelUID, getWindSpeed());
                        break;

                    // astronomy channels
                    case CHANNEL_ASTRONOMY_SUNRISE:
                        updateState(channelUID, getAstronomySunrise());
                        break;
                    case CHANNEL_ASTRONOMY_SUNSET:
                        updateState(channelUID, getAstronomySunset());
                        break;

                    // atmosphere channels
                    case CHANNEL_ATMOSPHERE_RISING:
                        updateState(channelUID, getAtmosphereRising());
                        break;
                    case CHANNEL_ATMOSPHERE_HUMIDITY:
                        updateState(channelUID, getAtmosphereHumidity());
                        break;
                    case CHANNEL_ATMOSPHERE_PRESSURE:
                        updateState(channelUID, getAtmospherePressure());
                        break;
                    case CHANNEL_ATMOSPHERE_VISIBILITY:
                        updateState(channelUID, getAtmosphereVisibility());
                        break;

                    // location channels
                    case CHANNEL_LOCATION_CITY:
                        updateState(channelUID, getLocationCity());
                        break;
                    case CHANNEL_LOCATION_COUNTRY:
                        updateState(channelUID, getLocationCountry());
                        break;
                    case CHANNEL_LOCATION_REGION:
                        updateState(channelUID, getLocationRegion());
                        break;
                    case CHANNEL_LOCATION_LATITUDE:
                        updateState(channelUID, getLocationLatitude());
                        break;
                    case CHANNEL_LOCATION_LONGITUDE:
                        updateState(channelUID, getLocationLongitude());
                        break;

                    // misc channels
                    case CHANNEL_MISC_PUBLICATION_DATE:
                        updateState(channelUID, getMiscPublicationDate());
                        break;
                    case CHANNEL_MISC_LANGUAGE_CODE:
                        updateState(channelUID, getMiscLanguageCode());
                        break;

                    // condition channels
                    case CHANNEL_CONDITION_CODE:
                        updateState(channelUID, getConditionCode());
                        break;
                    case CHANNEL_CONDITION_DATE:
                        updateState(channelUID, getConditionDate());
                        break;
                    case CHANNEL_CONDITION_TEMPERATURE:
                        updateState(channelUID, getConditionTemperature());
                        break;
                    case CHANNEL_CONDITION_TEXT:
                        updateState(channelUID, getConditionText());
                        break;

                    // forecast channels
                    case CHANNEL_FORECAST_CODE:
                        updateState(channelUID, getForecastCode(forecastId));
                        break;
                    case CHANNEL_FORECAST_DATE:
                        updateState(channelUID, getForecastDate(forecastId));
                        break;
                    case CHANNEL_FORECAST_WEEKDAY:
                        updateState(channelUID, getForecastWeekday(forecastId));
                        break;
                    case CHANNEL_FORECAST_MIN:
                        updateState(channelUID, getForecastMin(forecastId));
                        break;
                    case CHANNEL_FORECAST_MAX:
                        updateState(channelUID, getForecastMax(forecastId));
                        break;
                    case CHANNEL_FORECAST_TEXT:
                        updateState(channelUID, getForecastText(forecastId));
                        break;

                    // unknown channel ID handling
                    default:
                        logger.debug("Command received for an unknown channel: {}", channelUID.getId());
                        break;
                }
            }
        } else {
            logger.debug("Command {} is not supported for channel: {}", command, channelUID.getId());
        }
    }

    private State constructDecimalType(BigDecimal decimal) {
        if (decimal == null) {
            return UnDefType.UNDEF;
        }
        return new DecimalType(decimal);
    }

    private State constructDateTimeType(LocalTime localTime) {
        if (localTime == null) {
            return UnDefType.UNDEF;
        }
        final ZonedDateTime zonedDateTime = LocalDateTime.of(LocalDate.now(), localTime).atZone(ZoneId.systemDefault());
        return new DateTimeType(GregorianCalendar.from(zonedDateTime));
    }

    private State constructDateTimeType(LocalDate localDate) {
        if (localDate == null) {
            return UnDefType.UNDEF;
        }
        final ZonedDateTime zonedDateTime = LocalDateTime.of(localDate, LocalTime.of(0, 0))
                .atZone(ZoneId.systemDefault());
        return new DateTimeType(GregorianCalendar.from(zonedDateTime));
    }

    private State constructDateTimeType(ZonedDateTime zonedDateTime) {
        if (zonedDateTime == null) {
            return UnDefType.UNDEF;
        }
        return new DateTimeType(GregorianCalendar.from(zonedDateTime));
    }

    private State constructStringType(String value) {
        if (value == null) {
            return UnDefType.UNDEF;
        }
        return new StringType(value);
    }

    private Forecast getForecast(Integer forecastId) {
        if (weatherData == null) {
            return null;
        }
        final List<Forecast> forecasts = weatherData.getForecasts();
        if (forecastId < 1 || forecastId > forecasts.size()) {
            return null;
        }
        return forecasts.get(forecastId - 1);
    }

    private State getForecastText(final Integer forecastId) {
        final Forecast forecast = getForecast(forecastId);
        if (weatherData == null || forecast == null) {
            return UnDefType.UNDEF;
        }
        return constructStringType(forecast.getText());
    }

    private State getForecastMax(final Integer forecastId) {
        final Forecast forecast = getForecast(forecastId);
        if (weatherData == null || forecast == null) {
            return UnDefType.UNDEF;
        }
        return constructDecimalType(forecast.getHighTemperature());
    }

    private State getForecastMin(final Integer forecastId) {
        final Forecast forecast = getForecast(forecastId);
        if (weatherData == null || forecast == null) {
            return UnDefType.UNDEF;
        }
        return constructDecimalType(forecast.getLowTemperatur());
    }

    private State getForecastWeekday(final Integer forecastId) {
        final Forecast forecast = getForecast(forecastId);
        if (weatherData == null || forecast == null) {
            return UnDefType.UNDEF;
        }
        return constructStringType(forecast.getDay());
    }

    private State getForecastDate(final Integer forecastId) {
        final Forecast forecast = getForecast(forecastId);
        if (weatherData == null || forecast == null) {
            return UnDefType.UNDEF;
        }
        return constructDateTimeType(forecast.getDate());
    }

    private State getForecastCode(final Integer forecastId) {
        final Forecast forecast = getForecast(forecastId);
        if (weatherData == null || forecast == null) {
            return UnDefType.UNDEF;
        }
        return constructDecimalType(forecast.getCode());
    }

    private State getLocationLongitude() {
        if (weatherData == null) {
            return UnDefType.UNDEF;
        }
        return constructDecimalType(weatherData.getLongitude());
    }

    private State getLocationLatitude() {
        if (weatherData == null) {
            return UnDefType.UNDEF;
        }
        return constructDecimalType(weatherData.getLatitude());
    }

    private State getAstronomySunset() {
        if (weatherData == null) {
            return UnDefType.UNDEF;
        }
        return constructDateTimeType(weatherData.getAstronomy().getSunset());
    }

    private State getAstronomySunrise() {
        if (weatherData == null) {
            return UnDefType.UNDEF;
        }
        return constructDateTimeType(weatherData.getAstronomy().getSunrise());
    }

    private State getMiscLanguageCode() {
        if (weatherData == null) {
            return UnDefType.UNDEF;
        }
        return constructStringType(weatherData.getLanguage());
    }

    private State getMiscPublicationDate() {
        if (weatherData == null) {
            return UnDefType.UNDEF;
        }
        return constructDateTimeType(weatherData.getPublicationDate());
    }

    private State getUnitsTemperature() {
        if (weatherData == null) {
            return UnDefType.UNDEF;
        }
        return constructStringType(weatherData.getUnits().getTemperature());
    }

    private State getUnitsSpeed() {
        if (weatherData == null) {
            return UnDefType.UNDEF;
        }
        return constructStringType(weatherData.getUnits().getSpeed());
    }

    private State getUnitsPressure() {
        if (weatherData == null) {
            return UnDefType.UNDEF;
        }
        return constructStringType(weatherData.getUnits().getPressure());
    }

    private State getUnitsDistance() {
        if (weatherData == null) {
            return UnDefType.UNDEF;
        }
        return constructStringType(weatherData.getUnits().getDistance());
    }

    private State getAtmosphereRising() {
        if (weatherData == null) {
            return UnDefType.UNDEF;
        }
        return constructDecimalType(weatherData.getAtmosphere().getRising());
    }

    private State getAtmosphereHumidity() {
        if (weatherData == null) {
            return UnDefType.UNDEF;
        }
        return constructDecimalType(weatherData.getAtmosphere().getHumidity());
    }

    private State getAtmospherePressure() {
        if (weatherData == null) {
            return UnDefType.UNDEF;
        }
        return constructDecimalType(weatherData.getAtmosphere().getPressureInHPa());
    }

    private State getAtmosphereVisibility() {
        if (weatherData == null) {
            return UnDefType.UNDEF;
        }
        return constructDecimalType(weatherData.getAtmosphere().getVisibility());

    }

    private State getConditionText() {
        if (weatherData == null) {
            return UnDefType.UNDEF;
        }
        return constructStringType(weatherData.getCondition().getText());
    }

    private State getConditionDate() {
        if (weatherData == null) {
            return UnDefType.UNDEF;
        }
        return constructDateTimeType(weatherData.getCondition().getDate());
    }

    private State getConditionCode() {
        if (weatherData == null) {
            return UnDefType.UNDEF;
        }
        return constructDecimalType(weatherData.getCondition().getCode());
    }

    private State getConditionTemperature() {
        if (weatherData == null) {
            return UnDefType.UNDEF;
        }
        return constructDecimalType(weatherData.getCondition().getTemperature());
    }

    private State getLocationCity() {
        if (weatherData == null) {
            return UnDefType.UNDEF;
        }
        return constructStringType(weatherData.getLocation().getCity());
    }

    private State getLocationCountry() {
        if (weatherData == null) {
            return UnDefType.UNDEF;
        }
        return constructStringType(weatherData.getLocation().getCountry());
    }

    private State getLocationRegion() {
        if (weatherData == null) {
            return UnDefType.UNDEF;
        }
        return constructStringType(weatherData.getLocation().getRegion());
    }

    private State getWindChill() {
        if (weatherData == null) {
            return UnDefType.UNDEF;
        }
        return constructDecimalType(weatherData.getWind().getChillInDegreesCelsius());
    }

    private State getWindSpeed() {
        if (weatherData == null) {
            return UnDefType.UNDEF;
        }
        return constructDecimalType(weatherData.getWind().getSpeed());
    }

    private State getWindDirection() {
        if (weatherData == null) {
            return UnDefType.UNDEF;
        }
        return constructDecimalType(weatherData.getWind().getDirection());
    }

    private String getValue(String data, String element, String param) {
        String tmp = StringUtils.substringAfter(data, element);
        if (tmp != null) {
            return StringUtils.substringBetween(tmp, param + "\":\"", "\"");
        }
        return null;
    }
}
