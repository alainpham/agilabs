import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class GenerateCSVData {

	public static void main(String[] args) throws IOException {
		
		byte[] mockData = Files.readAllBytes(Paths.get("/home/workdrive/TAZONE/WORKSPACES/ws-agilabs/lab14-large-http-streams/src/data/MOCK_DATA.csv"));
		long totalBytes = 0;
		//create a 700MB file
		while (totalBytes < (1024*1024*700)){
			Files.write(Paths.get("./../700MB.csv"), mockData, StandardOpenOption.APPEND,StandardOpenOption.CREATE);
			totalBytes+=mockData.length;
	    }
	}

}
