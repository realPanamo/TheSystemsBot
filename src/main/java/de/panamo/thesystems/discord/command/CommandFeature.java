package de.panamo.thesystems.discord.command;

import de.panamo.thesystems.discord.TheSystemsBot;
import de.panamo.thesystems.discord.command.single.PollCommandExecutor;
import de.panamo.thesystems.discord.richpresence.command.PresenceCommandExecutor;
import de.panamo.thesystems.discord.command.info.Command;
import de.panamo.thesystems.discord.feature.BotFeature;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CommandFeature extends ListenerAdapter implements BotFeature<CommandConfiguration> {
    private TheSystemsBot instance;
    private CommandConfiguration configuration;
    private Map<String, Command> commands = new HashMap<>();

    private boolean readConsole;

    @Override
    public void handleStart(TheSystemsBot instance, CommandConfiguration configuration) {
        this.instance = instance;
        this.configuration = configuration;
        for(Command command : configuration.getCommands()) {
            this.commands.put(command.getName().toLowerCase(), command);
            for(String commandAlias : command.getAliases())
                this.commands.put(commandAlias.toLowerCase(), command);
        }

        this.getCommand("presence").setCommandExecutor(new PresenceCommandExecutor().setInstance(instance));
        this.getCommand("poll").setCommandExecutor(new PollCommandExecutor().setInstance(instance));

        instance.getJDA().addEventListener(this);
        this.readConsole();
    }

    public Command getCommand(String name) {
        return this.commands.get(name.toLowerCase());
    }

    private void executeCommandByText(String text, Message message, Command.CommandTarget target) {
        boolean discordCommand = message != null;
        if(discordCommand && this.configuration.getIgnoredChannels().contains(message.getChannel().getIdLong()))
            return;

        String[] args = text.split(" ");
        if(args.length > 0) {
            String commandName = args[0].toLowerCase();
            if(commandName.startsWith(this.configuration.getCommandIdentifier())) {
                Command command = this.getCommand(commandName.replace(this.configuration.getCommandIdentifier(), ""));
                if(command != null && command.getTargets().contains(target)) {
                    if(discordCommand && !this.instance.userHasRole(message.getAuthor(), command.getAllowedRanks()))
                        return;

                    if(!command.getCommandExecutor().handleCommandExecution(command, message, Arrays.copyOfRange(args, 1, args.length))) {
                        if(discordCommand) {
                            EmbedBuilder embedBuilder = new EmbedBuilder();
                            embedBuilder.setTitle(command.getName() + " | Info");
                            embedBuilder.setColor(Color.RED);
                            embedBuilder.setDescription(command.getDescription());
                            embedBuilder.addField("Usage", command.getSyntax(), false);
                            embedBuilder.setFooter(message.getAuthor().getAsTag(), message.getAuthor().getAvatarUrl());
                            message.getChannel().sendMessage(embedBuilder.build()).queue(embedMessage ->
                                    embedMessage.delete().queueAfter(7, TimeUnit.SECONDS));
                        } else
                            System.out.println("Usage: " + command.getSyntax());
                    }
                }
            }
        }
    }

    private void readConsole() {
        this.readConsole = true;

        TheSystemsBot.THREAD_POOL.execute(() -> {
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                String line;
                while ((line = reader.readLine()) != null && this.readConsole)
                    this.executeCommandByText(line, null, Command.CommandTarget.CONSOLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        this.executeCommandByText(event.getMessage().getContentRaw(), event.getMessage(), Command.CommandTarget.GUILD);
    }

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
        this.executeCommandByText(event.getMessage().getContentRaw(), event.getMessage(), Command.CommandTarget.DIRECT_MESSAGE);
    }

    @Override
    public void handleStop() {
        this.instance.getJDA().removeEventListener(this);
        this.readConsole = false;
    }

    @Override
    public CommandConfiguration getConfiguration() {
        return this.configuration;
    }
}
