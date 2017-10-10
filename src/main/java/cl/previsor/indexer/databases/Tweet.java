package cl.previsor.indexer.databases;


import org.bson.Document;

import java.util.List;

/** Clase almacenadora de los Tweets que se encuentran en MongoDB.
 *  En esta clase se almacena la estructura de cada Tweet, incluyendo
 *  id, nombre del usuario, el texto, etc.
 */
public class Tweet {

    //Atributos de un Tweet

    //Tweet
    private Long idTweet;
    private String username;
    private String name;
    private String tweetText;
    private List<String> hashtags;

    //Hora y fecha del tweet
    private int day;
    private int month;
    private int year;
    private int hour;
    private int minute;

    //Ubicación geográfica del Tweet
    private String country;
    private String latitude;
    private String longitude;

    //Constructor
    public Tweet(Document tweet){
        this.idTweet = tweet.getLong("id");
        this.username = tweet.getString("user");
        this.name = tweet.getString("name");
        this.tweetText = tweet.getString("tweetText");
        this.hashtags = (List<String>)tweet.get("hashtags");
        this.day = tweet.getInteger("day");
        this.month = tweet.getInteger("month");
        this.year = tweet.getInteger("year");
        this.hour = tweet.getInteger("hour");
        this.minute = tweet.getInteger("minute");
        this.country = tweet.getString("country");
        this.latitude = tweet.getString("latitude");
        this.longitude = tweet.getString("longitude");
    }

    //Getters and Setters


    public Long getIdTweet() {
        return idTweet;
    }

    public void setIdTweet(Long idTweet) {
        this.idTweet = idTweet;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTweetText() {
        return tweetText;
    }

    public void setTweetText(String tweetText) {
        this.tweetText = tweetText;
    }

    public List<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(List<String> hashtags) {
        this.hashtags = hashtags;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
