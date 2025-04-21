package mames1.net.mamesosu;

import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.Date;

public abstract class Embed {

    public static EmbedBuilder getErrorEmbed(String error) {
        return new EmbedBuilder()
                .setTitle("<:warning:1285853296833335366> Error")
                .setDescription(error)
                .setColor(Color.BLACK)
                .setTimestamp(new Date().toInstant());
    }

    public static EmbedBuilder getMapRequestSuccessEmbed() {
        return new EmbedBuilder()
                .setTitle("<:check:1285853854667112480> Request submitted!")
                .setDescription("""
                        Thank you for submitting your request.
                        Our nominator team will usually review the map within 24 hours.
                        You’ll receive the result in your DMs — please stay tuned!""")
                .setColor(Color.BLACK)
                .setTimestamp(new Date().toInstant());
    }
}
