//Created By Raga Shalini Koka
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

public class AprioriAlgorithm {
	
	List<String> transactionList;
	String inputFilePath;
	String outputFilePath;
	int minSupport;
	int minItemCnt;
	
	public AprioriAlgorithm(String inputFilePath, String outputFilePath, 
			int minSupport, int minItemCnt) {
		// TODO Auto-generated constructor stub
		this.transactionList = new LinkedList<String>();
		this.inputFilePath = inputFilePath;
		this.outputFilePath = outputFilePath;
		this.minSupport = minSupport;
		this.minItemCnt = minItemCnt;
		
		
	}
	
	public static void main(String args[]) {
		// minSupport minItems inputFile outputFile
		System.out.println("Started generating....\n");
		Long timerStart = System.currentTimeMillis();
		if(args.length < 4) {
			System.out.println("Please provide valid no. of arguments");
			System.exit(0);
		}
		AprioriAlgorithm apriori = new AprioriAlgorithm(args[2], args[3], 
				Integer.parseInt(args[0]), Integer.parseInt(args[1]));
		apriori.readTransactions();
		//System.out.println("count : " + apriori.transactionList.size());
		List<TreeSet<ItemSet>> frequentItemList  = apriori.createFrequentCandidates();
		apriori.writeTransactions(frequentItemList);
		Long timerEnd = System.currentTimeMillis();
		System.out.println("Time taken : " + (timerEnd - timerStart)/1000 + " seconds\n");
		System.out.println("Completed generating....\n");
		
	}

	private void writeTransactions(List<TreeSet<ItemSet>> frequentItemList) {
		// TODO Auto-generated method stub
		String fileOutput = "";
		for(int i = 0; i < frequentItemList.size();i++) {
			Iterator<ItemSet> iterator = frequentItemList.get(i).iterator();
			while(iterator.hasNext()) {
				ItemSet itemSet = (ItemSet) iterator.next();
				if(itemSet.itemList.size() >= minItemCnt) {
					fileOutput += itemSet.formatOutput();
				}
			}
		}
		
		try {
			Files.write(Paths.get(outputFilePath), fileOutput.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private List<TreeSet<ItemSet>> createFrequentCandidates() {
		// TODO Auto-generated method stub
		FrequentSetGeneration frequentSetGeneration = new FrequentSetGeneration(transactionList, minSupport, minItemCnt);
		return frequentSetGeneration.generateFrequentItemSets();
		
		
		
	}

	private void readTransactions() {
		// TODO Auto-generated method stub
		try {
			transactionList = Files.readAllLines(Paths.get(inputFilePath));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error while reading file");
			e.printStackTrace();
		}
		
	}

}
