package mames1.net.mamesosu.object;

import lombok.Getter;
import lombok.Setter;
import mames1.net.mamesosu.object.model.ApprovalBot;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class Cache {

    Map<Long, ApprovalBot> awaitingJoinApprovalBots;

    public Cache () {
        awaitingJoinApprovalBots = new HashMap<>();
    }
}
