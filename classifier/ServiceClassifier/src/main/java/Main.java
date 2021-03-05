import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

/**
 * Find common words between mongo and mysql logs
 * @author phuong
 *
 */
public class Main {
	public static final String path = "/home/phuong/Documents/Master-thesis/implementation/services/databases/logs/";
	
	public static void main(String[] args) {
		System.out.println("Starting service classifier...");
		
		// TF-IDF Cosine Similarity
		try {
			ProcessBuilder processBuilder = new ProcessBuilder("python3", 
					"/home/phuong/Documents/Master-thesis/implementation/text_similarity.py");
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

		// Jaccard Similarity: common/total
		File mongo = new File(path+"mongo/mongo_queries.log");
		File mysql = new File(path+"mysql/mysql_queries.log");
		
		KeywordExtractor mongoExtractor = new KeywordExtractor(mongo);
		KeywordExtractor mysqlExtractor = new KeywordExtractor(mysql);
		HashSet<String> mongoWords = mongoExtractor.extract();
		HashSet<String> mysqlWords = mysqlExtractor.extract();
		
		int sum = mysqlWords.size() + mongoWords.size();

		writeWordsToFile(mongoWords, "src/main/resources/analysis/lemmatized/mongo_words_queries.txt");
		writeWordsToFile(mysqlWords, "src/main/resources/analysis/lemmatized/mysql_words_queries.txt");

		// find common words
		mysqlWords.retainAll(mongoWords);
		writeWordsToFile(mysqlWords, "src/main/resources/analysis/lemmatized/common_keywords_queries.txt");
		
		System.out.println("Jaccard Similarity: "+(double)mysqlWords.size()/sum);
		
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
