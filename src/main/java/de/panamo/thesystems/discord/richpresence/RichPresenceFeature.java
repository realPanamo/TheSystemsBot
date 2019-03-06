package de.panamo.thesystems.discord.richpresence;

import de.panamo.thesystems.discord.TheSystemsBot;
import de.panamo.thesystems.discord.command.CommandFeature;
import de.panamo.thesystems.discord.feature.BotFeature;
import de.panamo.thesystems.discord.richpresence.command.PresenceCommandExecutor;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class RichPresenceFeature implements BotFeature<RichPresenceConfiguration> {
    private RichPresenceConfiguration configuration;
    private Timer timer = new Timer();
    private Random random = new Random();

    @Override
    public void handleStart(TheSystemsBot instance, RichPresenceConfiguration configuration) {
        this.configuration = configuration;

        List<RichPresenceConfiguration.RichPresence> richPresences = configuration.getRichPresences();
        if(richPresences.isEmpty())
            return;

        this.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                RichPresenceConfiguration.RichPresence randomPresence = richPresences.get(random.nextInt(richPresences.size()));
                instance.getJDA().getPresence().setGame(randomPresence.toGame());
            }
        }, 0, configuration.getChangeMillis());

        instance.getFeature(CommandFeature.class).getCommand("presence").setCommandExecutor(new PresenceCommandExecutor().setInstance(instance));
    }

    @Override
    public void handleStop() {
        this.timer.cancel();
    }

    @Override
    public RichPresenceConfiguration getConfiguration() {
        return this.configuration;
    }
}
