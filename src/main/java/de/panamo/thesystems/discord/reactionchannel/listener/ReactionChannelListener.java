package de.panamo.thesystems.discord.reactionchannel.listener;


import de.panamo.thesystems.discord.reactionchannel.category.ReactionChannelCategory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

public class ReactionChannelListener {

    /**
     * Handles a new message in a reaction channel
     *
     * @param event the JDA-event for new messages
     * @param category the category the event was triggered on
     * @return if there should be reactions added to the new message
     */

    public boolean handleMessageSent(MessageReceivedEvent event, ReactionChannelCategory category) {
        return true;
    }

    /**
     * Handles a allowed reaction in a reaction channel
     *
     * @param event the JDA-event for message reactions
     * @param category the category the event was triggered on
     */

    public void handleReactionAddedAllowed(MessageReactionAddEvent event, ReactionChannelCategory category) {

    }

    /**
     * Handles a forbidden reaction in a reaction channel
     *
     * @param event the JDA-event for message reactions
     * @param category the category the event was triggered on
     * @return if the reaction should be removed from the message
     */

    public boolean handleReactionAddedForbidden(MessageReactionAddEvent event, ReactionChannelCategory category) {
        return true;
    }

}
