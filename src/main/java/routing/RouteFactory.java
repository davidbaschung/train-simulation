/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

//Version: 27/3/13

package routing;

import apps.Main;
import railway.*;
import sx.SX;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * This class is used for computing the best path between two
 * blocks. It uses Chandy-Misra's algorithm, or at least something
 * that is close to this algorithm. I choosed it because it is
 * easy to implement, easy to understand, and more importantly, it
 * can use well the informations of the railway to give weights to the
 * edges.
 * I have not found a good presentation of this algorithm, but it was
 * used in the 4th semester project and professor Hirsbrunner
 * explained it to us. I made it from what I remembered.
 *
 * @author ms
 */
public class RouteFactory implements Runnable {
	/**
	 * block to node map, to avoid to iterate through the list
	 */
	private static final HashMap<Block, Node> blockToNode = new HashMap<>();
	private static RouteFactory instance = null;
	/**
	 * A little safety to prevent several threads to access to the
	 * requests list at the same time. This cannot be done with the
	 * synchronized keyword because then two threads could access
	 * it (a train making a request while run() is running).
	 */
	private final Semaphore listLock = new Semaphore(1);
	/**
	 * FIFO list of route requests. If a route for a request cannot be
	 * found, it'll be put back at the end of the queue.
	 */
	private final Queue<RouteRequest> requests = new LinkedList<>();
	/**
	 * List of nodes
	 */
	private List<Node> nodes = null;


	/**
	 * Constructor
	 */
	private RouteFactory() {
		int nbNodes = Railway.instance().getBlocks().size();
		nodes = new ArrayList<>(nbNodes);
		listLock.release();
		(new Thread(this)).start();
	}

	/**
	 * Singleton pattern.
	 *
	 * @return the instance
	 */
	public static RouteFactory instance() {
		if (instance == null)
			instance = new RouteFactory();
		return instance;
	}

	/**
	 * Adds a node to the list
	 *
	 * @param block
	 */
	public void addNode(Block block) {
		Node n = new Node(block);
		nodes.add(n);
		blockToNode.put(block, n);
	}

	/**
	 * @return the list of nodes
	 */
	protected List<Node> getNodes() {
		return nodes;
	}

	/**
	 * @param id of the signal
	 * @return all nodes which block ends at a given signal id
	 */
	List<Node> getNodesByEndId(String id) {
		List<Node> result = new LinkedList<>();
		ArrayList<Block> blocks = Railway.instance().getBlocksByEndId(id);
		for (Block b : blocks) {
			result.add(blockToNode.get(b));
		}
		return result;
	}

	/**
	 * @param id of the signal
	 * @return all nodes which block starts at a given signal
	 */
	protected List<Node> getNodesByStartId(String id) {
		List<Node> result = new LinkedList<>();
		ArrayList<Block> blocks = Railway.instance().getBlocksByStartId(id);
		for (Block b : blocks) {
			result.add(blockToNode.get(b));
		}
		return result;
	}

	/**
	 * @param id of the signal
	 * @return all nodes which yblock has a stop at the given signal
	 */
	List<Node> getNodesByStopId(String id) {
		List<Node> result = new LinkedList<>();
		ArrayList<Block> blocks = Railway.instance().getBlocksByStopId(id);
		for (Block b : blocks) {
			result.add(blockToNode.get(b));
		}
		return result;
	}

	/**
	 * Connects the graph, that means each node will have to be
	 * told which are its nextBlocks.
	 */
	public void connectGraph() {
		int nbLinks = 0;
		for (Node node : nodes) {
			for (Block block : node.getBlock().getNextBlocks()) {
				node.addNextNode(blockToNode.get(block));
				nbLinks++;
			}
		}
		Main.getRailwayLogger("RouteFactory").info(nodes.size() + " vertices and " + nbLinks + " edges in the graph");
	}

	/**
	 * Requests a route for the train to any (the closest) signal of the
	 * station.
	 *
	 * @param train
	 * @param station
	 */
	public void requestRoute(Train train, Station station) {
		List<String> to = new LinkedList<>();
		to.addAll(station.getSignals());
		requestRoute(train, to);
	}

	/**
	 * Requests a route for a train to a specified signal
	 *
	 * @param train : reference to the train requesting the route
	 * @param to    : string ID of the destination signal
	 */
	public void requestRoute(Train train, String to) {
		List<String> newTo = new LinkedList<>();
		newTo.add(to);
		requestRoute(train, newTo);
	}

	/**
	 * Requests a route for a train to any (the closest) signal of
	 * a list.
	 *
	 * @param train : reference to the train requesting the route
	 * @param to    : list of string IDs of signals
	 */
	private void requestRoute(Train train, List<String> to) {
		RouteRequest request = new RouteRequest(train, to);
		Main.getRailwayLogger("RouteFactory").info("Train " + train + " requested a route from " + train.getSignalId() + " to " + to);
		try {
			listLock.acquire();
		} catch (InterruptedException e) {
			Main.getRailwayLogger("RouteFactory").warning("requestRoute: unable to acquire semaphore:\n" + e);
			return;
		}
		requests.add(request);
		listLock.release();
	}

