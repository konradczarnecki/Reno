package konra.reno.util;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;

@Service
public class FileService {

    private String appDir;

    public FileService(@Value("${blockchain.dir}") String blockchainDir){

        this.appDir = FileUtils.getUserDirectoryPath() + "/" + blockchainDir;
    }

    public void writeTxtFile(String file, String data, OpenOption... option) {

        Path p = Paths.get(appDir, file);
        try(BufferedWriter bw = Files.newBufferedWriter(p, option)) {
            bw.write(data);

        } catch (IOException e) {}
    }

    public void appendTxtFile(String file, String data) {

        writeTxtFile(file, data, StandardOpenOption.APPEND);
    }

    public void overwriteTxtFile(String file, String data) {

        writeTxtFile(file, data, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
    }

    public String readTextFile(String file) throws IOException {

        return FileUtils.readFileToString(new File(appDir, file));
    }

    @SneakyThrows
    public boolean overwriteLine(String file, String key, String newLine) {

        try(BufferedReader br = Files.newBufferedReader(Paths.get(appDir, file));
            BufferedWriter bw = Files.newBufferedWriter(Paths.get(appDir, file + "_tmp"))){

            String line;
            while((line = br.readLine()) != null) {
                if(line.contains(key)) bw.write(newLine + "\n");
                else bw.write(line + "\n");
            }
        }

        return true;
    }

    public String find(String file, String key) {

        try(BufferedReader br = Files.newBufferedReader(Paths.get(appDir, file))){

            String line;
            while((line = br.readLine()) != null)
                if(line.contains(key)) return line;

        } catch (IOException e) {}

        return null;
    }
}
