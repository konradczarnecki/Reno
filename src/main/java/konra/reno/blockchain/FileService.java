package konra.reno.blockchain;

import konra.reno.p2p.FileAgent;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

@Service
public class FileService {

    public LinkedList<Block> readBlockchain(){

        LinkedList<Block> blockchain = null;

        try {

            File blockchainFile = new File(FileAgent.appDir, "blockchain");
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(blockchainFile));
            blockchain = (LinkedList<Block>) ois.readObject();
            ois.close();

        } catch (Exception e){
            e.printStackTrace();
        }

        return blockchain;
    }

    public boolean writeBlockchain(LinkedList<Block> blockchain){

        try {

            File blockchainFile = new File(FileAgent.appDir, "blockchain");
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(blockchainFile));
            oos.writeObject(blockchain);
            oos.close();

        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean addAccount(Account account){

        try {

            File usersFile = new File(FileAgent.appDir, "accounts");
            FileWriter fw = new FileWriter(usersFile, true);
            fw.write(account.getAddress() + ":" + account.getBalance() + "\n");
            fw.close();

        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public String readTextFile(String name){

        Path file = Paths.get(FileAgent.appDir, name);
        if(!Files.exists(file)) return "file_not_found";

        StringBuilder sb = new StringBuilder();

        try {
            BufferedReader br = Files.newBufferedReader(file);

            String line;
            while((line = br.readLine()) != null) sb.append(line);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
