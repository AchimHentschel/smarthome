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
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.eclipse.smarthome.binding.yahooweather.internal.connection.YahooWeatherConnection;
import org.eclipse.smarthome.config.core.Configuration;
import org.eclipse.smarthome.config.core.status.ConfigStatusMessage;
import org.eclipse.smarthome.core.cache.ExpiringCacheMap;
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

    private String weatherData = null;

    ScheduledFuture<?> refreshJob;

    public YahooWeatherHandler(Thing thing) {
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

    private void startAutomaticRefresh() {
        refreshJob = scheduler.scheduleWithFixedDelay(() -> {
            try {
                boolean success = updateWeatherData();
                if (success) {
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_TEMPERATURE), getTemperature());
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_HUMIDITY), getHumidity());
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_PRESSURE), getPressure());

                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_VISIBILITY), getVisibility());
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_LOCATION_CITY), getLocationCity());
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_LOCATION_COUNTRY), getLocationCountry());
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_LOCATION_REGION), getLocationRegion());
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_WIND_CHILL), getWindChill());
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_WIND_DIRECTION), getWindDirection());
                    updateState(new ChannelUID(getThing().getUID(), CHANNEL_WIND_SPEED), getWindSpeed());
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
                switch (channelUID.getId()) {
                    case CHANNEL_TEMPERATURE:
                        updateState(channelUID, getTemperature());
                        break;
                    case CHANNEL_HUMIDITY:
                        updateState(channelUID, getHumidity());
                        break;
                    case CHANNEL_PRESSURE:
                        updateState(channelUID, getPressure());
                        break;
                    case CHANNEL_VISIBILITY:
                        updateState(channelUID, getVisibility());
                        break;
                    case CHANNEL_LOCATION_CITY:
                        updateState(channelUID, getLocationCity());
                        break;
                    case CHANNEL_LOCATION_COUNTRY:
                        updateState(channelUID, getLocationCountry());
                        break;
                    case CHANNEL_LOCATION_REGION:
                        updateState(channelUID, getLocationRegion());
                        break;
                    case CHANNEL_WIND_CHILL:
                        updateState(channelUID, getWindChill());
                        break;
                    case CHANNEL_WIND_DIRECTION:
                        updateState(channelUID, getWindDirection());
                        break;
                    case CHANNEL_WIND_SPEED:
                        updateState(channelUID, getWindSpeed());
                        break;
                    default:
                        logger.debug("Command received for an unknown channel: {}", channelUID.getId());
                        break;
                }
            }
        } else {
            logger.debug("Command {} is not supported for channel: {}", command, channelUID.getId());
        }
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
                weatherData = data;
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

    private State getHumidity() {
        if (weatherData != null) {
            String humidity = getValue(weatherData, "atmosphere", "humidity");
            if (humidity != null) {
                return new DecimalType(humidity);
            }
        }
        return UnDefType.UNDEF;
    }

    private State getPressure() {
        if (weatherData != null) {
            String pressure = getValue(weatherData, "atmosphere", "pressure");
            if (pressure != null) {
                DecimalType ret = new DecimalType(pressure);
                if (ret.doubleValue() > 10000.0) {
                    // Unreasonably high, record so far was 1085,8 hPa
                    // The Yahoo API currently returns inHg values although it claims they are mbar - therefore convert
                    ret = new DecimalType(BigDecimal.valueOf((long) (ret.doubleValue() / 0.3386388158), 2));
                }
                return ret;
            }
        }
        return UnDefType.UNDEF;
    }

    private State getVisibility() {
        if (weatherData != null) {
            String visibility = getValue(weatherData, "atmosphere", "visibility");
            if (visibility != null) {
                return new DecimalType(visibility);
            }
        }
        return UnDefType.UNDEF;
    }

    private State getTemperature() {
        if (weatherData != null) {
            String temp = getValue(weatherData, "condition", "temp");
            if (temp != null) {
                return new DecimalType(temp);
            }
        }
        return UnDefType.UNDEF;
    }

    private State getLocationCity() {
        if (weatherData != null) {
            String city = getValue(weatherData, "location", "city");
            if (city != null) {
                return new StringType(city);
            }
        }
        return UnDefType.UNDEF;
    }

    private State getLocationCountry() {
        if (weatherData != null) {
            String country = getValue(weatherData, "location", "country");
            if (country != null) {
                return new StringType(country);
            }
        }
        return UnDefType.UNDEF;
    }

    private State getLocationRegion() {
        if (weatherData != null) {
            String region = getValue(weatherData, "location", "region");
            if (region != null) {
                return new StringType(region);
            }
        }
        return UnDefType.UNDEF;
    }

    private State getWindChill() {
        if (weatherData != null) {
            String chill = getValue(weatherData, "wind", "chill");
            if (chill != null) {
                DecimalType ret = new DecimalType(chill);
                // The Yahoo API currently returns °F values although it claims they are °C - therefore convert
                double farenheit = ret.doubleValue();
                double celsius = (farenheit - 32.0d) * 5.0d / 9.0d;
                ret = new DecimalType(BigDecimal.valueOf(celsius));
                return ret;
            }
        }
        return UnDefType.UNDEF;
    }

    private State getWindSpeed() {
        if (weatherData != null) {
            String windSpeed = getValue(weatherData, "wind", "speed");
            if (windSpeed != null) {
                return new DecimalType(windSpeed);
            }
        }
        return UnDefType.UNDEF;
    }

    private State getWindDirection() {
        if (weatherData != null) {
            String windDirection = getValue(weatherData, "wind", "direction");
            if (windDirection != null) {
                return new DecimalType(windDirection);
            }
        }
        return UnDefType.UNDEF;
    }

    private String getValue(String data, String element, String param) {
        String tmp = StringUtils.substringAfter(data, element);
        if (tmp != null) {
            return StringUtils.substringBetween(tmp, param + "\":\"", "\"");
        }
        return null;
    }
}
