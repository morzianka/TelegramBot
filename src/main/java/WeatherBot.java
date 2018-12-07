import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;

public class WeatherBot extends TelegramLongPollingBot {
    private static final String botUsername = "Ask_WeatherBot";
    private static final String botToken = "786435269:AAHHZxOogIHgSohWeDYU9fb0oOiUOZ3p6GE";
    private static WeatherBot weatherBot;
    private BotMessage botMessage;
    private Model model = new Model();
    private static HashMap<Integer, String> cache = new HashMap<>();

    private WeatherBot() {
    }

    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            botMessage = new BotMessage(message);
            Integer id = message.getFrom().getId();
            try {
                checkAndSaveLocation(message);
                switch (message.getText()) {
                    case "/help":
                        botMessage.helpMessage();
                        break;
                    case "/weather":
                        if (hadLocation(message)) {
                            botMessage.getWeather(model.retrieveSub(id).getLoca());
                        }
                        else botMessage.sendLocPls();
                        break;
                    case "/sub":
                        botMessage.sendLocPls();
                        break;
                    case "/unsub":
                        cache.remove(id);
                        model.deleteSub(id);
                        botMessage.deletedSub();
                        break;
                    default:
                        botMessage.helpMessage();
                        break;
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    public void checkAndSaveLocation(Message message) throws TelegramApiException {
        if (message.hasLocation()) {
            User user = message.getFrom();
            if (!cache.containsKey(user.getId())) {
                if (!model.subExists(user.getId())) {
                    String location = locationToSring(message.getLocation());
                    model.saveSub(user, location, message.getChatId());
                    botMessage.successSub();
                    cache.put(user.getId(), location);
                } else {
                    botMessage.getWeather(model.retrieveSub(user.getId()).getLoca());
                }
            } else
                botMessage.getWeather(cache.get(user.getId()));
        }
    }

    public boolean hadLocation(Message message) {
        Integer id = message.getFrom().getId();
        return cache.containsKey(id) || model.subExists(id);
    }

    public String locationToSring(Location location) {
        return String.format("lat=%.2f&lon=%.2f", location.getLatitude(), location.getLongitude());
    }

    public static WeatherBot getInstance() {
        if (weatherBot == null) {
            synchronized (WeatherBot.class) {
                if (weatherBot == null)
                    weatherBot = new WeatherBot();
            }
        }
        return weatherBot;
    }

    public String getBotUsername() {
        return botUsername;
    }

    public String getBotToken() {
        return botToken;
    }
}
