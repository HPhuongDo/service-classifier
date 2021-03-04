import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.util.CoreMap;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.SimpleTokenizer;

/**
 * Find common words between mongo and mysql logs
 * @author phuong
 *
 */
public class Main {
	public static final String path = "/home/phuong/Documents/Master-thesis/implementation/services/databases/logs/";
	
	public static void main(String[] args) {
		System.out.println("Starting service classifier...");
		File mongo = new File(path+"mongo/mongo_queries.log");
		File mysql = new File(path+"mysql/mysql_queries.log");
		
		KeywordExtractor mongoExtractor = new KeywordExtractor(mongo);
		KeywordExtractor mysqlExtractor = new KeywordExtractor(mysql);
		HashSet<String> mongoWords = mongoExtractor.extract();
		HashSet<String> mysqlWords = mysqlExtractor.extract();

		writeWordsToFile(mongoWords, "src/main/resources/analysis/lemmatized/mongo_words_queries.txt");
		writeWordsToFile(mysqlWords, "src/main/resources/analysis/lemmatized/mysql_words_queries.txt");

		// find common words
		mysqlWords.retainAll(mongoWords);
		writeWordsToFile(mysqlWords, "src/main/resources/analysis/lemmatized/common_keywords_queries.txt");

		System.out.println("Finished.");
	}
	
	/**
	 * 
	 * @param words
	 */
	public static void writeWordsToFile(HashSet<String> words, String filePath) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
//			PrintWriter writer = new PrintWriter(new File(Main.class.getResource(filePath).getPath()));
			for(String word : words) {
				writer.append('\n');
			    writer.append(word);
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
