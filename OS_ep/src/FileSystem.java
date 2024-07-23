import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileSystem {
    private Map<String, File> files;
    private List<File> fileList;

    public FileSystem() {
        files = new HashMap<>();
        fileList = new ArrayList<>();
    }

    public String createFile(String name, int size, int physicalLocation) {
        if (files.containsKey(name)) {
            return "File already exists.";
        } else {
            File file = new File(name, size, physicalLocation);
            files.put(name, file);
            fileList.add(file);
            return "File " + name + " created.";
        }
    }

    public String deleteFile(String name) {
        if (files.containsKey(name)) {
            File file = files.remove(name);
            fileList.remove(file);
            return "File " + name + " deleted.";
        } else {
            return "File not found.";
        }
    }

    public File getFile(String name) {
        return files.get(name);
    }

    public String listFiles() {
        StringBuilder sb = new StringBuilder();
        for (File file : fileList) {
            sb.append("File ID: ").append(file.getId())
                    .append(", Name: ").append(file.getName())
                    .append(", Size: ").append(file.getSize())
                    .append(", Physical Location: ").append(file.getPhysicalLocation())
                    .append("\n");
        }
        return sb.toString();
    }

    public List<File> getFiles() {
        return fileList;
    }
}
