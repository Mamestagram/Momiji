package mames1.net.mamesosu.object.model;

import net.dv8tion.jda.api.entities.Member;

public class MapRequest {

    public String mode;
    public int beatmapId;
    public int beatmapsetId;
    public Member requester;


    public MapRequest(String mode, int beatmapId, int beatmapsetId, Member requester) {
        this.mode = mode;
        this.beatmapId = beatmapId;
        this.beatmapsetId = beatmapsetId;
        this.requester = requester;
    }
}
