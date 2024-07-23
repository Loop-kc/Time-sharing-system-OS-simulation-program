public class File {
    private static int counter = 0;
    private int id;
    private String name;
    private int size;
    private int physicalLocation;

    public File(String name, int size, int physicalLocation) {
        this.id = counter++;
        this.name = name;
        this.size = size;
        this.physicalLocation = physicalLocation;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public int getPhysicalLocation() {
        return physicalLocation;
    }

    public static void resetCounter() {
        counter = 0;
    }
}
