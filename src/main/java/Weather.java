import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Weather implements Callable {
    private double temp;
    private int pressure;
    private int humidity;
    private double wind;
    private String description;
    private String location;
    private static String key = "e321f2732a4038a50a66295b045be017";
    private static Logger logger = Logger.getLogger(Weather.class.getName());

    public Weather(String location) {
        this.location = location;
    }

    @Override
    public synchronized String call() {
        URL url = null;
        try {
            url = new URL(String.format("http://api.openweathermap.org/data/2.5/weather?%s&units=metric&APPID=%s",
                    location, key));
        } catch (MalformedURLException e) {
            logger.log(Level.SEVERE, "URLException: ", e);
        }
        try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            logger.log(Level.FINE, response.toString());
            parseJSON(response.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return this.toString();
    }

    public void parseJSON(String json) {
        System.out.println(json);
        JSONObject jsonObject = new JSONObject(json);
        this.temp = jsonObject.getJSONObject("main").getDouble("temp");
        this.humidity = jsonObject.getJSONObject("main").getInt("humidity");
        this.pressure = jsonObject.getJSONObject("main").getInt("pressure");
        this.wind = jsonObject.getJSONObject("wind").getDouble("speed");
        JSONArray jsonArray = jsonObject.getJSONArray("weather");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            this.description = obj.getString("description");
        }
    }

    @Override
    public String toString() {
        return "Weather now: " +
                "\ntemperature = " + temp + " °C" +
                "\npressure = " + pressure + " mm" +
                "\nhumidity = " + humidity + "%" +
                "\nwind = " + wind + " m/s" +
                "\n" + description;
    }
}
