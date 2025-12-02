package mames1.net.mamesosu.object;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ApprovalBot {

    Member botMember;
    List<Message> awaitingApprovalMessages;

    public ApprovalBot () {
        botMember = null;
        awaitingApprovalMessages = new ArrayList<>();
    }
}
