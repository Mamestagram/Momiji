package mames1.net.mamesosu.listener;

import mames1.net.mamesosu.utils.modal.InputField;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.util.HashMap;
import java.util.Map;

public class MapStatusChangeRequest extends ListenerAdapter {

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent e) {

        String key = e.getValues().get(0);
        TextInput mapInput;
        Modal modal;
        Map<String, String> formTitles = new HashMap<>() {
            {
                put("all_ranked", "Ranked Application Form (All Difficulties)");
                put("all_deranked", "DeRanked Application Form (All Difficulties)");
                put("diff_ranked", "Ranked Application Form (A Difficulty)");
                put("diff_deranked", "DeRanked Application Form (A Difficulty)");
            }
        };

        if(!formTitles.containsKey(key)) {
            return;
        }

        mapInput = InputField.createTextInput(
                "map_url", "Beatmap URL", "Please enter the beatmap URL.", true, TextInputStyle.SHORT
        );

        modal = Modal.create(
                key + "_form", formTitles.get(key)
        ).addActionRow(
                mapInput
        ).build();

        e.replyModal(modal).queue();
    }
}
