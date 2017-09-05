import java.util.ArrayList;

//SUBMIT
public class BNode implements BNodeInterface {

	// ///////////////////BEGIN DO NOT CHANGE ///////////////////
	// ///////////////////BEGIN DO NOT CHANGE ///////////////////
	// ///////////////////BEGIN DO NOT CHANGE ///////////////////
	private final int t;
	private int numOfBlocks;
	private boolean isLeaf;
	private ArrayList<Block> blocksList;
	private ArrayList<BNode> childrenList;

	/**
	 * Constructor for creating a node with a single child.<br>
	 * Useful for creating a new root.
	 */
	public BNode(int t, BNode firstChild) {
		this(t, false, 0);
		this.childrenList.add(firstChild);
	}

	/**
	 * Constructor for creating a <b>leaf</b> node with a single block.
	 */
	public BNode(int t, Block firstBlock) {
		this(t, true, 1);
		this.blocksList.add(firstBlock);
	}

	public BNode(int t, boolean isLeaf, int numOfBlocks) {
		this.t = t;
		this.isLeaf = isLeaf;
		this.numOfBlocks = numOfBlocks;
		this.blocksList = new ArrayList<Block>();
		this.childrenList = new ArrayList<BNode>();
	}

	// For testing purposes.
	public BNode(int t, int numOfBlocks, boolean isLeaf,
			ArrayList<Block> blocksList, ArrayList<BNode> childrenList) {
		this.t = t;
		this.numOfBlocks = numOfBlocks;
		this.isLeaf = isLeaf;
		this.blocksList = blocksList;
		this.childrenList = childrenList;
	}

	@Override
	public int getT() {
		return t;
	}

	@Override
	public int getNumOfBlocks() {
		return numOfBlocks;
	}

	@Override
	public boolean isLeaf() {
		return isLeaf;
	}

	@Override
	public ArrayList<Block> getBlocksList() {
		return blocksList;
	}

	@Override
	public ArrayList<BNode> getChildrenList() {
		return childrenList;
	}

	@Override
	public boolean isFull() {
		return numOfBlocks == 2 * t - 1;
	}

	@Override
	public boolean isMinSize() {
		return numOfBlocks == t - 1;
	}
	
	@Override
	public boolean isEmpty() {
		return numOfBlocks == 0;
	}
	
	@Override
	public int getBlockKeyAt(int indx) {
		return blocksList.get(indx).getKey();
	}
	
	@Override
	public Block getBlockAt(int indx) {
		return blocksList.get(indx);
	}

	@Override
	public BNode getChildAt(int indx) {
		return childrenList.get(indx);
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((blocksList == null) ? 0 : blocksList.hashCode());
		result = prime * result
				+ ((childrenList == null) ? 0 : childrenList.hashCode());
		result = prime * result + (isLeaf ? 1231 : 1237);
		result = prime * result + numOfBlocks;
		result = prime * result + t;
		return result;
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BNode other = (BNode) obj;
		if (blocksList == null) {
			if (other.blocksList != null)
				return false;
		} else if (!blocksList.equals(other.blocksList))
			return false;
		if (childrenList == null) {
			if (other.childrenList != null)
				return false;
		} else if (!childrenList.equals(other.childrenList))
			return false;
		if (isLeaf != other.isLeaf)
			return false;
		if (numOfBlocks != other.numOfBlocks)
			return false;
		if (t != other.t)
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "BNode [t=" + t + ", numOfBlocks=" + numOfBlocks + ", isLeaf="
				+ isLeaf + ", blocksList=" + blocksList + ", childrenList="
				+ childrenList + "]";
	}

	// ///////////////////DO NOT CHANGE END///////////////////
	// ///////////////////DO NOT CHANGE END///////////////////
	// ///////////////////DO NOT CHANGE END///////////////////
	
	
	
	@Override
	public Block search(int key) {
		int i=0;
		while(i<this.numOfBlocks && key>this.getBlockKeyAt(i)){
			i++;
		}
		if(i<numOfBlocks && key==this.getBlockKeyAt(i)){
			return getBlockAt(i);
		}
		else if(isLeaf){
			return null;
		}
		else{
			return this.getChildAt(i).search(key);
		}
	}

	@Override
	public void insertNonFull(Block d) {
		int i=numOfBlocks-1;
		if(isLeaf){
			if(i==numOfBlocks-1){
				if(d.getKey()>=this.getBlockKeyAt(i)){
					blocksList.add(d);
					numOfBlocks++;
					return;
				}
				blocksList.add(getBlockAt(i));
				i--;
			}
			while(i>=0 && d.getKey()<this.getBlockKeyAt(i)){
				this.blocksList.set(i+1,getBlockAt(i));
				i--;
			}
			this.blocksList.set(i+1,d);
			numOfBlocks++;
		}
		else{
			while(i>=0 && d.getKey()<this.getBlockKeyAt(i)){
				i--;
			}
			i++;
			if(this.getChildAt(i).isFull()){
				this.splitChild(i);
				if(d.getKey()>this.getBlockKeyAt(i)){
					i++;
				}
			}
			getChildAt(i).insertNonFull(d);
		}
	}