	/**
	 * This method looks if any route request can be answered to. It first
	 * checks if there are route requests, then tries to find a path, and
	 * if one is found, it's assigned to the train.
	 */
	@Override
	public void run() {
		Main.getRailwayLogger("RouteFactory").finest("RouteFactory initialized.");
		while (true) {
			RouteRequest request = null;
			/*
			 * If there is a request, it'll be put in the
			 * request variable
			 */
			try {
				listLock.acquire();
			} catch (InterruptedException e) {
				Main.getRailwayLogger("RouteFactory").warning("Graph: unable to acquire listLock");
				continue;
			}
			if (!requests.isEmpty()) {
				request = requests.remove();
			}
			listLock.release();

			/*
			 * There is a request
			 */
			if (request != null) {

				/*
				 * If already there (now managed in OnRouteSecured)
				 */

				if (request.getToIds().contains(request.getTrain().getSignalId())) {
					ArrayList<Block> blocklist = new ArrayList<>();
					blocklist.add(Railway.instance().getBlocksByEndId(request.getTrain().getSignalId()).get(0));

					Route route = new Route(blocklist);
					request.getTrain().setRoute(route);
					Main.getRailwayLogger("RouteFactory").info(request.getTrain() + ":" + route);

					continue;

				}





				/*
				 * Reset nodes
				 */
				for (Node node : nodes) {
					node.reset();
				}

				/*
				 * Set start nodes
				 */

				for (Node node : request.getFrom()) {
					//Secure tail if first block is a yblock.

					if (node.getBlock().containsStop()) {
						if (!((YBlock) node.getBlock()).isTailSecurable()) {
							continue;
						}
					} else {

						if (!node.getBlock().isSecurable()) {
							// Do nothing. First block does not need to be securable.
						}
					}

					node.setStart();
				}



				/*
				 * Send messages
				 */
				for (Node node : request.getFrom()) {
					node.emit(request.getTrain().getLength());
				}

				/*
				 * Looking for the best destination node
				 */
				float bestDistance = Float.POSITIVE_INFINITY;
				Node bestNode = null;
				for (Node node : request.getTo()) {
					/*
					 * We'd prefer not a YBlock
					 */
					//if (node.getBlock().containsStop()) {
					//    node.setDistance(/*5.0f* */node.getDistance());
					// }

					/*
					 * Now, let's see if it's good
					 */
					if (node.getDistance() < bestDistance) {
						bestNode = node;
						bestDistance = node.getDistance();
					}
				}

				/*
				 * if no route has been found...
				 */
				if (bestNode == null) {
					try {
						listLock.acquire();
					} catch (InterruptedException e) {
						Main.getRailwayLogger("RouteFactory").severe("Graph: unable to acquire listLock. "
								+ "The program must be restarted.");
						continue;
					}
					requests.add(request);
					listLock.release();
				} else {
					/*
					 * A route was found, it must be built
					 */
					Stack<Block> stack = new Stack<>();
					Node currentNode = bestNode;
					while (true) {
						stack.add(currentNode.getBlock());

						//reached first block
						if (currentNode.getPreviousNode() == null) {
							//lock some sectors if first block is a yblock
							if (currentNode.getBlock().containsStop()) {
								YBlock yb = (YBlock) currentNode.getBlock();
								if (yb.isOccupied()) {
									yb.lockEndSectors();
								} else {
									yb.lockEndSectors();
									yb.lockSectors();
								}
							}
							break;
						} else {
							currentNode.getBlock().lockSectors();
						}
						currentNode = currentNode.getPreviousNode();
					}
					/*
					 * Must a dummy block be added ?
					 */

					if (!stack.peek().isOccupied()) {
						// no block stopping there can be locked because it's occupied
						if (!SX.instance().isEmulating()) {
							stack.add(Railway.instance().getBlocksByEndId(request.getTrain().getSignalId()).get(0));
							Main.getRailwayLogger("RouteFactory").severe("First block not occupied. Adding dummy block.");
						}
					}


					/*
					 * Remove from stack, add to list
					 */
					ArrayList<Block> list = new ArrayList<>();
					while (!stack.isEmpty()) {
						list.add(stack.pop());
					}
					Route route = new Route(list);
					request.getTrain().setRoute(route);
					Main.getRailwayLogger("RouteFactory").info("Train " + request.getTrain() + " received the route " + route);
				}
			}

			/*
			 * Let's sleep a bit
			 */
			try {
				if (request == null) {
					Thread.sleep(2000);
				} else {
					Thread.sleep(2500);
				}
			} catch (InterruptedException ignored) {
			}
		}
	}
}
