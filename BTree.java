// SUBMIT
public class BTree implements BTreeInterface {

	// ///////////////////BEGIN DO NOT CHANGE ///////////////////
	// ///////////////////BEGIN DO NOT CHANGE ///////////////////
	// ///////////////////BEGIN DO NOT CHANGE ///////////////////
	private BNode root;
	private final int t;

	/**
	 * Construct an empty tree.
	 */
	public BTree(int t) { //
		this.t = t;
		this.root = null;
	}

	// For testing purposes.
	public BTree(int t, BNode root) {
		this.t = t;
		this.root = root;
	}

	@Override
	public BNode getRoot() {
		return root;
	}

	@Override
	public int getT() {
		return t;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((root == null) ? 0 : root.hashCode());
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
		BTree other = (BTree) obj;
		if (root == null) {
			if (other.root != null)
				return false;
		} else if (!root.equals(other.root))
			return false;
		if (t != other.t)
			return false;
		return true;
	}

	// ///////////////////DO NOT CHANGE END///////////////////
	// ///////////////////DO NOT CHANGE END///////////////////
	// ///////////////////DO NOT CHANGE END///////////////////


	@Override
	public Block search(int key) {
		if(root==null){
			return null;
		}
		else{
			return root.search(key);
		}
	}

	@Override
	public void insert(Block b) {
		if(root==null){
			BNode newRoot = new BNode(t,b);
			root = newRoot;
			return;
		}
		if(root.isFull()){
			BNode newRoot = new BNode(t,root);
			root = newRoot;
			root.splitChild(0);
			root.insertNonFull(b);
		}
		else{
			root.insertNonFull(b);
		}
	}

	@Override
	public void delete(int key) {
		if(root==null || root.isEmpty()){
			return;
		}
		else if (search(key)==null){
			return;
		}
		else if(root.isLeaf()){
			if(root.getNumOfBlocks()==1) {
				root.deleteFromNonMinimalLeaf(key);
				root = null;
			}
			else{
				root.deleteFromNonMinimalLeaf(key);
			}
		}
		else{
			if(root.getNumOfBlocks()==1 && root.getChildAt(0).isMinSize() && root.getChildAt(1).isMinSize()){
				root.mergeChildWithRightSibling(0);
				root = root.getChildAt(0);
			}
			root.delete(key);
		}
	}

	@Override
	public MerkleBNode createMBT() {
		if(root==null || root.isEmpty()){
			return null;
		}
		else{
			return root.createHashNode();
		}
	}
}
