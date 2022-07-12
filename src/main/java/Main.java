import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;

public class Main {
    public static void main(String[] args) throws LoginException {
        JDA jda = JDABuilder.createDefault("Insert here your token")
                .setActivity(Activity.listening("Rick Astley - Never Gonna Give You Up"))
                .addEventListeners(new Commands())
                .build();
    }
}