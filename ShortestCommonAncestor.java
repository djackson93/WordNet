/******************************************************************************
 * Compilation:  javac ShortestCommonAncestor.java
 * ----------------------------------------------------------------------------
 * Program used for finding the shortest common ancestor from any given two 
 * points 'v' and 'w'. It takes in a Digraph input, calculates the ancestral
 * paths for the given points using Breadth-First Search and determines the
 * shortest ancestral path. Using the shortest ancestral path, it then finds 
 * the shortest common ancestor on that path.
 * 
 * Author:@Julian Ceja, @Dakota Jackson
 * ***************************************************************************/

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.LinearProbingHashST;
import edu.princeton.cs.algs4.Queue;
import java.util.HashSet;
import java.util.Iterator;


public class ShortestCommonAncestor {
  
  Digraph graph;
  private int root;
  
  
  //Constructor for ShortestCommonAncestor object
  // -Throws 'NullPointerException" if null inputted Digraph
  public ShortestCommonAncestor( Digraph G ) {
    if ( G == null ) throw new NullPointerException();
    
    graph = G;
  }
  
  /* Method giving the associated length for the shortest common ancestral path
   *   -Method creates 2 BFSPath objects 
   *    -1st object gets the path from 'v' to root
   *    -2nd object gets path from 'w' to any of the vertices that were visited
   *     on the pathV
   *   -Shortetst Common Ancestor is whatever vertex is the stopVertex from pathW
   *  -length is then determined from the distances from paths to sca
   * 
   * @param 2 int variables 'v' and 'w' representing 2 vertices on Digraph
   * @return int variable representing the associated length
   */ 
  public int length( int v, int w ){
    BFSPath pathV = new BFSPath( graph, v );
    BFSPath pathW = new BFSPath( graph, w, pathV.getVisitedVertices() );
    int sca = pathW.getStopVertex();
    return pathV.distTo( sca ) + pathW.distTo( sca );
  }
  
  /* Method for returning the shortest common ancestor between 2 vertices
   *  -creates 2 BFSPath objects for each path
   *  -calls on the "getStopVertex" method in the BFSPath class from pathW
   * 
   * @param 2 int variables 'v' and 'w' representing 2 vertices on Digraph
   * @return int variable representing the vertex for Shortest Common Ancestor
   */
  public int ancestor( int v, int w ) {
    BFSPath pathV = new BFSPath( graph, v );
    BFSPath pathW = new BFSPath( graph, w, pathV.getVisitedVertices() );
    return pathW.getStopVertex();
  }
  
  /* Overloaded method for returning the length of path containing Shortest 
   * Common Ancestor between 2 lists of Vertices 
   *  -creates 2 BFSPath objects for each path
   *  -creates a queue that stores the common ancestors from both sets
   *  -compares the paths of all the ancestors and returns the length
   *   of the shortest path
   * 
   * @param 2 lists of int variables (representing vertices)
   * @return int variable representing the length of the shortest common
   *         ancestral path
   */
  public int length( Iterable<Integer> subsetA, Iterable<Integer> subsetB ) {
    BFSPath pathA = new BFSPath( graph, subsetA );
    BFSPath pathB = new BFSPath( graph, subsetB );
    
    Queue<Integer> q = new Queue<Integer>();
    for ( int i : pathA.getVisitedVertices() ) {
      for ( int j : pathB.getVisitedVertices() ) {
        if ( i == j ) {
          q.enqueue( i );
          break;
        }
      }
    }
    int sca = root;
    int scaPathLength = graph.V();
    for ( int i : q ) {
      int pathLength = pathA.distTo( i ) + pathB.distTo( i );
      if ( pathLength < scaPathLength ) {
        sca = i;
        scaPathLength = pathLength;
      }
    }
    return scaPathLength;
  }
  
