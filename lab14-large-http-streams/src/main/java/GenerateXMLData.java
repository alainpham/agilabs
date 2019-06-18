import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class GenerateXMLData {

	public static void main(String[] args) throws IOException {
		
		byte[] mockData = Files.readAllBytes(Paths.get("/home/workdrive/TAZONE/WORKSPACES/ws-agilabs/lab14-large-http-streams/src/data/root.xml"));
		String xml = new String(mockData);
		String block = xml.substring(xml.indexOf("<root>")+"<root>".length(),xml.indexOf("</root>"));
		System.out.println(block.substring(0, 30));
		System.out.println(block.substring(block.length()-30, block.length()));
		long totalBytes = 0;
		//create a 700MB file
		Files.write(Paths.get("./../700MB.xml"), "<root>".getBytes(), StandardOpenOption.CREATE);
		while (totalBytes < (1024*1024*700)){
			Files.write(Paths.get("./../700MB.xml"), block.getBytes(), StandardOpenOption.APPEND);
			totalBytes+=mockData.length;
	    }
		Files.write(Paths.get("./../700MB.xml"), "\n</root>".getBytes(), StandardOpenOption.APPEND);
	}

}
