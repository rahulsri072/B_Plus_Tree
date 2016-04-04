import sun.security.action.GetLongAction;

/**
 * The BPlusTree class implements B+-trees. Each BPlusTree stores its elements in the main memory (not on disks) for
 * simplicity reasons.
 * 
 * @author Jeong-Hyon Hwang (jhh@cs.albany.edu)
 */
public class BPlusTree {

	/**
	 * The maximum number of pointers that each node of this BPlusTree can have.
	 */
	protected int fanout;

	/**
	 * The root node of this BPlusTree.
	 */
	protected Node root;

	/**
	 * The Node class implements nodes that constitute a B+-tree. Each Node instance has multiple pointers to other
	 * nodes. At each node, the number of keys is smaller than the number of pointers by one.
	 * 
	 * @author Jeong-Hyon Hwang (jhh@cs.albany.edu)
	 * 
	 */
	protected class Node {

		/**
		 * The number of keys that this Node currently maintains.
		 */
		int numberOfKeys;

		/**
		 * The keys that this Node maintains.
		 */
		Object[] keys;

		/**
		 * The pointers that this Node maintains.
		 */
		Object[] pointers;

		/**
		 * Constructs a Node.
		 */
		protected Node(int fanout) {
			numberOfKeys = 0;
			keys = new Comparable[fanout - 1];
			pointers = new Object[fanout];
		}

		/**
		 * Copy-constructs a Node.
		 * 
		 * @param node
		 *            the other node to copy from.
		 */
		protected Node(Node node) {
			this.numberOfKeys = node.numberOfKeys;
			keys = new Object[node.keys.length];
			System.arraycopy(node.keys, 0, keys, 0, node.keys.length);
			pointers = new Object[node.pointers.length];
			for (int i = 0; i < node.pointers.length; i++) {
				Object pointer = node.pointers[i];
				if (pointer instanceof Node)
					pointers[i] = new Node((Node) pointer); // copy construct the node.
				else
					pointers[i] = pointer;
			}
		}

		/**
		 * Clears this Node.
		 */
		protected void clear() {
			numberOfKeys = 0;
			for (int i = 0; i < keys.length; i++)
				keys[i] = null;
			for (int i = 0; i < pointers.length; i++)
				pointers[i] = null;
		}

		/**
		 * Determines whether or not this Node is a leaf node. It is assumed that all the pointers of every non-leaf
		 * node reference Nodes whereas the pointers of leaf nodes can reference something else.
		 * 
		 * @return true if this Node is a leaf node; false otherwise.
		 */
		protected boolean isLeafNode() {
			return !(pointers[0] instanceof Node);
		}

		/**
		 * Determines whether or not this Node has room for a new entry.
		 * 
		 * @return true if this Node has room for a new node; false otherwise.
		 */
		protected boolean hasRoom() {
			return numberOfKeys < fanout - 1;
		}

		/**
		 * Returns the first index i such that keys[i] >= the given key.
		 * 
		 * @param key
		 *            the given key.
		 * @return the first index i such that keys[i] >= the given key; -1 if there is no such i.
		 */
		protected int findIndexGE(Object key) {
			for (int i = 0; i < numberOfKeys; i++) {
				if (compare(keys[i], key) >= 0)
					return i;
			}
			return -1;
		}

		/**
		 * Returns the largest index i such that keys[i] < the given key.
		 * 
		 * @param key
		 *            the given key.
		 * @return the largest index i such that keys[i] < the given key; -1 if there is no such i.
		 */
		protected int findIndexL(Object key) {
			for (int i = numberOfKeys - 1; i >= 0; i--) {
				if (compare(keys[i], key) < 0)
					return i;
			}
			return -1;
		}

		/**
		 * Returns the last non-null pointer (assuming that this Node is a non-leaf node).
		 * 
		 * @return the last non-null pointer.
		 */
		protected Object getLastNonNullPointer() {
			return pointers[numberOfKeys];
		}

		/**
		 * Inserts the specified key and value at the specified location.
		 * 
		 * @param key
		 *            the key to insert.
		 * @param value
		 *            the value to insert.
		 * @param pos
		 *            the insertion position
		 */
		protected void insert(Object key, Object value, int pos) {
			for (int i = numberOfKeys; i > pos; i--) {
				keys[i] = keys[i - 1];
				pointers[i] = pointers[i - 1];
			}
			keys[pos] = key;
			pointers[pos] = value;
			numberOfKeys++;
		}

