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

import org.eclipse.smarthome.core.thing.ThingTypeUID;

/**
 * The {@link YahooWeatherBinding} class defines common constants, which are
 * used across the whole binding.
 *
 * @author Kai Kreuzer - Initial contribution
 * @author Achim Hentschel - added some more channels fetched from yahoo weather API
 */
public class YahooWeatherBindingConstants {

    public static final String BINDING_ID = "yahooweather";

    // List all Thing Type UIDs, related to the YahooWeather Binding
    public static final ThingTypeUID THING_TYPE_WEATHER = new ThingTypeUID(BINDING_ID, "weather");

    // List all channels
    public static final String CHANNEL_HUMIDITY = "atmosphereHumidity";
    public static final String CHANNEL_PRESSURE = "atmospherePressure";
    public static final String CHANNEL_VISIBILITY = "atmosphereVisibility";

    public static final String CHANNEL_LOCATION_CITY = "locationCity";
    public static final String CHANNEL_LOCATION_COUNTRY = "locationCountry";
    public static final String CHANNEL_LOCATION_REGION = "locationRegion";

    public static final String CHANNEL_WIND_CHILL = "windChill";
    public static final String CHANNEL_WIND_DIRECTION = "windDirection";
    public static final String CHANNEL_WIND_SPEED = "windSpeed";

    public static final String CHANNEL_TEMPERATURE = "conditionTemp";

    public static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.singleton(THING_TYPE_WEATHER);
}
