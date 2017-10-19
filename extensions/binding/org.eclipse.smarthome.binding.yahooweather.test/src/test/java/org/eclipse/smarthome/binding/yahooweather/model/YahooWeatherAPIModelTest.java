package org.eclipse.smarthome.binding.yahooweather.model;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.eclipse.smarthome.binding.yahooweather.model.YahooWeatherAPIModel.Astronomy;
import org.eclipse.smarthome.binding.yahooweather.model.YahooWeatherAPIModel.Atmosphere;
import org.eclipse.smarthome.binding.yahooweather.model.YahooWeatherAPIModel.Condition;
import org.eclipse.smarthome.binding.yahooweather.model.YahooWeatherAPIModel.Forecast;
import org.eclipse.smarthome.binding.yahooweather.model.YahooWeatherAPIModel.Location;
import org.eclipse.smarthome.binding.yahooweather.model.YahooWeatherAPIModel.Units;
import org.eclipse.smarthome.binding.yahooweather.model.YahooWeatherAPIModel.Wind;
import org.junit.Test;

/**
 * Tests for API result after mapping it to object via Gson
 *
 * @author Achim Hentschel
 *
 */
public class YahooWeatherAPIModelTest {

    // @formatter:off
    private String DEMO_API_RESULT =
            "{\"query\":{\""
            +   "count\":1"
            +   ",\"created\":\"2017-10-17T10:21:16Z\""
            +   ",\"lang\":\"en-us\""
            +   ",\"results\":{\""
            +       "channel\":{\""
            +           "units\":{\"distance\":\"km\",\"pressure\":\"mb\",\"speed\":\"km/h\",\"temperature\":\"C\"}"
            +           ",\"title\":\"Yahoo! Weather - Berlin, BE, DE\""
            +           ",\"link\":\"http://us.rd.yahoo.com/dailynews/rss/weather/Country__Country/*https://weather.yahoo.com/country/state/city-638242/\""
            +           ",\"description\":\"Yahoo! Weather for Berlin, BE, DE\""
            +           ",\"language\":\"en-us\""
            +           ",\"lastBuildDate\":\"Tue, 17 Oct 2017 12:21 PM CEST\""
            +           ",\"ttl\":\"60\""
            +           ",\"location\":{\"city\":\"Berlin\",\"country\":\"Germany\",\"region\":\" BE\"}"
            +           ",\"wind\":{\"chill\":\"66\",\"direction\":\"250\",\"speed\":\"11.27\"}"
            +           ",\"atmosphere\":{\"humidity\":\"62\",\"pressure\":\"34405.72\",\"rising\":\"0\",\"visibility\":\"25.91\"}"
            +           ",\"astronomy\":{\"sunrise\":\"7:36 am\",\"sunset\":\"6:6 pm\"}"
            +           ",\"image\":{\"title\":\"Yahoo! Weather\",\"width\":\"142\",\"height\":\"18\",\"link\":\"http://weather.yahoo.com\",\"url\":\"http://l.yimg.com/a/i/brand/purplelogo//uh/us/news-wea.gif\"}"
            +           ",\"item\":{"
            +               "\"title\":\"Conditions for Berlin, BE, DE at 11:00 AM CEST\""
            +               ",\"lat\":\"52.516071\""
            +               ",\"long\":\"13.37698\""
            +               ",\"link\":\"http://us.rd.yahoo.com/dailynews/rss/weather/Country__Country/*https://weather.yahoo.com/country/state/city-638242/\""
            +               ",\"pubDate\":\"Tue, 17 Oct 2017 11:00 AM CEST\""
            +               ",\"condition\":{\"code\":\"32\",\"date\":\"Tue, 17 Oct 2017 11:00 AM CEST\",\"temp\":\"18\",\"text\":\"Sunny\"}"
            +               ",\"forecast\":["
            +                   "{\"code\":\"34\",\"date\":\"17 Oct 2017\",\"day\":\"Tue\",\"high\":\"21\",\"low\":\"13\",\"text\":\"Mostly Sunny\"}"
            +                   ",{\"code\":\"30\",\"date\":\"18 Oct 2017\",\"day\":\"Wed\",\"high\":\"18\",\"low\":\"11\",\"text\":\"Partly Cloudy\"}"
            +                   ",{\"code\":\"30\",\"date\":\"19 Oct 2017\",\"day\":\"Thu\",\"high\":\"17\",\"low\":\"10\",\"text\":\"Partly Cloudy\"}"
            +                   ",{\"code\":\"28\",\"date\":\"20 Oct 2017\",\"day\":\"Fri\",\"high\":\"17\",\"low\":\"10\",\"text\":\"Mostly Cloudy\"}"
            +                   ",{\"code\":\"28\",\"date\":\"21 Oct 2017\",\"day\":\"Sat\",\"high\":\"15\",\"low\":\"11\",\"text\":\"Mostly Cloudy\"}"
            +                   ",{\"code\":\"28\",\"date\":\"22 Oct 2017\",\"day\":\"Sun\",\"high\":\"13\",\"low\":\"10\",\"text\":\"Mostly Cloudy\"}"
            +                   ",{\"code\":\"28\",\"date\":\"23 Oct 2017\",\"day\":\"Mon\",\"high\":\"11\",\"low\":\"8\",\"text\":\"Mostly Cloudy\"}"
            +                   ",{\"code\":\"28\",\"date\":\"24 Oct 2017\",\"day\":\"Tue\",\"high\":\"12\",\"low\":\"7\",\"text\":\"Mostly Cloudy\"}"
            +                   ",{\"code\":\"28\",\"date\":\"25 Oct 2017\",\"day\":\"Wed\",\"high\":\"12\",\"low\":\"8\",\"text\":\"Mostly Cloudy\"}"
            +                   ",{\"code\":\"28\",\"date\":\"26 Oct 2017\",\"day\":\"Thu\",\"high\":\"12\",\"low\":\"8\",\"text\":\"Mostly Cloudy\"}"
            +               "]"
            +               ",\"description\":\"<![CDATA[<img src=\\\"http://l.yimg.com/a/i/us/we/52/32.gif\\\"/>\\n<BR />\\n<b>Current Conditions:</b>\\n<BR />Sunny\\n<BR />\\n<BR />\\n<b>Forecast:</b>\\n<BR /> Tue - Mostly Sunny. High: 21Low: 13\\n<BR /> Wed - Partly Cloudy. High: 18Low: 11\\n<BR /> Thu - Partly Cloudy. High: 17Low: 10\\n<BR /> Fri - Mostly Cloudy. High: 17Low: 10\\n<BR /> Sat - Mostly Cloudy. High: 15Low: 11\\n<BR />\\n<BR />\\n<a href=\\\"http://us.rd.yahoo.com/dailynews/rss/weather/Country__Country/*https://weather.yahoo.com/country/state/city-638242/\\\">Full Forecast at Yahoo! Weather</a>\\n<BR />\\n<BR />\\n<BR />\\n]]>\""
            + "             ,\"guid\":{\"isPermaLink\":\"false\"}}}}}}";
    // @formatter:on

