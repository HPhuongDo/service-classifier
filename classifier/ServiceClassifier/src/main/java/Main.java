import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

/**
 * Find common words between mongo and mysql logs
 * @author phuong
 *
 */
public class Main {
	public static final String path = "/home/phuong/Documents/Master-thesis/implementation/classifier/ServiceClassifier/src/main/resources/";
//	public static final String fileToComparePath = path+"logs/microservices-startup/docker-compose_orders-db_1.log";
	public static final String fileToComparePath = "/home/phuong/Documents/Master-thesis/implementation/services/databases/logs/mongo/mongo_startup.log";
	public static final String fileToClassifyPath = path+"logs/microservices-startup/docker-compose_carts-db_1.log";
	public static final String keywordsPath = path+"categories/database/keywords_mongo-db.txt";
//	public static final String keywordsPath = path+"categories/backend/keywords_backend.txt";
	
	public static void main(String[] args) {
		System.out.println("Starting service classifier...");
//        ClassLoader classLoader = Main.class.getClassLoader();
//        String pathFile1 = classLoader.getResource(file1Path).getPath();
//        String pathFile2 = classLoader.getResource(file2Path).getPath();

		// TF-IDF Cosine Similarity
		calculateTFIDF(fileToComparePath, fileToClassifyPath);
		
		// Jaccard Similarity: common/total
		calculateJaccard(fileToComparePath, fileToClassifyPath);
		
//		File fileToCompare = new File(fileToComparePath);
//		KeywordExtractor file1Extractor = new KeywordExtractor(fileToCompare);
//		writeWordsToFile(file1Extractor.clean(), path + "BoW_springboot-ref.txt");
		
		System.out.println("Finished.");
	}
	
	/**
	 * TF-IDF similarity
	 * @param fileToComparePath
	 * @param fileToClassifyPath
	 */
	public static void calculateTFIDF(String fileToComparePath, String fileToClassifyPath) {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder("python3", 
					"/home/phuong/Documents/Master-thesis/implementation/text_similarity.py", fileToComparePath, fileToClassifyPath);
		    processBuilder.redirectErrorStream(true);
		    Process p = processBuilder.start();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String s = null;
			while ((s = stdInput.readLine()) != null) {
				System.out.println("TF-IDF Cosine Similarity: "+s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Jaccard similarity with extra weight on keywords of type
	 * Weighted Jaccard using Vector formular: sum min(Xi, Yi) / sum max(Xi, Yi)
	 * @param fileToComparePath
	 * @param fileToClassifyPath
	 */
	public static void calculateJaccard(String fileToComparePath, String fileToClassifyPath) {
		File fileToCompare = new File(fileToComparePath);
		File fileToClassify = new File(fileToClassifyPath);
		
		KeywordExtractor file1Extractor = new KeywordExtractor(fileToCompare);
		KeywordExtractor file2Extractor = new KeywordExtractor(fileToClassify);
		HashSet<String> file1Words = file1Extractor.extract();
		HashSet<String> file2Words = file2Extractor.extract();
		
		int sum = file2Words.size() + file1Words.size();

//		writeWordsToFile(file1Words, "src/main/resources/analysis/difference/nginx_words_request.txt");
//		writeWordsToFile(file2Words, "src/main/resources/analysis/difference/flask_words_request.txt");

		// find common words
		file2Words.retainAll(file1Words);
		double jaccard = file2Words.size();
		HashMap<String, Double> words = getKeywordsOfType();
		for (String word : file2Words) {
			if (words.containsKey(word)) {
				jaccard +=words.get(word);	// add weight for an important word
				sum +=words.get(word);
//				jaccard++;
//				sum++;
			}
		}
		jaccard /= sum;
//		writeWordsToFile(file2Words, "src/main/resources/categories/backend/common_keywords_carts_orders.txt");
		
		System.out.println("Jaccard Similarity: "+jaccard);
	}
	
	/**
	 * 
	 * @return
	 */
	public static HashMap<String, Double> getKeywordsOfType() {
		HashMap<String, Double> words = new HashMap<String, Double>();
		try {
	        Scanner sc = new Scanner(new File(keywordsPath));
	        while (sc.hasNextLine()) {
	            String line = sc.nextLine();
	            String[] wordPairs =  line.split(";");
	            words.put(wordPairs[0], Double.parseDouble(wordPairs[1]));
	        }
	        sc.close();
	    } 
	    catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
		return words;
	}
	
	/**
	 * 
	 * @param words
	 */
	public static void writeWordsToFile(Collection<String> words, String filePath) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
//			PrintWriter writer = new PrintWriter(new File(Main.class.getResource(filePath).getPath()));
			for(String word : words) {
				writer.append(' ');
			    writer.append(word);
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
