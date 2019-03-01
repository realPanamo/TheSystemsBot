package de.panamo.thesystems.discord.reactionchannel;

import de.panamo.thesystems.discord.TheSystemsBot;
import de.panamo.thesystems.discord.feature.BotFeature;
import de.panamo.thesystems.discord.reactionchannel.category.ReactionChannelCategory;
import de.panamo.thesystems.discord.reactionchannel.category.ReactionChannelReaction;
import de.panamo.thesystems.discord.reactionchannel.listener.ReactionChannelListener;
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

        this.listeners.get(category.getName()).handleMessageSent(event, category);

        for(ReactionChannelReaction reaction : category.getReactions())
            event.getMessage().addReaction(reaction.getEmote()).queue();
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        ReactionChannelCategory category = this.reactionChannelCategories.stream()
                .filter(value -> value.getChannels().contains(event.getChannel().getIdLong())).findFirst().orElse(null);

        if(category == null)
            return;

        ReactionChannelListener listener = this.listeners.get(category.getName().toLowerCase());

        ReactionChannelReaction reaction = category.getReactions().stream().filter(value -> value.getEmote()
                .equalsIgnoreCase(event.getReactionEmote().getName())).findFirst().orElse(null);

        if(reaction == null || !this.instance.memberHasRole(event.getMember(), reaction.getAllowedRanks())) {
            if(listener.handleReactionAddedForbidden(event, category))
                event.getReaction().removeReaction().queue();
        } else
            listener.handleReactionAddedAllowed(event, category);

    }

    @Override
    public void handleStop() { }


}
