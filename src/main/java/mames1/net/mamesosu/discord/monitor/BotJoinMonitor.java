package mames1.net.mamesosu.discord.monitor;

import mames1.net.mamesosu.Main;
import mames1.net.mamesosu.constants.LogLevel;
import mames1.net.mamesosu.constants.ServerRole;
import mames1.net.mamesosu.object.ApprovalBot;
import mames1.net.mamesosu.object.Cache;
import mames1.net.mamesosu.utils.log.AppLogger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.*;
import java.util.List;

public class BotJoinMonitor extends ListenerAdapter {

    // 新規Bot参加監視
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent e) {

        Cache cache = Main.cache;

        ApprovalBot approvalBot = new ApprovalBot();
        Map<Long, ApprovalBot> awaitingJoinApprovalBots = cache.getAwaitingJoinApprovalBots();
        EmbedBuilder botJoinAlertEmbed = new EmbedBuilder();
        List<Message> awaitingApprovalMessages = new ArrayList<>();

        Role adminRole = e.getGuild().getRoleById(ServerRole.DISCORD_MODS.getId());
        List<Member> backendDevMembers = e.getGuild().getMembersWithRoles(adminRole);


        if(!e.getMember().getUser().isBot()) {
            return;
        }

        botJoinAlertEmbed.setTitle("新規Botがサーバーに参加しました");
        botJoinAlertEmbed.setDescription("""
                このBotを追加した覚えがない場合, 危険な可能性があります.
                以下のボタンから承認またはキックを選択してください.
                """);

        botJoinAlertEmbed.addField("Bot名", e.getMember().getUser().getEffectiveName(), false);
        botJoinAlertEmbed.setColor(Color.ORANGE);
        botJoinAlertEmbed.setTimestamp(new Date().toInstant());

        backendDevMembers.forEach(m -> m.getUser().openPrivateChannel().queue(
                p -> p.sendMessageEmbeds(
                        botJoinAlertEmbed.build()
                ).addActionRow(
                        Button.success("btn_approve_bot:" + e.getMember().getId(), "承認"),
                        Button.danger("btn_reject_bot:" + e.getMember().getId(), "キック"),
                        Button.danger("btn_reject_all_bots:" + e.getMember().getId(), "非承認Botを一斉キック")
                ).queue(
                        success -> {
                            awaitingApprovalMessages.add(success);
                            AppLogger.log("管理者 " + m.getUser().getAsTag() + " に新規Bot参加のDM通知を送信しました.", LogLevel.INFO);
                        },
                        failure -> {}
                ),
                failure -> {}
        ));

        approvalBot.setBotMember(e.getMember());
        approvalBot.setAwaitingApprovalMessages(awaitingApprovalMessages);

        awaitingJoinApprovalBots.put(e.getMember().getIdLong(), approvalBot);

        cache.setAwaitingJoinApprovalBots(awaitingJoinApprovalBots);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent e) {

        if(Objects.requireNonNull(e.getButton().getId()).contains("btn_approve_bot") || e.getButton().getId().contains("btn_reject_bot") || e.getButton().getId().contains("btn_reject_all_bots")) {

            ApprovalBot approvalBot;
            Cache cache = Main.cache;
            String[] buttonIdParts = e.getButton().getId().split(":");
            String buttonId = e.getButton().getId();
            String botNickname;
            Map<Long, ApprovalBot> awaitingJoinApprovalBots = cache.getAwaitingJoinApprovalBots();
            EmbedBuilder responseEmbed = new EmbedBuilder();

            e.deferEdit().queue();

            if(!awaitingJoinApprovalBots.containsKey(Long.parseLong(buttonIdParts[1]))) {
                return;
            }

            approvalBot = awaitingJoinApprovalBots.get(Long.parseLong(buttonIdParts[1]));
            botNickname = approvalBot.getBotMember().getUser().getEffectiveName();

            responseEmbed.setTitle("導入されたBotに対するアクションが実行されました");

            responseEmbed.setDescription("以下のBotに対してアクションが実行されました.\n" +
                    (buttonId.contains("approve") ? botNickname + " を承認しました." :
                            (buttonId.contains("reject_all") ? "非承認Botの一斉キックを実行しました." :
                                    botNickname + " をキックしました.")));

            responseEmbed.addField("実行者", e.getUser().getAsTag(), false);
            responseEmbed.setColor(buttonId.contains("approve") ? Color.GREEN : Color.RED);
            responseEmbed.setTimestamp(new Date().toInstant());

            // ボタンが押されたとき、ボタンを消す
            for(Message msg : approvalBot.getAwaitingApprovalMessages()) {
                msg.editMessageEmbeds(
                        responseEmbed.build()
                ).setComponents().queue();
            }

            if(e.getButton().getId().contains("btn_approve_bot")) {
                // Botを承認
                awaitingJoinApprovalBots.remove(Long.parseLong(buttonIdParts[1]));
                cache.setAwaitingJoinApprovalBots(awaitingJoinApprovalBots);

                e.getHook().editOriginal("Bot " + approvalBot.getBotMember().getUser().getAsTag() + " の参加を承認しました.").setComponents().queue();

                AppLogger.log("Bot " + approvalBot.getBotMember().getUser().getAsTag() + " の参加を承認しました.", LogLevel.INFO);
                return;
            }

            if(e.getButton().getId().contains("btn_reject_bot")) {
                // Botをキック
                approvalBot.getBotMember().kick().queue(
                        success -> AppLogger.log("Bot " + approvalBot.getBotMember().getUser().getAsTag() + " をキックしました.", LogLevel.INFO),
                        failure -> AppLogger.log("Bot " + approvalBot.getBotMember().getUser().getAsTag() + " のキックに失敗しました: " + failure.getMessage(), LogLevel.ERROR)
                );

                awaitingJoinApprovalBots.remove(Long.parseLong(buttonIdParts[1]));
                cache.setAwaitingJoinApprovalBots(awaitingJoinApprovalBots);

                e.getHook().editOriginal("Bot " + approvalBot.getBotMember().getUser().getAsTag() + " をキックしました.").setComponents().queue();

                return;
            }

            // 非承認Botを一斉キック

            for(ApprovalBot ab : awaitingJoinApprovalBots.values()) {
                ab.getBotMember().kick().queue(
                        success -> AppLogger.log("Bot " + ab.getBotMember().getUser().getAsTag() + " をキックしました.", LogLevel.INFO),
                        failure -> AppLogger.log("Bot " + ab.getBotMember().getUser().getAsTag() + " のキックに失敗しました: " + failure.getMessage(), LogLevel.ERROR)
                );

                cache.setAwaitingJoinApprovalBots(new HashMap<>());
            }

            e.getHook().editOriginal("非承認Botの一斉キックを実行しました.").setComponents().queue();
        }
    }
}
