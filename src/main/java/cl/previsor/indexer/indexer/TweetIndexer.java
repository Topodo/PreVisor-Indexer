package cl.previsor.indexer.indexer;

import cl.previsor.indexer.databases.Tweet;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase encargada de crear los índices invertidos para realizar
 * las búsquedas de Tweets.
 */
public class TweetIndexer {

    private List<Tweet> tweets;
    private Directory dir;


    //Constructor
    public TweetIndexer(List<Tweet> tweets){
        this.tweets = tweets;
    }

    public int createIndex() throws IOException {
        this.dir = new RAMDirectory();
        Analyzer analyzer = new SpanishAnalyzer(Version.LUCENE_43);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_43, analyzer);
	    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(this.dir, config);
        int indexedTweets = 0;
        try{
            Document document;
            for(Tweet tweet : this.tweets){
                document = new Document();
                document.add(new TextField("tweet", tweet.getTweetText(), Field.Store.YES));
                document.add(new StringField("username", tweet.getName(), Field.Store.YES));
                writer.addDocument(document);
                indexedTweets++;
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            writer.close();
        }
        return indexedTweets;
    }

    //Método que realiza una búsqueda en el índice
    public List<Document> searchTweets(String queryStr) throws IOException, ParseException {

        //Se crea un nuevo índice invertido
        int indexedTweets = createIndex();
        List<Document> foundTweets = new ArrayList<>();
        //Query tokenizada
        String[] tokenizedQuery = queryStr.split(" ");

        IndexReader reader = DirectoryReader.open(this.dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new SpanishAnalyzer(Version.LUCENE_43);

        //Se buscan los 30 mejores tweets
        QueryParser parser = new QueryParser(Version.LUCENE_43, "tweet", analyzer);
        TopDocs topDocs;
        ScoreDoc[] hits;
        //Se verifica si la consulta contiene más de una palabra, se busca al menos una coincidencia
        if(tokenizedQuery.length > 1){
            PhraseQuery phraseQuery = new PhraseQuery();
            for(String token : tokenizedQuery){
                phraseQuery.add(new Term("tweet", token));
            }
            //Se obtienen los 100 mejores resultados
            topDocs = searcher.search(phraseQuery, 100);
            hits = topDocs.scoreDocs;
            for(ScoreDoc hit : hits){
                Document tweet = searcher.doc(hit.doc);
                foundTweets.add(tweet);
            }
        }
        //En caso de que la consulta sea de una sola palabra
        else{
            WildcardQuery termQuery = new WildcardQuery(new Term("tweet", "*" + queryStr + "*"));
            topDocs = searcher.search(termQuery, 100);
            hits = topDocs.scoreDocs;
            for(ScoreDoc hit : hits){
                Document document = searcher.doc(hit.doc);
                foundTweets.add(document);
            }
        }
        reader.close();
        return foundTweets;
    }

}
