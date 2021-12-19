/******************************************************************************
 * Compilation:  javac Outcast.java
 * Dependencies: WordNet.java
 * ----------------------------------------------------------------------------
 * Program takes in a list of WordNet nouns and determines the "outcast" of the 
 * set (by finding the distances between the nouns).
 * 
 * Author:@Julian Ceja, @Dakota Jackson
 * ***************************************************************************/
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
  
  private WordNet wordnet;
  
  //Constructor for creating a Outcast object taking in a WordNet object as input
  public Outcast( WordNet wordnet ) {
    this.wordnet = wordnet;
  }
  
  /* Method for returning the Outcast noun of a set
   * -Finds the outcast by computing the sum of distances between each noun and
   *  all the others
   *
   * @param String array of nouns
   * @return String variable representing the outcacst noun
   */
  public String outcast( String[] nouns ) {
    int n = nouns.length;
    int outcastDistance = 0;
    int indexOfOutcast = 0;
    
    for ( int i = 0; i < n; i++ ) {
      int d = 0;
      for ( int j = 0; j < n; j++ ) {
        if ( i != j ) {
          int l = wordnet.getSCA().length( wordnet.synsetIDs( nouns[i] ), wordnet.synsetIDs( nouns[j] ) );
          d += l;
        }
      }
      if ( d > outcastDistance ) {
        outcastDistance = d;
        indexOfOutcast = i;
      }
    }
    return nouns[indexOfOutcast];
  }
  
  //Test Driver
  public static void main( String[] args ) {
    WordNet wn = new WordNet( "synsets.txt", "hypernyms.txt" );
    Outcast oc = new Outcast( wn );
    String[] s = { "apple","pear","peach","banana","lime","lemon","blueberry","strawberry","mango","watermelon","potato" };
      StdOut.println("Outcast: " + oc.outcast( s ) );
  }
}