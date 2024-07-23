import java.util.ArrayList;
import java.util.List;

public class DiskManager {
    private int size;
    private int[] disk;
    private List<DiskBlock> diskUsage;
    private List<DiskBlock> freeSpaceTable;

    public DiskManager(int size) {
        this.size = size;
        this.disk = new int[size];
        this.diskUsage = new ArrayList<>();
        this.freeSpaceTable = new ArrayList<>();
        // 初始化整个磁盘空间为一个空闲块
        freeSpaceTable.add(new DiskBlock(0, size, "Free"));
    }

    public int getSize() {
        return size;
    }

    public int allocate(int size) {
        for (DiskBlock block : freeSpaceTable) {
            if (block.getSize() >= size) {
                int start = block.getStart();
                // 更新空闲分区表
                if (block.getSize() > size) {
                    freeSpaceTable.add(new DiskBlock(block.getStart() + size, block.getSize() - size, "Free"));
                }
                freeSpaceTable.remove(block);
                diskUsage.add(new DiskBlock(start, size, "Used"));
                // 更新磁盘数组
                for (int i = start; i < start + size; i++) {
                    disk[i] = 1;
                }
                return start;
            }
        }
        return -1; // 没有足够的连续空间
    }

    public void free(int address, int size) {
        // 更新磁盘数组
        for (int i = address; i < address + size; i++) {
            disk[i] = 0;
        }
        diskUsage.removeIf(block -> block.getStart() == address && block.getSize() == size);
        // 更新空闲分区表
        freeSpaceTable.add(new DiskBlock(address, size, "Free"));
        // 合并相邻的空闲块
        mergeFreeBlocks();
    }

    private void mergeFreeBlocks() {
        freeSpaceTable.sort((a, b) -> Integer.compare(a.getStart(), b.getStart()));
        List<DiskBlock> merged = new ArrayList<>();
        DiskBlock previous = null;
        for (DiskBlock block : freeSpaceTable) {
            if (previous == null) {
                previous = block;
            } else {
                if (previous.getStart() + previous.getSize() == block.getStart()) {
                    previous = new DiskBlock(previous.getStart(), previous.getSize() + block.getSize(), "Free");
                } else {
                    merged.add(previous);
                    previous = block;
                }
            }
        }
        if (previous != null) {
            merged.add(previous);
        }
        freeSpaceTable = merged;
    }

    public String show() {
        StringBuilder sb = new StringBuilder();
        for (DiskBlock block : diskUsage) {
            sb.append("Used - Start: ").append(block.getStart()).append(", Size: ").append(block.getSize()).append("\n");
        }
        for (DiskBlock block : freeSpaceTable) {
            sb.append("Free - Start: ").append(block.getStart()).append(", Size: ").append(block.getSize()).append("\n");
        }
        return sb.toString();
    }
}
