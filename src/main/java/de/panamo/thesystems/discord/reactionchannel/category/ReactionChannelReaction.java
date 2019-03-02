package de.panamo.thesystems.discord.reactionchannel.category;

import java.util.Collection;

public class ReactionChannelReaction {
    private String emote;
    private EmoteType emoteType;
    private Collection<Long> allowedRanks;

    public ReactionChannelReaction(String emote, EmoteType emoteType, Collection<Long> allowedRanks) {
        this.emote = emote;
        this.emoteType = emoteType;
        this.allowedRanks = allowedRanks;
    }

    public String getEmote() {
        return emote;
    }

    public EmoteType getEmoteType() {
        return emoteType;
    }

    public Collection<Long> getAllowedRanks() {
        return allowedRanks;
    }

    public enum EmoteType {

        UNICODE,
        EMOTE_ID

    }
}
