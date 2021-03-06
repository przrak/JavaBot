package core.commands.spam;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import core.commands.ServiceCommand;
import core.modules.UsersDB;
import core.modules.config.Configuration;
import core.modules.config.Settings;
import core.modules.parser.itmo.schedule.ScheduleParser;
import core.modules.res.MenheraSprite;
import vk.VKManager;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author Arthur Kupriyanov
 */
public class MorningSpam implements ServiceCommand {


    private void morningSpam(){
        UsersDB usersDB = new UsersDB();
        try {
            HashMap<Integer, String> users = usersDB.getVKIDListWithGroup();
            for(int key : users.keySet()){
                sendMorningSpam(key, users.get(key));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void sendMorningSpam(int vkid, String group){

        Settings settings = Configuration.getSettings(vkid);
        if (!settings.isMorningSpam()) return;

        UserXtrCounters user_info = VKManager.getUserInfo(vkid);
        String user_name = "печенька";
        if (user_info!=null) user_name = user_info.getFirstName();

        String weather = SpamDataGetter.getWeather();

        String schedule = SpamDataGetter.getSchedule(group);

        String scheduleAdditionalData;
        String parity = ScheduleParser.getWeekParity() ? "четная" : "нечетная";
        scheduleAdditionalData = "Группа: " + group + "\n" +
                "Четность: " + parity + "\n";
        if (!schedule.equals("")) {
            String msg = "C добрым утром, " + user_name +
                    "\n\n" + weather + "\n\n" + "Вот расписание. Не забудь все проверить! " +
                    "Я ведь тоже могу ошибаться.\n\n" + scheduleAdditionalData + schedule + "\n\n" +
                    "Удачи сегодня на парах!";

            try {
                new VKManager().getSendQuery()
                        .peerId(vkid)
                        .message(msg)
                        .attachment(MenheraSprite.MM_SPRITE)
                        .execute();

            } catch (ApiException | ClientException ignored) {

            }
        }

    }

    public static void main(String[] args) {
        MorningSpam ed = new MorningSpam();
        ed.sendMorningSpam(255396611, "P3112");

    }

    @Override
    public void service() {
        morningSpam();
    }
}
