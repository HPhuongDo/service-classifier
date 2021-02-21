import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

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
		// extract unique words
		HashSet<String> mongoWords = readFileToWords(mongo);
		HashSet<String> mysqlWords = readFileToWords(mysql);
		
		writeWordsToFile(mongoWords, "src/main/resources/logs/mongo_words.txt");
		writeWordsToFile(mysqlWords, "src/main/resources/logs/mysql_words.txt");
		
		// find common words
		mysqlWords.retainAll(mongoWords);
		writeWordsToFile(mysqlWords, "src/main/resources/logs/common_words.txt");
		System.out.println("Finished.");
	}
	
	/**
	 * read file, convert to lower case, remove non-letter characters 
	 * @param file
	 * @return
	 */
	public static HashSet<String> readFileToWords(File file) {
		HashSet<String> words = new HashSet<String>();
		try {
	        Scanner sc = new Scanner(file);
	        while (sc.hasNextLine()) {
	            String line = sc.nextLine();
	            line = line.replaceAll("[^a-zA-Z ]", " ").toLowerCase();
	            words.addAll(Arrays.asList(tokenize(line)));
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
	public static String[] tokenize(String line) {
		// Use OpenNLP
		SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
		String tokens[] = tokenizer.tokenize(line);
		return tokens;
	}
	
	/**
	 * to test the Tokenizer
	 * @param words
	 */
	public static void writeWordsToFile(HashSet<String> words, String filePath) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
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
