package de.panamo.thesystems.discord.reactionchannel;

import de.panamo.thesystems.discord.configuration.GeneralConfiguration;
import de.panamo.thesystems.discord.reactionchannel.category.ReactionChannelCategory;
import java.util.Collection;
import java.util.HashSet;

public class ReactionChannelConfiguration implements GeneralConfiguration {

    private Collection<ReactionChannelCategory> reactionChannelCategories = new HashSet<>();

    Collection<ReactionChannelCategory> getReactionChannelCategories() {
        return reactionChannelCategories;
    }
}
