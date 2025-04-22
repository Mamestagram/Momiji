package mames1.net.mamesosu.support.beatmap;

import mames1.net.mamesosu.Embed;
import mames1.net.mamesosu.Main;
import mames1.net.mamesosu.object.DataBase;
import mames1.net.mamesosu.object.Setting;
import mames1.net.mamesosu.utils.Format;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.forums.ForumTagSnowflake;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Nominate extends ListenerAdapter {

    final String OSU_REGEX = "beatmapsets/(\\d+)#(osu|taiko|fruits|mania)/(\\d+)";

    private long getTagWithMode(int mode) {

        switch (mode) {
            case 0 -> {
                return 1127420212422127707L;
            }
            case 1 -> {
                return 1127420270253187162L;
            }
            case 2 -> {
                return 1127420319720816640L;
            }
            case 3 -> {
                return 1127420174656610374L;
            }
            default -> {
                return 0;
            }
        }
    }



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
            String url = data[0] + ":" + data[1];
            Pattern pattern = Pattern.compile(OSU_REGEX);
            Matcher matcher = pattern.matcher(url);

            if(matcher.find()) {
                String mapTitle = "";
                int beatmapID = Integer.parseInt(matcher.group(3));
                int beatmapSetID = Integer.parseInt(matcher.group(1));
                int status = data[3].contains("deranked") ? 5 : 3;
                int i = 0;
                int mode = 0;
                String column = data[3].contains("all") ? "set_id" : "id";

                EmbedBuilder eb = new EmbedBuilder();

                try {

                    ps = connection.prepareStatement("update maps set status = ? where ? = ?");
                    ps.setInt(1, status);
                    ps.setString(2, column);
                    ps.setInt(3, data[3].contains("all") ? beatmapSetID : beatmapID);
                    ps.executeUpdate();

                    RequestSender.sendHttpRequest(
                            column,
                            data[3].contains("all") ? beatmapSetID : beatmapID,
                            status
                    );

                    e.getMessage().removeReaction(
                            Emoji.fromUnicode("U+2753")
                    ).queue();

                    e.getMessage().addReaction(
                            Emoji.fromUnicode("U+2714")
                    ).queue();

                    e.reply("The database values have been updated!").setEphemeral(true).queue();

                    ps = connection.prepareStatement("select * from maps where ? = ? order by diff");
                    ps.setString(1, column);
                    ps.setInt(2, data[3].contains("all") ? beatmapSetID : beatmapID);
                    result = ps.executeQuery();

                    while(result.next()) {
                        if (i < 24) {
                            String key = "";
                            String mapDiffTitle = result.getString("version");
                            double mapDiffRate = result.getDouble("diff");
                            String mapLength = Format.getConvertFormatTime(result.getInt("total_length"));
                            int maxCombo = result.getInt("max_combo");
                            int bpm = result.getInt("bpm");
                            mode = result.getInt("mode");

                            if (mode == 3) {
                                key = "[" + result.getInt("cs") + "K] ";
                            }

                            if(i == 0) {
                                mapTitle = result.getString("title") + " - " + result.getString("artist");
                                String mapURL = setting.getBeatmapMirror() + "/d/" + beatmapSetID;

                                eb.setTitle("<:chevronup:1285984561473519768> **" + mapTitle + "**");
                                eb.setDescription("<:download:1285987441928568933> **Download**\n* [Nerinyan.moe](" + mapURL + ")");
                                eb.setImage("https://assets.ppy.sh/beatmaps/" + beatmapSetID + "/covers/cover.jpg?");
                            }

                            eb.addField(key + "<:star:1285993568283922493> " + mapDiffRate + " **[" + mapDiffTitle + "]**", "* <:gauge:1285992347527807119> "+ "bpm (DT " + (int) (bpm * 1.5) + "bpm) " +
                                    "<:music:1285991542900789430> " + mapLength + " <:circle:1285985582362923008> " + maxCombo
                                    , false);
                        }
                        i++;
                    }

                    eb.addField("<:mail:1285915444984680448> **Comment** (" + Objects.requireNonNull(e.getMember()).getEffectiveName() + ")","* " + Objects.requireNonNull(e.getValue("note")).getAsString(), false);
                    eb.setTimestamp(new Date().toInstant());

                    try {
                        URL ur = new URL("https://assets.ppy.sh/beatmaps/" + beatmapSetID + "/covers/cover.jpg?");
                        BufferedImage img = ImageIO.read(ur);
                        File file = new File("temp.jpg");
                        ImageIO.write(img, "jpg", file);
                        FileUpload fileUpload = FileUpload.fromData(file);

                        MessageCreateData d  = new MessageCreateBuilder()
                                .setFiles(fileUpload)
                                .addEmbeds(eb.build())
                                .build();

                        if(mapTitle.length() >= 100) {
                            mapTitle = mapTitle.substring(0, 96) + "...";
                        }

                        Objects.requireNonNull(e.getJDA().getForumChannelById(setting.getRankedMapForumChannelId()))
                                .createForumPost(mapTitle, d)
                                .setTags(ForumTagSnowflake.fromId(setting.getRankedForumTagId()),
                                        ForumTagSnowflake.fromId(getTagWithMode(mode))).queue(a -> {
                                            boolean isDeleted = false;
                                            if (file.exists()) {
                                                isDeleted = file.delete();
                                            }
                                            if (isDeleted) {
                                                System.out.println("Temporary file deleted successfully.");
                                            }
                                });

                        e.reply("The database values have been updated!").setEphemeral(true).queue();

                    } catch (Exception ex1) {
                        e.replyEmbeds(Embed.getErrorEmbed(
                                "An error occurred while sending the message to the forum channel.\n" +
                                        "Please check the bot's permissions and try again."
                        ).build()).setEphemeral(true).queue();
                        ex1.fillInStackTrace();
                    }
                } catch (SQLException ex) {
                    e.replyEmbeds(Embed.getErrorEmbed(
                            "SQL error occurred while processing the request."
                    ).build()).setEphemeral(true).queue();
                    ex.fillInStackTrace();
                }
            }
        }
    }
}
