package cl.previsor.indexer.indexer;

import cl.previsor.indexer.databases.Tweet;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
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
    private final File indexDirectory;


    //Constructor
    public TweetIndexer(List<Tweet> tweets){
        this.tweets = tweets;
        this.indexDirectory  = new File("/home/ariel/Escritorio/PreVisor Indexer/index/");
    }

    public int createIndex() throws IOException {
        Directory dir = FSDirectory.open(this.indexDirectory);
        Analyzer analyzer = new SpanishAnalyzer(Version.LUCENE_43);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_43, analyzer);
        IndexWriter writer = new IndexWriter(dir, config);
        int indexedTweets = 0;
        try{
            Document document;
            for(Tweet tweet : this.tweets){
                document = new Document();
                document.add(new TextField("tweet", tweet.getTweetText(), Field.Store.YES));
                document.add(new StringField("username", tweet.getName(), Field.Store.YES));
                System.out.println(tweet.getTweetText());
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

        Directory dir = FSDirectory.open(this.indexDirectory);
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new SpanishAnalyzer(Version.LUCENE_43);

        //Se buscan los 20 mejores tweets
        TopScoreDocCollector collector = TopScoreDocCollector.create(20, true);
        Query query = new QueryParser(Version.LUCENE_43, "tweet", analyzer).parse(queryStr);
        searcher.search(query, collector);

        //Se obtienen los resultados
        ScoreDoc[] hits = collector.topDocs().scoreDocs;
        for(ScoreDoc hit : hits){
            int docId = hit.doc;
            Document document = searcher.doc(docId);
            foundTweets.add(document);
            System.out.println(document.get("tweet"));
        }
        reader.close();
        return foundTweets;
    }

}
