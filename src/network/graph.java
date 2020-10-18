package network;

import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.generate.CompleteGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;
import org.jgrapht.util.SupplierUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Represents a network graph.
 * @author Sudesh Agrawal (sudesh@utexas.edu).
 * Last Updated: October 17, 2020.
 */
public class graph
{
	/**
	 * A network graph ({@link org.jgrapht.graph.DefaultUndirectedGraph})
	 * with {@link java.lang.Integer} vertices and {@link org.jgrapht.graph.DefaultEdge} edges.
	 * <br>
	 * <b>Note:</b>
	 * <ol>
	 *     <li>Methods defined in this class caters to an undirected graph.</li>
	 *     <li>Edges should have the same weight.</li>
	 *     <li>Multiple edges not allowed.</li>
	 *     <li>Methods are likely to remove any self-loops.</li>
	 * </ol>
	 */
	private Graph<Integer, DefaultEdge> g;
	/**
	 * Name of the network.
	 */
	private String networkName;
	
	/**
	 * Constructor to instantiate with the network name ({@code networkName}),
	 * and a default undirected graph ({@link org.jgrapht.graph.DefaultUndirectedGraph}).
	 *
	 * @param networkName name of the network.
	 */
	public graph(String networkName)
	{
		this.g = new DefaultUndirectedGraph<>(DefaultEdge.class);
		this.networkName = networkName;
	}
	
	/**
	 * Constructor to instantiate with a network name and a network graph.
	 *
	 * @param g network graph
	 * @param networkName name of the network
	 * @param copyGraphReference {@code true} if reference to parameter {@code g} should be copied;
	 *                           {@code false} if vertices and edges from parameter {@code g} should be copied.
	 */
	public graph(Graph<Integer, DefaultEdge> g, String networkName, boolean copyGraphReference)
	{
		if (copyGraphReference)
			this.g = g;
		else
			g.vertexSet().forEach(v -> Graphs.neighborListOf(g, v)
					.forEach(neighbor -> this.g.addEdge(v, neighbor)));
		this.networkName = networkName;
	}
	
	/**
	 * Returns a string representation of the object.
	 *
	 * @return a string representation of the object.
	 */
	@Override
	public String toString()
	{
		return networkName+": g<"+g.vertexSet()+", "+g.edgeSet()+">";
	}
	
	/**
	 * Indicates whether some other object is "equal to" this one.
	 * Used guidelines at <a href="http://www.technofundo.com/tech/java/equalhash.html" target="_blank">
	 *     "Equals and Hash Code"</a>.
	 *
	 * @param o the reference object with which to compare.
	 * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object o)
	{
		// this instance check
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		graph graph = (graph) o;
		return this.g.equals(graph.g) &&
				networkName.equals(graph.networkName);
	}
	
	/**
	 * Returns a hash code value for the object.
	 *
	 * @return a hash code value for this object.
	 */
	@Override
	public int hashCode()
	{
		return Objects.hash(this.g, networkName);
	}
	
