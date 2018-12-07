import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class BotMessage {
    private Message message;
    private WeatherBot weatherBot;

    public BotMessage(Message message) {
        this.message = message;
        this.weatherBot = WeatherBot.getInstance();
    }

    public BotMessage() {}

    public void helpMessage() throws TelegramApiException {
        reply("Available commands: \n" +
                "/weather - get actual weather\n" +
                "/sub - subscribe to get daily forecast\n" +
                "/unsub - unsubscribe\n" +
                "Also you can press it on keyboard below.");
    }

    public void getWeather(String location) throws TelegramApiException {
        FutureTask<String> futureTask = executeTask(location);
        try {
            reply(futureTask.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void sendWeatherForSubs(Subscriber subscriber) throws TelegramApiException {
        FutureTask<String> futureTask = executeTask(subscriber.getLoca());
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(subscriber.getChatId());
        try {
            while(!futureTask.isDone()) {
                Thread.sleep(1000);
            }
            sendMessage.setText("Your forecast for today!\nHave a nice day!\n" + futureTask.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        setKeyboard(sendMessage);
        WeatherBot.getInstance().execute(sendMessage);
    }

    public FutureTask executeTask(String location) {
        Weather weather = new Weather(location);
        FutureTask<String> futureTask = new FutureTask<String>(weather);
        new Thread(futureTask).start();
        return futureTask;
    }

    public void sendLocPls() throws TelegramApiException {
        reply("Send your location first");
    }

    public void successSub() throws TelegramApiException {
        reply("Congrats new subscriber! You will get your personal forecast delivery every day =)");
    }

    public void deletedSub() throws TelegramApiException {
        reply("Arrivederci!");
    }

    public void reply(String text) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        setKeyboard(sendMessage);
        weatherBot.execute(sendMessage);
    }

    public void setKeyboard(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton("/help"));
        keyboardRow1.add(new KeyboardButton("/weather"));
        keyboardRow1.add(new KeyboardButton("/location").setRequestLocation(true));
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(new KeyboardButton("/sub"));
        keyboardRow2.add(new KeyboardButton("/unsub"));
        keyboardRows.add(keyboardRow1);
        keyboardRows.add(keyboardRow2);
        replyKeyboardMarkup.setKeyboard(keyboardRows);
    }
}
