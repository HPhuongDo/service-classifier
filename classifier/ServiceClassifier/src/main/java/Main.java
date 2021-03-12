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
	public static final String path = "/home/phuong/Documents/Master-thesis/implementation/services/webapi/logs/";
	public static final String file1Path = path+"nginx/nginx_request.log";
	public static final String file2Path = path+"flask-api/flask-api.log";
	
	public static void main(String[] args) {
		System.out.println("Starting service classifier...");
		
		// TF-IDF Cosine Similarity
		try {
			ProcessBuilder processBuilder = new ProcessBuilder("python3", 
					"/home/phuong/Documents/Master-thesis/implementation/text_similarity.py", file1Path, file2Path);
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
		File file1 = new File(file1Path);
		File file2 = new File(file2Path);
		
		KeywordExtractor file1Extractor = new KeywordExtractor(file1);
		KeywordExtractor file2Extractor = new KeywordExtractor(file2);
		HashSet<String> file1Words = file1Extractor.extract();
		HashSet<String> file2Words = file2Extractor.extract();
		
		int sum = file2Words.size() + file1Words.size();

		writeWordsToFile(file1Words, "src/main/resources/analysis/webapi/nginx_words_request.txt");
		writeWordsToFile(file2Words, "src/main/resources/analysis/webapi/flask_words_request.txt");

		// find common words
		file2Words.retainAll(file1Words);
		writeWordsToFile(file2Words, "src/main/resources/analysis/webapi/common_keywords_request.txt");
		
		System.out.println("Jaccard Similarity: "+(double)file2Words.size()/sum);
		
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
