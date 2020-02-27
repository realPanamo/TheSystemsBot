package de.panamo.thesystems.discord.reactionchannel.category;

import java.util.Collection;
import java.util.List;

public class ReactionChannelCategory {
    private String name;
    private Collection<Long> channels;
    private List<ReactionChannelReaction> reactions;

    public ReactionChannelCategory(String name, Collection<Long> channels, List<ReactionChannelReaction> reactions) {
        this.name = name;
        this.channels = channels;
        this.reactions = reactions;
    }

    public String getName() {
        return name;
    }

    public Collection<Long> getChannels() {
        return channels;
    }

    public List<ReactionChannelReaction> getReactions() {
        return reactions;
    }


}
