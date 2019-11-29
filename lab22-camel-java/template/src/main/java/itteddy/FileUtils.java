package itteddy;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class FileUtils implements Processor {

    public static File getAsFile(final String fileName) {
        return new File(fileName);
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        String fileName = exchange.getIn().getHeader("fileName",String.class);
        File f = new File(fileName);
        Path path = f.toPath();
        String mimeType = Files.probeContentType(path);
        exchange.getIn().setBody(f);
        exchange.getIn().setHeader("Content-Type",mimeType);
    }
}