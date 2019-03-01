package de.panamo.thesystems.discord.reactionchannel.listener;


import de.panamo.thesystems.discord.reactionchannel.category.ReactionChannelCategory;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;

public class ReactionChannelListener {

    public void handleMessageSent(MessageReceivedEvent event, ReactionChannelCategory category) {

    }

    public void handleReactionAddedAllowed(MessageReactionAddEvent event, ReactionChannelCategory category) {

    }

    public boolean handleReactionAddedForbidden(MessageReactionAddEvent event, ReactionChannelCategory category) {
        return true;
    }

}
