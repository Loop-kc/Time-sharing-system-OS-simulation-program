public class MemoryBlock {
    private int start;
    private int size;
    private String usedBy;

    public MemoryBlock(int start, int size, String usedBy) {
        this.start = start;
        this.size = size;
        this.usedBy = usedBy;
    }

    public int getStart() {
        return start;
    }

    public int getSize() {
        return size;
    }

    public String getUsedBy() {
        return usedBy;
    }
}
