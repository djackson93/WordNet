/******************************************************************************
 * Compilation:  javac WordNet.java
 * Dependencies: ShortestCommonAncestor.java
 * ----------------------------------------------------------------------------
 * Program takes in a String containing information about sets of nouns and
 * their synonyms and a string outlining the relationship between the sets.
 * It creates a Digraph representation of these sets and relationships and then
 * calls on the ShortestCommonAncestor.java Program.
 * 
 * Author:@Julian Ceja, @Dakota Jackson
 * ***************************************************************************/
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.LinearProbingHashST;
import java.util.Arrays;
import java.util.LinkedList;


public class WordNet {
  
  private LinearProbingHashST<Integer, String> synsetTable;
  private ShortestCommonAncestor SCA;
  private String[] nouns;
  
  /* Constructor method for WordNet object
   *  -Takes in 2 String variables (read from text files)
   *  -Throws "NullPointerException" if either string is null
   */
  public WordNet( String synsets, String hypernyms ) {
    if ( synsets == null || hypernyms == null ) 
      throw new NullPointerException();
    
    //Block of code: Reads synsets and stores them in hashing table
    synsetTable = new LinearProbingHashST<Integer, String>();
    In synsetsInput = new In( synsets );
    int nounCount = 0;
    /* Loop to put an array representation of a synset (group of nouns)
     * at each index of the table
     *  -SynsetInfo = String array with SynsetInfo[1] representing the noun
     *                and SynsetInfo[0] representing the attatched Synset id
     *  -Also keeps track of the amount of total nouns through int nounCount
     */ 
    while ( !synsetsInput.isEmpty() ) {
      String[] synsetInfo = synsetsInput.readLine().split( "," );
      String[] synsetNouns = synsetInfo[1].split( " " );
      synsetTable.put( Integer.valueOf( synsetInfo[0] ), synsetInfo[1] );
      nounCount += synsetNouns.length;
    }
    synsetsInput.close();
    
    // Reads hypernyms and creates graph based on those relationships
    Digraph graph = new Digraph( synsetTable.size() );
    In hypernymsInput = new In( hypernyms );
    int root = -1;
     /* Loop to put hypernyms as appropriate edges in created Digraph
      *  -Format of given text has hypernyms seperated by a ","
      */
    while ( !hypernymsInput.isEmpty() ) {
      String[] hypernymsInfo = hypernymsInput.readLine().split( "," );
      int v = Integer.parseInt( hypernymsInfo[0] );
      if ( hypernymsInfo.length == 1 ) root = v;
      for ( int i = 1; i < hypernymsInfo.length; i++ ) {
        int w = Integer.parseInt( hypernymsInfo[i] );
        graph.addEdge( v, w );
      }
    }
   //Creates ShortestCommonAncestor object using created graph for constructor
    SCA = new ShortestCommonAncestor( graph );
    SCA.setRoot( root );
    //Calls on extractNouns method using the aforementioned variable nounCount
    extractNouns( nounCount );
  }
  
  //Returns an iterable list of the nouns
  public Iterable<String> nouns() {
    return Arrays.asList( nouns );
  }
  
  /* Method for checking if an inputted string is a noun within the WordNet
   *  -Uppercases the given word to alleviate casing issues
   *  -Uses Binary Search algorithm to give logarithmic time complexity
   *    -"split" method to grab just noun part of concatenated string
   * 
   * @param String object "word"
   * @return Boolean value based off if the noun is in the WordNet
   */
  public boolean isNoun( String word ) {
    //word = word.toUpperCase();
    int lo = 0;
    int hi = nouns.length - 1;
    while ( lo <= hi ) {
      int mid = ( lo + hi ) / 2;
      String[] noun = nouns[mid].split( " " );
      if ( word.compareTo( noun[0] ) < 0 ) hi = mid - 1;
      else if ( word.compareTo( noun[0] ) > 0 ) lo = mid + 1;
      else return true;
    }
    return false;
  }
  
  //Method for returning shortest common ancestor as a string
  public String sca( String noun1, String noun2 ) {
    int sca = SCA.ancestor( synsetIDs( noun1 ), synsetIDs( noun2 ) );
    return synsetTable.get( sca );
  }
  
  // Method for returning the shortest distance between the two nouns
  public int distance( String noun1, String noun2 ) {
    return SCA.length( synsetIDs( noun1 ), synsetIDs( noun2 ) );
    
  }
  
  
  //Returns ShortestCommonAncestor object
  public ShortestCommonAncestor getSCA() {
    return SCA;
  }
  
  /* Method for extracting the Nouns out of the Synsets stored in the table
   * -Outer loop iterates through the index of the table 
   * -Inner loop interates through the array of nouns in the synset at index
   *   -Set to uppercase to alleviate any casing issues on searching for nouns
   *   -The "+i" concatenates the Synset id to the noun for easy tracking
   * 
   * @param int variable representing the amount of nouns in the WordNet
   */
  private void extractNouns( int nounCount ) {
    nouns = new String[nounCount];
    for ( int k = 0, i = 0; i < synsetTable.size(); i++ ) {
      String[] currentSynset = synsetTable.get( i ).split( " " );
      for ( int j = 0; j < currentSynset.length; j++, k++ ) {
        nouns[k] = currentSynset[j] + " " + i;
      }
    }
    Arrays.sort( nouns );
  }
  
  // Method for getting Synset id by splitting concatented string at space
  // and then grabbing the 2nd index of string array post-split
  public Iterable<Integer> synsetIDs( String noun ) {
    if ( !isNoun( noun ) ) throw new IllegalArgumentException( "Noun not in WordNet" );
    
    // noun = noun.toUpperCase();
    LinkedList<Integer> synsetIDs = new LinkedList<Integer>();
    int lo = 0;
    int hi = nouns.length - 1;
    while ( lo <= hi ) {
      int mid = ( lo + hi ) / 2;
      String[] nounInfo = nouns[mid].split( " " );
      if ( noun.compareTo( nounInfo[0] ) < 0 ) hi = mid - 1;
      else if ( noun.compareTo( nounInfo[0] ) > 0 ) lo = mid + 1;
      else {
        for ( int i = mid; noun.equals( nounInfo[0] ); nounInfo = nouns[--i].split( " " ) ) {
          synsetIDs.add( Integer.valueOf( nounInfo[1] ) );
        }
        nounInfo = nouns[mid + 1].split( " " );
        for ( int i = mid + 1; noun.equals( nounInfo[0] ); nounInfo = nouns[++i].split( " " ) ) {
          synsetIDs.add( Integer.valueOf( nounInfo[1] ) );
        }
        break;
      }
    }
    return synsetIDs;
  }
  
  // Testing Driver
  public static void main( String[] args ) {
    WordNet wn = new WordNet( "synsets.txt", "hypernyms.txt" );
    StdOut.println("distance: " +  wn.distance( "district", "geographic_area" ) );
    StdOut.println("Shortest Common Ancestor: " + wn.sca( "district", "geographic_area" ) );
    
  }
}

