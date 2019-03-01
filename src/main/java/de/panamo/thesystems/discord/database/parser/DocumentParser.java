package de.panamo.thesystems.discord.database.parser;

public interface DocumentParser<DatabaseObject, Document> {

    DatabaseObject fromDocument(Document document);

    Document toDocument(String key, DatabaseObject databaseObject);

}
