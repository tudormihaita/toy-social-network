package application.toysocialnetwork.utils;

import java.util.*;

/**
 * Utility Class for computing generic Graph Algorithms
 * @param <T> Data Type of the Graph Nodes
 */
public class GraphManager<T> {

    private Map<T, Set<T>> graph;

    /**
     * Initializes the GraphManager with a given graph
     * @param adjList Adjacency List of a graph
     */
    public GraphManager(Map<T, Set<T>> adjList) {
        this.graph = adjList;
    }

    /**
     * Constructs a GraphManager with an empty graph
     */
    public GraphManager() {
        this.graph = new HashMap<>();
    }

    /**
     * @return the current graph
     */
    public Map<T, Set<T>> getGraph() {
        return graph;
    }

    /**
     * Setter for the graph
     * @param graph Adjacency List of the new graph
     */
    public void setGraph(Map<T, Set<T>> graph) {
        this.graph = graph;
    }

    /**
     * Algorithm for computing the Longest path existing in a Graph
     * Traverses the graph exhaustively with DFS for finding the longest possible path
     * @return List with the nodes which form the longest path
     */
    public List<T> findLongestPath() {
        List<T> longestPath = new ArrayList<>();
        for (T vertex : graph.keySet()) {
            List<T> currentPath = new ArrayList<>();
            Set<T> visited = new HashSet<>();
            DFSLongestPathFrom(vertex, visited, currentPath, longestPath);
        }
        return longestPath;
    }

    /**
     * DFS utility algorithm used for computing the longest path
     * @param vertex current vertex for traversing the graph
     * @param visited Set for marking the visited nodes with DFS
     * @param currentPath List with nodes which construct the current path traversed with DFS
     * @param longestPath List with the longest path computed while traversing with DFS
     */
    private void DFSLongestPathFrom(T vertex, Set<T> visited, List<T> currentPath, List<T> longestPath) {
        visited.add(vertex);
        currentPath.add(vertex);

        if (currentPath.size() > longestPath.size()) {
            longestPath.clear();
            longestPath.addAll(currentPath);
        }

        for (T neighbor : graph.get(vertex)) {
            if (!visited.contains(neighbor)) {
                DFSLongestPathFrom(neighbor, visited, currentPath, longestPath);
            }
        }

        visited.remove(vertex);
        currentPath.remove(vertex);
    }

    /**
     * Algorithm for computing all Connected Components of a graph
     * @return a List of all connected components
     */
    public List<List<T>> findConnectedComponents() {
        Set<T> visited = new HashSet<>();
        List<List<T>> connectedComponents = new ArrayList<>();

        graph.keySet().forEach(vertex -> {
            if(!visited.contains(vertex)) {
                List<T> connectedComponent = new ArrayList<>();
                DFSConnectedComponent(vertex, visited, connectedComponent);
                connectedComponents.add(connectedComponent);
            }
        });

        return connectedComponents;
    }

    /**
     * DFS utility algorithm for computing the connected components
     * @param vertex current vertex for traversing with DFS
     * @param visited Set for marking the already visited nodes within the current traversal
     * @param connectedComponent List of all nodes within the currently traversed connected component
     */
    private void DFSConnectedComponent(T vertex, Set<T> visited, List<T> connectedComponent) {
        visited.add(vertex);
        connectedComponent.add(vertex);

        graph.get(vertex).forEach(neighbour -> {
            if(!visited.contains(neighbour)) {
                DFSConnectedComponent(neighbour, visited, connectedComponent);
            }
        });
    }
}
