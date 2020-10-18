package network;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

/**
 * Unit tests for {@link graph}.
 * @author Sudesh Agrawal (sudesh@utexas.edu).
 * Last Updated: October 18, 2020.
 */
class graphTest
{
	/**
	 * Test for {@link graph#buildGraphFromFile(String, String)}.
	 *
	 * @throws Exception thrown if {@link graph#buildGraphFromFile(String, String)} throws an exception.
	 */
	@Test
	void buildGraphFromFile() throws Exception
	{
		graph network = new graph("testNetwork");
		String separator = ",";
		network.buildGraphFromFile("./test/resources/networks/testNetworkFile.txt", separator);
		
		// check vertex set
		assert network.getG().vertexSet().size()==7;
		for (int i=1; i<=7; i++)
			assert network.getG().containsVertex(i);
		
		// check edge set
		assert network.getG().edgeSet().size()==6;
		assert network.getG().containsEdge(1, 2);
		assert network.getG().containsEdge(2, 3);
		assert network.getG().containsEdge(3, 4);
		assert network.getG().containsEdge(4, 5);
		assert network.getG().containsEdge(5, 6);
		assert network.getG().containsEdge(6, 7);
	}
	
	/**
	 * Test for {@link graph#initializeAsCompleteGraph(int, int)}.
	 *
	 * @throws Exception thrown if {@link graph#initializeAsCompleteGraph(int, int)} throws an exception.
	 */
	@Test
	void initializeAsCompleteGraph() throws Exception
	{
		// starting node 1
		int size = 4;
		graph network = new graph("CompleteGraph_size"+size);
		network.initializeAsCompleteGraph(size, 1);
		for (int i=1; i<=size; i++)
		{
			for (int j=1; j <=size; j++)
			{
				if (i==j)
					assert !network.getG().containsEdge(i, j);
				else
					assert network.getG().containsEdge(i, j);
			}
		}
		
		// starting node 0
		network = new graph("CompleteGraph_size"+size);
		network.initializeAsCompleteGraph(size, 0);
		for (int i=0; i<size; i++)
		{
			for (int j=0; j <size; j++)
			{
				if (i==j)
					assert !network.getG().containsEdge(i, j);
				else
					assert network.getG().containsEdge(i, j);
			}
		}
		
		// starting node 3
		network = new graph("CompleteGraph_size"+size);
		network.initializeAsCompleteGraph(size, 3);
		for (int i=3; i<(size+3); i++)
		{
			for (int j=3; j <(size+3); j++)
			{
				if (i==j)
					assert !network.getG().containsEdge(i, j);
				else
					assert network.getG().containsEdge(i, j);
			}
		}
		
		// invalid size
		network = new graph("CompleteGraph_size"+size);
		graph finalNetwork = network;
		Exception exception = assertThrows(Exception.class, () -> finalNetwork.initializeAsCompleteGraph(-2,
				3));
		String expectedMessage = "Size cannot be negative!";
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
		
		// invalid starting node label
		network = new graph("CompleteGraph_size"+size);
		graph finalNetwork1 = network;
		exception = assertThrows(Exception.class, () -> finalNetwork1.initializeAsCompleteGraph(size,
				-3));
		expectedMessage = "Node labels should be non-negative integers!";
		actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
		
		network = new graph("CompleteGraph_size"+size);
		graph finalNetwork2 = network;
		exception = assertThrows(Exception.class, () -> finalNetwork2.initializeAsCompleteGraph(size,
				10));
		expectedMessage = "'startingNodeLabel<size' should hold!";
		actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
		
		// graph not empty
		network = new graph("CompleteGraph_size"+size);
		network.addVertex(2);
		graph finalNetwork3 = network;
		exception = assertThrows(Exception.class, () -> finalNetwork3.initializeAsCompleteGraph(size,
				3));
		expectedMessage = "Graph is not empty!";
		actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}
	
