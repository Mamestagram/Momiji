package mames1.net.mamesosu.discord.monitor;

import mames1.net.mamesosu.constants.Channel;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.member.update.GuildMemberUpdateBoostTimeEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.Date;

public class BoostMonitor extends ListenerAdapter {

    @Override
    public void onGuildMemberUpdateBoostTime(GuildMemberUpdateBoostTimeEvent e) {

        EmbedBuilder announceEmbed = new EmbedBuilder();

        announceEmbed.setTitle("# :tada: The server has been boosted! :tada:");
        announceEmbed.setDescription("Thanks to " + e.getMember().getEffectiveName() + ", our server has just been boosted!\n" +
                "We truly appreciate your support! :sparkles:\n" +
                "This boost helps enhance our server’s features and overall experience.\n" +
                "We’ll continue working to make this community even better!");

        announceEmbed.setColor(Color.CYAN);
        announceEmbed.setTimestamp(new Date().toInstant());

        if (e.getMember().isBoosting()) {

            TextChannel announceChannelJP = e.getGuild().getTextChannelById(Channel.ANNOUNCE_JP.getId());
            TextChannel announceChannelEN = e.getGuild().getTextChannelById(Channel.ANNOUNCE_EN.getId());

            if (announceChannelJP != null) {
                announceChannelJP.sendMessageEmbeds(announceEmbed.build()).queue();
            }

            if (announceChannelEN != null) {
                announceChannelEN.sendMessageEmbeds(announceEmbed.build()).queue();
            }
        }
    }
}
