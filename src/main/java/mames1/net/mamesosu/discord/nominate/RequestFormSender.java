package mames1.net.mamesosu.discord.nominate;

import mames1.net.mamesosu.constants.Channel;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;

import java.awt.*;

public class RequestFormSender extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        EmbedBuilder aboutEmbed = new EmbedBuilder();
        EmbedBuilder availableEmbed = new EmbedBuilder();
        EmbedBuilder warningEmbed = new EmbedBuilder();
        EmbedBuilder footerEmbed = new EmbedBuilder();

        StringSelectMenu.Builder builder = StringSelectMenu.create("menu:dropdown");
        StringSelectMenu menu;

        builder.addOption("Ranked (All difficulties)", "all_ranked");
        builder.addOption("DeRanked (All difficulties)", "all_deranked");
        builder.addOption("Ranked (A difficulty)", "diff_ranked");
        builder.addOption("DeRanked (A difficulty)", "diff_deranked");

        menu = builder.build();

        if(!e.getChannelType().isGuild()) {
            return;
        }

        if(e.getChannel().getIdLong() != Channel.MAP_REQUEST.getId()) {
            return;
        }

        if(e.getMember() == null || e.getMember().getUser().isBot()) {
            return;
        }

        aboutEmbed.setTitle("<:question:1285854271857889291> **What's this?**");
        aboutEmbed.setDescription("When a BAT approves your request, the map will be changed to the status you requested.");
        aboutEmbed.setColor(Color.magenta);

        availableEmbed.setTitle("<:check:1285853854667112480> **Available Status Changes**");
        availableEmbed.setDescription("""
                * All difficulties to Ranked
                * All difficulties to DeRanked (remove from Ranked)
                * A difficulty to Ranked
                * A difficulty to DeRanked (remove from Ranked)
                """);
        availableEmbed.setColor(Color.GREEN);

        warningEmbed.setTitle("<:warning:1285853296833335366> **Warning**");
        warningEmbed.addField("**If any of the following conditions apply, the map cannot be ranked.**",
                """
                * Maps with a Pending status on Bancho
                * Maps with an OD of 1 or less
                * Maps that have not been played on Mamestagram
                * Maps with variant difficulties
                * Maps with a drain time of 30 seconds or less
                """, false);
        warningEmbed.setColor(Color.YELLOW);

        footerEmbed.setTitle("<:arrowup:1285855460510924882> **Let’s get started! The following information is required:**");
        footerEmbed.setDescription("* Map URL (Bancho)");
        footerEmbed.setColor(Color.magenta);


        e.getChannel().sendMessage(
                "# <:flash:1285854876215279628> Change the map status!"
        ).addEmbeds(
                aboutEmbed.build(),
                availableEmbed.build(),
                warningEmbed.build(),
                footerEmbed.build()
        ).addComponents(
                ActionRow.of(menu)
        ).queue();
    }
}
