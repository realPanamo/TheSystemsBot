package de.panamo.thesystems.discord.reactionchannel.category;

import java.util.Collection;

public class ReactionChannelReaction {
    private String emote;
    private Collection<Long> allowedRanks;

    public ReactionChannelReaction(String emote, Collection<Long> allowedRanks) {
        this.emote = emote;
        this.allowedRanks = allowedRanks;
    }

    public String getEmote() {
        return emote;
    }

    public Collection<Long> getAllowedRanks() {
        return allowedRanks;
    }
}