	/**
	 * Build network graph {@link graph#g} from a text file.
	 * Each line in the text file is an edge, where the vertices are separated by commas.
	 *
	 * @param filename path of the file to be read
	 * @param separator character that separates source node and target node.
	 * @throws Exception thrown if vertex set of {@link graph#g} is not empty.
	 */
	public void buildGraphFromFile(String filename, String separator) throws Exception
	{
		if ((!this.g.vertexSet().isEmpty()) && (this.g.vertexSet().size()>0))
		{
			throw new Exception("Graph is not empty!");
		}
		else
		{
			try
			{
				File myObj = new File(filename);
				Scanner myReader = new Scanner(myObj);
				while (myReader.hasNextLine())
				{
					String data = myReader.nextLine();
					if (data.equals(""))
						continue;
					String[] tokens = data.split(separator);
					int source = Integer.parseInt(tokens[0].trim());
					int destination = Integer.parseInt(tokens[1].trim());
					this.g.addVertex(source);
					this.g.addVertex(destination);
					this.g.addEdge(source, destination);
					
				}
			}
			catch (FileNotFoundException e)
			{
				System.out.println("An error occurred while trying to read the file \""+filename+"\":");
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Initialize {@link graph#g}, assumed to be empty, with a complete graph of given size.
	 *
	 * @param size number of vertices (nodes) in the complete graph
	 * @param startWithZerothNode {@code true} if the first node should be 0 (instead of 1), {@code false} otherwise.
	 * @throws Exception thrown if vertex set of {@link graph#g} is not empty.
	 */
	public void initializeAsCompleteGraph(int size, boolean startWithZerothNode) throws Exception
	{
		if ((!this.g.vertexSet().isEmpty()) && (this.g.vertexSet().size()>0))
		{
			throw new Exception("Graph is not empty!");
		}
		else
		{
			Supplier<Integer> vertexSupplier = new Supplier<>()
			{
				private int id = startWithZerothNode ? 0 : 1;
				
				@Override
				public Integer get()
				{
					return id++;
				}
			};
			this.g = new DefaultUndirectedGraph<>(vertexSupplier, SupplierUtil.createDefaultEdgeSupplier(),
											false);
			CompleteGraphGenerator<Integer, DefaultEdge> completeGraphGenerator =
															new CompleteGraphGenerator<>(size);
			completeGraphGenerator.generateGraph(this.g);
		}
	}
	
	/**
	 * Initialize an empty graph {@link graph#g} with a circulant graph of given size.
	 * <br>
	 * A circulant graph is a graph of {@code n (= size)} vertices in which the {@code i}<sup>th</sup> vertex is
	 * adjacent to the {@code (i+j)}<sup>th</sup> and the {@code (i-j)}<sup>th</sup> vertices for each {@code j} in the
	 * array offsets.
	 *
	 * @param size number of vertices (nodes) in the circulant graph
	 * @param offsets defines the list of all distances in any edge
	 * @param startWithZerothNode {@code true} if the first node should be 0 (instead of 1), {@code false} otherwise.
	 * @throws Exception thrown if vertex set of {@link graph#g} is not empty.
	 */
	public void initializeAsCirculantGraph(int size, int[] offsets, boolean startWithZerothNode) throws Exception
	{
		if ((!this.g.vertexSet().isEmpty()) && (this.g.vertexSet().size()>0))
		{
			throw new Exception("Graph is not empty!");
		}
		else
		{
			// starts with node 0
			if (startWithZerothNode)
			{
				// add vertices
				for (int i=0; i<size; i++)
					this.g.addVertex(i);
				// add edges
				for (int i=0; i<size; i++)
				{
					for (int offset : offsets)
					{
						this.g.addEdge(i, i-offset>=0 ? (i-offset)%size : (i-offset+size));
						this.g.addEdge(i, (i+offset)%size);
					}
				}
			}
			// stats with node 1
			else
			{
				// add vertices
				for (int i=1; i<=size; i++)
					this.g.addVertex(i);
				// add edges
				for (int i=1; i<=size; i++)
				{
					for (int offset : offsets)
					{
						this.g.addEdge(i, i-offset>=1 ? i-offset : (i-offset+size));
						this.g.addEdge(i, i+offset<=size ? i+offset : (i+offset) % size);
					}
				}
			}
		}
	}
	
	/**
	 * Getter.
	 *
	 * @return {@link graph#g}.
	 */
	public Graph<Integer, DefaultEdge> getG()
	{
		return g;
	}
	
	/**
	 * Getter.
	 *
	 * @return {@link graph#networkName}.
	 */
	public String getNetworkName()
	{
		return networkName;
	}
	
	/**
	 * Setter.
	 *
	 * @param networkName name of the network.
	 */
	public void setNetworkName(String networkName)
	{
		this.networkName = networkName;
	}
	
	/**
	 * Adds a vertex to the graph {@link graph#g}.
	 *
	 * @param v vertex (node) to be added to the graph.
	 */
	public void addVertex(Integer v)
	{
		this.g.addVertex(v);
	}
	
	/**
	 * Adds an edge to the graph {@link graph#g}.
	 *
	 * @param s source of the edge to be added
	 * @param t target (destination) of the edge to be added.
	 */
	public void addEdge(Integer s, Integer t)
	{
		this.g.addEdge(s, t);
	}
	
	/**
	 * Adds an edge between {@code sourceNode} and all nodes in {@code targetNodes}.
	 * Overloaded function: see {@link graph#addEdges(Integer, Set)}.
	 *
	 * @param sourceNode source of the edge to be added
	 * @param targetNodes list of target nodes.
	 */
	public void addEdges(Integer sourceNode, List<Integer> targetNodes)
	{
		targetNodes.forEach(targetNode -> this.addEdge(sourceNode, targetNode));
	}
	
	/**
	 * Adds an edge between {@code sourceNode} and all nodes in {@code targetNodes}.
	 * Overloaded function: see {@link graph#addEdges(Integer, List)}.
	 *
	 * @param sourceNode source of the edge to be added
	 * @param targetNodes set of target nodes.
	 */
	public void addEdges(Integer sourceNode, Set<Integer> targetNodes)
	{
		targetNodes.forEach(targetNode -> this.addEdge(sourceNode, targetNode));
	}
	
	/**
	 * Returns a set of nodes contained in the graph {@link graph#g}.
	 *
	 * @return a set of nodes contained in the graph {@link graph#g}.
	 */
	public Set<Integer> getVertexSet()
	{
		return this.g.vertexSet();
	}
	
	/**
	 * Returns the set of edges contained in the graph {@link graph#g}.
	 *
	 * @return a set of the edges contained in the graph {@link graph#g}.
	 */
	public Set<DefaultEdge> getEdgeSet()
	{
		return this.g.edgeSet();
	}
	
	/**
	 * Removes nodes in {@code nodesToBeRemoved} from graph {@link graph#g}.
	 *
	 * @param nodesToBeRemoved nodes to be removed from the graph {@link graph#g}.
	 */
	public void removeAllVertices(Set<Integer> nodesToBeRemoved)
	{
		this.g.removeAllVertices(nodesToBeRemoved);
	}
	
	/**
	 * Get a map from nodes to their neighbors.
	 *
	 * @return a map from nodes to their neighbors.
	 */
	public Map<Integer, List<Integer>> getNeighbors()
	{
		Set<Integer> nodes = this.g.vertexSet();
		return nodes.stream().collect(Collectors.toMap(v -> v, v -> Graphs.neighborListOf(this.g, v), (a, b) -> b));
	}
	
	/**
	 * Removes self-loops from the graph {@link graph#g}.
	 */
	public void removeSelfLoops()
	{
		for (Integer v: this.g.vertexSet())
			this.g.removeEdge(v, v);
	}
	
	/**
	 * Checks whether the graph {@link graph#g} has self-loops.
	 *
	 * @return {@code true} if graph {@link graph#g} has self-loops, {@code false} otherwise.
	 */
	public boolean hasSelfLoops()
	{
		return GraphTests.hasSelfLoops(this.g);
	}
}
