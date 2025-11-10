import java.util.ArrayList;
public interface IReadData {
    boolean openDataFile(String fileName); 
    boolean readDataFile();
    ArrayList<String> getRawDataList();
}
