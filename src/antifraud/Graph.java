package antifraud;

import java.util.HashMap;
import java.util.HashSet;

/**
 * The Graph object represents the current state given
 * all transactions seen so far.
 */
public class Graph {
	HashMap<String,HashSet<String>> adjacencyList;
	HashMap<String,BloomFilter<String>> degree2Neighbors;
	// Used when creating the bloom filter. Influences the error
	// percentage for features 2 and 3.
	double falsePositiveProbability = .1;
	// The number of undirected edges in the graph
	int E; 
	
	public Graph() {
		adjacencyList = new HashMap<>();
		degree2Neighbors = new HashMap<>();
		E = 0;
	}
	
	/**
	 * Returns the number of edges that have been added to the graph
	 * @return the number of edges
	 */
	public int getNumEdges() {
		return E;
	}
	
	/**
	 * Returns the number of nodes in the graph by getting the number of
	 * unique keys in the adjacency list hash map
	 * @return
	 */
	public int getNumNodes() {
		return adjacencyList.size();
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
		E++;
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
		boolean degree1 = adjacencyList.containsKey(id1) && adjacencyList.get(id1).contains(id2);
		boolean degree2 = degree2Neighbors.containsKey(id1) && degree2Neighbors.get(id1).contains(id2);
		boolean trusted = degree1 || degree2;
		return trusted ? "trusted" : "unverified";
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
		BloomFilter<String> id1Neighbors2Bloom = degree2Neighbors.get(id1);
		if (id1Neighbors.contains(id2) || id1Neighbors2Bloom.contains(id2))
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

	/**
	 * Finds extended neighbors of each vertex and stores the results.
	 */
	public void processNeighbors() {
		int maxDegree = 0;
		for (String id : adjacencyList.keySet()) {
			maxDegree = Math.max(maxDegree, adjacencyList.get(id).size());
		}
		for (String id : adjacencyList.keySet()) {
			HashSet<String> idAdj = adjacencyList.get(id);
			int bloomFilterExpectedNumber = bloomFilterNum(idAdj.size());
			BloomFilter<String> idNeighbors2 = new BloomFilter<String>(falsePositiveProbability, bloomFilterExpectedNumber);
			for (String idNeighbor : idAdj) {
				for (String idNeighbor2 : adjacencyList.get(idNeighbor)) {
					if (!id.equals(idNeighbor2) && !idAdj.contains(idNeighbor2)) {
						idNeighbors2.add(idNeighbor2);
					}
				}
			}
			degree2Neighbors.put(id, idNeighbors2);
		}
	}
	
	/**
	 * Updates the extended neighbors for id1 and id2
	 * @param id1 the first node of the new edge
	 * @param id2 the second node of the new edge
	 */
	public void updateNeighbors(String id1, String id2) {
		updateNeighborsDirected(id1, id2);
		updateNeighborsDirected(id2, id1);
	}
	
	/**
	 * When a new edge is added, we have to update the extended neighbors of each vertex.
	 * @param id1
	 * @param id2
	 */
	private void updateNeighborsDirected(String id1, String id2) {
		// Update degree2Neighbors
		// Add all the degree 1 neighbors from id2 into id1's degree 2 neighbors
		HashSet<String> id1Neighbors = adjacencyList.get(id1);
		BloomFilter<String> id1Neighbors2;
		if (degree2Neighbors.containsKey(id1))
			id1Neighbors2 = degree2Neighbors.get(id1);
		else {
			int bloomFilterExpectedNumber = bloomFilterNum(id1Neighbors.size());
			id1Neighbors2 = new BloomFilter<String>(falsePositiveProbability, bloomFilterExpectedNumber);
		}
		for (String id2Neighbor : adjacencyList.get(id2)) {
			if (!id1.equals(id2Neighbor) && !id1Neighbors.contains(id2Neighbor)) {
				id1Neighbors2.add(id2Neighbor);
			}
		}
		degree2Neighbors.put(id1, id1Neighbors2);
		// Add id2 to the degree 2 neighbors of the degree 1 neighbors of id1
		for (String id1Neighbor : adjacencyList.get(id1)) {
			BloomFilter<String> id1NeighborNeighbors2;
			if (degree2Neighbors.containsKey(id1Neighbor))
				id1NeighborNeighbors2 = degree2Neighbors.get(id1Neighbor);
			else {
				int bloomFilterExpectedNumber = bloomFilterNum(adjacencyList.get(id1Neighbor).size());
				id1NeighborNeighbors2 = new BloomFilter<String>(falsePositiveProbability, bloomFilterExpectedNumber);
			}
			if (!id1Neighbor.equals(id2))
				id1NeighborNeighbors2.add(id2);
			degree2Neighbors.put(id1Neighbor, id1NeighborNeighbors2);
		}
	}

	/**
	 * Calculates the estimated number of nodes that will be inserted into
	 * the bloom filter. Takes the degree of the current node and
	 * multiplies it by the average degree among all nodes in the graph.
	 * @param degree
	 * @return estimated number of bloom filter elements
	 */
	private int bloomFilterNum(int degree) {
		int averageDegree = 2 * getNumEdges() / getNumNodes();
		return degree * averageDegree;
	}

}