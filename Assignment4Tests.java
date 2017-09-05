import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

/**
 * @author nadav
 */
public class Assignment4Tests {

    public static void main(String[] args) {
        System.out.println("-------------Start of Tests-------------");
        testInsert();
        testSearch();
        testCreateMBT();
       testDelete();
        System.out.println("-------------End of Tests-------------");
    }

    private static void testInsert() {
        BTree firstTree = new BTree(3);
        initTree(firstTree, 40);
        ArrayList<Integer> arrayListFirst = inorder(new ArrayList<>(), firstTree.getRoot());
        ArrayList<Integer> expectedFirst = getIntegers(41);
        Test(true, arrayListFirst.containsAll(expectedFirst), "Insert - Test 1 of 8", "some keys were not inserted ");
        Test(expectedFirst, arrayListFirst, "Insert - Test 2 of 8", "keys are in the wrong place in the tree");
        Test(true, checkDegree(firstTree, firstTree.getRoot()), "Insert - Test 3 of 8", "found nodes with less than t-1 blocks or with more than 2t-1 blocks");
        Test(true, checkNumOfChildren(firstTree, firstTree.getRoot()), "Insert - Test 4 of 8", "found nodes with less or more than numOfBlocks+1 children");
        Test(true, checkLeavesLevel(firstTree), "Insert - Test 5 of 8", "found leaves not in the same depth");

        //same test as the previous but with more nodes.
      /*  BTree SecondTree = new BTree(15);
        initTree(SecondTree, 10000);
        ArrayList<Integer> arrayListSecond = inorder(new ArrayList<>(), SecondTree.getRoot());
        ArrayList<Integer> expectedSecond = getIntegers(10001);
        Test(expectedSecond, arrayListSecond, "Insert - Test 6 of 8", "keys are in the wrong place in the tree");
        Test(true, checkDegree(SecondTree, SecondTree.getRoot()), "Insert - Test 7 of 8", "found nodes with less than t-1 blocks or with more than 2t-1 blocks");
        Test(true, checkNumOfChildren(SecondTree, SecondTree.getRoot()), "Insert - Test 8 of 8", "found nodes with less or more than numOfBlocks+1 children");
*/
    }

    private static void testSearch() {
        BTree Tree = new BTree(3);
        initTree(Tree, 40);
        ArrayList<Integer> arrayList = new ArrayList<>();
        ArrayList<Integer> expected = new ArrayList<>();
        for (int i = 1; i < 41; i++) {
            expected.add(i);
            Block foundBlock = Tree.search(i);
            arrayList.add(foundBlock == null ? -1 : foundBlock.getKey());
        }

        Test(expected, arrayList, "Search - Test 1 of 2", "didn't find some keys");
        Test(null, Tree.search(1000), "Search - Test 2 of 2", "the key is not in the tree. null should be returned");
    }

    private static void testCreateMBT() {
        ArrayList<byte[]> output = new ArrayList<>();
        String s = "abcdefghijklmnopqrstuvwxyz";
        BTree tree = new BTree(3);
        for (int i = 0; i < s.length(); i++) {
            output.add(s.substring(i, i + 1).getBytes());
            tree.insert(new Block(i, s.substring(i, i + 1).getBytes()));
        }
        MerkleBNode MBTRoot = tree.createMBT();
        ArrayList<String> StringArray = getHashValues(MBTRoot, new ArrayList<>());
        String[] array = new String[StringArray.size()];
        StringArray.toArray(array);
        String expected = "[[-38, 35, 97, 78, 2, 70, -102, 13, 124, 123, -47, -67, -85, 92, -100, 71, 75, 25, 4, -36], [96, 12, -51, 27, 113, 86, -110, 50, -48, 29, 17, 11, -58, 62, -112, 107, -22, -80, 77, -116], [16, 65, 23, -100, -67, -38, 54, 111, -41, -80, 52, 127, 9, 37, 95, 119, 81, 112, -31, 3], [-128, 34, -18, -114, -104, 1, -91, -87, 15, -117, -93, 119, -56, 106, 54, -39, 71, 57, 111, -33], [104, -90, 88, -76, 75, -70, -17, -85, 62, -11, -51, 35, 25, 103, 25, 95, -64, -1, -99, 105], [120, -36, 12, -45, -25, -17, 71, 93, 44, 62, 102, 64, 120, -27, -15, -41, -77, -73, 25, -85], [87, -5, -11, 86, 23, -106, -84, 63, 64, -41, -5, -8, -26, -74, 73, -19, 86, 56, 85, 46], [-101, 2, -39, -105, 76, 20, -26, 35, -55, -1, -66, -41, 54, 11, -22, -53, -16, -36, -71, 95], [125, 31, 19, 48, -18, 12, -1, 68, 34, 101, -88, 47, 38, -41, 21, -44, 19, 47, 94, 17], [-94, 14, -40, 51, 57, -85, 50, -118, -24, 23, -84, -53, 63, -123, -102, -23, -24, -7, 48, -7], [-61, 91, 53, 73, 3, -82, -76, -68, -76, 13, -68, 84, -30, -120, 89, 122, -104, 51, -127, -123]]";
        Test(expected, Arrays.toString(array), "CreateMBT - Test 1 of 1");
    }

