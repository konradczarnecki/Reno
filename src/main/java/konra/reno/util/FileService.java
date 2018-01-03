package konra.reno.util;

import konra.reno.account.Account;
import konra.reno.blockchain.Block;
import konra.reno.blockchain.ChainChunk;
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

    public long chunkCount() throws IOException {

        return Files.list(Paths.get(appDir)).count();
    }

    public void saveChunk(ChainChunk chunk) throws IOException {

        Path chunkPath = Paths.get(appDir, "chunk_" + chunk.getId());
        BufferedWriter bw = Files.newBufferedWriter(chunkPath);

        bw.write("Chunk " + chunk.getId());
        bw.newLine();

        bw.write(chunk.getState().getVal() + " ");
        bw.write(chunk.getBlocks().size());
        bw.newLine();

        bw.write(chunk.getPreviousChunkHash() + " ");
        bw.write(chunk.hash());

        for(Block b: chunk.getBlocks()) bw.write(b.data());
        bw.close();
    }

    public ChainChunk readChunk(int chunkId) throws IOException {

        ChainChunk chunk = new ChainChunk();
        chunk.setId(chunkId);

        Path chunkPath = Paths.get(appDir, "chunk_" + chunkId);
        BufferedReader br = Files.newBufferedReader(chunkPath);

        String header = br.readLine();
        String secondLine = br.readLine();

        int blockCount = 256;
        if(secondLine.substring(0,1).equals("H")) {
            blockCount = Integer.parseInt(secondLine.split(" ")[1]);
            chunk.setState(ChainChunk.ChunkState.HEAD);

        } else chunk.setState(ChainChunk.ChunkState.COMPLETE);

        for(int i = 0; i < blockCount; i++) {

            StringBuilder sb = new StringBuilder();
            for(int j = 0; j < 4; j++) sb.append(br.readLine()).append("\n");
            Block b = Block.parse(sb.toString());
            chunk.getBlocks().add(b);
        }

        return chunk;
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
