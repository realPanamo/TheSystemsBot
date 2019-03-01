package de.panamo.thesystems.discord.database.parser;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class GsonDocumentParser<DatabaseObject> implements DocumentParser<DatabaseObject, String> {
    private Gson gson;
    private Class<? extends DatabaseObject> databaseObjectClass;

    public GsonDocumentParser(Gson gson, Class<? extends DatabaseObject> databaseObjectClass) {
        this.gson = gson;
        this.databaseObjectClass = databaseObjectClass;
    }

    public void setDatabaseObjectClass(Class<? extends DatabaseObject> databaseObjectClass) {
        this.databaseObjectClass = databaseObjectClass;
    }

    @Override
    public DatabaseObject fromDocument(String json) {
        return this.gson.fromJson(json, databaseObjectClass);
    }

    @Override
    public String toDocument(String key, DatabaseObject databaseObject) {
        JsonElement jsonElement = this.gson.toJsonTree(databaseObject);
        if(!jsonElement.isJsonObject())
            throw new IllegalArgumentException("DatabaseObject is not a JSON object!");

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        jsonObject.addProperty("_key", key);

        return jsonObject.toString();
    }
}
