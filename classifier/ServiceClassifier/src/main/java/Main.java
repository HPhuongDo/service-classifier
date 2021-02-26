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
	
	public enum Model{
		OPEN_NLP,
		UIMA,
		STANFORD_NLP
	}
	
	public static void main(String[] args) {
		System.out.println("Starting service classifier...");
		File mongo = new File(path+"mongo/mongo_queries.log");
		File mysql = new File(path+"mysql/mysql_queries.log");
		// extract unique words
		HashSet<String> mongoWords = readFileToWords(Model.STANFORD_NLP, mongo);
		HashSet<String> mysqlWords = readFileToWords(Model.STANFORD_NLP, mysql);
		
		writeWordsToFile(mongoWords, "src/main/resources/analysis/StanfordNLP/mongo_words.txt");
		writeWordsToFile(mysqlWords, "src/main/resources/analysis/StanfordNLP/mysql_words.txt");
		
		// find common words
		mysqlWords.retainAll(mongoWords);
		writeWordsToFile(mysqlWords, "src/main/resources/analysis/StanfordNLP/common_words.txt");

		// extract nouns as keywords
		HashSet<String> commonKeywords = extractKeywords(Model.STANFORD_NLP, mysqlWords.toArray(new String[mysqlWords.size()]));
		writeWordsToFile(commonKeywords, "src/main/resources/analysis/StanfordNLP/common_keywords_StanfordNLP.txt");
		
		System.out.println("Finished.");
	}
	
	/**
	 * read file, convert to lower case, remove non-letter characters 
	 * @param file
	 * @return
	 */
	public static HashSet<String> readFileToWords(Model model, File file) {
		HashSet<String> words = new HashSet<String>();
		try {
	        Scanner sc = new Scanner(file);
	        while (sc.hasNextLine()) {
	            String line = sc.nextLine();
	            line = line.replaceAll("[^a-zA-Z ]", " ").toLowerCase();
	            words.addAll(tokenize(model, line));
	        }
	        sc.close();
	    } 
	    catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
		return words;
	}
	
	/**
	 * tokenize into words using character classes
	 */
	public static List<String> tokenize(Model model, String line) {
		switch(model) {
		case OPEN_NLP:
			SimpleTokenizer simpleTokenizer = SimpleTokenizer.INSTANCE;
			return Arrays.asList(simpleTokenizer.tokenize(line));
		case STANFORD_NLP:
			Reader r = new StringReader(line);
			PTBTokenizer<Word> ptbTokenizer = PTBTokenizer.newPTBTokenizer(r);
			ArrayList<String> words = new ArrayList<String>();
			while (ptbTokenizer.hasNext()) {
				Word w = ptbTokenizer.next();
				words.add(w.word());
			}
			return words;
		case UIMA:
			return null;
		default:
			return null;
		}
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
	
	/**
	 * extract only nouns (NN, NNS, NNP, NNPS tags in the Penn Treebank Project)
	 * @param words
	 * @return
	 */
	public static HashSet<String> extractKeywords(Model model, String[] words) {
		HashSet<String> keywords = new HashSet<String>();
		switch (model) {
		case OPEN_NLP:
			try {
				InputStream inputStreamPOSTagger = Main.class.getResourceAsStream("/models/en-pos-perceptron.bin");
				POSModel posModel = new POSModel(inputStreamPOSTagger);
				POSTaggerME posTagger = new POSTaggerME(posModel);
				String tags[] = posTagger.tag(words);
				// extract nouns
				for(int i=0; i<words.length; i++) {
					if (tags[i].startsWith("N")) {
						keywords.add(words[i]);
					}
				}
				return keywords;
			} catch (IOException e) {
				e.printStackTrace();
			}
		case STANFORD_NLP:
			Properties props = new Properties();
	        props.setProperty("annotators", "tokenize, ssplit, pos");
			StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
			// extract nouns
			String str = String.join(" ", words);
			Annotation document = new Annotation(str);
			pipeline.annotate(document);
			List<CoreMap> sentences = document.get(SentencesAnnotation.class);
			for(CoreMap sentence: sentences) {
				  // traversing the words in the current sentence
				  // a CoreLabel is a CoreMap with additional token-specific methods
				  for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
				    // this is the POS tag of the token
				    String pos = token.get(PartOfSpeechAnnotation.class);
				    if (pos.startsWith("N")) {
						keywords.add(token.originalText());
					}
				  }
			}
			return keywords;
		default:
			return null;
		}
		
	}
	
}
