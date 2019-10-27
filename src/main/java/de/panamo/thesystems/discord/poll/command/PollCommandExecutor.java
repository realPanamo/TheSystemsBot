package de.panamo.thesystems.discord.poll.command;

import de.panamo.thesystems.discord.command.info.Command;
import de.panamo.thesystems.discord.command.info.CommandExecutor;
import de.panamo.thesystems.discord.poll.PollConfiguration;
import de.panamo.thesystems.discord.poll.PollFeature;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import java.awt.*;
import java.time.Instant;
import java.util.Arrays;

public class PollCommandExecutor extends CommandExecutor {


    @Override
    public boolean handleCommandExecution(Command command, Message message, String[] args) {
        if(args.length > 0) {
            PollConfiguration configuration = super.instance.getFeature(PollFeature.class).getConfiguration();

            User user = message.getAuthor();

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setAuthor(user.getAsTag(), user.getAvatarUrl(), user.getAvatarUrl());
            embedBuilder.setColor(Color.green);
            embedBuilder.setTimestamp(Instant.now());

            String argumentText = String.join(" ", args).trim();
            if(argumentText.contains("|")) {

                embedBuilder.setTitle(configuration.getMultiAnswerPollTitle());

                String[] splitText = argumentText.split("\\|");
                if(splitText.length < 3 || splitText.length > 21)
                    return false;

                embedBuilder.addField("Question", splitText[0], false);

                String[] answerOptions = Arrays.copyOfRange(splitText, 1, splitText.length);
                StringBuilder builder = new StringBuilder();
                for(int i = 0; i < answerOptions.length; i++)
                    builder.append(this.getLetterUnicodeByIndex(i)).append(" ").append(answerOptions[i].trim()).append("\n");

                embedBuilder.addField("Answers", builder.toString(), false);

                message.getTextChannel().sendMessage(embedBuilder.build()).queue(newMessage -> {
                    for(int i = 0; i < answerOptions.length; i++)
                        newMessage.addReaction(this.getLetterUnicodeByIndex(i)).queue();

                    message.delete().queue();
                });

            } else {
                String pollQuestion = String.join(" ", args);
                embedBuilder.setTitle(configuration.getPollTitle());
                embedBuilder.addField("Question", pollQuestion, false);

                message.getTextChannel().sendMessage(embedBuilder.build()).queue(newMessage -> {
                    newMessage.addReaction(super.instance.getMainGuild().getEmoteById(configuration.getPositiveEmoteId())).queue();
                    newMessage.addReaction(super.instance.getMainGuild().getEmoteById(configuration.getNegativeEmoteId())).queue();

                    message.delete().queue();
                });
            }
            return true;
        }
        return false;
    }

    private String getLetterUnicodeByIndex(int index) {
        return "\uD83C" + (char)('\uDDE6' + index);
    }

}
