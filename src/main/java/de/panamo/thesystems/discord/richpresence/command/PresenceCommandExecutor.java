package de.panamo.thesystems.discord.richpresence.command;

import de.panamo.thesystems.discord.command.info.Command;
import de.panamo.thesystems.discord.command.info.CommandExecutor;
import de.panamo.thesystems.discord.richpresence.RichPresenceConfiguration;
import de.panamo.thesystems.discord.richpresence.RichPresenceFeature;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Message;
import java.awt.*;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PresenceCommandExecutor extends CommandExecutor {


    @Override
    public boolean handleCommandExecution(Command command, Message message, String[] args) {
        RichPresenceFeature richPresenceFeature = super.instance.getFeature(RichPresenceFeature.class);
        if(args.length > 0) {
            String commandName = args[0].toLowerCase();
            switch (commandName) {
                case "add":
                    if(args.length > 2) {
                        Game.GameType gameType;
                        try {
                            gameType = Game.GameType.valueOf(args[1].toUpperCase());
                        } catch (Exception exception) {
                            this.basicResponseEmbed(message, Color.RED, "Wrong gameTye!", "Available types: " +
                                    Arrays.stream(Game.GameType.values()).map(Enum::toString).collect(Collectors.joining(", ")));
                            return true;
                        }
                        richPresenceFeature.getConfiguration().getRichPresences().add(
                                new RichPresenceConfiguration.RichPresence(gameType, this.collectText(args, 2)));
                        super.instance.getConfigurationProvider().save("richpresence", richPresenceFeature.getConfiguration());
                        this.basicResponseEmbed(message, Color.GREEN, "Success!", "Presence was added successfully!");
                    } else
                        this.basicResponseEmbed(message, Color.RED, "Wrong subcommand syntax!", String.format("Usage: %s add <gameType> <text>", command.getName()));
                    return true;
                case "remove":
                    if(args.length > 1) {
                        String text = this.collectText(args, 1);

                        RichPresenceConfiguration.RichPresence presence = richPresenceFeature.getConfiguration().getRichPresences()
                                .stream().filter(value -> value.getText().equalsIgnoreCase(text)).findFirst().orElse(null);

                        if(presence != null) {
                            richPresenceFeature.getConfiguration().getRichPresences().remove(presence);
                            super.instance.getConfigurationProvider().save("richpresence", richPresenceFeature.getConfiguration());
                            this.basicResponseEmbed(message, Color.GREEN, "Success!", "Presence was removed successfully!");
                        } else
                            this.basicResponseEmbed(message, Color.RED, "Could not remove presence!", "No presence found!");

                    } else
                        this.basicResponseEmbed(message, Color.RED, "Wrong subcommand syntax!",  String.format("Usage: %s remove <text>", command.getName()));
                    return true;
                    default:
                        return false;
            }

        }
        return false;
    }

    private String collectText(String[] args, int startIndex) {
        return String.join(" ", Arrays.copyOfRange(args, startIndex, args.length)).trim();
    }

    private void basicResponseEmbed(Message message, Color color, String title, String description) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(title);
        embedBuilder.setColor(color);
        embedBuilder.setDescription(description);
        embedBuilder.setFooter(message.getAuthor().getAsTag(), message.getAuthor().getAvatarUrl());
        message.getChannel().sendMessage(embedBuilder.build()).queue(embedMessage ->
                embedMessage.delete().queueAfter(7, TimeUnit.SECONDS));
    }
}
