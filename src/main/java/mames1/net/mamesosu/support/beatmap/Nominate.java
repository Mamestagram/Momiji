package mames1.net.mamesosu.support.beatmap;

import mames1.net.mamesosu.Embed;
import mames1.net.mamesosu.Main;
import mames1.net.mamesosu.object.DataBase;
import mames1.net.mamesosu.object.Setting;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Objects;

public class Nominate extends ListenerAdapter {

    final String OSU_REGEX = "beatmapsets/(\\d+)#(osu|taiko|fruits|mania)/(\\d+)";

    @Override
    public void onModalInteraction(ModalInteractionEvent e) {

        if(e.getModalId().contains("accept_form") || e.getModalId().contains("deny_form")) {

            Setting setting = new Setting();
            String[] data = Objects.requireNonNull(e.getMessage()).getContentRaw().split(":");

            Member m = Objects.requireNonNull(e.getJDA().getGuildById(setting.getGuildId())).getMemberById(Long.parseLong(data[0]));
            assert m != null;
            User u = m.getUser();

            if (e.getModalId().contains("deny_form")) {

                u.openPrivateChannel()
                        .flatMap(channel -> channel.sendMessageEmbeds(
                                Embed.getNominateNotifyEmbed(
                                        false,
                                        data[0] + ":" + data[1],
                                        Objects.requireNonNull(e.getValue("reason")).getAsString(),
                                        Objects.requireNonNull(e.getMember()).getEffectiveName()).build())
                        ).queue(a -> {
                            e.getMessage().removeReaction(
                                     Emoji.fromUnicode("U+2753")
                            ).queue();
                            e.getMessage().addReaction(
                                    Emoji.fromUnicode("U+274E")
                            ).queue();
                        });

                e.reply("The message has been sent, and all processing is complete!").setEphemeral(true).queue();

                return;
            }

            DataBase dataBase = Main.dataBase;
            PreparedStatement ps;
            ResultSet result;
            Connection connection = dataBase.getConnection();

        }
    }
}
