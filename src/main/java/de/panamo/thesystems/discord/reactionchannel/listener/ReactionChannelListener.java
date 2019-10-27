package de.panamo.thesystems.discord.reactionchannel.listener;

import de.panamo.thesystems.discord.reactionchannel.category.ReactionChannelCategory;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;

public class ReactionChannelListener {

    /**
     * Handles a new message in a reaction channel
     *
     * @param message the JDA-message which was received
     * @param category the category the event was triggered on
     * @return if there should be reactions added to the new message
     */

    public boolean handleMessageSent(Message message, ReactionChannelCategory category) {
        return true;
    }

    /**
     * Handles a allowed reaction in a reaction channel
     *
     * @param event the JDA-event for message reactions
     * @param category the category the event was triggered on
     */

    public void handleReactionAddedAllowed(GuildMessageReactionAddEvent event, ReactionChannelCategory category) {

    }

    /**
     * Handles a forbidden reaction in a reaction channel
     *
     * @param event the JDA-event for message reactions
     * @param category the category the event was triggered on
     * @return if the reaction should be removed from the message
     */

    public boolean handleReactionAddedForbidden(GuildMessageReactionAddEvent event, ReactionChannelCategory category) {
        return true;
    }

}
