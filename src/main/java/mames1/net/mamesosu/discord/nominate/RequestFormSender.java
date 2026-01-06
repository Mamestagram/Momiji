package mames1.net.mamesosu.discord.nominate;

import mames1.net.mamesosu.constants.Channel;
import mames1.net.mamesosu.constants.emoji.CustomEmoji;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class RequestFormSender extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        EmbedBuilder aboutEmbed = new EmbedBuilder();

        if(!e.getChannelType().isGuild()) {
            return;
        }

        if(e.getChannel().getIdLong() != Channel.MAP_REQUEST.getId()) {
            return;
        }

        if(e.getMember() == null || e.getMember().getUser().isBot()) {
            return;
        }

        aboutEmbed.setTitle("**# " + CustomEmoji.INFO.getId() + " What can you do here?**");
        aboutEmbed.setDescription("In this channel, you can submit requests to promote maps that are currently marked as Graveyard on Bancho, or beatmaps that are registered only on Mamestagram, to Ranked.\n" +
                "Since these two types of beatmaps use different application formats, please be sure to read the information below carefully.");
        aboutEmbed.setColor(Color.GREEN);

    }
}
