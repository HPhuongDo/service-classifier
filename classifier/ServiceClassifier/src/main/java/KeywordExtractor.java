import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.logging.RedwoodConfiguration;

/**
 * Use StanfordNLP
 * given a log document, tokenize, lemmatize, extract UNIQUE keywords (verbs and nouns)
 * @author phuong
 *
 */
public class KeywordExtractor {
	File file;
	
	public KeywordExtractor(File file) {
		RedwoodConfiguration.current().clear().apply();	// to disable logging from StanfordNLP
		this.file = file;
	}

	/**
	 * read file, convert to lower case, remove non-letter characters, extract keywords
	 * @param file
	 * @return unique keywords of file
	 */
	public HashSet<String> extract() {
		HashSet<String> words = new HashSet<String>();
		try {
	        Scanner sc = new Scanner(this.file);
	        while (sc.hasNextLine()) {
	            String line = sc.nextLine();
	            line = line.replaceAll("[^a-zA-Z ]", " ").toLowerCase();
	            words.addAll(extractKeywords(line));
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
	 * get POS and Lemma
	 * @return set of unique words in base form
	 */
	private HashSet<String> extractKeywords(String line) {
		HashSet<String> keywords = new HashSet<String>();
		Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		// extract verbs and nouns
		Annotation document = new Annotation(line);
		pipeline.annotate(document);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		for(CoreMap sentence: sentences) {
			  // traversing the words in the current sentence
			  // a CoreLabel is a CoreMap with additional token-specific methods
			  for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
				  String pos = token.get(PartOfSpeechAnnotation.class);
			    if (pos.startsWith("N") || pos.startsWith("V")) {
					keywords.add(token.lemma());
				}
			  }
		}
		return keywords;
	}
	
}
