package org.eclipse.smarthome.binding.yahooweather.model;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.eclipse.smarthome.binding.yahooweather.model.YahooWeatherAPIModel.Astronomy;
import org.eclipse.smarthome.binding.yahooweather.model.YahooWeatherAPIModel.Atmosphere;
import org.eclipse.smarthome.binding.yahooweather.model.YahooWeatherAPIModel.Channel;
import org.eclipse.smarthome.binding.yahooweather.model.YahooWeatherAPIModel.Condition;
import org.eclipse.smarthome.binding.yahooweather.model.YahooWeatherAPIModel.Forecast;
import org.eclipse.smarthome.binding.yahooweather.model.YahooWeatherAPIModel.Item;
import org.eclipse.smarthome.binding.yahooweather.model.YahooWeatherAPIModel.Location;
import org.eclipse.smarthome.binding.yahooweather.model.YahooWeatherAPIModel.Query;
import org.eclipse.smarthome.binding.yahooweather.model.YahooWeatherAPIModel.Results;
import org.eclipse.smarthome.binding.yahooweather.model.YahooWeatherAPIModel.Units;
import org.eclipse.smarthome.binding.yahooweather.model.YahooWeatherAPIModel.Wind;
import org.junit.Test;

/**
 * Tests for API result after mapping it to object via Gson
 *
 * @author achimhentschel
 *
 */
public class YahooWeatherAPIModelTest {

    // @formatter:off
    private String DEMO_API_RESULT = "{\"query\":{"
            +   "\"count\":1"
            +   ",\"created\":\"2017-10-10T11:45:05Z\""
            +   ",\"lang\":\"de-DE\""
            +   ",\"results\":{"
            +       "\"channel\":{"
            +           "\"units\":{\"distance\":\"km\",\"pressure\":\"mb\",\"speed\":\"km/h\",\"temperature\":\"C\"}"
            +           ",\"title\":\"Yahoo! Weather - Stolberg, NW, DE\""
            +           ",\"link\":\"http://us.rd.yahoo.com/dailynews/rss/weather/Country__Country/*https://weather.yahoo.com/country/state/city-697493/\""
            +           ",\"description\":\"Yahoo! Weather for Stolberg, NW, DE\""
            +           ",\"language\":\"en-us\""
            +           ",\"lastBuildDate\":\"Tue, 10 Oct 2017 01:45 PM CEST\""
            +           ",\"ttl\":\"60\""
            +           ",\"location\":{\"city\":\"Stolberg\",\"country\":\"Germany\",\"region\":\" NW\"}"
            +           ",\"wind\":{\"chill\":\"55\",\"direction\":\"245\",\"speed\":\"28.97\"}"
            +           ",\"atmosphere\":{\"humidity\":\"75\",\"pressure\":\"33457.53\",\"rising\":\"0\",\"visibility\":\"25.91\"}"
            +           ",\"astronomy\":{\"sunrise\":\"7:51 am\",\"sunset\":\"6:52 pm\"}"
            +           ",\"image\":{\"title\":\"Yahoo! Weather\",\"width\":\"142\",\"height\":\"18\",\"link\":\"http://weather.yahoo.com\",\"url\":\"http://l.yimg.com/a/i/brand/purplelogo//uh/us/news-wea.gif\"}"
            +           ",\"item\":{"
            +               "\"title\":\"Conditions for Stolberg, NW, DE at 01:00 PM CEST\""
            +               ",\"lat\":\"50.771412\""
            +               ",\"long\":\"6.22767\""
            +               ",\"link\":\"http://us.rd.yahoo.com/dailynews/rss/weather/Country__Country/*https://weather.yahoo.com/country/state/city-697493/\""
            +               ",\"pubDate\":\"Tue, 10 Oct 2017 01:00 PM CEST\""
            +               ",\"condition\":{\"code\":\"26\",\"date\":\"Tue, 10 Oct 2017 01:00 PM CEST\",\"temp\":\"14\",\"text\":\"Cloudy\"}"
            +               ",\"forecast\":["
            +                   "{\"code\":\"12\",\"date\":\"10 Oct 2017\",\"day\":\"Tue\",\"high\":\"15\",\"low\":\"11\",\"text\":\"Rain\"}"
            +                   ",{\"code\":\"28\",\"date\":\"11 Oct 2017\",\"day\":\"Wed\",\"high\":\"16\",\"low\":\"13\",\"text\":\"Mostly Cloudy\"}"
            +                   ",{\"code\":\"28\",\"date\":\"12 Oct 2017\",\"day\":\"Thu\",\"high\":\"15\",\"low\":\"11\",\"text\":\"Mostly Cloudy\"}"
            +                   ",{\"code\":\"28\",\"date\":\"13 Oct 2017\",\"day\":\"Fri\",\"high\":\"17\",\"low\":\"11\",\"text\":\"Mostly Cloudy\"}"
            +                   ",{\"code\":\"34\",\"date\":\"14 Oct 2017\",\"day\":\"Sat\",\"high\":\"20\",\"low\":\"12\",\"text\":\"Mostly Sunny\"}"
            +                   ",{\"code\":\"32\",\"date\":\"15 Oct 2017\",\"day\":\"Sun\",\"high\":\"21\",\"low\":\"12\",\"text\":\"Sunny\"}"
            +                   ",{\"code\":\"34\",\"date\":\"16 Oct 2017\",\"day\":\"Mon\",\"high\":\"19\",\"low\":\"14\",\"text\":\"Mostly Sunny\"}"
            +                   ",{\"code\":\"30\",\"date\":\"17 Oct 2017\",\"day\":\"Tue\",\"high\":\"18\",\"low\":\"11\",\"text\":\"Partly Cloudy\"}"
            +                   ",{\"code\":\"30\",\"date\":\"18 Oct 2017\",\"day\":\"Wed\",\"high\":\"17\",\"low\":\"13\",\"text\":\"Partly Cloudy\"}"
            +                   ",{\"code\":\"30\",\"date\":\"19 Oct 2017\",\"day\":\"Thu\",\"high\":\"16\",\"low\":\"12\",\"text\":\"Partly Cloudy\"}]"
            +               ",\"description\":\"<![CDATA[<img src=\\\"http://l.yimg.com/a/i/us/we/52/26.gif\\\"/>\\n<BR />\\n<b>Current Conditions:</b>\\n<BR />Cloudy\\n<BR />\\n<BR />\\n<b>Forecast:</b>\\n<BR /> Tue - Rain. High: 15Low: 11\\n<BR /> Wed - Mostly Cloudy. High: 16Low: 13\\n<BR /> Thu - Mostly Cloudy. High: 15Low: 11\\n<BR /> Fri - Mostly Cloudy. High: 17Low: 11\\n<BR /> Sat - Mostly Sunny. High: 20Low: 12\\n<BR />\\n<BR />\\n<a href=\\\"http://us.rd.yahoo.com/dailynews/rss/weather/Country__Country/*https://weather.yahoo.com/country/state/city-697493/\\\">Full Forecast at Yahoo! Weather</a>\\n<BR />\\n<BR />\\n<BR />\\n]]>\",\"guid\":{\"isPermaLink\":\"false\"}}}}}}";
    // @formatter:on

