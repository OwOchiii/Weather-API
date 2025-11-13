package Orochi;

import Orochi.api.WeatherService;

import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        WeatherService service = new WeatherService("API_KEY_HERE","localhost",6379);
        try
        {   //While the args is zipcode, it can work with location name
            System.out.println("Enter Zipcode or name of Location: ");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            String weatherJson = service.getWeather(input);
            System.out.println(weatherJson);
        }
        catch (Exception e)
        {
            System.err.println("Error fetching weather data: " + e.getMessage());
        }
    }
}