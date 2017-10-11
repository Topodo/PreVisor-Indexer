package cl.previsor.indexer;

import cl.previsor.indexer.databases.MongoLoader;
import cl.previsor.indexer.databases.Tweet;
import cl.previsor.indexer.indexer.TweetIndexer;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * Clase que contiene el método main del indexador.
 * Se encarga además de cargar las configuracioens de
 * las bases de datos de MySQL, MongoDB y Neo4j.
 */

public class IndexerMain {
    public static void main(String[] args){

        //Se obtiene el path del archivo con las propiedades de las bases de datos
        File file = new File(IndexerMain.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        Properties properties = new Properties();
        try{
            String resourcePath = file.getPath().replace("libs/PreVisor-Indexer-1.0.jar", "") + "resources/main/bd.properties";
            resourcePath = resourcePath.replace("%20", " ");
            InputStream inputStream = new FileInputStream(resourcePath);
            properties.load(inputStream);
            List<Tweet> tweets = new MongoLoader(properties.getProperty("mongo_host"), properties.getProperty("mongo_port"), properties.getProperty("mongo_db_name"), properties.getProperty("mongo_coll_name")).getTweets();
            TweetIndexer indexer = new TweetIndexer(tweets);
            indexer.searchTweets("ambulancias");

        } catch(IOException | ParseException e){
            e.printStackTrace();
        }
    }
}
