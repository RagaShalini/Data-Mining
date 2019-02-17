//Created By Raga Shalini Koka
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class FrequentSetGeneration {
	
	List<String> transactionList;
	int minSupport;
	int minItems;
	List<TreeSet<ItemSet>> frequentItemList;
	Map<String,BitSet> itemToBitSetMap;
	int validTransactionCnt;
	public FrequentSetGeneration(List<String> transactionList, int minSupport,
							int minItems) {
		// TODO Auto-generated constructor stub
		this.transactionList = transactionList;
		this.minSupport = minSupport;
		this.minItems = minItems;
		frequentItemList = new LinkedList<TreeSet<ItemSet>>();
		itemToBitSetMap = new HashMap<String,BitSet>();
		validTransactionCnt = 0;
	}
	
	public List<TreeSet<ItemSet>> generateFrequentItemSets() {
		generateItemToBitSetMap();
		TreeSet<ItemSet> firstPhaseFreqItemSet = getPhaseOneFrequentItemSets();
		frequentItemList.add(firstPhaseFreqItemSet);
		//System.out.println( firstPhaseFreqItemSet);
		TreeSet<ItemSet> previousPassItemSet = firstPhaseFreqItemSet;
		while(!previousPassItemSet.isEmpty()) {
			TreeSet<ItemSet> nextPassCandidates = generateCandidates(previousPassItemSet);
			frequentItemList.add(nextPassCandidates);
			previousPassItemSet = nextPassCandidates;
		}
		
		return frequentItemList;
		
	}

	private TreeSet<ItemSet> generateCandidates(TreeSet<ItemSet> previousPassItemSet) {
		// TODO Auto-generated method stub
		ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		TreeSet<ItemSet> nextPassCandidateTreeSet = new TreeSet<ItemSet>();
		Iterator<ItemSet> itemSetIterator = previousPassItemSet.iterator();
		SortedSet<ItemSet> nextPassCandidateSet = Collections.synchronizedSortedSet(new TreeSet<ItemSet>());
		
		while(itemSetIterator.hasNext()) {
			ItemSet first = itemSetIterator.next();
			Iterator<ItemSet> tailItemSetIterator = previousPassItemSet.tailSet(first,false).iterator();
			//pairSets(first, tailItemSetIterator, previousPassItemSet, nextPassCandidateSet);
			executor.execute(new Runnable() {
				
				@Override
				public void run() {
					//System.out.println("thread is : " + Thread.currentThread().getName());
					// TODO Auto-generated method stub
					while(tailItemSetIterator.hasNext()) {
						ItemSet second = tailItemSetIterator.next();
						if(isJoinPossible(first, second)) {
							ItemSet nextPassCandidate = new ItemSet(first, second);
							nextPassCandidate.support = calculateSupport(nextPassCandidate);
							if(nextPassCandidate.support >= minSupport) {
								if(!toBePruned(previousPassItemSet, nextPassCandidate)) {
									nextPassCandidateSet.add(nextPassCandidate);
								}
							}
						}
						
					}
				}
			});
			
			
		}
		
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.HOURS);
			nextPassCandidateTreeSet.addAll(nextPassCandidateSet);
		}
		catch(InterruptedException interruptedException) {
			System.out.println("Interrupted Exception has occured");
		}
		
		return nextPassCandidateTreeSet;
	}

	

	private void pairSets(ItemSet first, Iterator<ItemSet> tailItemSetIterator, TreeSet<ItemSet> previousPassItemSet, TreeSet<ItemSet> nextPassCandidateSet) {
		// TODO Auto-generated method stub
		previousPassItemSet.tailSet(first, false).parallelStream().forEach(second->join(first,second, previousPassItemSet, nextPassCandidateSet));
	}

	private void join(ItemSet first, ItemSet second, TreeSet<ItemSet> previousPassItemSet, TreeSet<ItemSet> nextPassCandidateSet) {
		// TODO Auto-generated method stub
		if(isJoinPossible(first, second)) {
			ItemSet nextPassCandidate = new ItemSet(first, second);
			nextPassCandidate.support = calculateSupport(nextPassCandidate);
			if(nextPassCandidate.support >= minSupport) {
				if(!toBePruned(previousPassItemSet, nextPassCandidate)) {
					nextPassCandidateSet.add(nextPassCandidate);
				}
			}
		}
		
	}

	private boolean toBePruned(TreeSet<ItemSet> previousPassItemSet, ItemSet nextPassCandidate) {
		// TODO Auto-generated method stub
		
		List<String> itemList = nextPassCandidate.itemList;
		for(int i = 0; i < itemList.size();i++) {
			String ele = itemList.remove(i);
			if(!previousPassItemSet.contains(nextPassCandidate)) {
				return true;
			}
			itemList.add(i, ele);
			
		}
		
		return false;
	}

	private int calculateSupport(ItemSet nextPassCandidate) {
		// TODO Auto-generated method stub
		BitSet bitSet = new BitSet(validTransactionCnt);
		bitSet.set(0, validTransactionCnt);
		List<String> itemList = nextPassCandidate.itemList;
		for(int  i = 0; i < itemList.size();i++) {
			bitSet.and(itemToBitSetMap.get(itemList.get(i)));
		}
		return bitSet.cardinality();
	}

	private boolean isJoinPossible(ItemSet first, ItemSet second) {
		// TODO Auto-generated method stub
		int itemListSize = first.itemList.size();
		for(int i = 0;i < itemListSize - 1;i++) {
			if(first.itemList.get(i) != second.itemList.get(i)) {
				return false;
			}
		}
		
		String lastItemInFirst = first.itemList.get(itemListSize - 1);
		String lastItemInSecond = second.itemList.get(itemListSize - 1);
		
		if(lastItemInFirst.compareTo(lastItemInSecond) < 0) {
			return true;
		}
		
		return false;
	}

	private TreeSet<ItemSet> getPhaseOneFrequentItemSets() {
		// TODO Auto-generated method stub
		TreeSet<ItemSet> firstPhaseFrequentItemSets = new TreeSet<ItemSet>();
		for(Map.Entry<String, BitSet> item : itemToBitSetMap.entrySet()) {
			if(item.getValue().cardinality() >= minSupport) {
				ItemSet set = new ItemSet(item.getKey(), item.getValue().cardinality());
				//System.out.println("item : " + set.itemList +  set.support);
				firstPhaseFrequentItemSets.add(set);
			}
		}
		return firstPhaseFrequentItemSets;
	}

	private void generateItemToBitSetMap() {
		// TODO Auto-generated method stub
		for(int i = 0;i < transactionList.size();i++) {
			String individualItems[] = transactionList.get(i).split(" ");
			if(individualItems.length >= minItems) {
				for(String item : individualItems) {
					BitSet bitset;
					if(itemToBitSetMap.containsKey(item)) {
						bitset = itemToBitSetMap.get(item);
					}
					else {
						bitset = new BitSet();
					}
					bitset.set(validTransactionCnt);
					itemToBitSetMap.put(item, bitset);
				}
				validTransactionCnt++;
			}
		}
		
	}
	
	

}
