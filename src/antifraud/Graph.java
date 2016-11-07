package antifraud;

import java.util.HashMap;
import java.util.HashSet;

/**
 * The Graph object represents the current state given
 * all transactions seen so far.
 */
public class Graph {
	HashMap<String,HashSet<String>> adjacencyList;
	public Graph() {
		adjacencyList = new HashMap<>();
	}

	/**
	 * Adds an undirected edge to the graph data structure by
	 * adding two directed edges.
	 * @param id1 the first node of the new edge
	 * @param id2 the second node of the new edge
	 */
	public void addEdge(String id1, String id2) {
		addDirectedEdge(id1, id2);
		addDirectedEdge(id2, id1);
	}
	
	/**
	 * Adds id2 to the neighbor set of id1. Creates a new
	 * neighbor set if necessary.
	 * @param id1
	 * @param id2
	 */
	public void addDirectedEdge(String id1, String id2) {
		HashSet<String> id1Neighbors;
		if (adjacencyList.containsKey(id1))
			id1Neighbors = adjacencyList.get(id1);
		else
			id1Neighbors = new HashSet<>();
		id1Neighbors.add(id2);
		adjacencyList.put(id1, id1Neighbors);
	}

	/**
	 * Returns "trusted" if id2 is in the neighbor set of id1.
	 * @param id1
	 * @param id2
	 * @return "trusted" or "unverified"
	 */
	public String getFeature1Output(String id1, String id2) {
		boolean trusted = adjacencyList.containsKey(id1) && adjacencyList.get(id1).contains(id2);
		return trusted ? "trusted" : "unverified";
	}

	/**
	 * Returns "trusted" if id2 is in (1) the neighbor set of id1, or (2) the
	 * neighbor set of any member of the neighbor set of id1.
	 * @param id1
	 * @param id2
	 * @return "trusted" or "unverified"
	 */
	public String getFeature2Output(String id1, String id2) {
		if (!(adjacencyList.containsKey(id1) && adjacencyList.containsKey(id2)))
			return "unverified";
		HashSet<String> id1Neighbors = adjacencyList.get(id1);
		if (id1Neighbors.contains(id2))
			return "trusted";
		for (String id1Neighbor : id1Neighbors) {
			if (adjacencyList.get(id1Neighbor).contains(id2))
				return "trusted";
		}
		return "unverified";
	}

	/**
	 * Returns "trusted" if id2 is (1) id1's neighbor, (2) id1's neighbor's
	 * neighbor, (3) id1's neighbor's neighbor's neighbor, (4) id1's
	 * neighbor's neighbor's neighbor's neighbor.
	 * @param id1
	 * @param id2
	 * @return "trusted" or "unverified"
	 */
	public String getFeature3Output(String id1, String id2) {
		if (!(adjacencyList.containsKey(id1) && adjacencyList.containsKey(id2)))
			return "unverified";
		HashSet<String> id1Neighbors = adjacencyList.get(id1);
		if (id1Neighbors.contains(id2))
			return "trusted";
		for (String id1Neighbor : id1Neighbors) {
			HashSet<String> id1Neighbors2 = adjacencyList.get(id1Neighbor);
			if (id1Neighbors2.contains(id2))
				return "trusted";
			for (String id1Neighbor2 : id1Neighbors2) {
				HashSet<String> id1Neighbors3 = adjacencyList.get(id1Neighbor2);
				if (id1Neighbors3.contains(id2))
					return "trusted";
				for (String id1Neighbor3 : id1Neighbors3) {
					HashSet<String> id1Neighbors4 = adjacencyList.get(id1Neighbor3);
					if (id1Neighbors4.contains(id2))
						return "trusted";
				}
			}
		}
		return "unverified";
	}
}