package mames1.net.mamesosu.constants;

import lombok.Getter;

@Getter
public enum RankEmoji {

    XH("<:XH:1286157852523757741>"),
    X("<:X_:1286157493378224191>"),
    SH("<:SH:1286157172740329483>"),
    S("<:S_:1286156921757499493>"),
    A("<:A_:1286155726951288924>"),
    B("<:B_:1286155991913594891>"),
    C("<:C_:1286156350774312981>"),
    D("<:D_:1286156609046708296>");

    final String id;

    RankEmoji(String id) {
        this.id = id;
    }

    public static String getRankEmojiByRank(String rank) {
       for(RankEmoji rankEmoji : RankEmoji.values()) {

           if(rank.equals(rankEmoji.name())) {
               return rankEmoji.getId();
           }
       }

       return "";
    }
}
