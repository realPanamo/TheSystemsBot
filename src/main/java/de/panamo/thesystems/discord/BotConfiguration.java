package de.panamo.thesystems.discord;

import de.panamo.thesystems.discord.configuration.GeneralConfiguration;

class BotConfiguration implements GeneralConfiguration {
    private String botToken, databaseHost, databaseUser, databasePassword, database;
    private int databasePort;

    BotConfiguration() {
        this.botToken = "TOKEN";
        this.databaseHost = "127.0.0.1";
        this.databaseUser = "root";
        this.databasePassword = "password";
        this.database = "thesystems";
        this.databasePort = 8529;
    }

    String getBotToken() {
        return botToken;
    }

    String getDatabaseHost() {
        return databaseHost;
    }

    String getDatabaseUser() {
        return databaseUser;
    }

    String getDatabasePassword() {
        return databasePassword;
    }

    String getDatabase() {
        return database;
    }

    int getDatabasePort() {
        return databasePort;
    }
}
