package de.panamo.thesystems.discord.command.info;

import net.dv8tion.jda.core.entities.Message;

public interface CommandExecutor {

    boolean handleCommandExecution(Message sentMessage, String[] args);

}