    /**
     * Test content of parsed JSON API result value
     */
    @Test
    public void testParsedContent() {
        final YahooWeatherAPIModelParser parser = new YahooWeatherAPIModelParser();
        final YahooWeatherAPIModel yahooWeatherApiModel = parser.parseFromJson(DEMO_API_RESULT);

        assertThat(yahooWeatherApiModel.getQuery(), is(notNullValue()));

        // check Query content
        final Query query = yahooWeatherApiModel.getQuery();
        assertThat(query.getCount(), is(1));
        assertThat(query.getCreated(), is("2017-10-10T11:45:05Z"));
        assertThat(query.getLanguage(), is("de-DE"));

        assertThat(query.getResults(), is(notNullValue()));

        // check Query.Results content
        final Results result = query.getResults();
        assertThat(result.getChannel(), is(notNullValue()));

        // check Query.Results.Channel content
        final Channel channel = result.getChannel();
        assertThat(channel.getAstronomy(), is(notNullValue()));
        assertThat(channel.getAtmosphere(), is(notNullValue()));
        assertThat(channel.getLocation(), is(notNullValue()));
        assertThat(channel.getUnits(), is(notNullValue()));
        assertThat(channel.getWind(), is(notNullValue()));
        assertThat(channel.getItem(), is(notNullValue()));

        // check Query.Results.Channel.Astronomy content
        final Astronomy astronomy = channel.getAstronomy();
        assertThat(astronomy.getSunrise(), is("7:51 am"));
        assertThat(astronomy.getSunset(), is("6:52 pm"));

        // check Query.Results.Channel.Atmosphere content
        final Atmosphere atmosphere = channel.getAtmosphere();
        assertThat(atmosphere.getHumidity(), is("75"));
        assertThat(atmosphere.getPressure(), is("33457.53"));
        assertThat(atmosphere.getRising(), is("0"));
        assertThat(atmosphere.getVisibility(), is("25.91"));

        // check Query.Results.Channel.Location content
        final Location location = channel.getLocation();
        assertThat(location.getCity(), is("Stolberg"));
        assertThat(location.getCountry(), is("Germany"));
        assertThat(location.getRegion(), is(" NW"));

        // check Query.Results.Channel.Units content
        final Units units = channel.getUnits();
        assertThat(units.getDistance(), is("km"));
        assertThat(units.getPressure(), is("mb"));
        assertThat(units.getSpeed(), is("km/h"));
        assertThat(units.getTemperature(), is("C"));

        // check Query.Results.Channel.Wind content
        final Wind wind = channel.getWind();
        assertThat(wind.getChill(), is("55"));
        assertThat(wind.getDirection(), is("245"));
        assertThat(wind.getSpeed(), is("28.97"));

        // check Query.Results.Channel.Item content
        final Item item = channel.getItem();
        assertThat(item.getLatitude(), is("50.771412"));
        assertThat(item.getLongtitude(), is("6.22767"));
        assertThat(item.getPublicationDate(), is("Tue, 10 Oct 2017 01:00 PM CEST"));
        assertThat(item.getCondition(), is(notNullValue()));
        assertThat(item.getForecasts(), is(notNullValue()));

        // check Query.Results.Channel.Item.Condition content
        final Condition condition = item.getCondition();
        assertThat(condition.getCode(), is("26"));
        assertThat(condition.getDate(), is("Tue, 10 Oct 2017 01:00 PM CEST"));
        assertThat(condition.getTemperature(), is("14"));
        assertThat(condition.getText(), is("Cloudy"));

        // check Query.Results.Channel.Item.Lis<Forecast> content
        final List<Forecast> forecasts = item.getForecasts();
        assertThat(forecasts.size(), is(10));
        // and check first item
        final Forecast forecast = forecasts.get(0);
        assertThat(forecast.getCode(), is("12"));
        assertThat(forecast.getDate(), is("10 Oct 2017"));
        assertThat(forecast.getDay(), is("Tue"));
        assertThat(forecast.getHighTemperature(), is("15"));
        assertThat(forecast.getLowTemperatur(), is("11"));
        assertThat(forecast.getText(), is("Rain"));
    }

}