	@Override
	public void delete(int key) {
		int i=0;
		while(i<this.numOfBlocks && key>this.getBlockKeyAt(i)){//search for key to delete int the block
			i++;
		}
		if(i<numOfBlocks && key==this.getBlockKeyAt(i)) {//the key is in the block
			if (isLeaf) {//the node is a non minimal leaf
					deleteFromNonMinimalLeaf(key);
			}
			else{
				BNode y = getChildAt(i);
				BNode z = getChildAt(i+1);
				if (!y.isMinSize()){
					//case 2
					Block pred = y.predecessor(key);
					blocksList.set(i,pred);
					y.delete(pred.getKey());

				}
				else if(y.isMinSize() && !z.isMinSize()){
					//case 3
					Block suc = z.successor(key);
					blocksList.set(i,suc);
					z.delete(suc.getKey());
				}
				else{
					//case 4
					mergeChildWithRightSibling(i);
					y.delete(key);
				}
			}
		}
		else{//the key is not in the node so it is located in the subtree
			BNode child = getChildAt(i);
			if (child.isMinSize()){//before entering a minimal node preform shifting or merging
				if(childHasNonMinimalLeftSibling(i)){
					shiftFromLeftSibling(i);
				}
				else if(childHasNonMinimalRightSibling(i)){
					shiftFromRightSibling(i);
				}
				else{
					if(i==numOfBlocks){
						i--;
					}
					mergeChildWithRightSibling(i);
					child = getChildAt(i);

				}
			}
			child.delete(key);
		}
	}

	@Override
	public MerkleBNode createHashNode() {
		if (isLeaf){
			return createLeafHashNode();
		}
		else{
			MerkleBNode root = createNonLeafHashNode();
			return root;
		}
	}

	/**
	 * Splits the child node at childIndex into 2 nodes.
	 * @param childIndex
	 */
	public void splitChild(int childIndex){
		BNode nodeToSplit = getChildAt(childIndex);
		BNode rNode = new BNode(t,nodeToSplit.isLeaf,t-1);
		for(int i=0; i<t-1; i++){//copy the t-1 biggest blocks
			rNode.blocksList.add(i,nodeToSplit.getBlockAt(i+t));
		}
		for(int i=2*t-2; i>t-1;i--){//remove these blocks from the node to split
			nodeToSplit.blocksList.remove(i);
		}
		if(!nodeToSplit.isLeaf){
			for(int i=0; i<t; i++){//if not a leaf copy the t-1 biggest children
				rNode.childrenList.add(i,nodeToSplit.getChildAt(i+t));
			}
			for(int i=2*t-1; i>t-1; i--){//remove these childrens from the node to spllit
				nodeToSplit.childrenList.remove(i);
			 }
		}
		for(int i=numOfBlocks; i>childIndex; i--){//move the childrens one index to the right
			if(i==numOfBlocks){
				childrenList.add(i+1,childrenList.get(i));
			}
			else{
				childrenList.set(i+1,childrenList.get(i));
			}
		}
		if(numOfBlocks>childIndex){//set rNode as a child
			childrenList.set(childIndex+1,rNode);
		}
		else{//set rNode as a child when spliting the root
			childrenList.add(childIndex+1,rNode);
		}
		for(int i=numOfBlocks-1; i>=childIndex; i--){// move the blocks one index to the right
			if(i==numOfBlocks-1){
				blocksList.add(i+1,blocksList.get(i));
			}
			else{
				blocksList.set(i+1,blocksList.get(i));
			}
		}
		if(numOfBlocks-1>=childIndex){
			blocksList.set(childIndex, nodeToSplit.blocksList.remove(t-1));//move the middle block from node to split to the father
		}
		else{
			blocksList.add(childIndex, nodeToSplit.blocksList.remove(t-1));//move the middle block from node to split to the father
		}
		numOfBlocks++;
		nodeToSplit.numOfBlocks= t-1;
	}

	/**
	 * Delete a key from a leaf who has more than t-1 blocks.
	 * @param key
	 */
	public void deleteFromNonMinimalLeaf(int key){
		blocksList.remove(search(key));
		numOfBlocks--;
	}

	/**
	 * True iff the child node at childIndx-1 exists and has more than t-1 blocks.
	 * @param childIndx
	 * @return
	 */
	private boolean childHasNonMinimalLeftSibling(int childIndx){
		if (childIndx==0){//the child has no left sibling
			return false;
		}
		else if(childrenList.get(childIndx-1).isMinSize()){//the child's left sibling is minimal
			return false;
		}
		return true;
	}

