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
            System.out.println("Empezo a indexar");
            for(Tweet tweet : this.tweets){
                document = new Document();
                document.add(new TextField("tweet", tweet.getTweetText(), Field.Store.YES));
                document.add(new StringField("username", tweet.getName(), Field.Store.YES));
                writer.addDocument(document);
                indexedTweets++;
            }
            System.out.println("Termino de indexar");
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

        Directory dir = FSDirectory.open(this.indexDirectory);
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new SpanishAnalyzer(Version.LUCENE_43);

        //Se buscan los 30 mejores tweets
        QueryParser parser = new QueryParser(Version.LUCENE_43, "tweet", analyzer);
        TopDocs topDocs;
        //Se verifica si la consulta contiene más de una palabra, se busca al menos una coincidencia
        if(tokenizedQuery.length > 1){
            ScoreDoc[] hits;
            BooleanQuery queryBuilder = new BooleanQuery();
            WildcardQuery wildcardQuery = new WildcardQuery(new Term("tweet", tokenizedQuery[tokenizedQuery.length - 1] + "*"));
            /**
             * Se verifica si la búsqueda corresponde a "colmena golden cross"
             */
            if(tokenizedQuery[0].equals("colmena")){
                PhraseQuery phraseQuery = new PhraseQuery();
                for(String subQuery : tokenizedQuery){
                    phraseQuery.add(new Term("tweet", subQuery));
                }
                topDocs = searcher.search(phraseQuery, 30);
                hits = topDocs.scoreDocs;
                for(ScoreDoc hit : hits){
                    Document document = searcher.doc(hit.doc);
                    foundTweets.add(document);
                }

            }
            /**
             * Verifica si la búsqueda contiene la palabra isapre al principio,
             * luego busca el nombre de la isapre en específico.
             */
            else if(tokenizedQuery[0].equals("isapre")){
                Query query = parser.parse(tokenizedQuery[0]);
                queryBuilder.add(query, BooleanClause.Occur.SHOULD);
                PhraseQuery phraseQuery = new PhraseQuery();
                for(int i = 1; i < tokenizedQuery.length; i++){
                    if(i != tokenizedQuery.length - 1){
                        phraseQuery.add(new Term("tweet", tokenizedQuery[i]));
                        queryBuilder.add(phraseQuery, BooleanClause.Occur.MUST);
                    } else {
                        queryBuilder.add(wildcardQuery, BooleanClause.Occur.MUST);
                    }
                }
                topDocs = searcher.search(queryBuilder, 30);
                hits = topDocs.scoreDocs;
                for(ScoreDoc hit : hits){
                    Document document = searcher.doc(hit.doc);
                    foundTweets.add(document);
                }
            }
            /**
             * En caso de que no se cumpla que la isapre es colmena golden cross
             * o cualquier otra que puede empezar o no con isapre, se realiza una búsqueda
             * normal del keyword. Generalmente, esta consulta se refiere a búsquedas sobre
             * alguna temática relacionada a los prestadores, como los cobros excesivos, etc.
             */
            else{
                WildcardQuery termQuery = new WildcardQuery(new Term("tweet", "*" + queryStr + "*"));
                topDocs = searcher.search(termQuery, 30);
                hits = topDocs.scoreDocs;
                for(ScoreDoc hit : hits){
                    Document document = searcher.doc(hit.doc);
                    foundTweets.add(document);
                }
            }

        } else{
            WildcardQuery termQuery = new WildcardQuery(new Term("tweet", "*" + queryStr + "*"));
            topDocs = searcher.search(termQuery, 30);
            ScoreDoc[] hits = topDocs.scoreDocs;
            for(ScoreDoc hit : hits){
                Document document = searcher.doc(hit.doc);
                foundTweets.add(document);
            }
        }
        reader.close();
        return foundTweets;
    }

}