	/**
	 * Test for {@link graph#initializeAsCirculantGraph(int, int[], int)}.
	 *
	 * @throws Exception thrown if {@link graph#initializeAsCirculantGraph(int, int[], int)} throws an exception.
	 */
	@Test
	void initializeAsCirculantGraph() throws Exception
	{
		// starting node 1
		int size = 7;
		int[] offsets = {2, 4};
		graph network = new graph("CirculantGraph_size"+size+"_offsets_"+ Arrays.toString(offsets));
		network.initializeAsCirculantGraph(size, offsets, 1);
		assert network.getG().containsEdge(1, 6);
		assert network.getG().containsEdge(1, 3);
		assert network.getG().containsEdge(1, 4);
		assert network.getG().containsEdge(1, 5);
		assert !network.getG().containsEdge(1, 1);
		assert !network.getG().containsEdge(1, 2);
		assert !network.getG().containsEdge(1, 7);
		
		assert network.getG().containsEdge(2, 7);
		assert network.getG().containsEdge(2, 4);
		assert network.getG().containsEdge(2, 5);
		assert network.getG().containsEdge(2, 6);
		assert !network.getG().containsEdge(2, 2);
		assert !network.getG().containsEdge(2, 3);
		
		assert network.getG().containsEdge(3, 5);
		assert network.getG().containsEdge(3, 6);
		assert network.getG().containsEdge(3, 7);
		assert !network.getG().containsEdge(3, 3);
		assert !network.getG().containsEdge(3, 4);
		
		assert network.getG().containsEdge(4, 6);
		assert network.getG().containsEdge(4, 7);
		assert !network.getG().containsEdge(4, 4);
		assert !network.getG().containsEdge(4, 5);
		
		assert network.getG().containsEdge(5, 7);
		assert !network.getG().containsEdge(5, 5);
		assert !network.getG().containsEdge(5, 6);
		
		assert !network.getG().containsEdge(6, 6);
		assert !network.getG().containsEdge(6, 7);
		
		assert !network.getG().containsEdge(7, 7);
		
		// starting node 0
		network = new graph("CirculantGraph_size"+size+"_offsets_"+ Arrays.toString(offsets));
		network.initializeAsCirculantGraph(size, offsets, 0);
		assert network.getG().containsEdge(0, 5);
		assert network.getG().containsEdge(0, 2);
		assert network.getG().containsEdge(0, 3);
		assert network.getG().containsEdge(0, 4);
		assert !network.getG().containsEdge(0, 0);
		assert !network.getG().containsEdge(0, 1);
		assert !network.getG().containsEdge(0, 6);
		
		assert network.getG().containsEdge(1, 6);
		assert network.getG().containsEdge(1, 3);
		assert network.getG().containsEdge(1, 4);
		assert network.getG().containsEdge(1, 5);
		assert !network.getG().containsEdge(1, 1);
		assert !network.getG().containsEdge(1, 2);
		
		assert network.getG().containsEdge(2, 4);
		assert network.getG().containsEdge(2, 5);
		assert network.getG().containsEdge(2, 6);
		assert !network.getG().containsEdge(2, 2);
		assert !network.getG().containsEdge(2, 3);
		
		assert network.getG().containsEdge(3, 5);
		assert network.getG().containsEdge(3, 6);
		assert !network.getG().containsEdge(3, 3);
		assert !network.getG().containsEdge(3, 4);
		
		assert network.getG().containsEdge(4, 6);
		assert !network.getG().containsEdge(4, 4);
		assert !network.getG().containsEdge(4, 5);
		
		assert !network.getG().containsEdge(5, 5);
		assert !network.getG().containsEdge(5, 6);
		
		assert !network.getG().containsEdge(6, 6);
		
		
		// starting node 3
		network = new graph("CirculantGraph_size"+size+"_offsets_"+ Arrays.toString(offsets));
		network.initializeAsCirculantGraph(size, offsets, 3);
		assert network.getG().containsEdge(3, 8);
		assert network.getG().containsEdge(3, 5);
		assert network.getG().containsEdge(3, 6);
		assert network.getG().containsEdge(3, 7);
		assert !network.getG().containsEdge(3, 3);
		assert !network.getG().containsEdge(3, 4);
		assert !network.getG().containsEdge(3, 9);
		
		assert network.getG().containsEdge(4, 9);
		assert network.getG().containsEdge(4, 6);
		assert network.getG().containsEdge(4, 7);
		assert network.getG().containsEdge(4, 8);
		assert !network.getG().containsEdge(4, 4);
		assert !network.getG().containsEdge(4, 5);
		
		assert network.getG().containsEdge(5, 7);
		assert network.getG().containsEdge(5, 8);
		assert network.getG().containsEdge(5, 9);
		assert !network.getG().containsEdge(5, 5);
		assert !network.getG().containsEdge(5, 6);
		
		assert network.getG().containsEdge(6, 8);
		assert network.getG().containsEdge(6, 9);
		assert !network.getG().containsEdge(6, 6);
		assert !network.getG().containsEdge(6, 7);
		
		assert network.getG().containsEdge(7, 9);
		assert !network.getG().containsEdge(7, 7);
		assert !network.getG().containsEdge(7, 8);
		
		assert !network.getG().containsEdge(8, 8);
		assert !network.getG().containsEdge(8, 9);
		
		assert !network.getG().containsEdge(9, 9);
		
		
		// invalid size
		network = new graph("CirculantGraph_size"+size+"_offsets_"+ Arrays.toString(offsets));
		graph finalNetwork = network;
		Exception exception = assertThrows(Exception.class, () -> finalNetwork.initializeAsCirculantGraph(-2,
				offsets, 5));
		String expectedMessage = "Size cannot be negative!";
		String actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
		
		// invalid starting node label
		network = new graph("CirculantGraph_size"+size+"_offsets_"+ Arrays.toString(offsets));
		graph finalNetwork1 = network;
		exception = assertThrows(Exception.class, () -> finalNetwork1.initializeAsCirculantGraph(size,
				offsets, -3));
		expectedMessage = "Node labels should be non-negative integers!";
		actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
		
		network = new graph("CirculantGraph_size"+size+"_offsets_"+ Arrays.toString(offsets));
		graph finalNetwork2 = network;
		exception = assertThrows(Exception.class, () -> finalNetwork2.initializeAsCirculantGraph(size,
				offsets, 10));
		expectedMessage = "'startingNodeLabel<size' should hold!";
		actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
		
		// graph not empty
		network = new graph("CirculantGraph_size"+size+"_offsets_"+ Arrays.toString(offsets));
		network.addVertex(2);
		graph finalNetwork3 = network;
		exception = assertThrows(Exception.class, () -> finalNetwork3.initializeAsCirculantGraph(size,
				offsets, 5));
		expectedMessage = "Graph is not empty!";
		actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
		
		// invalid offset values
		network = new graph("CirculantGraph_size"+size+"_offsets_"+ Arrays.toString(offsets));
		graph finalNetwork4 = network;
		int[] newOffsets1 = {2, -1, 3};
		exception = assertThrows(Exception.class, () -> finalNetwork4.initializeAsCirculantGraph(size,
				newOffsets1, 5));
		expectedMessage = "Offset values cannot be negative!";
		actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
		
		network = new graph("CirculantGraph_size"+size+"_offsets_"+ Arrays.toString(offsets));
		graph finalNetwork5 = network;
		int[] newOffsets2 = {2, 3, 10};
		exception = assertThrows(Exception.class, () -> finalNetwork5.initializeAsCirculantGraph(size,
				newOffsets2, 5));
		expectedMessage = "Offset values cannot be larger than size of the network!";
		actualMessage = exception.getMessage();
		assertEquals(expectedMessage, actualMessage);
	}
	