    private static void testDelete() {

        BTree tree = new BTree(2);
        BTreeLatex b = new BTreeLatex(tree,"Delete Test");
        initTree(tree,100);
        b.addTreeState("After Insertion");
        tree.delete(74);
        b.addTreeState("After Deletion of 74");
        tree.delete(89);
        b.addTreeState("After Deletion of 89");
        tree.delete(32);
        b.addTreeState("After Deletion of 32");
        tree.delete(103);//should not change anything in the tree because the key is not in the tree.
        b.addTreeState("After Deletion of 103");
        b.commitBufferedStates();
        b.finish();
        ArrayList<Integer> arrayList = inorder(new ArrayList<>(), tree.getRoot());
        ArrayList<Integer> expected = getIntegers(101);
        Test(null, tree.search(74), "Delete - Test 1 of 8", "the key 74 is still in the tree");
        Test(null, tree.search(32), "Delete - Test 2 of 8", "the key 32 is still in the tree");
        Test(null, tree.search(89), "Delete - Test 3 of 8", "the key 89 is still in the tree");
        expected.remove(88);
        expected.remove(73);
        expected.remove(31);
        Test(expected, arrayList, "Delete - Test 4 of 8", "keys are in the wrong place in the tree");

        for (int i = 0; i < 50; i++) {
            SecureRandom rnd = new SecureRandom();
            int keyToDelete = rnd.nextInt(100);
            Integer DeletedKey = keyToDelete;
            expected.remove(DeletedKey);
            tree.delete(keyToDelete);
        }
        Test(true, checkLeavesLevel(tree), "Delete - Test 5 of 8", "found leaves not in the same depth");
        Test(expected, inorder(new ArrayList<>(), tree.getRoot()), "Delete - Test 6 of 8", "keys are in the wrong place in the tree");
        Test(true, checkDegree(tree, tree.getRoot()), "Delete - Test 7 of 8", "found nodes with less than t-1 blocks or with more than 2t-1 blocks");
        Test(true, checkNumOfChildren(tree, tree.getRoot()), "Delete - Test 8 of 8", "found nodes with less or more than numOfBlocks+1 children");
    }


    private static ArrayList<Integer> getIntegers(int size) {
        ArrayList<Integer> expected = new ArrayList<>();
        for (int i = 1; i < size; i++) {
            expected.add(i);
        }
        return expected;
    }

    private static void initTree(BTree Tree, int toKey) {
        ArrayList<Block> Blocks = new ArrayList<>();
        for (int i = 1; i <toKey+1 ; i++) {
            Blocks.add(new Block(i, null));
        }
        Collections.shuffle(Blocks);
        for (Block block : Blocks) {
            Tree.insert(block);
        }
    }

    private static <T> void Test(T expected, T actual, String CallingMethod) {
        if (expected == null) {
            if (actual != null) {
                System.err.println(CallingMethod + ": was <" + actual + "> but should be <" + null + ">");
                return;
            }
            System.out.println(CallingMethod + "-Passed");
            return;
        }
        if (!(actual.equals(expected))) {
            System.err.println(CallingMethod + ": was <" + actual + "> but should be <" + expected + ">");
        } else System.out.println(CallingMethod + "-Passed");
    }

