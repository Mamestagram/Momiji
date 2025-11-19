package mames1.net.mamesosu.constants;

import lombok.Getter;

@Getter
public enum ModeEmoji {

    CIRCLE("<:circle:1285985582362923008>"),
    DRUM("<:drum:1285985809056530507>"),
    FRUITS( "<:fruits:1285986010265550904>"),
    PIANO( "<:piano:1285986234241515521>");

    final String id;

    ModeEmoji(String id) {
        this.id = id;
    }

    public static String getModeEmojiByMode(int mode) {
       for(ModeEmoji modeEmoji : ModeEmoji.values()) {

           if(mode % 4 == modeEmoji.ordinal()) {
               return modeEmoji.getId();
           }
       }

       return "";
    }
}
