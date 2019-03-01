package de.panamo.thesystems.discord;

import com.arangodb.ArangoDB;
import com.arangodb.ArangoDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.panamo.thesystems.discord.configuration.provider.ConfigurationProvider;
import de.panamo.thesystems.discord.configuration.provider.FileConfigurationProvider;
import de.panamo.thesystems.discord.configuration.GeneralConfiguration;
import de.panamo.thesystems.discord.feature.BotFeature;
import de.panamo.thesystems.discord.reactionchannel.ReactionChannelConfiguration;
import de.panamo.thesystems.discord.reactionchannel.ReactionChannelFeature;
import de.panamo.thesystems.discord.richpresence.RichPresenceConfiguration;
import de.panamo.thesystems.discord.richpresence.RichPresenceFeature;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Member;
import javax.security.auth.login.LoginException;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TheSystemsBot {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void main(String[] args) {
        try {
            new TheSystemsBot();
        } catch (LoginException exception) {
            System.out.println("Failed to login to discord!");
            exception.printStackTrace();
        }
    }

    private JDA jda;
    private ArangoDatabase database;

    private Map<Class<? extends BotFeature>, BotFeature> features = new HashMap<>();
    private ConfigurationProvider configCollectionManager;

    private TheSystemsBot() throws LoginException {
        System.out.println("Loading Config...");

        BotConfiguration botConfiguration = this.loadConfiguration();

        System.out.println("Connecting to Discord...");

        jda = new JDABuilder()
                .setToken(botConfiguration.getBotToken())
                .setStatus(OnlineStatus.ONLINE)
                .setAutoReconnect(true)
                .setEnableShutdownHook(true)
                .build();

        Runtime.getRuntime().addShutdownHook(new Thread(this.handleShutdown()));

        System.out.println("Connecting to ArangoDB...");

        ArangoDB arangoDB = new ArangoDB.Builder().host(botConfiguration.getDatabaseHost(), botConfiguration.getDatabasePort())
                .user(botConfiguration.getDatabaseUser())
                .password(botConfiguration.getDatabasePassword())
                .build();

        String database = botConfiguration.getDatabase();

        this.database = arangoDB.db(database);
        if(!this.database.exists()) {
            arangoDB.createDatabase(database);
            this.database = arangoDB.db(database);
        }

        System.out.println("Successfully connected!");

        this.configCollectionManager = new ConfigurationProvider(this.database);

        try {
            this.jda.awaitReady();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Adding features...");

        this.addFeature(new RichPresenceFeature(), "richpresence", new RichPresenceConfiguration());
        this.addFeature(new ReactionChannelFeature(), "reactionchannel", new ReactionChannelConfiguration());
        this.jda.addEventListener();
    }

    private BotConfiguration loadConfiguration() {
        File configurationFile = new File("config.json");
        BotConfiguration botConfiguration = new BotConfiguration();

        if(!configurationFile.exists()) {
            FileConfigurationProvider.saveFileConfiguration(configurationFile, botConfiguration);
            return botConfiguration;
        } else {
            BotConfiguration savedConfiguration = FileConfigurationProvider.getFileConfiguration(configurationFile, BotConfiguration.class);
            if(savedConfiguration != null)
                return savedConfiguration;
        }
        return botConfiguration;
    }

    private Runnable handleShutdown() {
        return () -> {
            for(BotFeature feature : this.features.values())
                feature.handleStop();
        };
    }

    public boolean memberHasRole(Member user, Collection<Long> roleIds) {
        return roleIds.isEmpty() || user.getRoles().stream().filter(role -> roleIds.contains(role.getIdLong())).findFirst().orElse(null) != null;
    }

    public <Configuration extends GeneralConfiguration> void addFeature(BotFeature<Configuration> botFeature, String configName, Configuration defaultConfiguration) {
        boolean existsConfig = this.configCollectionManager.exists(configName);
        if(!existsConfig)
            this.configCollectionManager.insert(configName, defaultConfiguration);
        Configuration configuration = existsConfig ? this.configCollectionManager
                .getConfiguration(configName, (Class<? extends Configuration>) defaultConfiguration.getClass()) : defaultConfiguration;
        botFeature.handleStart(this, configuration);
        System.out.println("Started feature " + botFeature.getClass().getSimpleName());
        this.features.put(botFeature.getClass(), botFeature);
    }

    public <Feature extends BotFeature> Feature getFeature(Class<Feature> featureClass) {
        if(!this.features.containsKey(featureClass))
            return null;
        return (Feature) this.features.get(featureClass);
    }

    public JDA getJDA() {
        return jda;
    }

    public ArangoDatabase getDatabase() {
        return database;
    }

    public Map<Class<? extends BotFeature>, BotFeature> getFeatures() {
        return features;
    }

    public ConfigurationProvider getConfigCollectionManager() {
        return configCollectionManager;
    }
}