package de.panamo.thesystems.discord.richpresence;


import de.panamo.thesystems.discord.configuration.GeneralConfiguration;
import net.dv8tion.jda.api.entities.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RichPresenceConfiguration implements GeneralConfiguration {
    private long changeMillis = TimeUnit.SECONDS.toMillis(30);
    private List<RichPresence> richPresences = new ArrayList<>();

    public long getChangeMillis() {
        return changeMillis;
    }

    public List<RichPresence> getRichPresences() {
        return richPresences;
    }

    public static class RichPresence {

        private Activity.ActivityType activityType;
        private String text;

        public RichPresence(Activity.ActivityType activityType, String text) {
            this.activityType = activityType;
            this.text = text;
        }

        public String getText() {
            return text;
        }

        Activity toActivity() {
            return Activity.of(this.activityType, this.text);
        }

    }

}