	@Test
	void getNetworkName() throws Exception
	{
		String networkName = "testNetwork";
		graph network = new graph(networkName);
		String separator = ",";
		network.buildGraphFromFile("./test/resources/networks/testNetworkFile.txt", separator);
		
		assert network.getNetworkName().equals(networkName);
	}
	
	@Test
	void setNetworkName() throws Exception
	{
		String networkName = "testNetwork";
		graph network = new graph(networkName);
		String separator = ",";
		network.buildGraphFromFile("./test/resources/networks/testNetworkFile.txt", separator);
		
		assert network.getNetworkName().equals(networkName);
		
		String newNetworkName = "newtestNetwork";
		network.setNetworkName(newNetworkName);
		assert network.getNetworkName().equals(newNetworkName);
	}
	
	@Test
	void addVertex() throws Exception
	{
		int size = 4;
		graph network = new graph("CompleteGraph_size"+size);
		network.initializeAsCompleteGraph(size, 1);
		
		assert network.getVertexSet().size()==4;
		network.addVertex(size+1);
		assert network.getVertexSet().size()==size+1;
		assert network.getVertexSet().contains(size+1);
	}
	
	@Test
	void addEdge() throws Exception
	{
		int size = 4;
		graph network = new graph("CompleteGraph_size"+size);
		network.initializeAsCompleteGraph(size, 1);
		
		assert network.getEdgeSet().size()==6;
		network.addVertex(size+1);
		network.addEdge(1, 5);
		assert network.getEdgeSet().size()==7;
		assert Graphs.neighborListOf(network.getG(), 1).contains(5);
	}
	
