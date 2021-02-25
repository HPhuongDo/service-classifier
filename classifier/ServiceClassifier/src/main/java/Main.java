import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

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
		Stanford_NLP
	}
	
	public static void main(String[] args) {
		System.out.println("Starting service classifier...");
		File mongo = new File(path+"mongo/mongo_queries.log");
		File mysql = new File(path+"mysql/mysql_queries.log");
		// extract unique words
		HashSet<String> mongoWords = readFileToWords(Model.OPEN_NLP, mongo);
		HashSet<String> mysqlWords = readFileToWords(Model.OPEN_NLP, mysql);
		
		writeWordsToFile(mongoWords, "src/main/resources/analysis/OpenNLP/mongo_words.txt");
		writeWordsToFile(mysqlWords, "src/main/resources/analysis/OpenNLP/mysql_words.txt");
		
		// find common words
		mysqlWords.retainAll(mongoWords);
		writeWordsToFile(mysqlWords, "src/main/resources/analysis/OpenNLP/common_words.txt");

		// extract nouns as keywords
		HashSet<String> commonKeywords = extractKeywords(Model.OPEN_NLP, mysqlWords.toArray(new String[mysqlWords.size()]));
		writeWordsToFile(commonKeywords, "src/main/resources/analysis/OpenNLP/common_keywords_POS_perceptron.txt");
		
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
	            words.addAll(Arrays.asList(tokenize(model, line)));
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
	public static String[] tokenize(Model model, String line) {
		switch(model) {
		case OPEN_NLP:
			SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
			String tokens[] = tokenizer.tokenize(line);
			return tokens;
		case UIMA:
			return null;
		case Stanford_NLP:
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
		switch (model) {
		case OPEN_NLP:
			try {
				HashSet<String> keywords = new HashSet<String>();
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
		default:
			return null;
		}
		
	}
	
}