  /* Overloaded method for finding the Shortest Common Ancestor
   *  -creates 2 BFSPath objects for each path
   *  -creates a queue that stores the common ancestors from both sets
   *  -compares the paths of all the ancestors and returns the shortest
   *   common ancestor
   * 
   * @param 2 lists of int variables (representing vertices)
   * @return int variable representing the vertex for Shortest Common Ancestor 
   */
  public int ancestor( Iterable<Integer> subsetA, Iterable<Integer> subsetB ) {
    BFSPath pathA = new BFSPath( graph, subsetA );
    BFSPath pathB = new BFSPath( graph, subsetB );
    
    Queue<Integer> q = new Queue<Integer>();
    for ( int i : pathA.getVisitedVertices() ) {
      for ( int j : pathB.getVisitedVertices() ) {
        if ( i == j ) {
          q.enqueue( i );
          break;
        }
      }
    }
    int sca = root;
    int scaPathLength = graph.V();
    for ( int i : q ) {
      int pathLength = pathA.distTo( i ) + pathB.distTo( i );
      if ( pathLength < scaPathLength ) {
        sca = i;
        scaPathLength = pathLength;
      }
    }
    return sca;
  }
  
  /* Method for setting the root of the Digraph
   * 
   * @param int variable representing the root of the Digraph
   */
  public void setRoot( int root ) {
    this.root = root;
  }
  
  //Created class for obtaining the path needed via BFS
  private class BFSPath {
    HashSet<Integer> visited;
    //LinearProbingHashST<Integer, Integer> edgeTo;
    LinearProbingHashST<Integer, Integer> distTo;
    int stopVertex = root;
    
    /* Constructor for BFSPath object
     *  -Uses 3 Data Structures:
     *    -HashSet for keeping track of the vertices that have been visited
     *    -Linear-Probing Hash Set for "edgeTo" instead of array representation
     *    -Linear-Probing Hash Set for "distTo" instead of array representation
     * 
     * -It then calls the bfs method
     * @param Digraph object and int variable representing source vertex
     */
    public BFSPath( Digraph graph, int source ) {
      visited = new HashSet<Integer>();
      //edgeTo = new LinearProbingHashST<Integer, Integer>();
      distTo = new LinearProbingHashST<Integer, Integer>();
      
      bfs( graph, source );
    }
    
    /* Overloaded constructor for BFSPath object
     *  -Same Data Structures as normal constructor
     *  -This allows an iterable list of sources to be used in forming path
     *  -Calls on bfs method
     * 
     * @param Digraph object and iterable list of vertices "sources"
     */
    public BFSPath( Digraph graph, Iterable<Integer> sources ) {
      visited = new HashSet<Integer>();
      //edgeTo = new LinearProbingHashST<Integer, Integer>();
      distTo = new LinearProbingHashST<Integer, Integer>();
      
      bfs( graph, sources );
    }
    
    /* Overloaded constructor for BFSPath object
     *  -Same Data Structures as normal constructor
     *  -This constructor allows a single source vertex to form a path towards
     *   a iterable list of "stopVertices" as the destination for the path
     * 
     * -This method calls on bfsStopAt Method
     * @param Digraph object, int variable rep. source vertex, Integer list of
     *        target vertices
     */
    public BFSPath( Digraph graph, int source, Iterable<Integer> stopVertices ) {
      visited = new HashSet<Integer>();
      //edgeTo = new LinearProbingHashST<Integer, Integer>();
      distTo = new LinearProbingHashST<Integer, Integer>();
      
      bfsStopAt( graph, source, stopVertices );
    }
    
    /* Method for implementing the Breadth-First Search algorithm for finding 
     * the paths
     *   -Uses a queue data structure that starts by putting the source vertex
     *    on the queue and finding the adjacent vetices to it. It then puts 
     *    those vertices on the queue and repeats the process till the queue is
     *    empty (while keeping track of distances from vertices and what edges
     *    where taken)
     *
     * @param Digraph object and an int variable representing the source vertex
     */
    private void bfs( Digraph graph, int source ) {
      Queue<Integer> q = new Queue<Integer>();
      visited.add( source );
      distTo.put( source, 0 );
      q.enqueue( source );
      while ( !q.isEmpty() ) {
        int v = q.dequeue();
        for ( int w : graph.adj( v ) ) {
          if ( !visited.contains( w ) ) {
            //edgeTo.put( w, v );
            distTo.put( w, distTo.get( v ) + 1 );
            visited.add( w );
            q.enqueue( w );
          }
        }
      }
    }
    
