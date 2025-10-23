package org.jenga.MCP_Server;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.P;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped // Make it a CDI bean
public class WeatherTool {

    record WeatherForecast(
        String location,
        String forecast,
        int temperature) {}

    @Tool("Get the current weather forecast for a specified location")
    public WeatherForecast getForecast(@P("The city name") String location) {
        if (location.equalsIgnoreCase("Paris")) {
            return new WeatherForecast("Paris", "sunny", 20);
        } else if (location.equalsIgnoreCase("London")) {
            return new WeatherForecast("London", "rainy", 15);
        } else if (location.equalsIgnoreCase("Tokyo")) {
            return new WeatherForecast("Tokyo", "warm", 32);
        } else {
            return new WeatherForecast(location, "unavailable", 0);
        }
    }

    @Tool("Convert temperature from Celsius to Fahrenheit")
    public Double convertCelsiusToFahrenheit(@P("Temperature in Celsius") double celsius) {
        return (celsius * 9/5) + 32; // This works because Java auto-boxes double to Double
    }
}