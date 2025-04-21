package mames1.net.mamesosu.support.beatmap;

import mames1.net.mamesosu.object.Setting;
import mames1.net.mamesosu.utils.ModalText;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateRequest extends ListenerAdapter {

    final String OSU_REGEX = "beatmapsets/(\\d+)#(osu|taiko|fruits|mania)/(\\d+)";
    final String DISCORD_REGEX = "https://discord.com/channels/([0-9]+)/([0-9]+)";

    @Override
    public void onModalInteraction(ModalInteractionEvent e) {

        Setting setting = new Setting();

        // BNCH, BNROLE
        Map<String, List<Long>> bnData = new HashMap<>() {
            {
                put("osu", List.of(setting.getBnOsuChannelId(), setting.getBnOsuRoleId()));
                put("taiko", List.of(setting.getBnTaikoChannelId(), setting.getBnTaikoRoleId()));
                put("catch", List.of(setting.getBnCatchChannelId(), setting.getBnCatchRoleId()));
                put("mania", List.of(setting.getBnManiaChannelId(), setting.getBnManiaRoleId()));
            }
        };




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
