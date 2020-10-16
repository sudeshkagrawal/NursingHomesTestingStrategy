package network;

import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.Test;

import java.util.*;

/**
 * Unit tests for {@link graph}.
 * @author Sudesh Agrawal (sudesh@utexas.edu).
 * Last Updated: October 15, 2020.
 */
class graphTest
{
	
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
	
	@Test
	void initializeAsCompleteGraph() throws Exception
	{
		int size = 4;
		graph network = new graph("CompleteGraph_size"+size);
		network.initializeAsCompleteGraph(size);
		for (int i=1; i<=size; i++)
			for (int j = 1; j <=size; j++)
				assert i == j || (network.getG().containsEdge(i, j));
	}
	
	@Test
	void initializeAsCirculantGraph() throws Exception
	{
		int size = 7;
		int[] offsets = {2, 4};
		graph network = new graph("CirculantGraph_size"+size+"_offsets_"+ Arrays.toString(offsets));
		network.initializeAsCirculantGraph(size, offsets);
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
		network.initializeAsCompleteGraph(size);
		
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
		network.initializeAsCompleteGraph(size);
		
		assert network.getEdgeSet().size()==6;
		network.addVertex(size+1);
		network.addEdge(1, 5);
		assert network.getEdgeSet().size()==7;
		assert Graphs.neighborListOf(network.getG(), 1).contains(5);
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
			int source = network.getG().getEdgeSource(e);
			switch (source)
			{
				case 1:
				case 3:
					assert (network.getG().getEdgeTarget(e)==2 ||
							network.getG().getEdgeTarget(e)==4);
					break;
				case 2:
					assert (network.getG().getEdgeTarget(e)==1 ||
							network.getG().getEdgeTarget(e)==3 ||
							network.getG().getEdgeTarget(e)==4);
					break;
				case 4:
					assert (network.getG().getEdgeTarget(e)==1 ||
							network.getG().getEdgeTarget(e)==2 ||
							network.getG().getEdgeTarget(e)==3);
					break;
				case 5:
					assert (network.getG().getEdgeTarget(e)==6 ||
							network.getG().getEdgeTarget(e)==7);
					break;
				case 6:
					assert (network.getG().getEdgeTarget(e)==5 ||
							network.getG().getEdgeTarget(e)==7);
					break;
				case 7:
					assert (network.getG().getEdgeTarget(e)==5 ||
							network.getG().getEdgeTarget(e)==6);
					break;
				case 8:
					assert (network.getG().getEdgeTarget(e)==9 ||
							network.getG().getEdgeTarget(e)==10);
					break;
				case 9:
					assert (network.getG().getEdgeTarget(e)==8 ||
							network.getG().getEdgeTarget(e)==10);
					break;
				case 10:
					assert (network.getG().getEdgeTarget(e)==8 ||
							network.getG().getEdgeTarget(e)==9 ||
							network.getG().getEdgeTarget(e)==11 ||
							network.getG().getEdgeTarget(e)==12);
					break;
				case 11:
					assert (network.getG().getEdgeTarget(e)==10 ||
							network.getG().getEdgeTarget(e)==12);
					break;
				case 12:
					assert (network.getG().getEdgeTarget(e)==10 ||
							network.getG().getEdgeTarget(e)==11);
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
}