    /**
     * Test content of parsed JSON API result value
     */
    @Test
    public void testParsedContent() {
        final YahooWeatherAPIModelParser parser = new YahooWeatherAPIModelParser();
        final YahooWeatherAPIModel yahooWeatherApiModel = parser.parseFromJson(DEMO_API_RESULT);

        // check Query content
        assertThat(yahooWeatherApiModel.getCreated(),
                is(ZonedDateTime.of(2017, 10, 17, 10, 21, 16, 0, ZoneOffset.UTC))); // "2017-10-17T10:21:16Z"
        assertThat(yahooWeatherApiModel.getLanguage(), is("en-us"));

        // check Query.Results.Channel content
        assertThat(yahooWeatherApiModel.getAstronomy(), is(notNullValue()));
        assertThat(yahooWeatherApiModel.getAtmosphere(), is(notNullValue()));
        assertThat(yahooWeatherApiModel.getLocation(), is(notNullValue()));
        assertThat(yahooWeatherApiModel.getUnits(), is(notNullValue()));
        assertThat(yahooWeatherApiModel.getWind(), is(notNullValue()));

        // check Query.Results.Channel.Astronomy content
        final Astronomy astronomy = yahooWeatherApiModel.getAstronomy();
        assertThat(astronomy.getSunrise(), is(LocalTime.of(7, 36)));
        assertThat(astronomy.getSunset(), is(LocalTime.of(18, 6)));

        // check Query.Results.Channel.Atmosphere content
        final Atmosphere atmosphere = yahooWeatherApiModel.getAtmosphere();
        assertThat(atmosphere.getHumidity(), is(BigDecimal.valueOf(62L, 0)));
        assertThat(atmosphere.getPressure(), is(BigDecimal.valueOf(3440572, 2)));
        assertThat(atmosphere.getRising(), is(BigDecimal.valueOf(0)));
        assertThat(atmosphere.getVisibility(), is(BigDecimal.valueOf(2591, 2)));
        assertThat(atmosphere.getPressureInHPa(), is(BigDecimal.valueOf(101600L, 2)));

        // check Query.Results.Channel.Location content
        final Location location = yahooWeatherApiModel.getLocation();
        assertThat(location.getCity(), is("Berlin"));
        assertThat(location.getCountry(), is("Germany"));
        assertThat(location.getRegion(), is("BE"));

        // check Query.Results.Channel.Units content
        final Units units = yahooWeatherApiModel.getUnits();
        assertThat(units.getDistance(), is("km"));
        assertThat(units.getPressure(), is("mb"));
        assertThat(units.getSpeed(), is("km/h"));
        assertThat(units.getTemperature(), is("C"));

        // check Query.Results.Channel.Wind content
        final Wind wind = yahooWeatherApiModel.getWind();
        assertThat(wind.getChill(), is(BigDecimal.valueOf(66, 0)));
        assertThat(wind.getDirection(), is(BigDecimal.valueOf(250, 0)));
        assertThat(wind.getSpeed(), is(BigDecimal.valueOf(1127, 2)));
        assertThat(wind.getChillInDegreesCelsius(), is(BigDecimal.valueOf(1888L, 2)));

        // check Query.Results.Channel.Item content
        assertThat(yahooWeatherApiModel.getLatitude(), is(BigDecimal.valueOf(52.516071d)));
        assertThat(yahooWeatherApiModel.getLongitude(), is(BigDecimal.valueOf(13.37698d)));
        assertThat(yahooWeatherApiModel.getPublicationDate(),
                is(ZonedDateTime.of(2017, 10, 17, 11, 00, 00, 0, ZoneId.of("Europe/Berlin")))); // "Tue, 17 Oct 2017
                                                                                                // 11:00 AM
        // CEST"
        assertThat(yahooWeatherApiModel.getCondition(), is(notNullValue()));
        assertThat(yahooWeatherApiModel.getForecasts(), is(notNullValue()));

        // check Query.Results.Channel.Item.Condition content
        final Condition condition = yahooWeatherApiModel.getCondition();
        assertThat(condition.getCode(), is(BigDecimal.valueOf(32, 0)));
        assertThat(condition.getDate(), is(ZonedDateTime.of(2017, 10, 17, 11, 00, 00, 0, ZoneId.of("Europe/Berlin"))));
        assertThat(condition.getTemperature(), is(BigDecimal.valueOf(18, 0)));
        assertThat(condition.getText(), is("Sunny"));

        // check Query.Results.Channel.Item.Lis<Forecast> content
        final List<Forecast> forecasts = yahooWeatherApiModel.getForecasts();
        assertThat(forecasts.size(), is(10));
        // and check first item
        final Forecast forecast = forecasts.get(0);
        assertThat(forecast.getCode(), is(BigDecimal.valueOf(34, 0)));
        assertThat(forecast.getDate(), is(LocalDate.of(2017, 10, 17))); // "17 Oct 2017"
        assertThat(forecast.getDay(), is("Tue"));
        assertThat(forecast.getHighTemperature(), is(BigDecimal.valueOf(21, 0)));
        assertThat(forecast.getLowTemperatur(), is(BigDecimal.valueOf(13, 0)));
        assertThat(forecast.getText(), is("Mostly Sunny"));
    }

