package org.eclipse.smarthome.binding.yahooweather.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * This class is used as mapping object for yahoo weather API JSON results during conversion via Gson
 * 
 * @author Achim Hentschel
 *
 */
public class YahooWeatherAPIModel {

    /**
     * Class storing parsed atmosphere data
     *
     * @author Achim Hentschel
     *
     */
    public static class Atmosphere {
        private String rising;
        private String visibility;
        private String humidity;
        private String pressure;

        public String getRising() {
            return rising;
        }

        public String getVisibility() {
            return visibility;
        }

        public String getHumidity() {
            return humidity;
        }

        public String getPressure() {
            return pressure;
        }

        /**
         * This methods returns pressure converted into hPa
         *
         * @return pressure in [hPa]
         */
        public BigDecimal getPressureInHPa() {
            if (getPressure() == null) {
                return null;
            }

            BigDecimal resultValue = new BigDecimal(getPressure());
            if (resultValue.doubleValue() > 10000.0) {
                // Unreasonably high, record so far was 1085,8 hPa
                // The Yahoo API currently returns inHg values although it claims they are mbar - therefore convert
                resultValue = BigDecimal.valueOf((long) (resultValue.doubleValue() / 0.3386388158), 2);
            }
            return resultValue;
        }
    }

    /**
     * Class storing parsed wind data
     *
     * @author Achim Hentschel
     *
     */
    public static class Wind {
        // example content: {"chill":"55","direction":"245","speed":"28.97"}
        private String chill;
        private String speed;
        private String direction;

        public String getChill() {
            return chill;
        }

        /**
         * This method returns wind chill converted into degrees celsius
         *
         * @return wind chill in degrees celsius
         */
        public BigDecimal getChillInDegreesCelsius() {
            String chill = getChill();
            if (chill == null) {
                return null;
            }

            BigDecimal resultValue = new BigDecimal(chill);
            // The Yahoo API currently returns °F values although it claims they are °C - therefore convert
            double farenheit = resultValue.doubleValue();
            double celsius = (farenheit - 32.0d) * 5.0d / 9.0d * 100.0d;
            resultValue = BigDecimal.valueOf((long) celsius, 2);
            return resultValue;
        }

        public String getSpeed() {
            return speed;
        }

        public String getDirection() {
            return direction;
        }
    }

    /**
     * Class storing parsed astronomy data
     *
     * @author Achim Hentschel
     *
     */
    public static class Astronomy {
        private String sunrise;
        private String sunset;

        public String getSunrise() {
            return sunrise;
        }

        public String getSunset() {
            return sunset;
        }
    }

    /**
     * Class storing parsed location data
     *
     * @author Achim Hentschel
     *
     */
    public static class Location {
        private String country;
        private String region;
        private String city;

        public String getCountry() {
            return country;
        }

        public String getRegion() {
            return region;
        }

        public String getCity() {
            return city;
        }
    }

    /**
     * Class storing parsed unit data
     *
     * @author Achim Hentschel
     *
     */
    public static class Units {
        String distance;
        String pressure;
        String speed;
        String temperature;

        public String getDistance() {
            return distance;
        }

        public String getPressure() {
            return pressure;
        }

        public String getSpeed() {
            return speed;
        }

        public String getTemperature() {
            return temperature;
        }
    }

    /**
     * Internal class storing parsed item data
     *
     * @author Achim Hentschel
     *
     */
    private static class Item {
        @SerializedName("lat")
        private String latitude;

        @SerializedName("long")
        private String longtitude;

        @SerializedName("pubDate")
        private String publicationDate;

        Condition condition;
        @SerializedName("forecast")
        private List<Forecast> forecasts;

        public String getLatitude() {
            return latitude;
        }

        public String getLongtitude() {
            return longtitude;
        }

        public String getPublicationDate() {
            return publicationDate;
        }

        public Condition getCondition() {
            return condition;
        }

        public List<Forecast> getForecasts() {
            return forecasts;
        }
    }

    /**
     * Class storing parsed weather condition data
     *
     * @author Achim Hentschel
     *
     */
    public static class Condition {
        // example content: {"code":"26","date":"Tue, 10 Oct 2017 01:00 PM CEST","temp":"14","text":"Cloudy"}"
        private String code;
        private String date;
        @SerializedName("temp")
        private String temperature;
        private String text;

        public String getCode() {
            return code;
        }

        public String getDate() {
            return date;
        }

        public String getTemperature() {
            return temperature;
        }

        public String getText() {
            return text;
        }
    }

    /**
     * Class storing parsed forecast data
     *
     * @author Achim Hentschel
     *
     */
    public static class Forecast {
        // example content: "{"code":"12","date":"10 Oct 2017","day":"Tue","high":"15","low":"11","text":"Rain"}"
        private String code;
        private String date;
        private String day;
        @SerializedName("low")
        private String lowTemperatur;
        @SerializedName("high")
        private String highTemperature;
        private String text;

        public String getCode() {
            return code;
        }

        public String getDate() {
            return date;
        }

        public String getDay() {
            return day;
        }

        public String getLowTemperatur() {
            return lowTemperatur;
        }

        public String getHighTemperature() {
            return highTemperature;
        }

        public String getText() {
            return text;
        }
    }

