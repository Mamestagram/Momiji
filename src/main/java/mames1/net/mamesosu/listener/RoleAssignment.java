package mames1.net.mamesosu.listener;

import mames1.net.mamesosu.constants.Channel;
import mames1.net.mamesosu.constants.emoji.CustomEmoji;
import mames1.net.mamesosu.constants.emoji.ModeEmoji;
import mames1.net.mamesosu.constants.ServerRole;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class RoleAssignment extends ListenerAdapter {

    Role infoRole;
    Role stdRole;
    Role taikoRole;
    Role catchRole;
    Role maniaRole;
    Role multiPlayRole;
    Role streamRole;

    private void initRoles(Guild guild) {

        infoRole = guild.getRoleById(ServerRole.INFO.getId());
        stdRole = guild.getRoleById(ServerRole.OSU.getId());
        taikoRole = guild.getRoleById(ServerRole.TAIKO.getId());
        catchRole = guild.getRoleById(ServerRole.CATCH.getId());
        maniaRole = guild.getRoleById(ServerRole.MANIA.getId());
        multiPlayRole = guild.getRoleById(ServerRole.MULTIPLAY.getId());
        streamRole = guild.getRoleById(ServerRole.STREAM.getId());
    }


    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent e) {

        Emoji emoji = e.getReaction().getEmoji();
        Member member = e.getMember();

        initRoles(e.getGuild());

        if(e.getChannel().getIdLong() != Channel.ROLE_ASSIGNMENT.getId()) {
            return;
        }

        if (member == null) {
            return;
        }

        if(emoji.equals(Emoji.fromFormatted(CustomEmoji.INFO.getId()))) {
            e.getGuild().addRoleToMember(member, infoRole).queue();
            return;
        }

        if(emoji.equals(Emoji.fromFormatted(ModeEmoji.CIRCLE.getId()))) {
            e.getGuild().addRoleToMember(member, stdRole).queue();
            return;
        }

        if(emoji.equals(Emoji.fromFormatted(ModeEmoji.DRUM.getId()))) {
            e.getGuild().addRoleToMember(member, taikoRole).queue();
            return;
        }

        if(emoji.equals(Emoji.fromFormatted(ModeEmoji.FRUITS.getId()))) {
            e.getGuild().addRoleToMember(member, catchRole).queue();
            return;
        }

        if (emoji.equals(Emoji.fromFormatted(ModeEmoji.PIANO.getId()))) {
            e.getGuild().addRoleToMember(member, maniaRole).queue();
            return;
        }

        if (emoji.equals(Emoji.fromFormatted(CustomEmoji.PEOPLEGROUP.getId()))) {
            e.getGuild().addRoleToMember(member, multiPlayRole).queue();
            return;
        }

        if (emoji.equals(Emoji.fromFormatted(CustomEmoji.SCREEN.getId()))) {
            e.getGuild().addRoleToMember(member, streamRole).queue();
        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent e) {

        Emoji emoji;
        emoji = e.getReaction().getEmoji();
        Member member;
        member = e.getMember();

        initRoles(e.getGuild());

        if(e.getChannel().getIdLong() != Channel.ROLE_ASSIGNMENT.getId()) {
            return;
        }

        if (member == null) {
            return;
        }

        if(emoji.equals(Emoji.fromFormatted(CustomEmoji.INFO.getId()))) {
            e.getGuild().removeRoleFromMember(member, infoRole).queue();
            return;
        }

        if(emoji.equals(Emoji.fromFormatted(ModeEmoji.CIRCLE.getId()))) {
            e.getGuild().removeRoleFromMember(member, stdRole).queue();
            return;
        }

        if(emoji.equals(Emoji.fromFormatted(ModeEmoji.DRUM.getId()))) {
            e.getGuild().removeRoleFromMember(member, taikoRole).queue();
            return;
        }

        if(emoji.equals(Emoji.fromFormatted(ModeEmoji.FRUITS.getId()))) {
            e.getGuild().removeRoleFromMember(member, catchRole).queue();
            return;
        }

        if (emoji.equals(Emoji.fromFormatted(ModeEmoji.PIANO.getId()))) {
            e.getGuild().removeRoleFromMember(member, maniaRole).queue();
            return;
        }

        if (emoji.equals(Emoji.fromFormatted(CustomEmoji.PEOPLEGROUP.getId()))) {
            e.getGuild().removeRoleFromMember(member, multiPlayRole).queue();
            return;
        }

        if (emoji.equals(Emoji.fromFormatted(CustomEmoji.SCREEN.getId()))) {
            e.getGuild().removeRoleFromMember(member, streamRole).queue();
        }
    }
}
