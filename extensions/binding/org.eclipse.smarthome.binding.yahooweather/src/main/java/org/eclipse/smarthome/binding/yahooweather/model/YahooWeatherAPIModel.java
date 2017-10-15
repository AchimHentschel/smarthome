package org.eclipse.smarthome.binding.yahooweather.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class YahooWeatherAPIModel {

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
    }

    public static class Wind {
        // example content: {"chill":"55","direction":"245","speed":"28.97"}
        private String chill;
        private String speed;
        private String direction;

        public String getChill() {
            return chill;
        }

        public String getSpeed() {
            return speed;
        }

        public String getDirection() {
            return direction;
        }
    }

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

    public static class Item {
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

    public static class Channel {
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

    public static class Results {
        private Channel channel;

        public Channel getChannel() {
            return channel;
        }
    }

    public static class Query {
        private Integer count;
        private String created;
        @SerializedName("lang")
        private String language;
        private Results results;

        public Integer getCount() {
            return count;
        }

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

    public Query getQuery() {
        return query;
    }

}
