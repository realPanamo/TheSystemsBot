package de.panamo.thesystems.discord.configuration.provider;

import com.arangodb.ArangoDatabase;
import de.panamo.thesystems.discord.TheSystemsBot;
import de.panamo.thesystems.discord.configuration.GeneralConfiguration;
import de.panamo.thesystems.discord.database.CollectionManager;
import de.panamo.thesystems.discord.database.parser.GsonDocumentParser;

public class ConfigurationProvider extends CollectionManager<String, GeneralConfiguration, String> {

    public ConfigurationProvider(ArangoDatabase database) {
        super(database, "bot-configuration", String.class, new GsonDocumentParser<>(TheSystemsBot.GSON, GeneralConfiguration.class));
    }

    public <Configuration extends GeneralConfiguration> Configuration getConfiguration(String name, Class<Configuration> documentClass) {
        if(this.cache.containsKey(name))
            return (Configuration) this.cache.get(name);

        if(this.collection.documentExists(name)) {
            String document = this.collection.getDocument(name, String.class);
            GsonDocumentParser<Configuration> parser = (GsonDocumentParser<Configuration>) this.documentParser;
            parser.setDatabaseObjectClass(documentClass);
            Configuration configuration = parser.fromDocument(document);
            this.cache.put(name, configuration);
            return configuration;
        }
        return null;
    }

}
