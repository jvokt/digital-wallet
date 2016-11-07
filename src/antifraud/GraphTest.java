package antifraud;

import static org.junit.Assert.*;

import java.util.HashSet;

import org.junit.Test;

public class GraphTest {
	@Test
	public void addEdgeTest() {
		Graph graph = new Graph();
		String id1 = "1";
		String id2 = "2";
		graph.addEdge(id1, id2);
		assertTrue(graph.adjacencyList.containsKey(id1));
		assertTrue(graph.adjacencyList.get(id1).contains(id2));
		assertTrue(graph.adjacencyList.containsKey(id2));
		assertTrue(graph.adjacencyList.get(id2).contains(id1));
	}
	@Test
	public void addDirectedEdgeTest() {
		Graph graph = new Graph();
		String id1 = "1";
		String id2 = "2";
		graph.addDirectedEdge(id1, id2);
		assertTrue(graph.adjacencyList.containsKey(id1));
		assertTrue(graph.adjacencyList.get(id1).contains(id2));
		assertFalse(graph.adjacencyList.containsKey(id2));
	}
	@Test
	public void getFeature1OutputTest() {
		Graph graph = new Graph();
		String id1 = "1";
		String id2 = "2";
		String id3 = "3";
		String id4 = "4";
		graph.addEdge(id1, id2);
		graph.processNeighbors();
		assertEquals(graph.getFeature1Output(id1, id2), "trusted");
		assertEquals(graph.getFeature1Output(id2, id1), "trusted");
		assertEquals(graph.getFeature1Output(id1, id3), "unverified");
		assertEquals(graph.getFeature1Output(id3, id4), "unverified");
	}
	@Test
	public void getFeature2OutputTest() {
		Graph graph = new Graph();
		String id1 = "1";
		String id2 = "2";
		String id3 = "3";
		String id4 = "4";
		graph.addEdge(id1, id2);
		graph.addEdge(id2, id3);
		graph.processNeighbors();
		assertEquals(graph.getFeature2Output(id1, id2), "trusted");
		assertEquals(graph.getFeature2Output(id2, id1), "trusted");
		assertEquals(graph.getFeature2Output(id1, id3), "trusted");
		assertEquals(graph.getFeature2Output(id3, id4), "unverified");
	}
	@Test
	public void getFeature3OutputTest() {
		Graph graph = new Graph();
		String id1 = "1";
		String id2 = "2";
		String id3 = "3";
		String id4 = "4";
		String id5 = "5";
		String id6 = "6";
		graph.addEdge(id1, id2);
		graph.addEdge(id2, id3);
		graph.addEdge(id3, id4);
		graph.addEdge(id4, id5);
		graph.addEdge(id5, id6);
		graph.processNeighbors();
		assertEquals(graph.getFeature3Output(id1, id2), "trusted");
		assertEquals(graph.getFeature3Output(id2, id1), "trusted");
		assertEquals(graph.getFeature3Output(id1, id3), "trusted");
		assertEquals(graph.getFeature3Output(id1, id4), "trusted");
		assertEquals(graph.getFeature3Output(id1, id5), "trusted");
		assertEquals(graph.getFeature3Output(id1, id6), "unverified");
	}
	@Test
	public void processNeighborsTest() {
		Graph graph = new Graph();
		String id1 = "1";
		String id2 = "2";
		String id3 = "3";
		String id4 = "4";
		String id5 = "5";
		String id6 = "6";
		graph.addEdge(id1, id2);
		graph.addEdge(id2, id3);
		graph.addEdge(id3, id4);
		graph.addEdge(id4, id5);
		graph.addEdge(id5, id6);
		graph.processNeighbors();
		
		HashSet<String> id1adjacencyList = new HashSet<String>();
		id1adjacencyList.add("2");
		assertEquals(graph.adjacencyList.get(id1), id1adjacencyList);
		
		HashSet<String> id1degree2Neighbors = new HashSet<String>();
		id1degree2Neighbors.add("3");
		BloomFilter<String> bloom = graph.degree2Neighbors.get(id1);
		for (String id : id1degree2Neighbors)
			assertTrue(bloom.contains(id));
	}
	@Test
	public void updateNeighborsTest() {
		Graph graph = new Graph();
		String id1 = "1";
		String id2 = "2";
		String id3 = "3";
		String id4 = "4";
		String id5 = "5";
		String id6 = "6";
		String id7 = "7";
		String id8 = "8";
		String id9 = "9";
		String id10 = "10";
		graph.addEdge(id1, id2);
		graph.addEdge(id2, id3);
		graph.addEdge(id3, id4);
		graph.addEdge(id4, id5);
		graph.addEdge(id5, id6);
		graph.addEdge(id6, id7);
		graph.addEdge(id7, id8);
		graph.addEdge(id8, id9);
		graph.addEdge(id9, id10);
		graph.processNeighbors();
		graph.addEdge(id1, id10);
		graph.updateNeighbors(id1, id10);

		HashSet<String> id1adjacencyList = new HashSet<String>();
		id1adjacencyList.add("2");
		id1adjacencyList.add("10");
		assertEquals(graph.adjacencyList.get(id1), id1adjacencyList);
		
		HashSet<String> id1degree2Neighbors = new HashSet<String>();
		id1degree2Neighbors.add("3");
		id1degree2Neighbors.add("9");
		BloomFilter<String> bloom = graph.degree2Neighbors.get(id1);
		for (String id : id1degree2Neighbors)
			assertTrue(bloom.contains(id));
	}
}
