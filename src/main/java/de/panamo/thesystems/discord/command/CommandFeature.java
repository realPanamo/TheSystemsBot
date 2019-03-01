package de.panamo.thesystems.discord.command;

import de.panamo.thesystems.discord.TheSystemsBot;
import de.panamo.thesystems.discord.command.info.Command;
import de.panamo.thesystems.discord.command.info.CommandTarget;
import de.panamo.thesystems.discord.feature.BotFeature;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CommandFeature extends ListenerAdapter implements BotFeature<CommandConfiguration> {
    private TheSystemsBot instance;
    private String commandIdentifier;
    private Map<String, Command> commands = new HashMap<>();
    private Collection<Long> ignoredChannels;

    @Override
    public void handleStart(TheSystemsBot instance, CommandConfiguration configuration) {
        this.instance = instance;
        this.commandIdentifier = configuration.getCommandIdentifier();
        this.ignoredChannels = configuration.getIgnoredChannels();
        for(Command command : configuration.getCommands()) {
            this.commands.put(command.getName().toLowerCase(), command);
            for(String commandAlias : command.getAliases())
                this.commands.put(commandAlias.toLowerCase(), command);
        }

        this.readConsole();
    }

    public Command getCommand(String name) {
        return this.commands.get(name.toLowerCase());
    }

    private void executeCommandByText(String text, Message message, CommandTarget target) {
        boolean discordCommand = message != null;
        if(discordCommand && this.ignoredChannels.contains(message.getChannel().getIdLong()))
            return;

        String[] args = text.split(" ");
        if(args.length > 0) {
            String commandName = args[0].toLowerCase();
            if(commandName.startsWith(this.commandIdentifier)) {
                Command command = this.getCommand(commandName.replace(this.commandIdentifier, ""));
                if(command != null && command.getTargets().contains(target)) {
                    if(discordCommand && !this.instance.memberHasRole(message.getMember(), command.getAllowedRanks()))
                        return;

                    if(!command.getCommandExecutor().handleCommandExecution(message, Arrays.copyOfRange(args, 1, args.length))) {
                        if(discordCommand) {
                            EmbedBuilder embedBuilder = new EmbedBuilder();
                            embedBuilder.setColor(Color.RED);
                            embedBuilder.addField("syntax", "Usage: " + command.getSyntax(), true);
                            embedBuilder.setFooter(message.getMember().getNickname(), message.getMember().getUser().getAvatarUrl());
                            message.getTextChannel().sendMessage(embedBuilder.build()).queue();
                        } else
                            System.out.println("Usage: " + command.getSyntax());
                    }
                }
            }
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        this.executeCommandByText(event.getMessage().getContentRaw(), event.getMessage(), CommandTarget.GUILD);
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        this.executeCommandByText(event.getMessage().getContentRaw(), event.getMessage(), CommandTarget.DIRECT_MESSAGE);
    }

    public void readConsole() {
        TheSystemsBot.THREAD_POOL.execute(() -> {
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                String line;
                while ((line = reader.readLine()) != null)
                    this.executeCommandByText(line, null, CommandTarget.CONSOLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void handleStop() { }
}
