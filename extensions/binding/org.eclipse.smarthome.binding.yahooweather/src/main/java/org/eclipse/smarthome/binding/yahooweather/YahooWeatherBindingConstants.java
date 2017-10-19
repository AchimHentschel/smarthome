/**
 * Copyright (c) 2014-2017 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.yahooweather;

import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link YahooWeatherBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Kai Kreuzer - Initial contribution
 * @author Achim Hentschel - added some more channels fetched from yahoo weather API
 */
public class YahooWeatherBindingConstants {

    @NonNull
    public static final String BINDING_ID = "yahooweather";

    // List all Thing Type UIDs, related to the YahooWeather Binding
    public static final ThingTypeUID THING_TYPE_WEATHER = new ThingTypeUID(BINDING_ID, "weather");
    public static final ThingTypeUID THING_TYPE_WEATHER_EXTENSION = new ThingTypeUID(BINDING_ID, "weatherex");

    // List all channels
    public static final String CHANNEL_TEMPERATURE = "temperature";
    public static final String CHANNEL_HUMIDITY = "humidity";
    public static final String CHANNEL_PRESSURE = "pressure";

    // channel extension / grouping
    public static final String CHANNEL_UNITS_DISTANCE = "units#distance";
    public static final String CHANNEL_UNITS_PRESSURE = "units#pressure";
    public static final String CHANNEL_UNITS_SPEED = "units#speed";
    public static final String CHANNEL_UNITS_TEMPERATURE = "units#temperature";

    public static final String CHANNEL_WIND_CHILL = "wind#chill";
    public static final String CHANNEL_WIND_DIRECTION = "wind#direction";
    public static final String CHANNEL_WIND_SPEED = "wind#speed";

    public static final String CHANNEL_ASTRONOMY_SUNRISE = "astronomy#sunrise";
    public static final String CHANNEL_ASTRONOMY_SUNSET = "astronomy#sunset";

    public static final String CHANNEL_ATMOSPHERE_RISING = "atmosphere#rising";
    public static final String CHANNEL_ATMOSPHERE_HUMIDITY = "atmosphere#humidity";
    public static final String CHANNEL_ATMOSPHERE_PRESSURE = "atmosphere#pressure";
    public static final String CHANNEL_ATMOSPHERE_VISIBILITY = "atmosphere#visibility";

    public static final String CHANNEL_LOCATION_CITY = "location#city";
    public static final String CHANNEL_LOCATION_COUNTRY = "location#country";
    public static final String CHANNEL_LOCATION_REGION = "location#region";
    public static final String CHANNEL_LOCATION_LATITUDE = "location#latitude";
    public static final String CHANNEL_LOCATION_LONGITUDE = "location#longitude";

    public static final String CHANNEL_MISC_PUBLICATION_DATE = "misc#publicationDate";
    public static final String CHANNEL_MISC_LANGUAGE_CODE = "misc#languageCode";

    public static final String CHANNEL_CONDITION_CODE = "condition#code";
    public static final String CHANNEL_CONDITION_DATE = "condition#date";
    public static final String CHANNEL_CONDITION_TEMPERATURE = "condition#temperature";
    public static final String CHANNEL_CONDITION_TEXT = "condition#text";

    public static final String CHANNEL_FORECAST_CODE = "forecast#code";
    public static final String CHANNEL_FORECAST_DATE = "forecast#date";
    public static final String CHANNEL_FORECAST_WEEKDAY = "forecast#weekday";
    public static final String CHANNEL_FORECAST_MIN = "forecast#min";
    public static final String CHANNEL_FORECAST_MAX = "forecast#max";
    public static final String CHANNEL_FORECAST_TEXT = "forecast#text";

    public static final Pattern PATTERN_FORECAST = Pattern.compile("^(forecast)(/d+)#(.*)$");
    public static final int PATTERN_INDEX_CHANNEL_GROUP_NAME = 1;
    public static final int PATTERN_INDEX_FORECAST_INDEX = 2;
    public static final int PATTERN_INDEX_FORECAST_FIELD = 3;

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.singleton(THING_TYPE_WEATHER);
}
