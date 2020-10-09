package network;

import org.jgrapht.Graph;
import org.jgrapht.GraphTests;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultUndirectedGraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Represents a network graph.
 * @author Sudesh Agrawal (sudesh@utexas.edu).
 * Last Updated: October 8, 2020.
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
	 * Build network graph {@link graph#g} from a text file.
	 * Each line in the text file is an edge, where the vertices are separated by commas.
	 *
	 * @param filename path of the file to be read
	 * @param separator character that separates source node and target node.
	 * @throws Exception thrown if vertex set of {@link graph#g} is not empty.
	 */
	public void buildGraphFromFile(String filename, String separator) throws Exception
	{
		if (this.g.vertexSet().size()>0)
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
		g.addEdge(s, t);
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
