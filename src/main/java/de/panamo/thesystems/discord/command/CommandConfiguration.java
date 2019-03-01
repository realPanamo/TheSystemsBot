package de.panamo.thesystems.discord.command;

import de.panamo.thesystems.discord.command.info.Command;
import de.panamo.thesystems.discord.configuration.GeneralConfiguration;
import java.util.Collection;
import java.util.HashSet;

public class CommandConfiguration implements GeneralConfiguration {
    private String commandIdentifier = "!";
    private Collection<Command> commands = new HashSet<>();
    private Collection<Long> ignoredChannels = new HashSet<>();

    String getCommandIdentifier() {
        return commandIdentifier;
    }

    public void setCommandIdentifier(String commandIdentifier) {
        this.commandIdentifier = commandIdentifier;
    }

    Collection<Command> getCommands() {
        return commands;
    }

    Collection<Long> getIgnoredChannels() {
        return ignoredChannels;
    }
}
