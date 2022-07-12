import lavaplayer.PlayerManager;
import net.dv8tion.jda.api.entities.AudioChannel;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.net.URISyntaxException;

public class Commands extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Message message = event.getMessage();
        AudioManager audioManager = event.getGuild().getAudioManager();
        AudioChannel voiceChannel = event.getMember().getVoiceState().getChannel();
        GuildVoiceState userVoiceState = event.getMember().getVoiceState();
        GuildVoiceState botVoiceState = event.getGuild().getSelfMember().getVoiceState();

        if (message.getContentRaw().startsWith("!play")) {
            if (!userVoiceState.inAudioChannel()) {
                message.reply("You need to be in a voice channel!").queue();
            } else {
                if (!botVoiceState.inAudioChannel()) {
                    audioManager.openAudioConnection(voiceChannel);
                    audioManager.setSelfDeafened(true);
                }

                String link = String.join(" ", message.getContentRaw());
                if (!isUrl(link)) {
                    link = "ytsearch:" + link;
                }
                PlayerManager.getInstance().loadAndPlay(event.getTextChannel(), link);
            }
        }

        if (message.getContentRaw().startsWith("!skip")) {
            checkAudioChannel(userVoiceState, botVoiceState, message);

            PlayerManager.getInstance().getMusicManager(event.getGuild()).trackScheduler.nextTrack();
            message.reply("Success! Track skipped!").queue();
        }

        if (message.getContentRaw().startsWith("!stop")) {
            checkAudioChannel(userVoiceState, botVoiceState, message);

            audioManager.closeAudioConnection();
            PlayerManager.getInstance().getMusicManager(event.getGuild()).trackScheduler.audioPlayer.destroy();
            message.reply("Success! Music is no longer playing!").queue();
        }
    }

    public static void checkAudioChannel(GuildVoiceState userVoiceState, GuildVoiceState botVoiceState, Message message) {
        if (!userVoiceState.inAudioChannel()) {
            message.reply("You aren't in voice channel!").queue();
        }
        if (!botVoiceState.inAudioChannel()) {
            message.reply("I'm aren't in voice channel!").queue();
        }
    }

    public boolean isUrl(String url) {
        try {
            new URI(url);
            return true;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}