    /**
     * Internal class storing parsed channel data
     *
     * @author Achim Hentschel
     *
     */
    private static class Channel {
        private Units units;
        private Location location;
        private Astronomy astronomy;
        private Atmosphere atmosphere;
        private Wind wind;
        @SerializedName("item")
        private Item item;

        public Units getUnits() {
            return units;
        }

        public Location getLocation() {
            return location;
        }

        public Astronomy getAstronomy() {
            return astronomy;
        }

        public Atmosphere getAtmosphere() {
            return atmosphere;
        }

        public Wind getWind() {
            return wind;
        }

        public Item getItem() {
            return item;
        }
    }

    /**
     * Internal class storing parsed results data
     *
     * @author Achim Hentschel
     *
     */
    private static class Results {
        private Channel channel;

        public Channel getChannel() {
            return channel;
        }
    }

    /**
     * Internal class storing parsed query data
     *
     * @author Achim Hentschel
     *
     */
    private static class Query {
        @SuppressWarnings("unused")
        private Integer count;
        private String created;
        @SerializedName("lang")
        private String language;
        private Results results;

        public String getCreated() {
            return created;
        }

        public String getLanguage() {
            return language;
        }

        public Results getResults() {
            return results;
        }
    }

    private Query query;

    private Query getQuery() {
        return query;
    }

    /**
     * Wrapper method returning Query.created field content
     *
     * @return Query.created
     */
    public String getCreated() {
        try {
            return getQuery().getCreated();
        } catch (NullPointerException ex) {
            // some search path hierarchy level was null - just return null as result value and catch the exception
            return null;
        }
    }

    /**
     * Wrapper method returning Query.language field content
     *
     * @return
     */
    public String getLanguage() {
        try {
            return getQuery().getLanguage();
        } catch (NullPointerException ex) {
            // some search path hierarchy level was null - just return null as result value and catch the exception
            return null;
        }
    }

    /**
     * Wrapper method returning Atmosphere object
     *
     * @return
     */
    public Atmosphere getAtmosphere() {
        try {
            return getQuery().getResults().getChannel().getAtmosphere();
        } catch (NullPointerException ex) {
            // some search path hierarchy level was null - just return an empty object to prevent further NPEs
            return new Atmosphere();
        }
    }

    /**
     * Wrapper method returning Wind object
     *
     * @return
     */
    public Wind getWind() {
        try {
            return getQuery().getResults().getChannel().getWind();
        } catch (NullPointerException ex) {
            // some search path hierarchy level was null - just return an empty object to prevent further NPEs
            return new Wind();
        }
    }

    /**
     * Wrapper method returning Astronomy object
     *
     * @return
     */
    public Astronomy getAstronomy() {
        try {
            return getQuery().getResults().getChannel().getAstronomy();
        } catch (NullPointerException ex) {
            // some search path hierarchy level was null - just return an empty object to prevent further NPEs
            return new Astronomy();
        }
    }

    /**
     * Wrapper method returning Location object
     *
     * @return
     */
    public Location getLocation() {
        try {
            return getQuery().getResults().getChannel().getLocation();
        } catch (NullPointerException ex) {
            // some search path hierarchy level was null - just return an empty object to prevent further NPEs
            return new Location();
        }
    }

    /**
     * Wrapper method returning Units object
     *
     * @return
     */
    public Units getUnits() {
        try {
            return getQuery().getResults().getChannel().getUnits();
        } catch (NullPointerException ex) {
            // some search path hierarchy level was null - just return an empty object to prevent further NPEs
            return new Units();
        }
    }

    /**
     * Wrapper method returning Items.latitude value
     *
     * @return
     */
    public String getLatitude() {
        try {
            return getQuery().getResults().getChannel().getItem().getLatitude();
        } catch (NullPointerException ex) {
            // some search path hierarchy level was null - just return null as result value and catch the exception
            return null;
        }
    }

    /**
     * Wrapper method returning Items.longtitude value
     *
     * @return
     */
    public String getLongtitude() {
        try {
            return getQuery().getResults().getChannel().getItem().getLongtitude();
        } catch (NullPointerException ex) {
            // some search path hierarchy level was null - just return null as result value and catch the exception
            return null;
        }
    }

    /**
     * Wrapper method returning Item.publicationDate
     *
     * @return
     */
    public String getPublicationDate() {
        try {
            return getQuery().getResults().getChannel().getItem().getPublicationDate();
        } catch (NullPointerException ex) {
            // some search path hierarchy level was null - just return null as result value and catch the exception
            return null;
        }
    }

    /**
     * Wrapper method returning Item.condition object
     *
     * @return
     */
    public Condition getCondition() {
        try {
            return getQuery().getResults().getChannel().getItem().getCondition();
        } catch (NullPointerException ex) {
            // some search path hierarchy level was null - just return an empty object to prevent further NPEs
            return new Condition();
        }
    }

    /**
     * Wrapper method returning Item.forecasts list
     *
     * @return
     */
    public List<Forecast> getForecasts() {
        try {
            return getQuery().getResults().getChannel().getItem().getForecasts();
        } catch (NullPointerException ex) {
            // some search path hierarchy level was null - just return an empty object to prevent further NPEs
            return new ArrayList<>();
        }
    }

}
