package ca.pfv.spmf.algorithms.sequentialpatterns.clospan_AGP.items.trie;

import ca.pfv.spmf.algorithms.sequentialpatterns.clospan_AGP.items.abstractions.ItemAbstractionPair;
import ca.pfv.spmf.algorithms.sequentialpatterns.clospan_AGP.items.patterns.Pattern;
import ca.pfv.spmf.algorithms.sequentialpatterns.clospan_AGP.items.patterns.PatternCreator;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import java.util.*;

/**
 * Class that implement a trie structure. A trie is composed of a list of
 * nodes children that are also the beginning of other trie structure. Those
 * nodes are composed of both a ItemAstractionPair object and a Trie, where the
 * children appear.
 * <p/>
 * The current trie is referring to a pattern that can be obtained from the root
 * until this one, passing by the different nodes in the way that are ancestors
 * of the current trie. We do not keep any trace of the parent nodes since the
 * whole trie will be run at the end of the algorithm, just before applying the
 * postprocessing step to remove the remaining non-closed frequent patterns.
 * <p/>
 * Besides, in a trie we keep some information relative to that pattern that is
 * referred, such as the sequences where the pattern appears, its support, and
 * some other information used in the key generation of the pruning methods.
 * <p/>
 * Copyright Antonio Gomariz Peñalver 2013
 * <p/>
 * This file is part of the SPMF DATA MINING SOFTWARE
 * (http://www.philippe-fournier-viger.com/spmf).
 * <p/>
 * SPMF is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * <p/>
 * SPMF is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License along with
 * SPMF. If not, see <http://www.gnu.org/licenses/>.
 *
 * @author agomariz
 */
public class Trie implements Comparable<Trie> {

    /**
     * List of children of the current trie
     */
    private List<TrieNode> nodes;
    /**
     * List of sequences IDs where the pattern, to which the current trie is
     * referring to, appears
     */
    private BitSet appearingIn = new BitSet();
    /**
     * Support that the pattern, to which the current trie is referring to, has
     */
    private int support = -1;
    /**
     * Counter that keeps the sum of all the sequence IDs that are in
     * appearingIn list
     */
    private int sumSequencesIDs = -1;
    /**
     * Static field in order to generate a different identifier for all the
     * tries generated by the algorithm
     */
    private static int intId = 1;
    /**
     * Trie identifier
     */
    private int id;

    /**
     * Standard constructor of a Trie. It sets the list of nodes to empty.
     */
    public Trie() {
        nodes = new ArrayList<TrieNode>();
        id = intId++;
    }

    /**
     * Constructor of a Trie by means of a list of NodeTrie.
     *
     * @param nodes List of nodes with which we want to initialize the Trie
     */
    public Trie(List<TrieNode> nodes) {
        this.nodes = nodes;
        id = intId++;
    }

    /**
     * It obtain its ith trie child
     *
     * @param index Child index in which we are interested
     * @return the ith trie child.
     */
    public Trie getChild(int index) {
        return nodes.get(index).getChild();
    }

    /**
     * It set a child to the Trie given as parameter
     *
     * @param index Child index in which we are interested
     * @param child Trie that we want to insert
     */
    public void setChild(int index, Trie child) {
        this.nodes.get(index).setChild(child);
    }

    /**
     * It gets the list of nodes associated with the Trie
     *
     * @return the list of nodes
     */
    public List<TrieNode> getNodes() {
        return nodes;
    }

    /**
     * It updates the list of nodes associated with the Trie
     *
     * @param nodes the list of nodes to be used for updating
     */
    public void setNodes(List<TrieNode> nodes) {
        this.nodes = nodes;
    }

    /**
     * It removes the ith child of the Trie.
     *
     * @param index Child index in which we are interested
     * @return true if the node was removed, otherwise, the index was out of range and no node was removed.
     */
    public boolean remove(int index) {
        //if there are some nodes and the index is not within the range of nodes
        if (levelSize() == 0 || index >= levelSize()) {
            return false;
        }
        //We remove the child pointed out by index
        getChild(index).removeAll();
        return true;
    }

    /**
     * It gets the pair of the ith child
     *
     * @param index Child index in which we are interested
     * @return the pair of the ith child
     */
    public ItemAbstractionPair getPair(int index) {
        return nodes.get(index).getPair();
    }

    /**
     * It gets the whole TrieNode of the ith child
     *
     * @param index Child index in which we are interested
     * @return the trie node
     */
    public TrieNode getNode(int index) {
        return nodes.get(index);
    }

    /**
     * It updates the whole TrieNode of the ith child
     *
     * @param index Child index in which we are interested
     * @param node
     */
    public void setNode(int index, TrieNode node) {
        nodes.set(index, node);
    }

    /**
     * It returns the number of children that a Trie has
     *
     * @return the number of children
     */
    public int levelSize() {
        if (nodes == null) {
            return 0;
        }
        return nodes.size();
    }

    /**
     * It removes all its descendands tries and then the Trie itself.
     */
    public void removeAll() {
        //If there are no nodes
        if (levelSize() == 0) {
            //We have already finish
            return;
        }
        //Otherwise, for each node of the Trie children
        for (TrieNode node : nodes) {
            Trie currentChild = node.getChild();
            //We remove all the descendants appearing from its child
            if (currentChild != null) {
                currentChild.removeAll();
            }
            //And we make null both its child and pair
            node.setChild(null);
            node.setPair(null);
        }
        nodes.clear();
    }