    /**
     * Test content of newly created API model
     */
    @Test
    public void testEmptyContent() {
        final YahooWeatherAPIModel yahooWeatherApiModel = new YahooWeatherAPIModel();

        // check Query content
        assertThat(yahooWeatherApiModel.getCreated(), is(nullValue()));
        assertThat(yahooWeatherApiModel.getLanguage(), is(nullValue()));

        // check Query.Results.Channel content
        assertThat(yahooWeatherApiModel.getAstronomy(), is(notNullValue()));
        assertThat(yahooWeatherApiModel.getAtmosphere(), is(notNullValue()));
        assertThat(yahooWeatherApiModel.getLocation(), is(notNullValue()));
        assertThat(yahooWeatherApiModel.getUnits(), is(notNullValue()));
        assertThat(yahooWeatherApiModel.getWind(), is(notNullValue()));

        // check Query.Results.Channel.Astronomy content
        final Astronomy astronomy = yahooWeatherApiModel.getAstronomy();
        assertThat(astronomy.getSunrise(), is(nullValue()));
        assertThat(astronomy.getSunset(), is(nullValue()));

        // check Query.Results.Channel.Atmosphere content
        final Atmosphere atmosphere = yahooWeatherApiModel.getAtmosphere();
        assertThat(atmosphere.getHumidity(), is(nullValue()));
        assertThat(atmosphere.getPressure(), is(nullValue()));
        assertThat(atmosphere.getRising(), is(nullValue()));
        assertThat(atmosphere.getVisibility(), is(nullValue()));
        assertThat(atmosphere.getPressureInHPa(), is(nullValue()));

        // check Query.Results.Channel.Location content
        final Location location = yahooWeatherApiModel.getLocation();
        assertThat(location.getCity(), is(nullValue()));
        assertThat(location.getCountry(), is(nullValue()));
        assertThat(location.getRegion(), is(nullValue()));

        // check Query.Results.Channel.Units content
        final Units units = yahooWeatherApiModel.getUnits();
        assertThat(units.getDistance(), is(nullValue()));
        assertThat(units.getPressure(), is(nullValue()));
        assertThat(units.getSpeed(), is(nullValue()));
        assertThat(units.getTemperature(), is(nullValue()));

        // check Query.Results.Channel.Wind content
        final Wind wind = yahooWeatherApiModel.getWind();
        assertThat(wind.getChill(), is(nullValue()));
        assertThat(wind.getDirection(), is(nullValue()));
        assertThat(wind.getSpeed(), is(nullValue()));
        assertThat(wind.getChillInDegreesCelsius(), is(nullValue()));

        // check Query.Results.Channel.Item content
        assertThat(yahooWeatherApiModel.getLatitude(), is(nullValue()));
        assertThat(yahooWeatherApiModel.getLongitude(), is(nullValue()));
        assertThat(yahooWeatherApiModel.getPublicationDate(), is(nullValue()));
        assertThat(yahooWeatherApiModel.getCondition(), is(notNullValue()));
        assertThat(yahooWeatherApiModel.getForecasts(), is(notNullValue()));

        // check Query.Results.Channel.Item.Condition content
        final Condition condition = yahooWeatherApiModel.getCondition();
        assertThat(condition.getCode(), is(nullValue()));
        assertThat(condition.getDate(), is(nullValue()));
        assertThat(condition.getTemperature(), is(nullValue()));
        assertThat(condition.getText(), is(nullValue()));

        // check Query.Results.Channel.Item.Lis<Forecast> content
        final List<Forecast> forecasts = yahooWeatherApiModel.getForecasts();
        assertThat(forecasts.size(), is(0));
    }

    /**
     * Test null conversion of wind chill
     */
    @Test
    public void testWindChillNull() {
        final Wind wind = new Wind();
        assertThat(wind.getChillInDegreesCelsius(), is(nullValue()));
    }

    /**
     * Test null conversion of pressure
     */
    @Test
    public void testAtmospherePressureNull() {
        final Atmosphere atmosphere = new Atmosphere();
        assertThat(atmosphere.getPressureInHPa(), is(nullValue()));
    }

    @Test
    public void testParseDifferentZoneThanCEST() {
        final Set<ZoneId> zones = new HashSet<ZoneId>();
        zones.add(ZoneId.of("Europe/Berlin"));
        // @formatter:off
        final DateTimeFormatter formatter =
        new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("EEE, dd MMM yyyy h:m a ")
            .appendZoneText(TextStyle.SHORT, zones)
            .toFormatter(Locale.ENGLISH);
        // @formatter:on
        final ZonedDateTime zonedDateTime = ZonedDateTime.parse("Wed, 18 Oct 2017 04:25 AM AKDT", formatter);
        System.out.println(zonedDateTime);
    }

}