	/**
	 * True iff the child node at childIndx+1 exists and has more than t-1 blocks.
	 * @param childIndx
	 * @return
	 */
	private boolean childHasNonMinimalRightSibling(int childIndx){
		if (childIndx==numOfBlocks){//the child has no right sibling
			return false;
		}
		else if(childrenList.get(childIndx+1).isMinSize()){//the child's right sibling is minimal
			return false;
		}
		return true;
	}

	/**
	 * Add additional block to the child node at childIndx, by shifting from left sibling.
	 * @param childIndx
	 */
	private void shiftFromLeftSibling(int childIndx){
		BNode child= getChildAt(childIndx);
		BNode leftSibling = getChildAt(childIndx-1);
		child.blocksList.add(0,this.getBlockAt(childIndx-1));//move from the parent to the child
		child.numOfBlocks++;
		if(!child.isLeaf){
			child.childrenList.add(0, leftSibling.childrenList.remove(leftSibling.numOfBlocks));//move the child from left sibling
		}
		blocksList.set(childIndx-1,leftSibling.blocksList.remove(leftSibling.numOfBlocks-1));//move from the left sibling to the parent
		leftSibling.numOfBlocks--;
	}

	/**
	 * Add additional block to the child node at childIndx, by shifting from right sibling.
	 * @param childIndx
	 */
	private void shiftFromRightSibling(int childIndx){
		BNode child= getChildAt(childIndx);
		BNode rightSibling = getChildAt(childIndx+1);
		child.blocksList.add(this.getBlockAt(childIndx));//move from the parent to the child
		child.numOfBlocks++;
		if(!child.isLeaf){
			child.childrenList.add(child.numOfBlocks, rightSibling.childrenList.remove(0));//move the child from right sibling
		}
		blocksList.set(childIndx,rightSibling.blocksList.remove(0));//move from the right sibling to the parent
		rightSibling.numOfBlocks--;
	}

	/**
	 * Merges the child node at childIndx with its right sibling.<br>
	 * The right sibling node is removed.
	 * @param childIndx
	 */
	 void mergeChildWithRightSibling(int childIndx){
		BNode child= getChildAt(childIndx);
		BNode rightSibling = getChildAt(childIndx+1);
		child.blocksList.add(blocksList.remove(childIndx));
		child.numOfBlocks++;
		numOfBlocks--;
		for (Block b: rightSibling.blocksList) {
			child.blocksList.add(b);
			child.numOfBlocks++;
		}
		for (BNode b: rightSibling.childrenList) {
			child.childrenList.add(b);
		}
		childrenList.remove(rightSibling);
	}

	/**
	 * Finds and returns the block with the min key in the subtree.
	 * @return min key block
	 */
	private Block getMinKeyBlock(){
		return blocksList.get(0);
	}

	/**
	 * Finds and returns the block with the max key in the subtree.
	 * @return max key block
	 */
	private Block getMaxKeyBlock(){
		return blocksList.get(numOfBlocks-1);
	}

	/**
	 * Finds and return the predcessor of key
	 * @param key
	 * @return predecessor block
	 */
	private Block predecessor(int key){
		if(isLeaf){
			return getMaxKeyBlock();
		}
		else{
			return getChildAt(numOfBlocks).predecessor(key);
		}
	}

	/**
	 * Finds and return the successor of key
	 * @param key
	 * @return successor block
	 */
	private Block successor(int key){
		if(isLeaf){
			return getMinKeyBlock();
		}
		else{
			return getChildAt(0).successor(key);
		}
	}

	/**
	 * creates a MerkleBNode for a leaf node
	 * @return MerkleBNode
	 */
	private MerkleBNode createLeafHashNode(){
		ArrayList<byte[]> dataList = new ArrayList<byte[]>();
		for (Block b: blocksList) {
			dataList.add(b.getData());
		}
		MerkleBNode b = new MerkleBNode(HashUtils.sha1Hash(dataList));
		return b;
	}

	/**
	 * creates a MerkleBNode for a non-leaf node
	 * @return MerkleBNode
	 */
	private MerkleBNode createNonLeafHashNode(){
		ArrayList<byte[]> dataList = new ArrayList<byte[]>();
		for (int i=0; i<numOfBlocks; i++) {
			dataList.add(getChildAt(i).createHashNode().getHashValue());
			dataList.add(getBlockAt(i).getData());
		}
		dataList.add(getChildAt(numOfBlocks).createHashNode().getHashValue());
		ArrayList<MerkleBNode> MerklechildrenList = new ArrayList<>();
		for (BNode b:childrenList){
			MerkleBNode childNode = b.createHashNode();
			MerklechildrenList.add(childNode);
		}
		MerkleBNode b = new MerkleBNode(HashUtils.sha1Hash(dataList),MerklechildrenList);
		return b;
	}

}
