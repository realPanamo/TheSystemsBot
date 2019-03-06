package de.panamo.thesystems.discord.poll;


import de.panamo.thesystems.discord.TheSystemsBot;
import de.panamo.thesystems.discord.command.CommandFeature;
import de.panamo.thesystems.discord.feature.BotFeature;
import de.panamo.thesystems.discord.poll.command.PollCommandExecutor;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class PollFeature extends ListenerAdapter implements BotFeature<PollConfiguration> {
    private TheSystemsBot instance;
    private PollConfiguration configuration;

    @Override
    public void handleStart(TheSystemsBot instance, PollConfiguration configuration) {
        this.instance = instance;
        this.configuration = configuration;

        instance.getFeature(CommandFeature.class).getCommand("poll").setCommandExecutor(new PollCommandExecutor().setInstance(instance));

        this.instance.getJDA().addEventListener(this);
    }

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
        if(event.getUser().isBot())
            return;

        TheSystemsBot.THREAD_POOL.execute(() -> {
            Message message = event.getChannel().getMessageById(event.getMessageId()).complete();
            // the get the fresh reaction on the message
            MessageReaction reaction = message.getReactions().stream().filter(value ->
                    value.getReactionEmote().equals(event.getReaction().getReactionEmote())).findFirst().orElse(event.getReaction());

            if(!message.getAuthor().equals(event.getJDA().getSelfUser()))
                return;

            if(message.getEmbeds().size() > 0 && message.getEmbeds().get(0).getTitle() != null) {
                MessageEmbed embed = message.getEmbeds().get(0);
                if(embed.getTitle().equalsIgnoreCase(this.configuration.getPollTitle())) {
                    // searching for an already existing reaction of the user, which is not the new added one
                    MessageReaction existingReaction = message.getReactions().stream().filter(value ->
                            !value.getReactionEmote().equals(reaction.getReactionEmote()) && value.getUsers().complete().contains(event.getUser()))
                            .findFirst().orElse(null);

                    // removing the reaction if the user already reacted or he tries to add a new reaction
                    if(existingReaction != null || reaction.getCount() == 1)
                        reaction.removeReaction(event.getUser()).queue();
                } else if (embed.getTitle().equalsIgnoreCase(this.configuration.getMultiAnswerPollTitle()) && reaction.getCount() == 1)
                    reaction.removeReaction(event.getUser()).queue();
            }
        });
    }

    @Override
    public void handleStop() {
        this.instance.getJDA().removeEventListener(this);
    }

    @Override
    public PollConfiguration getConfiguration() {
        return this.configuration;
    }
}
