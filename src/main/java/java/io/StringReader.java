package java.io;

public class StringReader extends Reader {
    private final char[] charArray;
    private int where;
    public StringReader(String start) {
        this.charArray = start.toCharArray();
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if ((off < 0) || (off > cbuf.length) || (len < 0) ||
                ((off + len) > cbuf.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }
        if (where == charArray.length)
            return -1;

        int size = Math.min(charArray.length - where, len);

        System.arraycopy(charArray, where, cbuf, off, size);

        where += size;
        return size;
    }

    @Override
    public void close() {

    }
}
