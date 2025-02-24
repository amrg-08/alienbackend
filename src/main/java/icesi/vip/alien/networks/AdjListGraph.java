package icesi.vip.alien.networks;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public   class AdjListGraph<T> implements IGraph<T> {

	private boolean directed;
	private boolean weighted;
	private int numberOfVertices;
	private int numberOfEdges;
	private int n, s, t;
	private  long maxFlow;
	private long minCost;
	private boolean[] minCut;
	private List<Vertex<T>> vertices;
	private HashMap<T, AdjVertex<T>> map;
	private int visitedToken = 1;
	private int[] visited;
	private boolean solved;
	
	
	public AdjListGraph(boolean directed, boolean weighted) {
		this.directed = directed;
		this.weighted = weighted;
		numberOfVertices = 0;
		numberOfEdges = getNumberOfEdges();
		vertices = new LinkedList<Vertex<T>>();
		map = new HashMap<>();
	}
	public AdjListGraph(String path, boolean directed) throws IOException {
		this.directed = directed;
		this.weighted = true;
		numberOfVertices = 0;
		numberOfEdges = getNumberOfEdges();
		vertices = new ArrayList<Vertex<T>>();
		map = new HashMap<>();
		File file = new File(path);
		FileReader reader = new FileReader(file);
		BufferedReader in = new BufferedReader(reader);
		Integer node = 0;
		String line = in.readLine();
		while(line!=null) {
			
			addVertex((T) node);

			
			String[] parts = line.split(";");

			for (Integer i = 0; i < parts.length; i++) {
				Double weight = Double.parseDouble(parts[i]);
				if(weight!=-1) {
					addVertex((T) i);
					addEdge((T)node, (T)i, weight);
				}
			}

			line = in.readLine();
			node++;
		}
		reader.close();
		in.close();
	}
	
	public void networkFlowSolverBase(int n, int s, int t) {
	    this.n = n;
	    this.s = s;
	    this.t = t;
	    minCut = new boolean[n];
	    visited = new int[n];
	  }
	
	/*public void addEdge(Ver from, T to, long capacity) {
	    if (capacity < 0) throw new IllegalArgumentException("Capacity < 0");
	    Edge e1 = new Edge(from, to, capacity);
	    Edge e2 = new Edge(to, from, 0);
	    e1.residual = e2;
	    e2.residual = e1;
	    graph[from].add(e1);
	    graph[to].add(e2);
	  }

	  /** Cost variant of {@link #addEdge(int, int, int)} for min-cost max-flow */
	/*  public void addEdge(int from, int to, long capacity, long cost) {
	    Edge e1 = new Edge(from, to, capacity, cost);
	    Edge e2 = new Edge(to, from, 0, -cost);
	    e1.residual = e2;
	    e2.residual = e1;
	    graph[from].add(e1);
	    graph[to].add(e2);
	  }*/

	  // Marks node 'i' as visited.
	  public void visit(int i) {
	    visited[i] = visitedToken;
	  }

	  // Returns whether or not node 'i' has been visited.
	  public boolean visited(int i) {
	    return visited[i] == visitedToken;
	  }

	  // Resets all nodes as unvisited. This is especially useful to do
	  // between iterations of finding augmenting paths, O(1)
	  public void markAllNodesAsUnvisited() {
	    visitedToken++;
	  }

	  /**
	   * Returns the graph after the solver has been executed. This allow you to inspect the {@link
	   * Edge#flow} compared to the {@link Edge#capacity} in each edge. This is useful if you want to
	   * figure out which edges were used during the max flow.
	   */
	 // public List<Edge>[] getGraph() {
	 //   execute();
	    //return graph;
	 // }

	  // Returns the maximum flow from the source to the sink.
	  public long getMaxFlow() {
	    execute();
	    return maxFlow;
	  }

	  // Returns the min cost from the source to the sink.
	  // NOTE: This method only applies to min-cost max-flow algorithms.
	  public long getMinCost() {
	    execute();
	    return minCost;
	  }

	  // Returns the min-cut of this flow network in which the nodes on the "left side"
	  // of the cut with the source are marked as true and those on the "right side"
	  // of the cut with the sink are marked as false.
	  public boolean[] getMinCut() {
	    execute();
	    return minCut;
	  }

	  // Wrapper method that ensures we only call solve() once
	  private void execute() {
	    if (solved) return;
	    solved = true;
	    //solve();
	  }

	  // Method to implement which solves the network flow problem.
	  //public abstract void solve();
	
	public List<Vertex<T>> getVertices() {
		return vertices;
	}

	public int getNumberOfVertices() {
		return numberOfVertices;
	}

	public int getNumberOfEdges() {
		return numberOfEdges;
	}

	public boolean isDirected() {
		return directed;
	}

	public boolean isWeighted() {
		return weighted;
	}

	@Override
	public void addVertex(T value) {
		if (!isInGraph(value)) {
			AdjVertex<T> vertex = new AdjVertex<T>(value);
			map.put(value, vertex);
			vertex.setIndex(numberOfVertices);
			vertices.add(vertex);
			numberOfVertices++;
		}
	}

	@Override
	public void addEdge(T x, T y) {
		AdjVertex<T> from = searchVertex(x);
		AdjVertex<T> to = searchVertex(y);
		addEdge(from, to);
	}

	public void addEdge(AdjVertex<T> from, AdjVertex<T> to) {
		addEdge(from, to, 1D);
	}

	@Override
	public void addEdge(T x, T y, double w) {
		if (weighted) {
			AdjVertex<T> from = searchVertex(x);
			AdjVertex<T> to = searchVertex(y);
			addEdge(from, to, w);
		}
	}

	public void addEdge(AdjVertex<T> from, AdjVertex<T> to, double w) {
		if (from != null && to != null) {
			Edge<T> edge = new Edge<T>(from, to, w);
			from.getAdjList().add(edge);
			if (!isDirected()) {
				edge = new Edge<T>(to, from, w);
				to.getAdjList().add(edge);
			}
			numberOfEdges++;
		}

	}

	public void removeVertex(Vertex<T> v) {
		for (int i = 0; i < vertices.size(); i++) {
			removeEdge(vertices.get(i), v);
			if (isDirected()) {
				removeEdge(v, vertices.get(i));
			}
		}
		vertices.remove(v);
		map.remove(v.getValue());
		numberOfVertices--;
	}

	public void removeVertex(T v) {
		if (isInGraph(v)) {
			removeVertex(searchVertex(v));
		}
	}

	public void removeEdge(Vertex<T> x, Vertex<T> y) {
		AdjVertex<T> from = (AdjVertex<T>) x;
		AdjVertex<T> to = (AdjVertex<T>) y;
		List<Edge<T>> adjFrom = from.getAdjList();
		Edge<T> e = from.findEdge(to);
		if (e != null)
			adjFrom.remove(e);

		if (!isDirected()) {
			List<Edge<T>> adjTo = to.getAdjList();
			e = to.findEdge(from);
			if (e != null)
				adjTo.remove(e);
		}

		numberOfEdges--;
	}

	public void removeEdge(T x, T y) {
		if (isInGraph(x) && isInGraph(y)) {
			removeEdge(searchVertex(x), searchVertex(y));
		}
	}

	public List<Vertex<T>> getNeighbors(Vertex<T> x) {
		List<Vertex<T>> neigh = new ArrayList<>();
		AdjVertex<T> from = (AdjVertex<T>) x;
		List<Edge<T>> adj = from.getAdjList();
		for (int i = 0; i < adj.size(); i++) {
			neigh.add(adj.get(i).getDestination());
		}
		return neigh;
	}

	public boolean areAdjacent(Vertex<T> x, Vertex<T> y) {
		return getNeighbors(x).contains(y);
	}

	public boolean isInGraph(T value) {
		return searchVertex(value) != null;
	}

	public double getEdgeWeight(Vertex<T> x, Vertex<T> y) {
		double w = 0;
		if (isInGraph(x.getValue()) && isInGraph(y.getValue())) {
			AdjVertex<T> from = (AdjVertex<T>) x;
			AdjVertex<T> to = (AdjVertex<T>) y;
			Edge<T> e = from.findEdge(to);
			if (e != null)
				w = e.getWeight();
		}
		return w;
	}

	public void setEdgeWeight(Vertex<T> x, Vertex<T> y, double w) {
		if (isInGraph(x.getValue()) && isInGraph(y.getValue()) && weighted) {
			AdjVertex<T> from = (AdjVertex<T>) x;
			AdjVertex<T> to = (AdjVertex<T>) y;
			Edge<T> e = from.findEdge(to);
			if (e != null)
				e.setWeight(w);

			if (!isDirected()) {
				e = to.findEdge(from);
				if (e != null)
					e.setWeight(w);
			}
		}
	}

	public AdjVertex<T> searchVertex(T value) {
		return map.get(value);
	}

	public int getIndexOf(Vertex<T> v) {
		int index = -1;
		boolean searching = true;
		for (int i = 0; i < vertices.size() && searching; i++) {
			if (vertices.get(i) == v) {
				index = i;
				searching = false;
			}
		}
		return index;
	}

	public void bfs(Vertex<T> x) {
		AdjVertex<T> s = (AdjVertex<T>) x;
		for (Vertex<T> u : vertices) {
			u.setColor(Vertex.WHITE);
			u.setD(INF);
			u.setPred(null);
		}
		s.setColor(Vertex.GRAY);
		s.setD(0);
		s.setPred(null);
		Queue<AdjVertex<T>> q = new LinkedList<>();
		q.offer(s);
		while (!q.isEmpty()) {
			AdjVertex<T> u = q.poll();
			for (int i = 0; i < u.getAdjList().size(); i++) {
				AdjVertex<T> v = (AdjVertex<T>) u.getAdjList().get(i).getDestination();
				if (v.getColor() == Vertex.WHITE) {
					v.setColor(Vertex.GRAY);
					v.setD(u.getD() + 1);
					v.setPred(u);
					q.offer(v);
				}
			}
			u.setColor(Vertex.BLACK);
		}
	}

	public void dfs() {
		for (Vertex<T> u : vertices) {
			u.setColor(Vertex.WHITE);
			u.setPred(null);
		}
		int time = 0;
		for (Vertex<T> u : vertices) {
			if (u.getColor() == Vertex.WHITE)
				time = dfsVisit((AdjVertex<T>) u, time);
		}
	}

	private int dfsVisit(AdjVertex<T> u, int time) {
		time++;
		u.setD(time);
		u.setColor(Vertex.GRAY);
		for (int i = 0; i < u.getAdjList().size(); i++) {
			AdjVertex<T> v = (AdjVertex<T>) u.getAdjList().get(i).getDestination();
			if (v.getColor() == Vertex.WHITE) {
				v.setPred(u);
				time = dfsVisit(v, time);
			}
		}
		u.setColor(Vertex.BLACK);
		time++;
		u.setF(time);
		return time;
	}

	 
		

	private void initSingleSource(AdjVertex<T> s) {
		for (Vertex<T> u : vertices) {
			u.setD(INF);
			u.setPred(null);
		}
		s.setD(0);
	}

	public void dijkstra(Vertex<T> x) {
		AdjVertex<T> s = (AdjVertex<T>) x;
		initSingleSource(s);
		PriorityQueue<AdjVertex<T>> queue = new PriorityQueue<>();
		queue.add(s);
		while (!queue.isEmpty()) {
			AdjVertex<T> u = queue.poll();

			for (Edge<T> e : u.getAdjList()) {

				AdjVertex<T> v = (AdjVertex<T>) e.getDestination();
				double weight = e.getWeight();

				// relax(u,v,weight)
				double distanceFromU = u.getD() + weight;
				if (distanceFromU < v.getD()) {
					queue.remove(v);
					v.setD(distanceFromU);
					v.setPred(u);
					queue.add(v);

				}
			}
		}
	}

	public double[][] floydwarshall() {
		double[][] weights = getWeightsMatrix();
		for (int k = 0; k < vertices.size(); k++) {
			for (int i = 0; i < vertices.size(); i++) {
				for (int j = 0; j < vertices.size(); j++) {
					weights[i][j] = Math.min(weights[i][j], weights[i][k] + weights[k][j]);
				}
			}
		}
		return weights;
	}

	private double[][] getWeightsMatrix() {
		double[][] weights = new double[vertices.size()][vertices.size()];
		for (int i = 0; i < weights.length; i++) {
			Arrays.fill(weights[i], INF);
		}
		for (int i = 0; i < vertices.size(); i++) {
			weights[i][i] = 0;
			AdjVertex<T> u = (AdjVertex<T>) vertices.get(i);
			for (Edge<T> e : u.getAdjList()) {
				AdjVertex<T> v = (AdjVertex<T>) e.getDestination();
				double weight = e.getWeight();
				weights[i][getIndexOf(v)] = weight;
			}
		}
		return weights;
	}

	public void prim(Vertex<T> s) {
		AdjVertex<T> r = (AdjVertex<T>) s;
		for (Vertex<T> u : vertices) {
			u.setD(INF);
			u.setColor(Vertex.WHITE);
		}
		r.setD(0);
		r.setPred(null);
		PriorityQueue<AdjVertex<T>> queue = new PriorityQueue<>();
		for (Vertex<T> u : vertices) {
			queue.add((AdjVertex<T>) u);
		}
		while (!queue.isEmpty()) {
			AdjVertex<T> u = queue.poll();
			for (Edge<T> e : u.getAdjList()) {
				AdjVertex<T> v = (AdjVertex<T>) e.getDestination();
				if (v.getColor() == Vertex.WHITE && e.getWeight() < v.getD()) {
					queue.remove(v);
					v.setD(e.getWeight());
					queue.add(v);
					v.setPred(u);
				}
			}
			u.setColor(Vertex.BLACK);
		}
	}

	public ArrayList<Edge<T>> kruskal() { // Adapted from
											// www.geeksforgeeks.org/kruskals-minimum-spanning-tree-algorithm-greedy-algo-2/
		ArrayList<Edge<T>> result = new ArrayList<>(); // Tnis will store the resultant MST
		int e = 0; // An index variable, used for result[]
		int i = 0; // An index variable, used for sorted edges

		ArrayList<Edge<T>> edges = getEdges();

		// Step 1: Sort all the edges in non-decreasing order of their
		// weight. If we are not allowed to change the given graph, we
		// can create a copy of array of edges
		Collections.sort(edges);

		UnionFind uf = new UnionFind(vertices.size());

		i = 0; // Index used to pick next edge

		// Number of edges to be taken is equal to V-1
		while (e < vertices.size() - 1 && i < edges.size()) {
			// Step 2: Pick the smallest edge. And increment
			// the index for next iteration
			Edge<T> edge = edges.get(i);
			i++;

			int x = uf.find(getIndexOf(edge.getSource()));
			int y = uf.find(getIndexOf(edge.getDestination()));

			// If including this edge does't cause cycle,
			// include it in result and increment the index
			// of result for next edge
			if (x != y) {
				result.add(edge);
				e++;
				uf.union(x, y);
			}
			// Else discard the edge
		}
		return result;
	}

	public ArrayList<Edge<T>> getEdges() {
		ArrayList<Edge<T>> edges = new ArrayList<>();
		for (int i = 0; i < vertices.size(); i++) {
			AdjVertex<T> v = (AdjVertex<T>) vertices.get(i);
			for (int j = 0; j < v.getAdjList().size(); j++) {
				edges.add(v.getAdjList().get(j));
			}
		}
		return edges;
	}

}