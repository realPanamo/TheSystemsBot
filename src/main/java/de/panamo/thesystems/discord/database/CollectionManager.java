package de.panamo.thesystems.discord.database;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoDatabase;
import de.panamo.thesystems.discord.database.parser.DocumentParser;
import java.util.HashMap;
import java.util.Map;

public class CollectionManager<Key, DatabaseObject, Document> {
    protected ArangoCollection collection;
    protected Map<Key, DatabaseObject> cache = new HashMap<>();
    protected Class<? extends Document> documentClass;
    protected DocumentParser<DatabaseObject, Document> documentParser;

    public CollectionManager(ArangoDatabase database, String collection, Class<? extends Document> documentClass, DocumentParser<DatabaseObject, Document> documentParser) {
        if(!database.collection(collection).exists())
            database.createCollection(collection);
        this.collection = database.collection(collection);

        this.documentClass = documentClass;
        this.documentParser = documentParser;
    }

    public boolean exists(Key key) {
        return this.collection.documentExists(key.toString());
    }

    public void insert(Key key, DatabaseObject databaseObject) {
        if(!this.exists(key)) {
            Document document = this.documentParser.toDocument(key.toString(), databaseObject);
            this.collection.insertDocument(document);
        }
    }

    public DatabaseObject get(Key key, boolean cache) {
        if(this.cache.containsKey(key))
            return this.cache.get(key);

        if(this.collection.documentExists(key.toString())) {
            Document document = this.collection.getDocument(key.toString(), this.documentClass);
            DatabaseObject databaseObject = this.documentParser.fromDocument(document);
            if(cache)
                this.cache.put(key, databaseObject);
            return databaseObject;
        }
        return null;
    }

    public void save(Key key) {
        if(this.cache.containsKey(key))
            this.save(key, this.cache.get(key));
    }

    public void save(Key key, DatabaseObject databaseObject) {
        Document document = this.documentParser.toDocument(key.toString(), databaseObject);
        this.collection.updateDocument(key.toString(), document);
    }

    public void delete(Key key) {
        this.collection.deleteDocument(key.toString());
    }

    public ArangoCollection getCollection() {
        return collection;
    }

    public Map<Key, DatabaseObject> getCache() {
        return cache;
    }
}
