package mames1.net.mamesosu.discord.monitor;

import mames1.net.mamesosu.constants.LogLevel;
import mames1.net.mamesosu.utils.log.AppLogger;
import net.dv8tion.jda.api.events.guild.invite.GuildInviteCreateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class InviteCreateMonitor extends ListenerAdapter {

    @Override
    public void onGuildInviteCreate(GuildInviteCreateEvent e) {

        if(e.getInvite().getInviter() == null) {
            return;
        }

        if(e.getInvite().getInviter().getIdLong() == e.getJDA().getSelfUser().getIdLong()) {
            return;
        }

        // Botが作成した招待リンクを削除
        if(e.getInvite().getInviter().isBot()) {
            e.getInvite().delete().queue();

            AppLogger.log("Botが作成した招待リンクを削除しました: " + e.getInvite().getUrl(), LogLevel.INFO);
        }
    }
}
