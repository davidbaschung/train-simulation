/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package routing;

import railway.Block;
import railway.YBlock;

import java.util.LinkedList;
import java.util.List;

/**
 * This is a node of the graph.
 *
 * @author ms
 */
class Node {
	/**
	 * Block represented by the node
	 */
	private final Block block;

	/**
	 * List of nodes which can be reached from this node
	 */
	private final List<Node> nextNodes = new LinkedList<>();

	/**
	 * Node which has sent the smallest distance while the algorithm is
	 * running.
	 */
	private Node previousNode = null;

	/**
	 * Smallest distance received while the algorithm is running.
	 */
	private float distance = Float.POSITIVE_INFINITY;

	/**
	 * Is set to true before the algorithm runs if the block is securable,
	 * otherwise false. That is used for avoiding to check many times
	 * if the block is securable.
	 */
	private boolean securable;

	/**
	 * Constructor of the node.
	 *
	 * @param block
	 */
	public Node(Block block) {
		this.block = block;
	}

	/**
	 * Resets the data to make the node ready for running the
	 * algorithm again
	 */
	public void reset() {
		previousNode = null;
		setDistance(Float.POSITIVE_INFINITY);
		securable = block.isSecurable();
	}

	/**
	 * Adds a node to the next nodes.
	 *
	 * @param node
	 */
	public void addNextNode(Node node) {
		if (nextNodes.contains(node)) return;
		nextNodes.add(node);
	}

	/**
	 * @return the block which the node represents
	 */
	public Block getBlock() {
		return block;
	}

	/**
	 * @return the distance to the previous node
	 */
	public float getDistance() {
		return distance;
	}

	/**
	 * @param distance the distance to set
	 */
	private void setDistance(float distance) {
		this.distance = distance;
	}

	/**
	 * Sets the node as the start position for the path finding
	 * algorithm.
	 */
	public void setStart() {
		setDistance(0.0f);
		//System.err.println(block+" is start point");
	}

	/**
	 * @return the previous node
	 */
	public Node getPreviousNode() {
		return previousNode;
	}

	/**
	 * Sends distance to all next nodes
	 */
	public void emit(int minLength) {
		for (Node next : nextNodes) {
			next.receiveDistance(this, getDistance() + next.block.getCrossingTime(), minLength);
		}
	}

	private void receiveDistance(Node from, float distance, int minLength) {
		if (distance < this.getDistance() && securable) {
			/*
			 * In case of YBlock, one has to be sure that the switchless
			 * length is long enough for the train
			 */
			if (block.containsStop()) {
				YBlock yb = (YBlock) block;
				if (yb.getSwitchlessLength() < minLength) {
					return;
				}
			}
			//Logger.log(block+" at "+distance+" from "+from.getBlock());
			this.setDistance(distance);
			this.previousNode = from;
			emit(minLength);
		}
	}

	@Override
	public String toString() {
		return getBlock().toString();
	}
}
