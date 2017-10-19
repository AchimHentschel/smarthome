/**
 * Copyright (c) 2014-2017 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.yahooweather.internal;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.smarthome.binding.yahooweather.YahooWeatherBindingConstants;
import org.eclipse.smarthome.binding.yahooweather.handler.YahooWeatherHandler;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;

/**
 * The {@link YahooWeatherHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Kai Kreuzer - Initial contribution
 * @author Achim Hentschel - switched from constant SUPPORTED_THING_TYPES_UIDS to lazy initialized set to support
 *         multiple things using the same handler
 */
public class YahooWeatherHandlerFactory extends BaseThingHandlerFactory {

    private Set<ThingTypeUID> supportedThingTypeUIDsSet = null;

    private Set<ThingTypeUID> getSupportedThingTypeUIDsSet() {
        if (supportedThingTypeUIDsSet == null) {
            supportedThingTypeUIDsSet = new HashSet<>();
            supportedThingTypeUIDsSet.add(YahooWeatherBindingConstants.THING_TYPE_WEATHER);
            supportedThingTypeUIDsSet.add(YahooWeatherBindingConstants.THING_TYPE_WEATHER_EXTENSION);
        }
        return supportedThingTypeUIDsSet;
    }

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return getSupportedThingTypeUIDsSet().contains(thingTypeUID);
    }

    @Override
    protected ThingHandler createHandler(Thing thing) {

        ThingTypeUID thingTypeUID = thing.getThingTypeUID();

        if (getSupportedThingTypeUIDsSet().contains(thingTypeUID)) {
            return new YahooWeatherHandler(thing);
        }

        return null;
    }
}
