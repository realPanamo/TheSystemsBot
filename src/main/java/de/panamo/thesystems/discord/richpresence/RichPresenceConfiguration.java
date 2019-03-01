package de.panamo.thesystems.discord.richpresence;


import de.panamo.thesystems.discord.configuration.GeneralConfiguration;
import net.dv8tion.jda.core.entities.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RichPresenceConfiguration implements GeneralConfiguration {
    private long changeMillis = TimeUnit.SECONDS.toMillis(30);
    private List<RichPresence> richPresences = new ArrayList<>();

    long getChangeMillis() {
        return changeMillis;
    }

    List<RichPresence> getRichPresences() {
        return richPresences;
    }

    public class RichPresence {

        private Game.GameType gameType;
        private String text;

        public RichPresence(Game.GameType gameType, String text) {
            this.gameType = gameType;
            this.text = text;
        }

        Game toGame() {
            return Game.of(this.gameType, this.text);
        }

    }

}
