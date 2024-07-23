import java.util.ArrayList;
import java.util.List;

public class MemoryManager {
    private int size;
    private int[] memory;
    private List<MemoryBlock> memoryUsage;

    public MemoryManager(int size) {
        this.size = size;
        this.memory = new int[size];
        this.memoryUsage = new ArrayList<>();
    }

    public int allocate(int size) {
        for (int i = 0; i <= this.size - size; i++) {
            boolean free = true;
            for (int j = 0; j < size; j++) {
                if (memory[i + j] != 0) {
                    free = false;
                    break;
                }
            }
            if (free) {
                for (int j = 0; j < size; j++) {
                    memory[i + j] = 1;
                }
                memoryUsage.add(new MemoryBlock(i, size, "Used"));
                return i;
            }
        }
        return -1;
    }

    public void free(int address, int size) {
        for (int i = address; i < address + size; i++) {
            memory[i] = 0;
        }
        memoryUsage.removeIf(block -> block.getStart() == address && block.getSize() == size);
    }

    public String show() {
        StringBuilder sb = new StringBuilder();
        for (MemoryBlock block : memoryUsage) {
            sb.append("Start: ").append(block.getStart()).append(", Size: ").append(block.getSize())
                    .append(", Used By: ").append(block.getUsedBy()).append("\n");
        }
        return sb.toString();
    }

    public int getSize() {
        return size;
    }

    public int getAvailableMemory() {
        int availableMemory = 0;
        for (int i : memory) {
            if (i == 0) {
                availableMemory++;
            }
        }
        return availableMemory;
    }
}
