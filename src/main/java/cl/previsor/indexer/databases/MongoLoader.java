package cl.previsor.indexer.databases;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import cl.previsor.indexer.misc.MongoConnector;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de cargar los datos de una base de datos MongoDB
 */
public class MongoLoader {
    //Atributos
    private MongoConnector connector;
    private MongoCollection<Document> tweets;

    //Constructor
    public MongoLoader(String host, String port, String dbName, String collName){
        //Se crea la conexión a MongoDB
        this.connector = new MongoConnector(host, port, dbName, collName);
        //Se recuperan los Tweets almacenados en MongoDB
        this.tweets = connector.getCollection();
    }

    /**
     * Método que almacena los Tweets en una lista de la clase Tweet
     */
    public List<Tweet> getTweets(){
        List<Tweet> tweets = new ArrayList<>();
        Document tweetDoc;
        Tweet tweet;
        MongoCursor<Document> tweetIterator = this.tweets.find().iterator();
        while(tweetIterator.hasNext()){
            tweetDoc = tweetIterator.next();
            tweet = new Tweet(tweetDoc);
            tweets.add(tweet);
        }
        return tweets;
    }

    public MongoConnector getConnector() {
        return connector;
    }

    public void setConnector(MongoConnector connector) {
        this.connector = connector;
    }

    public void setTweets(MongoCollection<Document> tweets) {
        this.tweets = tweets;
    }

    public MongoCollection<Document> getCollection(){
        return this.tweets;
    }
}
