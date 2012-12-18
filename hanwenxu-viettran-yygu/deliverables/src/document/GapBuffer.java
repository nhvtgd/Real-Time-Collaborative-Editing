package document;

/**
 * This is the implementation of the ConcurrentBuffer
 * @author Hanwen Xu
 *
 */
public class GapBuffer implements ConcurrentBuffer {
    private char[] a;
    private int gapStart;
    private int gapLength;
    // Rep invariant:
    // a != null
    // 0 <= gapStart <= a.length
    // 0 <= gapLength <= a.length - gapStart
    // Abstraction function:
    // represents the sequence a[0],...,a[gapStart-1], // a[gapStart+gapLength],...,a[length-1]
    
    @Override
    public void insert(int pos, String s) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(int pos, int len) {
        // TODO Auto-generated method stub

    }

    @Override
    public int length() {
        // TODO Auto-generated method stub
        return 0;
    }


}
