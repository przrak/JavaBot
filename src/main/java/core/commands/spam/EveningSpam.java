package core.commands.spam;

import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import core.commands.ServiceCommand;
import core.modules.UsersDB;
import core.modules.config.ConfigDB;
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
public class EveningSpam implements ServiceCommand {

    @Override
    public void service() {
        evenSpam();
    }

    private void evenSpam(){
            UsersDB usersDB = new UsersDB();
            try {
                HashMap<Integer, String> users = usersDB.getVKIDListWithGroup();
                for(int key : users.keySet()){
                    sendEvenSpam(key, users.get(key));
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

    }

    public static void main(String[] args){
        EveningSpam e = new EveningSpam();
        e.service();

    }

    private void sendEvenSpam(int vkid, String group){

        Settings settings = Configuration.getSettings(vkid);
        if (!settings.isEveningSpam()) return;

        UserXtrCounters user_info = VKManager.getUserInfo(vkid);
        String user_name = "печенька";
        if (user_info!=null) user_name = user_info.getFirstName();

        String schedule = SpamDataGetter.getSchedule(group, 1);

        String scheduleAdditionalData;
        String parity = ScheduleParser.getWeekParity(1) ? "четная" : "нечетная";
        scheduleAdditionalData = "Группа: " + group + "\n" +
                "Четность: " + parity + "\n";

        String msg = "Хэй , " + user_name;
        if (!schedule.equals("")) {
            msg +=
                    "\n\n" + "Вот расписание на завтра. Не забудь все проверить! " +
                            "Может быть я что-то перепутала?.\n\n" + scheduleAdditionalData + schedule + "\n\n" +
                            "Не забудь сделать домашку!\n\nХорошего вечера!";
        } else {
            msg += "\n\n" + "Ммм... На завтра я пар не нашла. Можешь на всякий случай проверить, " +
                    "но если у тебя завтра действительно нет пар - то отдохни как следует! И соберись " +
                    "с силами на следующую учебную неделю.\nЕще раз приятного отдыха!";
        }
        try {
            if (!schedule.equals("")){
            new VKManager().getSendQuery()
                    .peerId(vkid)
                    .message(msg)
                    .attachment(MenheraSprite.GO_SLEEP)
                    .execute();}
            else {
                new VKManager().getSendQuery()
                        .peerId(vkid)
                        .message(msg)
                        .attachment(MenheraSprite.EATING)
                        .execute();}
            } catch (ApiException | ClientException ignored){}
    }
}