   /* public void mergeWithTrie(TrieNode newTrie) {
        if (levelSize() == 0) {
            if (nodes == null) {
                nodes = new ArrayList<TrieNode>(1);
            }
            nodes.add(newTrie);
        } else {
            nodes.add(newTrie);
        }
    }*/

    /**
     * It sorts the children by lexicographic order (given by their pair values)
     */
    public void sort() {
        Collections.sort(nodes);
    }

    /**
     * It returns the list of sequences Ids where the pattern referred by
     * the Trie appears
     *
     * @return the list of sequence ids
     */
    public BitSet getAppearingIn() {
        return this.appearingIn;
    }

    /**
     * It updates the list of sequences Ids where the pattern referred by
     * the Trie appears
     *
     * @param appearingIn The list of sequence Ids to update
     */
    public void setAppearingIn(BitSet appearingIn) {
        this.appearingIn = appearingIn;
    }

    /**
     * Get the string representation of this Trie
     *
     * @return the string representation
     */
    @Override
    public String toString() {
        if (nodes == null) {
            return "";
        }
        StringBuilder result = new StringBuilder("ID=" + id + "[");
        if (!nodes.isEmpty()) {
            for (TrieNode node : nodes) {
                result.append(node.getPair()).append(',');
            }
            result.deleteCharAt(result.length() - 1);
        } else {
            result.append("NULL");
        }
        result.append(']');
        return result.toString();
    }

    /**
     * It gets the support of the pattern referred by the Trie.
     *
     * @return the support
     */
    public int getSupport() {
        if (this.support < 0) {
            this.support = appearingIn.cardinality();
        }
        return this.support;
    }

    /**
     * It updates the support of the pattern referred by the Trie
     *
     * @param support the support
     */
    public void setSupport(int support) {
        this.support = support;
    }

    /**
     * It gets the sum of the sequence identifiers of the sequences where
     * the pattern, referred by the Trie, appears
     *
     * @return the sum of sequence identifiers
     */
    public int getSumIdSequences() {
        if (sumSequencesIDs < 0) {
            sumSequencesIDs = calculateSumIdSequences();
        }
        return sumSequencesIDs;
    }

    /**
     * It updates the sum of the sequence identifiers of the sequences where
     * the pattern, referred by the Trie, appears
     *
     * @param sumIdSequences Value of the sum of sequence identifiers to update
     */
    public void setSumIdSequences(int sumIdSequences) {
        this.sumSequencesIDs = sumIdSequences;
    }

    /**
     * It calculates the sum of the sequence identifiers
     *
     * @return the sum of the sequence identifiers
     */
    private int calculateSumIdSequences() {
        int acum = 0;
        for (int i = appearingIn.nextSetBit(0); i >= 0; i = appearingIn.nextSetBit(i + 1)) {
            acum += i;
        }
        return acum;
    }

    /**
     * It makes a pre-order traversal from the Trie. The result is concatenate
     * to the prefix pattern given as parameter
     *
     * @param p Prefix pattern
     * @return the list of patterns
     */
    public List<Pattern> preorderTraversal(Pattern p) {
        List<Pattern> result = new LinkedList<Pattern>();
        //If there is any node
        if (nodes != null) {
            //For each child
            for (TrieNode node : nodes) {
                Trie child = node.getChild();

                /* 
                 * We concatenate the pair component of this child with the 
                 * previous prefix pattern, we set its appearances and we add it 
                 * as a element in the result list
                 */
                Pattern newPattern = PatternCreator.getInstance().concatenate(p, node.getPair());
                newPattern.setAppearingIn(child.getAppearingIn());
                result.add(newPattern);

                if (child != null) {
                    /* 
                     * If the child is not null we make a recursive call with the 
                     * new pattern
                     */
                    List<Pattern> patternsFromChild = child.preorderTraversal(newPattern);
                    if (patternsFromChild != null) {
                        //If we find some descendants, we add them to the result list
                        result.addAll(patternsFromChild);
                    }
                }
            }

            return result;
        } else {
            return null;
        }
    }

    /**
     * Method to display graphically the Trie by means of a TreeModel
     *
     * @param model TreeModel when we want to insert the Trie nodes
     * @param p     TreeNode for the TreeModel
     */
    public void display(DefaultTreeModel model, MutableTreeNode p) {

        if (nodes != null) {
            //For each node
            for (int i = 0; i < nodes.size(); i++) {
                TrieNode node = nodes.get(i);
                Trie child = node.getChild();

                //We create a new TreeNode composed of the pair and the list of appearances
                DefaultMutableTreeNode currentNode = new DefaultMutableTreeNode(node.getPair().toString() + child.appearingIn);
                //And we insert it in the TreeModel
                model.insertNodeInto(currentNode, p, i);
                //And we go on doing the same process with the child
                child.display(model, currentNode);
            }
        }
    }

    /**
     * Compare this trie to another by id
     *
     * @param t the other trie
     * @return 0 if the id is the same, -1 if the id of this trie is smaller, otherwise 1.
     */
    @Override
    public int compareTo(Trie t) {
        return (new Integer(this.id)).compareTo(t.id);
    }

    /**
     * It adds a new node to the list of nodes associated with the Trie
     *
     * @param node
     */
    public void addNode(TrieNode node) {
        if (nodes == null) {
            nodes = new ArrayList<TrieNode>();
        }
        nodes.add(node);
    }
}