     /* Overloaded Method for implementing the Breadth-First Search algorithm 
      * for finding the paths
     *   -Uses a queue data structure that starts by iterating and putting the 
     *    source vertices onto the queue. Then runs a while loop to find the 
     *    adjacent vetices to each of the vertices in the queue. It then puts 
     *    those vertices on the queue and repeats the process till the queue is
     *    empty (while keeping track of distances from vertices and what edges
     *    where taken)
     *
     * @param Digraph object and an interable list of Integer variables 
     *        representing the source vertices
     */
    private void bfs( Digraph graph, Iterable<Integer> sources ) {
      Queue<Integer> q = new Queue<Integer>();
      for ( int i : sources ) {
        visited.add( i );
        distTo.put( i, 0 );
        q.enqueue( i );
      }
      while ( !q.isEmpty() ) {
        int v = q.dequeue();
        for ( int w : graph.adj( v ) ) {
          if ( !visited.contains( w ) ) {
            //edgeTo.put( w, v );
            distTo.put( w, distTo.get( v ) + 1 );
            visited.add( w );
            q.enqueue( w );
          }
        }
      }
    }
    
    /* Runs BFS from an inputted source vertex to an interable
     * list of stopping point vertices labeled "stopVertices" 
     *  -uses similar mmethods of implementation as above method using BFS to
     *   find the path from the source vertex to end vertices
     * 
     * @param Digraph object, int varaible representing source vertex, and an 
     *        interable list of Integer variables representing the source 
     *        vertices
     */
    private void bfsStopAt( Digraph graph, int source, Iterable<Integer> stopVertices ) {
      Queue<Integer> q = new Queue<Integer>();
      HashSet<Integer> stopVerticesSet = new HashSet<Integer>();
      for ( int i : stopVertices ) {
        stopVerticesSet.add( i );
      }
      visited.add( source );
      distTo.put( source, 0 );
      q.enqueue( source );
      while ( !q.isEmpty() ) {
        int v = q.dequeue();
        if ( stopVerticesSet.contains( v ) ) {
          stopVertex = v;
          break;
        }
        for ( int w : graph.adj( v ) ) {
          if ( !visited.contains( w ) ) {
            //edgeTo.put( w, v );
            distTo.put( w, distTo.get( v ) + 1 );
            visited.add( w );
            q.enqueue( w );
          }
        }
      }
    }
    
    //Method for returning the stopVertex
    public int getStopVertex() {
      return stopVertex;
    }
    
    //Method for returing an interable list of the vertices that have been 
    //visisted upon traversal 
    public Iterable<Integer> getVisitedVertices() {
      Queue<Integer> iterable = new Queue<Integer>();
      Iterator<Integer> iterator = visited.iterator();
      while ( iterator.hasNext() ) {
        iterable.enqueue( iterator.next() );
      }
      return iterable;
    }
    
    /*Method for returning the distance to a vertex v
     * 
     * @param int variable representing a vertex
     * @return int value representing the distance to vertex v
     */
    public int distTo( int v ) {
      return distTo.get( v );
    }
  }
  
  //Test Driver
  public static void main( String[] args ) {
      //Change file for personal testing
    ShortestCommonAncestor s = new ShortestCommonAncestor( new Digraph( new In( "yourExamplesHere.txt" ) ) );
     //Change what is queued in each for testing
    Queue<Integer> a = new Queue<Integer>();
    a.enqueue( 13 );
    a.enqueue( 23 );
    a.enqueue( 24 );
    Queue<Integer> b = new Queue<Integer>();
    b.enqueue( 6 );
    b.enqueue( 16 );
    b.enqueue( 17 );

    StdOut.println("Length: " + s.length( a, b ) );
  }
}