    private static <T> void Test(T expected, T actual, String CallingMethod, String ErrorMessage) {
        if (expected == null) {
            if (actual != null) {
                System.err.println(CallingMethod + " failed- " + ErrorMessage);
                return;
            }
            System.out.println(CallingMethod + "-Passed");
            return;
        }
        if (!(actual.equals(expected))) {
            System.err.println(CallingMethod + " failed- " + ErrorMessage);
        } else System.out.println(CallingMethod + "-Passed");

    }

    public static ArrayList<Integer> inorder(ArrayList<Integer> arrayList, BNode node) {
        if (node.isLeaf()) {
            for (int i = 0; i < node.getNumOfBlocks(); i++) {
                arrayList.add(node.getBlockKeyAt(i));
            }
        } else {
            for (int i = 0; i < node.getNumOfBlocks(); i++) {
                inorder(arrayList, node.getChildAt(i));
                arrayList.add(node.getBlockKeyAt(i));
            }
            inorder(arrayList, node.getChildAt(node.getNumOfBlocks()));
        }
        return arrayList;
    }

    public static boolean checkDegree(BTree tree, BNode node) {
        if (node.isLeaf())
            return (node.equals(tree.getRoot()) & node.getBlocksList().size() < (2 * node.getT())) || DegreeRange(node);
        boolean validDegree = true;
        for (int i = 0; i <= node.getBlocksList().size(); i++) {
            validDegree = checkDegree(tree, node.getChildAt(i)) && (node.equals(tree.getRoot()) || DegreeRange(node));
        }
        return validDegree;
    }

    public static boolean checkNumOfChildren(BTree tree, BNode node) {
        if (node.isLeaf())
            return true;
        boolean validNumOfChildren = true;
        for (int i = 0; i <= node.getBlocksList().size(); i++) {
            validNumOfChildren = checkNumOfChildren(tree, node.getChildAt(i)) && (node.equals(tree.getRoot()) || node.getChildrenList().size() == node.getBlocksList().size() + 1);
        }
        return validNumOfChildren;
    }

    private static boolean DegreeRange(BNode node) {
        return (node.getBlocksList().size() >= node.getT() - 1) & ((node.getBlocksList().size()) <= (2 * (node.getT())) - 1);
    }

    private static ArrayList<String> getHashValues(MerkleBNode merkleBNode, ArrayList<String> arrayList) {
        if (merkleBNode.isLeaf()) {
            arrayList.add(Arrays.toString(merkleBNode.getHashValue()));
        } else {
            for (int i = 0; i < merkleBNode.getChildrenList().size(); i++) {
                getHashValues(merkleBNode.getChildrenList().get(i), arrayList);
            }
            arrayList.add(Arrays.toString(merkleBNode.getHashValue()));
        }
        return arrayList;
    }


    private static int getHeightAndCheck(BNode node, boolean isRoot, int t) {
        if (node == null)
            return -2;

        if (node.isLeaf())
            return 0;

        if (node.getT() != t | t < 2)
            return -3;
// otherwise

        final int childrenCount = node.getNumOfBlocks() + 1;

        if (childrenCount > 2 * t)
            return -4;

        if (!isRoot & childrenCount < t)
            return -5;

        int heightMinus1 = getHeightAndCheck(node.getChildAt(0), false, t);
        if (heightMinus1 < 0)
            return heightMinus1;

        for (int i = 1; i < childrenCount; ++i) {
            int result = getHeightAndCheck(node.getChildAt(i), false, t);
            if (result < 0)
                return result;
            else if (result != heightMinus1)
                return -1;
// otherwise continue
        }

        return heightMinus1 + 1;
    }

    public static boolean checkLeavesLevel(BTree tree) {
        BNode root = tree.getRoot();
        if (root == null)
            return true;
        else
            return getHeightAndCheck(root, true, tree.getT()) >= 0;
    }

}


