package de.panamo.thesystems.discord.reactionchannel;

import de.panamo.thesystems.discord.TheSystemsBot;
import de.panamo.thesystems.discord.feature.BotFeature;
import de.panamo.thesystems.discord.reactionchannel.category.ReactionChannelCategory;
import de.panamo.thesystems.discord.reactionchannel.category.ReactionChannelReaction;
import de.panamo.thesystems.discord.reactionchannel.listener.ReactionChannelListener;
import de.panamo.thesystems.discord.reactionchannel.listener.TemplateListener;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveAllEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import java.util.*;
import java.util.function.Consumer;

public class ReactionChannelFeature extends ListenerAdapter implements BotFeature<ReactionChannelConfiguration> {
    private TheSystemsBot instance;
    private ReactionChannelConfiguration configuration;
    private Map<String, ReactionChannelListener> listeners = new HashMap<>();

    @Override
    public void handleStart(TheSystemsBot instance, ReactionChannelConfiguration configuration) {
        this.configuration = configuration;

        this.instance = instance;
        instance.getJDA().addEventListener(this);

        this.registerCategoryListener("suggestion", new TemplateListener());
        this.registerCategoryListener("bugreport", new TemplateListener());
    }

    public void registerCategoryListener(String categoryName, ReactionChannelListener listener) {
        this.listeners.put(categoryName.toLowerCase(), listener);
    }

    private ReactionChannelCategory getCategoryByChannel(MessageChannel channel) {
        return this.configuration.getReactionChannelCategories().stream()
                .filter(value -> value.getChannels().contains(channel.getIdLong())).findFirst().orElse(null);
    }

    private boolean equalEmotes(ReactionChannelReaction reaction, MessageReaction.ReactionEmote emote) {
        if(reaction.getEmoteType() == ReactionChannelReaction.EmoteType.UNICODE)
            return reaction.getEmote().equalsIgnoreCase(emote.getName());
        else
            return reaction.getEmote().equalsIgnoreCase(emote.getId());
    }

    private void addReactions(ReactionChannelCategory category, Message message) {
        List<ReactionChannelReaction> channelReactionsCopy = new ArrayList<>(category.getReactions());
        ReactionChannelReaction firstReaction = channelReactionsCopy.remove(0);

        Consumer<Void> successHandler = aVoid -> {
            for(ReactionChannelReaction reaction : channelReactionsCopy) {
                if(reaction.getEmoteType() == ReactionChannelReaction.EmoteType.UNICODE)
                    message.addReaction(reaction.getEmote()).queue();
                else if(reaction.getEmoteType() == ReactionChannelReaction.EmoteType.EMOTE_ID)
                    message.addReaction(message.getGuild().getEmoteById(reaction.getEmote())).queue();
            }
        };
        Consumer<Throwable> errorHandler = throwable -> message.delete().queue();

        if(firstReaction.getEmoteType() == ReactionChannelReaction.EmoteType.UNICODE)
            message.addReaction(firstReaction.getEmote()).queue(successHandler, errorHandler);
        else if(firstReaction.getEmoteType() == ReactionChannelReaction.EmoteType.EMOTE_ID)
            message.addReaction(message.getGuild().getEmoteById(firstReaction.getEmote())).queue(successHandler, errorHandler);
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if(event.getAuthor().isBot())
            return;

        ReactionChannelCategory category = this.getCategoryByChannel(event.getChannel());

        if(category == null || category.getReactions().isEmpty())
            return;

        if(this.listeners.containsKey(category.getName())) {
            if(!this.listeners.get(category.getName()).handleMessageSent(event.getMessage(), category))
                return;
        }

        this.addReactions(category, event.getMessage());
    }

    @Override
    public void onGuildMessageUpdate(GuildMessageUpdateEvent event) {
        ReactionChannelCategory category = this.getCategoryByChannel(event.getChannel());

        if(category != null && this.listeners.containsKey(category.getName()))
            this.listeners.get(category.getName()).handleMessageSent(event.getMessage(), category);
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if(event.getUser().isBot())
            return;

        TheSystemsBot.THREAD_POOL.execute(() -> {
            ReactionChannelCategory category = this.getCategoryByChannel(event.getChannel());

            if(category == null)
                return;

            MessageReaction.ReactionEmote emote = event.getReactionEmote();
            ReactionChannelListener listener = this.listeners.get(category.getName().toLowerCase());

            // getting an already existing reaction of the user, which is not the new added one
            Message message = event.getChannel().retrieveMessageById(event.getMessageId()).complete();
            MessageReaction existingReaction = message.getReactions().stream().filter(value ->
                    !value.getReactionEmote().equals(emote) && value.retrieveUsers().complete().contains(event.getUser())).findFirst().orElse(null);

            // getting the current reaction but as ReactionChannelReaction
            ReactionChannelReaction reaction = category.getReactions().stream()
                    .filter(value -> this.equalEmotes(value, emote)).findFirst().orElse(null);

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
    public void onGuildMessageReactionRemoveAll(GuildMessageReactionRemoveAllEvent event) {
        ReactionChannelCategory category = this.getCategoryByChannel(event.getChannel());

        if(category != null && !category.getReactions().isEmpty())
            event.getChannel().retrieveMessageById(event.getMessageIdLong()).queue(message -> this.addReactions(category, message));
    }

    @Override
    public void handleStop() {
        this.instance.getJDA().removeEventListener(this);
    }

    @Override
    public ReactionChannelConfiguration getConfiguration() {
        return this.configuration;
    }


}
