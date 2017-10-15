package org.eclipse.smarthome.binding.yahooweather.model;

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

    // TODO: migrate to test case
    public void testParsedContent() {
        YahooWeatherAPIModelParser parser = new YahooWeatherAPIModelParser();
        parser.parseFromJson(DEMO_API_RESULT);
    }

    public static void main(String[] args) {
        YahooWeatherAPIModelTest testSuite = new YahooWeatherAPIModelTest();
        testSuite.testParsedContent();
    }

}
