package de.panamo.thesystems.discord.command.info;

import de.panamo.thesystems.discord.TheSystemsBot;
import net.dv8tion.jda.core.entities.Message;

public abstract class CommandExecutor {
    protected TheSystemsBot instance;


    /**
     * Handles an execution of a command
     *
     * @param message discord-message which triggered the command
     * @param args the arguments the command was executed with
     * @return if the command was executed correctly
     */

    public abstract boolean handleCommandExecution(Command command, Message message, String[] args);

    public CommandExecutor setInstance(TheSystemsBot instance) {
        this.instance = instance;
        return this;
    }

}