		/**
		 * Inserts the specified key and value after the specified pointer.
		 * 
		 * @param key
		 *            the key to insert.
		 * @param value
		 *            the value to insert.
		 * @param pointer
		 *            the pointer after which the key and value will be inserted.
		 */
		protected void insertAfter(Object key, Object value, Object pointer) {
			int i = numberOfKeys;
			while (pointers[i] != pointer) {
				keys[i] = keys[i - 1];
				pointers[i + 1] = pointers[i];
				i--;
			}
			keys[i] = key;
			pointers[i + 1] = value;
			numberOfKeys++;
		}

		/**
		 * Inserts the specified key and value assuming that this Node has room for them and is a leaf node.
		 * 
		 * @param key
		 *            the key to insert.
		 * @param value
		 *            the value to insert.
		 */
		protected void insertInLeaf(Object key, Object value) {
			if (numberOfKeys == 0 || compare(key, keys[0]) < 0) {
				insert(key, value, 0);
			} else {
				int i = findIndexL(key);
				insert(key, value, i + 1);
			}
		}

	}

	/**
	 * Constructs a BPlusTree.
	 * 
	 * @param fanout
	 *            the maximum number of pointers that each node of this BPlusTree can have.
	 */
	public BPlusTree(int fanout) {
		this.fanout = fanout;
	}

	/**
	 * Copy-constructs a BPlusTree.
	 * 
	 * @param tree
	 *            another tree to copy from.
	 */
	public BPlusTree(BPlusTree tree) {
		this.fanout = tree.fanout;
		this.root = new Node(tree.root);
	}

	/**
	 * Finds the node in this BPlusTree that must be responsible for the specified key.
	 * 
	 * @param key
	 *            the search key.
	 * @return the node in this BPlusTree that must be responsible for the specified key.
	 */
	public Node find(Object key) {
		Node c = root;
		while (!c.isLeafNode()) {
			int i = c.findIndexGE(key); // find smallest i such that c.keys[i] >= key
			if (i < 0) { // if no i such that c.keys[i] >= key
				c = (Node) c.getLastNonNullPointer();
			} else if (compare(key, c.keys[i]) == 0) {
				c = (Node) c.pointers[i + 1];
			} else { // if c.keys[i] = key
				c = (Node) c.pointers[i];
			}
		}
		return c;
	}

	/**
	 * Finds the parent node of the specified node.
	 * 
	 * @param node
	 *            the node of which the parent needs to be found.
	 * @return the parent node of the specified node; null if the parent cannot be found.
	 */
	public Node findParent(Node node) {
		Node p = root;
		while (p != null) {
			Object key = node.keys[0];
			int i = p.findIndexGE(key); // find smallest i such that p.keys[i] >= key
			Node c;
			if (i < 0) { // if no i such that p.keys[i] >= key
				c = (Node) p.getLastNonNullPointer();
			} else if (compare(key, p.keys[i]) == 0) {
				c = (Node) p.pointers[i + 1];
			} else { // if p.keys[i] = key
				c = (Node) p.pointers[i];
			}
			if (c == node) { // if found the parent of the node.
				return p;
			}
			p = c;
		}
		return null;
	}

	/**
	 * Inserts the specified key and the value into this BPlusTree.
	 * 
	 * @param key
	 *            the key to insert.
	 * @param value
	 *            the value to insert.
	 */
	public void insert(Object key, Object value) {
		Node l;
		if (root == null) { // if the root is null
			root = new Node(fanout);
			l = root;
		} else { // if root is not null
			l = find(key);
		}
		if (l.hasRoom()) { // if node l has room for the new entry
			l.insertInLeaf(key, value);
		} else { // if split is required (l is a leaf node)
			Node t = new Node(fanout + 1); // create a temporary node
			for (int i = 0; i < l.numberOfKeys; i++) { // copy everything to the temporary node
				t.insert(l.keys[i], l.pointers[i], i);
			}
			t.insertInLeaf(key, value); // insert the key and values to the temporary node
			Node nl = new Node(fanout); // create a new leaf node
			nl.pointers[nl.pointers.length - 1] = l.pointers[l.pointers.length - 1]; // set the last pointer of n to
			// node nl
			l.clear(); // clear node l
			l.pointers[l.pointers.length - 1] = nl; // set the last pointer of l to nl
			int m = (int) Math.ceil(fanout / 2.0); // compute the split point
			for (int i = 0; i < m; i++) { // put the first half into node l
				l.insert(t.keys[i], t.pointers[i], i);
			}
			for (int i = m; i < t.numberOfKeys; i++) { // put the second half to node nl
				nl.insert(t.keys[i], t.pointers[i], i - m);
			}
			insertInParent(l, nl.keys[0], nl); // use the first key of nl as the separator.
		}
	}

