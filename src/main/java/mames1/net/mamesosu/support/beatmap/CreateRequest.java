package mames1.net.mamesosu.support.beatmap;

import mames1.net.mamesosu.Embed;
import mames1.net.mamesosu.Main;
import mames1.net.mamesosu.object.DataBase;
import mames1.net.mamesosu.object.Setting;
import mames1.net.mamesosu.utils.ModalText;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateRequest extends ListenerAdapter {

    final String OSU_REGEX = "beatmapsets/(\\d+)#(osu|taiko|fruits|mania)/(\\d+)";
    final String DISCORD_REGEX = "https://discord.com/channels/([0-9]+)/([0-9]+)";

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        Setting setting = new Setting();

        if(!e.getChannelType().isGuild()) {
            return;
        }

        if(!e.getAuthor().isBot()) {
            return;
        }

        if(e.getChannel().getIdLong() == setting.getBnOsuChannelId() ||
                e.getChannel().getIdLong() == setting.getBnTaikoChannelId() ||
                e.getChannel().getIdLong() == setting.getBnCatchChannelId() ||
                e.getChannel().getIdLong() == setting.getBnManiaChannelId()) {
            e.getMessage().addReaction(Emoji.fromUnicode("U+2753")).queue();
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent e) {

        if(!e.getModalId().contains("ranked")) {
            return;
        }

        Setting setting = new Setting();
        DataBase dataBase = Main.dataBase;

        PreparedStatement ps;
        ResultSet result;
        Connection connection = dataBase.getConnection();

        Pattern pattern = Pattern.compile(OSU_REGEX);
        Matcher matcher = pattern.matcher(Objects.requireNonNull(e.getValue("map_url")).getAsString());

        int beatmapSetID;
        int beatmapID;
        String mode;

        // BNCH, BNROLE
        Map<String, List<Long>> bnData = new HashMap<>() {
            {
                put("osu", List.of(setting.getBnOsuChannelId(), setting.getBnOsuRoleId()));
                put("taiko", List.of(setting.getBnTaikoChannelId(), setting.getBnTaikoRoleId()));
                put("catch", List.of(setting.getBnCatchChannelId(), setting.getBnCatchRoleId()));
                put("mania", List.of(setting.getBnManiaChannelId(), setting.getBnManiaRoleId()));
            }
        };

        if(matcher.find()) {
            mode = matcher.group(2);
            beatmapSetID = Integer.parseInt(matcher.group(1));
            beatmapID = Integer.parseInt(matcher.group(3));
            String column = e.getModalId().contains("all") ? "set_id" : "id";

            Role role = Objects.requireNonNull(e.getGuild()).getRoleById(bnData.get(mode).get(1));

            String button = e.getModalId().replace("all_", "").replace("diff_", "")
                    .replace("_form", "");
            try {

                ps = connection.prepareStatement("select * from maps where ? = ?");
                ps.setString(1, column);
                ps.setLong(2, e.getModalId().contains("all") ? beatmapSetID : beatmapID);

                result = ps.executeQuery();

                int combo = 0;

                if (!ps.executeQuery().next()) {
                    e.replyEmbeds(Embed.getErrorEmbed(
                            "The beatmap you are trying to submit could not be found in the database.\n" +
                                    "You need to play the beatmap once on the server before submitting it!"
                    ).build()).setEphemeral(true).queue();
                    return;
                }

                // データベース確認
                // ODが0の譜面と差分譜面を弾く

                while (result.next()) {
                    if (combo != 0) {
                        if (result.getInt("max_combo") == combo)  {
                            e.replyEmbeds(Embed.getErrorEmbed(
                                    "Beatmaps that include difficulty variants like 1.1x or 1.2x are against the rules and cannot be Ranked.\n" +
                                            "Please review the rules and submit again!").build()).setEphemeral(true).queue();
                            return;
                        }
                    }

                    if(result.getDouble("od") <= 1.0) {
                        e.replyEmbeds(Embed.getErrorEmbed(
                                "Beatmaps with an OD of 0 cannot be submitted for Ranked!"
                        ).build()).setEphemeral(true).queue();
                        return;
                    }

                    combo = result.getInt("max_combo");
                }

                // 確認完了
                // モードの確認

                if(!bnData.containsKey(mode)) {
                    e.replyEmbeds(Embed.getErrorEmbed(
                            "There is an issue with the beatmap URL.\n" +
                                    "Please check the URL you entered!"
                    ).build()).setEphemeral(true).queue();
                    return;
                }

                pattern = Pattern.compile(DISCORD_REGEX);
                matcher = pattern.matcher(bnData.get(mode).get(0).toString());

                // 全てのチェック完了！
                // 送信処理
                if(matcher.find()) {
                    EmbedBuilder eb = new EmbedBuilder();
                    long channelId = Long.parseLong(matcher.group(2));
                    int i = 0;

                    eb.setTitle("**<:mail:1285915444984680448> A new application has arrived!**");

                    ps = connection.prepareStatement("select * from maps where ? = ?");
                    ps.setString(1, column);
                    ps.setLong(2, e.getModalId().contains("all") ? beatmapSetID : beatmapID);

                    result = ps.executeQuery();

                    while(result.next()) {
                        if (i < 24) {
                            eb.setDescription("**[" + result.getString("title") + " - " + result.getString("artist") + "](" + Objects.requireNonNull(e.getValue("map_url")).getAsString() + ")**");
                            eb.addField("> " + result.getString("version") + " [★" + result.getString("diff") + "]",
                                    "CS: " + result.getString("cs") + " / " + "AR: " + result.getString("ar") + " / " + "OD: " + result.getString("od") + " / HP: " + result.getString("hp"), true);
                            eb.setImage("https://assets.ppy.sh/beatmaps/" + beatmapSetID + "/covers/cover.jpg?");
                        }
                        i++;
                    }
                    connection.close();
                    eb.addField("**Request**", "* " + Objects.requireNonNull(e.getMember()).getAsMention(), false);
                    eb.setColor(Color.BLACK);

                    assert role != null;
                    Objects.requireNonNull(e.getJDA().getTextChannelById(channelId)).sendMessage(Objects.requireNonNull(e.getValue("map_url")).getAsString() + ":" + e.getMember().getId() + ":" + e.getModalId().replace("_form", "") + ":" + role.getAsMention()).addEmbeds(
                                eb.build()
                        ).addActionRow(
                                net.dv8tion.jda.api.interactions.components.buttons.Button.primary("btn_" + button, button.toUpperCase()),
                                net.dv8tion.jda.api.interactions.components.buttons.Button.danger("btn_deny", "DENY")
                        ).queue();
                    e.replyEmbeds(Embed.getMapRequestSuccessEmbed().build()).setEphemeral(true).queue();
                }

            } catch (SQLException ex) {
                ex.fillInStackTrace();
            }
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent e) {
        if (e.getComponentId().contains("menu:dropdown")) {
            String value = e.getValues().get(0);
            Map<String, String> modalTitle = new HashMap<>() {
                {
                    put("all_ranked", "Ranked Application Form (All Difficulties)");
                    put("all_deranked", "DeRanked Application Form (All Difficulties)");
                    put("diff_ranked", "Ranked Application Form (A Difficulty)");
                    put("diff_deranked", "DeRanked Application Form (A Difficulty)");
                }
            };

            TextInput modalInput = ModalText.createTextInput("map_url", "Map URL", "eg: https://osu.ppy.sh/beatmapsets/1#osu/75", true, TextInputStyle.SHORT);
            Modal modal = Modal.create(
                    value + "_form",
                    modalTitle.get(value)
            ).addActionRow(modalInput).build();

            e.replyModal(modal).queue();
        }
    }
}
