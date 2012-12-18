package document;

/**
 * This is our concurrent, thread safe string buffer
 * 
 * Thread safe argument:  In our implementation, none of the data is shared.  Therefore, everything is thread safe from a 
 * data perspective.  However, the algorithm that is run locally will ensure that the documents at all ends
 * are convergent to the same string. 
 * 
 * Rep invariant:  none
 * @author Hanwen Xu
 *
 */
public interface ConcurrentBuffer {

    /**
     * Modifies this buffer by adding the string
     * @param pos integer, requires to be 0<=pos<=length
     * @param s string to insert
     */
    public void insert(int pos, String s);
    
    /**
    * Modifies this by deleting a substring
    * @param pos start of substring to delete
    * (requires 0 <= pos <= current buffer length)
    * @param len length of substring to delete
    * (requires 0 <= len <= current buffer length - pos)
    */
    public void delete(int pos, int len);
    /**
    * @return length of text sequence in this edit buffer
    */
    public int length();
    /**
    * @return content of this edit buffer
    */
    public String toString();
    
}
