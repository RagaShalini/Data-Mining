import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class BooleanRetrieval {

	HashMap<String, Set<Integer>> invIndex;
	int[][] docs;
	HashSet<String> vocab;
	HashMap<Integer, String> map; // int -> word
	HashMap<String, Integer> i_map; // inv word -> int map

	public BooleanRetrieval() throws Exception {
		// Initialize variables and Format the data using a pre-processing class and set
		// up variables
		invIndex = new HashMap<String, Set<Integer>>();
		DatasetFormatter formater = new DatasetFormatter();
		formater.textCorpusFormatter("./all.txt");
		docs = formater.getDocs();
		vocab = formater.getVocab();
		map = formater.getVocabMap();
		i_map = formater.getInvMap();
	}

	void createPostingList() {
		// Initialze the inverted index with a SortedSet (so that the later additions
		// become easy!)
		for (String s : vocab) {
			invIndex.put(s, new TreeSet<Integer>());
		}
		// for each doc
		for (int i = 0; i < docs.length; i++) {
			// for each word of that doc
			for (int j = 0; j < docs[i].length; j++) {
				// Get the actual word in position j of doc i
				String w = map.get(docs[i][j]);

				/*
				 * TO-DO: Get the existing posting list for this word w and add the new doc in
				 * the list. Keep in mind doc indices start from 1, we need to add 1 to the doc
				 * index , i
				 */
				TreeSet<Integer> documentsSet = (TreeSet<Integer>) invIndex.get(w);
				documentsSet.add(i + 1);

			}

		}
	}

	Set<Integer> intersection(Set<Integer> a, Set<Integer> b) {
		/*
		 * First convert the posting lists from sorted set to something we can iterate
		 * easily using an index. I choose to use ArrayList<Integer>. Once can also use
		 * other enumerable.
		 */
		ArrayList<Integer> PostingList_a = new ArrayList<Integer>(a);
		ArrayList<Integer> PostingList_b = new ArrayList<Integer>(b);
		TreeSet<Integer> result = new TreeSet<Integer>();

		// Set indices to iterate two lists. I use i, j
		int i = 0;
		int j = 0;

		while (i != PostingList_a.size() && j != PostingList_b.size()) {

			// TO-DO: Implement the intersection algorithm here
			int item_a = PostingList_a.get(i);
			int item_b = PostingList_b.get(j);

			if (item_a == item_b) {
				result.add(item_a);
				i++;
				j++;
			} else if (item_a < item_b) {
				i++;
			} else {
				j++;
			}

		}
		return result;
	}

	Set<Integer> evaluateANDQuery(String a, String b) {
		return intersection(invIndex.get(a), invIndex.get(b));
	}

	Set<Integer> union(Set<Integer> a, Set<Integer> b) {
		/*
		 * IMP note: you are required to implement OR and cannot use Java Collections
		 * methods directly, e.g., .addAll whcih solves union in 1 line! TO-DO: Figure
		 * out how to perform union extending the posting list intersection method
		 * discussed in class?
		 */
		TreeSet<Integer> result = new TreeSet<Integer>();
		// Implement Union here
		ArrayList<Integer> postingList_a = new ArrayList<Integer>(a);
		ArrayList<Integer> postingList_b = new ArrayList<Integer>(b);
		// System.out.println("posting list A : " + postingList_a);
		// System.out.println("Posting list B : " + postingList_b);
		int i = 0;
		int j = 0;
		while (i != postingList_a.size() && j != postingList_b.size()) {
			int item_a = postingList_a.get(i);
			int item_b = postingList_b.get(j);

			if (item_a == item_b) {
				result.add(item_a);
				i++;
				j++;
			} else if (item_a < item_b) {
				result.add(item_a);
				i++;
			} else {
				result.add(item_b);
				j++;
			}

		}

		while (i != postingList_a.size()) {
			result.add(postingList_a.get(i));
			i++;
		}
		while (j != postingList_b.size()) {
			result.add(postingList_b.get(j));
			j++;
		}

		return result;
	}

	Set<Integer> evaluateORQuery(String a, String b) {
		return union(invIndex.get(a), invIndex.get(b));
	}

	Set<Integer> not(Set<Integer> a) {
		TreeSet<Integer> result = new TreeSet<Integer>();
		/*
		 * Hint: NOT is very simple. I traverse the sorted posting list between i and
		 * i+1 index and add the other (NOT) terms in this posting list between these
		 * two pointers First convert the posting lists from sorted set to something we
		 * can iterate easily using an index. I choose to use ArrayList<Integer>. Once
		 * can also use other enumerable.
		 * 
		 * 
		 */

		ArrayList<Integer> PostingList_a = new ArrayList<Integer>(a);
		int total_docs = docs.length;
		// TO-DO: Implement the not method using above idea or anything you find better!
		for (int i = 1; i < total_docs + 1; i++) {
			if (!PostingList_a.contains(i)) {
				result.add(i);
			}
		}

		return result;
	}

	Set<Integer> evaluateNOTQuery(String a) {
		return not(invIndex.get(a));
	}

	Set<Integer> evaluateAND_NOTQuery(String a, String b) {
		return intersection(invIndex.get(a), not(invIndex.get(b)));
	}

	public static void main(String[] args) throws Exception {

		if (args.length < 3) {
			System.out.println("Please Enter the arguments corrects : query_type query_string output_file_path");
			System.exit(0);
		}

		// Initialize parameters
		BooleanRetrieval model = new BooleanRetrieval();

		// Generate posting lists
		model.createPostingList();

		String queryType = args[0];
		String queryString = args[1];
		String outputFilePath = args[2];

		model.generateResults(queryType, queryString, outputFilePath);

		/*
		  System.out.println();
		  System.out.println("freeze : " +  model.invIndex.get("freeze"));
		  System.out.println("ctrl : " +  model.invIndex.get("ctrl"));
		  System.out.println("wifi : " +  model.invIndex.get("wifi")); 
		  System.out.println("cpu : " +	  model.invIndex.get("cpu"));
		  
		  System.out.println();
		  System.out.println("mouse and wifi : " +  model.evaluateANDQuery("mouse", "wifi"));
		  System.out.println("mouse and scrolling : " + model.evaluateANDQuery("mouse","scrolling")); 
		  System.out.println("errors and report : " +  model.evaluateANDQuery("error", "report"));
		  
		  System.out.println(); 
		  System.out.println("youtube or reported : " +  model.evaluateORQuery("youtube", "reported"));
		  System.out.println("errors or report : " + model.evaluateORQuery("errors","report")); 
		  System.out.println("hell or movie : " +  model.evaluateORQuery("hell", "movie"));
		 
		  System.out.println(); 
		  System.out.println("scroll and not mouse : " +  model.evaluateAND_NOTQuery("scroll", "mouse"));
		  System.out.println("lenovo and not logitech : " +   model.evaluateAND_NOTQuery("lenovo", "logitech"));
		 
		 */
		// Print the posting lists from the inverted index

		
		 System.out.println("\nPrinting posting list:"); for(String s :
		  model.invIndex.keySet()){ System.out.println(s + " -> " +	  model.invIndex.get(s)); }
		  
		  
		  //Print test cases
		 
		  System.out.println();
		  
		  System.out.println("\nTesting AND queries \n");
		  System.out.println("1) " +  model.evaluateANDQuery("mouse", "keyboard")); 
		  System.out.println("2) " +  model.evaluateANDQuery("mouse", "wifi"));
		  System.out.println("3) " +  model.evaluateANDQuery("button", "keyboard"));
		  
		  System.out.println("\nTesting OR queries \n"); 
		  System.out.println("4) " +  model.evaluateORQuery("wifi", "scroll")); 
		  System.out.println("5) " +  model.evaluateORQuery("youtube", "reported")); 
		  System.out.println("6) " +  model.evaluateORQuery("errors", "report"));
		  
		  System.out.println("\nTesting AND_NOT queries \n");
		  System.out.println("7) " + model.evaluateAND_NOTQuery("mouse", "scroll")); 
		  System.out.println("8) " + model.evaluateAND_NOTQuery("scroll", "mouse")); 
		  System.out.println("9) " +  model.evaluateAND_NOTQuery("lenovo", "logitech"));
		 
	}

	private void generateResults(String queryType, String queryString, String outputFilePath) {
		// TODO Auto-generated method stub
		Set<Integer> result = new TreeSet<Integer>();
		try {
			switch (queryType.toUpperCase()) {
			case "PLIST":
				result = invIndex.getOrDefault(queryString.toLowerCase(), new TreeSet<Integer>());
				break;
			case "AND":
				String[] andTerms = queryString.split("\\s[Aa][Nn][Dd]\\s");
				//System.out.println("and terms length : " + andTerms.length);
				if (andTerms.length == 2) {
					result = evaluateANDQuery(andTerms[0].trim().toLowerCase(), andTerms[1].trim().toLowerCase());
				}
				break;
			case "OR":
				String[] orTerms = queryString.split("\\s[Oo][Rr]\\s");
				
				System.out.println("OR length : " + orTerms.length);
				if (orTerms.length == 2) {
					//System.out.println("in or block");
					result = evaluateORQuery(orTerms[0].trim().toLowerCase(), orTerms[1].trim().toLowerCase());
				}
				break;
			case "AND-NOT":
				String[] andNotTerms = queryString.split("\\s[Aa][Nn][Dd]\\s");
				if (andNotTerms.length == 2) {
					String notTerm = andNotTerms[1];
					notTerm = notTerm.substring(notTerm.indexOf("NOT") + 3, notTerm.indexOf(")"));
					result = evaluateAND_NOTQuery(andNotTerms[0].trim().toLowerCase(), notTerm.trim().toLowerCase());

				}
				break;

			}
		} catch (Exception exp) {
			System.out.println("Index table does not contain the term");
		}
		System.out.println("set to string conversion : " + result.toString());
		writeResultsToFile(queryString, result.toString(), outputFilePath);
	}

	private void writeResultsToFile(String queryString, String result, String outputFilePath) {
		// TODO Auto-generated method stub
		String fileOutput = queryString + " -> " + result;
		try {
			Files.write(Paths.get("./" + outputFilePath), fileOutput.getBytes());
			System.out.println("Completed generating output file");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Unable to write to file");
			e.printStackTrace();
		}

	}

}