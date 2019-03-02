package de.panamo.thesystems.discord.reactionchannel.listener;

import de.panamo.thesystems.discord.reactionchannel.category.ReactionChannelCategory;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import java.awt.*;
import java.util.concurrent.TimeUnit;

public class TemplateListener extends ReactionChannelListener {

    @Override
    public boolean handleMessageSent(MessageReceivedEvent event, ReactionChannelCategory category) {
        Message message = event.getMessage();
        String messageText = message.getContentRaw().toLowerCase();
        boolean correctTemplate = messageText.startsWith("e:") && messageText.contains("w:")
                || messageText.startsWith("a:") && messageText.contains("y:");
        if(!correctTemplate) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Please stick to the template!");
            embedBuilder.setColor(Color.RED);
            embedBuilder.setDescription("A(rea) | E(bene): \nY(our Idea) | W(as):");
            embedBuilder.setFooter(message.getAuthor().getAsTag(), message.getAuthor().getAvatarUrl());
            message.getTextChannel().sendMessage(embedBuilder.build()).queue(botMessage ->
                    botMessage.delete().queueAfter(5, TimeUnit.SECONDS));

            message.delete().queue();
        }
        return correctTemplate;
    }
}