	/**
	 * Test for {@link graph#addEdges(Integer, List)}.
	 *
	 * @throws Exception thrown if {@link graph#initializeAsCompleteGraph(int, int)} throws an exception.
	 */
	@Test
	void addEdges() throws Exception
	{
		int size = 4;
		graph network = new graph("CompleteGraph_size"+size);
		network.initializeAsCompleteGraph(size, 1);
		List<Integer> nodes = new ArrayList<>(network.getVertexSet());
		network.addVertex(0);
		network.addEdges(0, nodes);
		
		assert Graphs.neighborListOf(network.getG(), 0).contains(1);
		assert Graphs.neighborListOf(network.getG(), 0).contains(2);
		assert Graphs.neighborListOf(network.getG(), 0).contains(3);
		assert Graphs.neighborListOf(network.getG(), 0).contains(4);
	}
	
	/**
	 * Test for {@link graph#addEdges(Integer, Set)}.
	 *
	 * @throws Exception thrown if {@link graph#initializeAsCompleteGraph(int, int)} throws an exception.
	 */
	@Test
	void testAddEdges() throws Exception
	{
		int size = 4;
		graph network = new graph("CompleteGraph_size"+size);
		network.initializeAsCompleteGraph(size, 1);
		network.addVertex(0);
		network.addEdges(0, network.getVertexSet());
		
		assert Graphs.neighborListOf(network.getG(), 0).contains(1);
		assert Graphs.neighborListOf(network.getG(), 0).contains(2);
		assert Graphs.neighborListOf(network.getG(), 0).contains(3);
		assert Graphs.neighborListOf(network.getG(), 0).contains(4);
	}
	
	@Test
	void removeSelfLoops() throws Exception
	{
		String networkName = "testnetwork8";
		graph network = new graph(networkName);
		String separator = ",";
		network.buildGraphFromFile("./test/resources/networks/"+networkName+".txt", separator);
		assert !network.hasSelfLoops();
		network.removeSelfLoops();
		assert !network.hasSelfLoops();
		
		networkName = "testnetwork10_selfLoop";
		network = new graph(networkName);
		network.buildGraphFromFile("./test/resources/networks/"+networkName+".txt", separator);
		assert network.hasSelfLoops();
		network.removeSelfLoops();
		assert !network.hasSelfLoops();
	}
	
	@Test
	void hasSelfLoops() throws Exception
	{
		String networkName = "testnetwork8";
		graph network = new graph(networkName);
		String separator = ",";
		network.buildGraphFromFile("./test/resources/networks/"+networkName+".txt", separator);
		assert !network.hasSelfLoops();
		
		networkName = "testnetwork10_selfLoop";
		network = new graph(networkName);
		network.buildGraphFromFile("./test/resources/networks/"+networkName+".txt", separator);
		assert network.hasSelfLoops();
	}
	
