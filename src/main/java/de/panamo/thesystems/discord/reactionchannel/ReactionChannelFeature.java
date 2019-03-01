package de.panamo.thesystems.discord.reactionchannel;

import de.panamo.thesystems.discord.TheSystemsBot;
import de.panamo.thesystems.discord.feature.BotFeature;
import de.panamo.thesystems.discord.reactionchannel.category.ReactionChannelCategory;
import de.panamo.thesystems.discord.reactionchannel.category.ReactionChannelReaction;
import de.panamo.thesystems.discord.reactionchannel.listener.ReactionChannelListener;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ReactionChannelFeature extends ListenerAdapter implements BotFeature<ReactionChannelConfiguration> {

    private TheSystemsBot instance;
    private Collection<ReactionChannelCategory> reactionChannelCategories;
    private Map<String, ReactionChannelListener> listeners = new HashMap<>();

    @Override
    public void handleStart(TheSystemsBot instance, ReactionChannelConfiguration configuration) {
        this.instance = instance;
        this.reactionChannelCategories = configuration.getReactionChannelCategories();
        instance.getJDA().addEventListener(this);
    }

    public void registerCategoryListener(String categoryName, ReactionChannelListener listener) {
        this.listeners.put(categoryName.toLowerCase(), listener);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        ReactionChannelCategory category = this.reactionChannelCategories.stream()
                .filter(value -> value.getChannels().contains(event.getChannel().getIdLong())).findFirst().orElse(null);

        if(category == null)
            return;

        if(this.listeners.containsKey(category.getName()))
            this.listeners.get(category.getName()).handleMessageSent(event, category);

        for(ReactionChannelReaction reaction : category.getReactions())
            event.getMessage().addReaction(reaction.getEmote()).queue();
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if(event.getUser().isBot())
            return;

        TheSystemsBot.THREAD_POOL.execute(() -> {
            ReactionChannelCategory category = this.reactionChannelCategories.stream()
                    .filter(value -> value.getChannels().contains(event.getChannel().getIdLong())).findFirst().orElse(null);

            if(category == null)
                return;

            String reactionUnicode = event.getReactionEmote().getName();
            ReactionChannelListener listener = this.listeners.get(category.getName().toLowerCase());

            // getting an already existing reaction of the user, which is not the new added one
            Message message = event.getChannel().getMessageById(event.getMessageId()).complete();
            MessageReaction existingReaction = message.getReactions().stream().filter(value -> !value.getReactionEmote().getName()
                    .equalsIgnoreCase(reactionUnicode) && value.getUsers().complete().contains(event.getUser())).findFirst().orElse(null);

            ReactionChannelReaction reaction = category.getReactions().stream()
                    .filter(value -> value.getEmote().equalsIgnoreCase(reactionUnicode)).findFirst().orElse(null);

            // checking if the user has already reacted, if it's his own message or the reaction is not allowed
            if(message.getMember().equals(event.getMember()) || existingReaction != null
                    || reaction == null || !this.instance.memberHasRole(event.getMember(), reaction.getAllowedRanks())) {
                // checking if the the reaction should be removed
                if(listener == null || listener.handleReactionAddedForbidden(event, category))
                    event.getReaction().removeReaction(event.getUser()).queue();
            } else if(listener != null)
                listener.handleReactionAddedAllowed(event, category);
        });
    }

    @Override
    public void handleStop() { }


}
