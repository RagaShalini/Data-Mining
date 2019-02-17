//Created by Raga Shalini Koka
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ItemSet implements Comparable<Object>{
	
	List<String> itemList;
	int support;
	
	public ItemSet(String item, int support) {
		itemList = new LinkedList<String>();
		itemList.add(item);
		this.support = support;
	}
	
	public ItemSet(ItemSet first, ItemSet second) {
		itemList = new LinkedList<String>();
		itemList.addAll(first.itemList);
		int secondItemListSize = second.itemList.size();
		itemList.add(second.itemList.get(secondItemListSize - 1));
		
	}

	@Override
	public int compareTo(Object secondObject) {
		// TODO Auto-generated method stub
		List<String> firstList = this.itemList;
		ItemSet second = (ItemSet)secondObject;
		List<String> secondList = second.itemList;
		int itemListSize = firstList.size();
		for(int i = 0;i < itemListSize;i++ ) {
			if(!firstList.get(i).equals(secondList.get(i))) {
				return firstList.get(i).compareTo(secondList.get(i));
			}
			
		}
		return 0;
	}

	public String formatOutput() {
		// TODO Auto-generated method stub
		List<String> itemList = this.itemList;
		String outputString = "";
		for(int i = 0;i < itemList.size();i++) {
			outputString += itemList.get(i) + " ";
		}
		
		outputString += "(" + this.support + ")\n";
		
		return outputString;
 		
	}

}
