package routing;

import railway.Train;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is used by the route factory to store route requests
 *
 * @author ms
 */
class RouteRequest {
	/**
	 * The train which asked for a route
	 */
	private final Train train;

	/**
	 * The list of accepted destinations
	 */
	private final List<Node> to = new LinkedList<>();

	/**
	 * The list of possible starting nodes
	 */
	private final List<Node> from = new LinkedList<>();

	/**
	 * Constructor : takes a train and a list of possible destinations as
	 * parameters.
	 *
	 * @param train
	 * @param to
	 */
	RouteRequest(Train train, List<String> to) {
		this.train = train;
		/*
		 * add destination nodes
		 */
		for (String id : to) {
			this.to.addAll(RouteFactory.instance().getNodesByEndId(id));
		}

		/*
		 * Add starting nodes. It is possible to start at the stop signal
		 * of a YBlock.
		 */
		this.from.addAll(RouteFactory.instance().getNodesByEndId(train.getSignalId()));
		//this.from.addAll(RouteFactory.instance().getNodesByStartId(train.getSignalId()));
		this.from.addAll(RouteFactory.instance().getNodesByStopId(train.getSignalId()));
	}

	/**
	 * @return the train requesting the route
	 */
	Train getTrain() {
		return train;
	}

	/**
	 * @return the possible start nodes
	 */
	List<Node> getFrom() {
		return from;
	}

	/**
	 * @return the possible destination nodes
	 */
	List<Node> getTo() {
		return to;
	}

	public List<String> getToIds() {
		List<String> result = new LinkedList<>();
		for (Node n : to) {
			result.add(n.getBlock().getEndId());
		}
		return result;
	}

	/**
	 * returns the class in a printable form
	 */
	@Override
	public String toString() {
		return "Request[ Train=" + train + ", from=" + train.getSignalId() + " to=" + to + "]";
	}

}
