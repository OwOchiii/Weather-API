package Orochi.api;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import redis.clients.jedis.Jedis;

public class WeatherService {
    private String apiKey ;
    private final Jedis jedis;
    private final Gson gson;
    private static final int CACHE_EXPIRY = 3600; // 1 hour in seconds
    private final OkHttpClient client;

    public WeatherService(String apiKey, String redisHost, int redisPort) {
        this.apiKey = apiKey;
        this.client = new OkHttpClient();
        this.jedis = new Jedis(redisHost, redisPort);
        this.gson = new Gson();
    }


    public String getWeather(String ZipCode) {
        String cacheKey = "weather:" + ZipCode;
        String cachedData = jedis.get(cacheKey);
        if (cachedData != null) {
            return cachedData;
        }
        String weatherData = null;

        String url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" + ZipCode + "?unitGroup=us&key=" + apiKey + "&contentType=json";
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try(Response response = client.newCall(request).execute())
        {
           if (!response.isSuccessful())
           {
               return "{ \"error\": \"Failed to fetch weather data\" }";
           }
            weatherData = response.body().string();
        }
        catch (Exception e)
        {
            return "{ \"error\": \"Failed to fetch weather data\" }";
        }


        // Cache the data
        jedis.setex(cacheKey, CACHE_EXPIRY, weatherData);

        return weatherData;
    }
}