	@Test
	void getVertexSet() throws Exception
	{
		// Test 1
		String networkName = "testnetwork8";
		graph network = new graph(networkName);
		String separator = ",";
		network.buildGraphFromFile("./test/resources/networks/"+networkName+".txt", separator);
		Set<Integer> nodes = network.getVertexSet();
		for (int i=1; i<=13; i++)
			assert nodes.contains(i);
		assert nodes.size()==13;
		
		// Test 2
		networkName = "testnetwork6withUnconnectedComponents";
		network = new graph(networkName);
		network.buildGraphFromFile("./test/resources/networks/"+networkName+".txt", separator);
		nodes = network.getVertexSet();
		for (int i=1; i<=12; i++)
			assert nodes.contains(i);
		assert nodes.size()==12;
		
		// Test 3
		networkName = "testnetwork9";
		network = new graph(networkName);
		network.buildGraphFromFile("./test/resources/networks/"+networkName+".txt", separator);
		nodes = network.getVertexSet();
		assert nodes.contains(1);
		assert !nodes.contains(2);
		assert nodes.contains(3);
		for (int i=4; i<=9; i++)
			assert nodes.contains(i);
		assert !nodes.contains(10);
		assert nodes.contains(11);
		assert !nodes.contains(12);
		assert nodes.contains(13);
		assert nodes.size()==11;
	}
	
	@Test
	void getEdgeSet() throws Exception
	{
		String networkName = "testnetwork6withUnconnectedComponents";
		graph network = new graph(networkName);
		String separator = ",";
		network.buildGraphFromFile("./test/resources/networks/"+networkName+".txt", separator);
		
		Set<DefaultEdge> edgeSet = network.getEdgeSet();
		for (DefaultEdge e: edgeSet)
		{
			int source = network.getEdgeSource(e);
			switch (source)
			{
				case 1:
				case 3:
					assert (network.getEdgeTarget(e)==2 ||
							network.getEdgeTarget(e)==4);
					break;
				case 2:
					assert (network.getEdgeTarget(e)==1 ||
							network.getEdgeTarget(e)==3 ||
							network.getEdgeTarget(e)==4);
					break;
				case 4:
					assert (network.getEdgeTarget(e)==1 ||
							network.getEdgeTarget(e)==2 ||
							network.getEdgeTarget(e)==3);
					break;
				case 5:
					assert (network.getEdgeTarget(e)==6 ||
							network.getEdgeTarget(e)==7);
					break;
				case 6:
					assert (network.getEdgeTarget(e)==5 ||
							network.getEdgeTarget(e)==7);
					break;
				case 7:
					assert (network.getEdgeTarget(e)==5 ||
							network.getEdgeTarget(e)==6);
					break;
				case 8:
					assert (network.getEdgeTarget(e)==9 ||
							network.getEdgeTarget(e)==10);
					break;
				case 9:
					assert (network.getEdgeTarget(e)==8 ||
							network.getEdgeTarget(e)==10);
					break;
				case 10:
					assert (network.getEdgeTarget(e)==8 ||
							network.getEdgeTarget(e)==9 ||
							network.getEdgeTarget(e)==11 ||
							network.getEdgeTarget(e)==12);
					break;
				case 11:
					assert (network.getEdgeTarget(e)==10 ||
							network.getEdgeTarget(e)==12);
					break;
				case 12:
					assert (network.getEdgeTarget(e)==10 ||
							network.getEdgeTarget(e)==11);
					break;
				default: assert false;
			}
		}
	}
	
	@Test
	void removeAllVertices() throws Exception
	{
		String networkName = "testnetwork6withUnconnectedComponents";
		graph network = new graph(networkName);
		String separator = ",";
		network.buildGraphFromFile("./test/resources/networks/"+networkName+".txt", separator);
		
		assert network.getVertexSet().size()==12;
		
		Set<Integer> nodesToBeRemoved = new HashSet<>(network.getVertexSet().size());
		nodesToBeRemoved.add(2);
		nodesToBeRemoved.add(10);
		nodesToBeRemoved.add(12);
		network.removeAllVertices(nodesToBeRemoved);
		assert network.getVertexSet().size()==12-nodesToBeRemoved.size();
		for (int i=1; i<=12; i++)
		{
			if (nodesToBeRemoved.contains(i))
				assert !network.getVertexSet().contains(i);
			else
				assert network.getVertexSet().contains(i);
		}
	}
	
