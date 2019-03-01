package de.panamo.thesystems.discord.configuration.provider;

import de.panamo.thesystems.discord.TheSystemsBot;
import de.panamo.thesystems.discord.configuration.GeneralConfiguration;

import java.io.*;

public class FileConfigurationProvider {

    public static <Configuration extends GeneralConfiguration> Configuration getFileConfiguration(File file, Class<Configuration> documentClass) {
        try(FileReader fileReader = new FileReader(file)) {
            return TheSystemsBot.GSON.fromJson(fileReader, documentClass);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <Configuration extends GeneralConfiguration> void saveFileConfiguration(File file, Configuration configuration) {
        try(FileWriter fileWriter = new FileWriter(file)) {
            if(!file.exists()) {
                if(!file.createNewFile())
                    return;
            }
            TheSystemsBot.GSON.toJson(configuration, fileWriter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