	/**
	 * Inserts pointers to the specified nodes into an appropriate parent node.
	 * 
	 * @param n
	 *            a node.
	 * @param key
	 *            the key that splits the nodes
	 * @param nn
	 *            a new node.
	 */
	void insertInParent(Node n, Object key, Node nn) {
		if (n == root) { // if the root was split
			root = new Node(fanout); // create a new node
			root.insert(key, n, 0); // make the new root point to the nodes.
			root.pointers[1] = nn;
			return;
		}
		Node p = findParent(n);
		if (p.hasRoom()) {
			p.insertAfter(key, nn, n); // insert key and nn right after n
		} else { // if split is required
			Node t = new Node(fanout + 1); // crate a temporary node
			for (int i = 0; i < p.numberOfKeys; i++) { // copy everything of p to the temporary node
				t.insert(p.keys[i], p.pointers[i], i);
			}
			t.pointers[p.numberOfKeys] = p.pointers[p.numberOfKeys];
			t.insertAfter(key, nn, n); // insert key and nn after n
			p.clear(); // clear p
			int m = (int) Math.ceil(fanout / 2.0); // compute the split point

			for (int i = 0; i < m - 1; i++) { // put the first half back to p
				p.insert(t.keys[i], t.pointers[i], i);
			}
			p.pointers[m - 1] = t.pointers[m - 1];

			Node np = new Node(fanout); // create a new node
			for (int i = m; i < t.numberOfKeys; i++) { // put the second half to np
				np.insert(t.keys[i], t.pointers[i], i - m);
			}
			np.pointers[t.numberOfKeys - m] = t.pointers[t.numberOfKeys];

			insertInParent(p, t.keys[m - 1], np); // use the middle key as the separator
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected int compare(Object k1, Object k2) {
		return ((Comparable) k1).compareTo(k2);
	}

	/**
	 * Deletes the specified key and the value from this BPlusTree.
	 * 
	 * @param key
	 *            the key to delete.
	 * @param value
	 *            the value to delete.
	 */
	public void delete(Object key, Object value) {

		/*Page 498, deletion algorithm, Database System Concepts: 6th Edition, Korth
		 * 
		 * */

		Node l = find(key); // Find node which contains the key
		for(int i=0; i<l.numberOfKeys; i++)
			if(l.keys[i]==key && key!=null)
				delete_entry(l, key, value); 

		// Delete_Entry would be called, when we find the node responsible for the key. 
		// Call the function, only if the key/value is present
	}

	public void delete_entry(Node n, Object key, Object value){
		//int m = (int) Math.ceil(fanout / 2.0); // Formula

		int i=0, indexDelete = 0;

		// delete (K, P) from N
		for(i=0; i<n.numberOfKeys; i++)
			if(n.keys[i]==key){ // Key found in the node.
				n.keys[i]=null; // Delete the key

				indexDelete = i; // Storing the Index of deletion, to be used for shifting
			}

		shift(indexDelete, n); // Shift to the left (pointers/values) in node n

		// N is the root and N has only one remaining child
		if(n==root && noOfpointers(root)==1){ 

			//make the child of N the new root of the tree and delete N

			for(i=0; i<n.pointers.length; i++){
				if(n.pointers[i]!=null) // Checking for child. This iwll be the new root node.
					root = (Node)n.pointers[i];
			}

			n.clear(); // Delete N

		}

		//int m = (int) Math.ceil(fanout / 2.0);
		else if( noOfpointers(n) < 2 ){ //N has too few values/pointers

			// Let N' be the previous or next child of parent(N)

			Node nParent = findParent(n); // Parent of Node N

			int nPos = 0; // Position of N
			int n_pos = 0; // Position of N'

			// Get the position of N in parent
			for(i=0; i < nParent.pointers.length; i++)
				if((Node)nParent.pointers[i]==n)
					nPos = i;


			//K' be the value between pointers N and N in parent(N)
			Object k_;
			int k_pos = 0;

			if(nPos==0){ // When N' is next node of same parent
				k_ = nParent.keys[nPos];
				n_pos = nPos + 1;
				k_pos = nPos;
			}
			else {// if(nPos==1){ // N' is previous/predecessor of N
				n_pos = nPos - 1;
				k_pos = n_pos;
				k_ = nParent.keys[n_pos];
			}

			Node n_ = (Node)nParent.pointers[n_pos];

			//entries in N and N' can fit in a single node

			// Fanout being the max capacity
			if( ( ((Node)nParent.pointers[nPos]).numberOfKeys + ((Node)nParent.pointers[n_pos]).numberOfKeys ) < fanout){

				// N is a predecessor of N'
				if(nPos < n_pos){
					// Swap N and N'

					/* temp = n
					 * n = n_
					 * n_ = temp
					 * */

					// Simple Swap using 3 variables. Here swapping Pointers as well as keys.

					Node temp = new Node(fanout);
					temp.numberOfKeys = 0;

					// Temp <-- N

					for(i=0; i<n.numberOfKeys; i++){
						temp.keys[i] = n.keys[i];
						temp.numberOfKeys++;
					}

					for(i=0; i<fanout; i++)
						temp.pointers[i] = n.pointers[i];



					// N <-- N'
					n.numberOfKeys=0;

					for(i=0; i<n_.numberOfKeys; i++){
						n.keys[i] = n_.keys[i];
						n.numberOfKeys++;
					}

					for(i=0; i<fanout; i++)
						n.pointers[i] = n_.pointers[i];


					// N' <-- Temp
					n_.numberOfKeys=0;

					for(i=0; i<temp.numberOfKeys; i++){
						n_.keys[i] = temp.keys[i];
						n_.numberOfKeys++;
					}

					for(i=0; i<fanout; i++)
						n_.pointers[i] = temp.pointers[i];

					temp.clear(); // Delete temp, as its not needed
				}

				// N is not a leaf
				if(!n.isLeafNode()){


					//append K' and all pointers and values in N to N'
					n_.keys[n_.numberOfKeys++] = k_;

					for(i=0; i < n_.numberOfKeys; i++){
						n_.pointers[n_.numberOfKeys] = n.pointers[i];
						n_.keys[n_.numberOfKeys++] = n.keys[i];
					}
					if(n.pointers[n.numberOfKeys]!=null) // Last pointer
						n_.pointers[n_.numberOfKeys] = n.pointers[n.numberOfKeys];
				}
				else{   // N is a leaf

					//append all (Ki , Pi) pairs in N to N'
					for(i=0; i < n.numberOfKeys; i++){
						n_.pointers[n_.numberOfKeys] = n.pointers[i];
						n_.keys[n_.numberOfKeys++] = n.keys[i];
					}


					// set N'.Pn = N.Pn
					// Pn is the last pointer, hence (fanout-1)
					n_.pointers[fanout-1] = n.pointers[fanout -1];
				}


				/* WHen root or Internal Node (Non Leaf) has exactly 1 child, make sure that its the left. In the example, I was getting a null child
				 * after deletion of 20, hence needed to delete.*/
				if(nParent.numberOfKeys==1)
					nParent.pointers[nPos] = null;

				delete_entry(nParent, k_, n); // delete entry(parent(N), K', N);
				n.clear(); // Delete Node N
			}

			else{ // /* Redistribution: borrow an entry from N' */


				// N' is a predecessor of N
				if(n_pos < nPos){

					// If N is not a leaf
					if(!n.isLeafNode()){

						int mPos = 0; // Position of last pointer

						for(i=0; i<n_.pointers.length; i++){
							if(n_.pointers[i]!=null){
								mPos++;
								//break;
							}
						}

						//let m be such that N'.Pm is the last pointer in N'
						Object m = n_.pointers[mPos];

						//remove (N'.Km−1, N'.Pm) from N'

						Object tempKm = n_.keys[mPos-1];  // Storing this for further use before removal

						n_.keys[mPos-1] = null;
						n_.numberOfKeys--;
						n_.pointers[mPos] = null;

						//insert (N'.Pm, K) as the first pointer and value in N, by shifting other pointers and values right

						n.insert(k_, m, 0);

						//replace K' in parent(N) by N'.Km−1
						nParent =  findParent(n);
						nParent.keys[k_pos] = nParent.keys[mPos-1];

					}
					else{ // N is a Leaf

						int mPos = 0; // Position of last pointer
						Object lastkey = n.keys[n.numberOfKeys]; // Last key

						for(i=0; i<n_.pointers.length; i++){
							if(n_.pointers[i]!=null){
								mPos++;
								//break;
							}
						}

						Object tempKm = n_.keys[mPos]; // Storing this for further use before removal

						Object m = n_.pointers[mPos];
						//remove (N'.Pm, N'.Km) from N'

						n_.pointers[mPos] = null;
						n_.keys[mPos] = null;
						n_.numberOfKeys--;

						//insert (N'.Pm, K'.Km) as the first pointer and value in N, by shifting other pointers and values right
						n.insert(lastkey, m, 0);

						//replace K' in parent(N) by N'.Km
						nParent =  findParent(n);
						nParent.keys[k_pos] = nParent.keys[mPos];

					}
				}
				else
				{
					// Symmetric case

					// If N is not a leaf
					if(!n.isLeafNode()){

						//let m be such that N'.Pm is the last pointer in N'

						int mPos = 0; // Position of last pointer

						for(i=0; i<n_.pointers.length; i++){
							if(n_.pointers[i]!=null){
								mPos++;
								//break;
							}
						}

						Object m = n_.pointers[mPos];

						//remove (N'.Km−1, N'.Pm) from N'

						Object tempKm = n_.keys[mPos-1];  // Storing this for further use before removal

						n_.keys[mPos-1] = null;
						n_.numberOfKeys--;
						n_.pointers[mPos] = null;

						//insert (N'.Pm, K) as the first pointer and value in N, by shifting other pointers and values right
						n.insert(k_, m, 0);

						//replace K' in parent(N) by N'.Km−1
						nParent =  findParent(n);
						nParent.keys[k_pos] = nParent.keys[mPos-1];

					}
					else{ // N is a Leaf

						int mPos = 0; // Position of last pointer
						Object lastkey = n.keys[n.numberOfKeys]; // Last key

						for(i=0; i<n_.pointers.length; i++){
							if(n_.pointers[i]!=null){
								mPos++;
								//break;
							}
						}

						Object tempKm = n_.keys[mPos];

						Object m = n_.pointers[mPos];

						//remove (N'.Pm, N'.Km) from N'
						n_.pointers[mPos] = null;
						n_.keys[mPos] = null;
						n_.numberOfKeys--;

						//insert (N'.Pm, K'.Km) as the first pointer and value in N, by shifting other pointers and values right
						n.insert(lastkey, m, 0);

						//replace K' in parent(N) by N'.Km
						nParent =  findParent(n);
						nParent.keys[k_pos] = nParent.keys[mPos];
					}

				}
			}


		}

	}

	private void shift(int index, Node n){
		int i=0;
		// Shifting keys/ pointers to the left. Suppose I'm deleting |10|20|, 10 here, I need to shift 20 to the left, else it'll have null value
		if(index!=n.numberOfKeys-1){
			for(i=index; i<n.numberOfKeys-1;i++){
				n.keys[i] = n.keys[i+1];
				n.pointers[i] = n.pointers[i+1];
			}
			n.pointers[i] = n.pointers[i+1];
			n.pointers[i+1]=null; // After shifting, we get 1 last pointer which is not gonna be used. Hence null.
			n.keys[n.numberOfKeys-1]=null; // This key is shifted, left, hence null now.
		}
		n.numberOfKeys--; // After deletion, decrease the no. of Keys for the node.
	}

	private int noOfpointers(Node p){

		// Find number of pointers in a node.

		int count = 0;
		for(int i=0; i < p.pointers.length; i++){
			if(p.pointers[i]!=null)
				count++; // Count increments, when node is not maintaining null pointers
		}
		return count; // Final count of pointers in the node.
	}


}
