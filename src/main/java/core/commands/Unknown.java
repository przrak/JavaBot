package core.commands;

import com.vk.api.sdk.objects.messages.Message;
import core.commands.VKCommands.VKCommand;

public class Unknown extends Command implements ProgramSpecification, VKCommand {

    @Override
    protected void setConfig() {
        commandName = "unknown";
    }
    @Override
    public String init(String... args) {
        return "У меня нет распознователя простой речи.\n" +
                "Не понимаю эту команду...";
    }

    @Override
    public String programInit(String... args) {
        return null;
    }

    @Override
    public String exec(Message message) {
        return null;
    }
}
