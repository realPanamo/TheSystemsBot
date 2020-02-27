package de.panamo.thesystems.discord.command.info;

import java.util.Collection;

public class Command {
    private String name, syntax, description;
    private Collection<String> aliases;
    private Collection<CommandTarget> targets;
    private Collection<Long> allowedRanks;

    private transient CommandExecutor commandExecutor;

    public Command(String name, String syntax, String description, Collection<String> aliases, Collection<CommandTarget> targets, Collection<Long> allowedRanks) {
        this.name = name;
        this.syntax = syntax;
        this.description = description;
        this.aliases = aliases;
        this.targets = targets;
        this.allowedRanks = allowedRanks;
    }

    public String getName() {
        return name;
    }

    public String getSyntax() {
        return syntax;
    }

    public String getDescription() {
        return description;
    }

    public Collection<String> getAliases() {
        return aliases;
    }

    public Collection<CommandTarget> getTargets() {
        return targets;
    }

    public Collection<Long> getAllowedRanks() {
        return allowedRanks;
    }

    public void setCommandExecutor(CommandExecutor commandExecutor) {
        this.commandExecutor = commandExecutor;
    }

    public CommandExecutor getCommandExecutor() {
        return commandExecutor;
    }


    public enum CommandTarget {

        GUILD,
        DIRECT_MESSAGE,
        CONSOLE

    }
}
