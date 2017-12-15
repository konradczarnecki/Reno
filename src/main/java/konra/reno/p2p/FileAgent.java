package konra.reno.p2p;


import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.hive2hive.core.file.IFileAgent;


public class FileAgent implements IFileAgent {

    public static String appDir;

    private final File root;

    public FileAgent() {

        root = new File(FileUtils.getUserDirectory(), ".reno_blockchain");
        appDir = root.getAbsolutePath();
        if(!root.exists()) root.mkdirs();
    }

    @Override
    public File getRoot() {
        return root;
    }

    @Override
    public void writeCache(String key, byte[] data) throws IOException {
        // do nothing as examples don't depend on performance
    }

    @Override
    public byte[] readCache(String key) throws IOException {
        // do nothing as examples don't depend on performance
        return null;
    }

}