	@Test
	void getNeighbors() throws Exception
	{
		String networkName = "testnetwork1";
		graph network = new graph(networkName);
		String separator = ",";
		network.buildGraphFromFile("./test/resources/networks/"+networkName+".txt", separator);
		Map<Integer, List<Integer>> neighbors = network.getNeighbors();
		assert neighbors.get(1).contains(2);
		assert neighbors.get(1).contains(3);
		assert neighbors.get(1).contains(4);
		assert neighbors.get(2).contains(5);
		assert neighbors.get(3).contains(6);
		assert neighbors.get(4).contains(5);
		assert neighbors.get(4).contains(6);
		assert neighbors.get(5).contains(6);
		assert neighbors.get(5).contains(7);
		assert neighbors.get(6).contains(7);
		
		assert neighbors.get(1).size()==3;
		assert neighbors.get(2).size()==2;
		assert neighbors.get(3).size()==2;
		assert neighbors.get(4).size()==3;
		assert neighbors.get(5).size()==4;
		assert neighbors.get(6).size()==4;
		assert neighbors.get(7).size()==2;
	}
	
	/**
	 * Unit test for {@link graph#getEdgeSource(DefaultEdge)}.
	 *
	 */
	@Test
	void getEdgeSource()
	{
		String networkName = "testNetwork";
		graph network = new graph(networkName);
		
		network.addVertex(-1);
		network.addVertex(10);
		network.addEdge(-1, 10);
		List<DefaultEdge> edgeSet = new ArrayList<>(network.getEdgeSet());
		assert network.getEdgeSource(edgeSet.get(0))==-1;
		
		network.addVertex(11);
		network.addEdge(11, -1);
		edgeSet = new ArrayList<>(network.getEdgeSet());
		assert network.getEdgeSource(edgeSet.get(1))==11;
	}
	
	/**
	 * Unit test for {@link graph#getEdgeTarget(DefaultEdge)}.
	 * 
	 */
	@Test
	void getEdgeTarget()
	{
		String networkName = "testNetwork";
		graph network = new graph(networkName);
		
		network.addVertex(-1);
		network.addVertex(10);
		network.addEdge(-1, 10);
		List<DefaultEdge> edgeSet = new ArrayList<>(network.getEdgeSet());
		assert network.getEdgeTarget(edgeSet.get(0))==10;
		
		network.addVertex(11);
		network.addEdge(11, -1);
		edgeSet = new ArrayList<>(network.getEdgeSet());
		assert network.getEdgeTarget(edgeSet.get(1))==-1;
	}
	
	/**
	 * Unit test for {@link graph#remapNodeLabels(int)}.
	 *
	 * @throws Exception thrown if {@link graph#buildGraphFromFile(String, String)} throws an exception.
	 */
	@Test
	void remapNodeLabels() throws Exception
	{
		String networkName = "braessNetwork";
		graph network = new graph(networkName);
		String separator = ",";
		network.buildGraphFromFile("./test/resources/networks/"+networkName+".txt", separator);
		
		Graph<Integer, DefaultEdge> remappedNetwork = network.remapNodeLabels(-1);
		graph remappedGraph = new graph(remappedNetwork, networkName+"_remapped", false);
		Set<Integer> newNodes = remappedGraph.getVertexSet();
		assert newNodes.contains(-1);
		assert newNodes.contains(0);
		assert newNodes.contains(1);
		assert newNodes.contains(2);
		
		List<Integer> neighbors = Graphs.neighborListOf(remappedGraph.getG(),-1);
		assert neighbors.contains(0);
		assert neighbors.contains(2);
		neighbors = Graphs.neighborListOf(remappedGraph.getG(),0);
		assert neighbors.contains(-1);
		assert neighbors.contains(1);
		assert neighbors.contains(2);
		neighbors = Graphs.neighborListOf(remappedGraph.getG(),1);
		assert neighbors.contains(0);
		assert neighbors.contains(2);
		neighbors = Graphs.neighborListOf(remappedGraph.getG(),2);
		assert neighbors.contains(-1);
		assert neighbors.contains(1);
	}
}