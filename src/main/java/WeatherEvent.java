import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class WeatherEvent implements Runnable {
    private Model model = new Model();
    private List<Subscriber> subscribers = new ArrayList<>();

    @Override
    public void run() {
        //while (true) {
            /*try {
                Thread.sleep(86400000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            subscribers = model.retrieveAllSubs();
            for (Subscriber sub : subscribers) {
                try {
                    new BotMessage().sendWeatherForSubs(sub);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        //}
    }
}
