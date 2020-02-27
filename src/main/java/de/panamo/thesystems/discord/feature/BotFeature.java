package de.panamo.thesystems.discord.feature;

import de.panamo.thesystems.discord.TheSystemsBot;
import de.panamo.thesystems.discord.configuration.GeneralConfiguration;

public interface BotFeature<Configuration extends GeneralConfiguration> {

    void handleStart(TheSystemsBot instance, Configuration configuration);

    void handleStop();

    Configuration getConfiguration();

}
