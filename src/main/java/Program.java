import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.logging.LogManager;

public class Program {
    public static void main(String[] args) {
        try {
            LogManager.getLogManager().readConfiguration(
                    Program.class.getResourceAsStream("logging.properties"));
        } catch (IOException e) {
            System.err.println("Could not setup logger configuration: " + e.toString());
        }

        ApiContextInitializer.init();

        TelegramBotsApi botsApi = new TelegramBotsApi();

        try {
            botsApi.registerBot(WeatherBot.getInstance());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        new Thread(new WeatherEvent()).start();
    }
}