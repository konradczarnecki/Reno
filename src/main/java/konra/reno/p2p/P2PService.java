package konra.reno.p2p;

import org.apache.commons.io.FileUtils;
import org.hive2hive.core.api.H2HNode;
import org.hive2hive.core.api.configs.FileConfiguration;
import org.hive2hive.core.api.configs.NetworkConfiguration;
import org.hive2hive.core.api.interfaces.*;
import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.hive2hive.core.exceptions.NoSessionException;
import org.hive2hive.processframework.ProcessState;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.hive2hive.processframework.interfaces.IProcessComponent;
import org.hive2hive.core.security.UserCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;


@Service
public class P2PService {

    private static String host;
    private static final Logger log = LoggerFactory.getLogger(P2PService.class);

    private IH2HNode node;
    private IProcessComponent downloading;

    public P2PService(){
    }

    public boolean bootstrap() throws UnknownHostException {

        INetworkConfiguration netConfig;
        if(host.equals("initial")) netConfig = NetworkConfiguration.createInitial();
        else netConfig = NetworkConfiguration.create(InetAddress.getByName(host)).setBootstrapPort(4622);

        IFileConfiguration fileConfig = FileConfiguration.createDefault();
        node = H2HNode.createNode(fileConfig);
        boolean connected = node.connect(netConfig);

        if(connected) try {

            log.info("connected");

            if(!node.getUserManager().isRegistered("common")){

                log.info("registering common user");

                UserCredentials credentials = new UserCredentials("common", "common", "1234");
                node.getUserManager().createRegisterProcess(credentials).execute();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }

        return connected;
    }

    public boolean register(String id, String password, String pin){

        IUserManager userManager = node.getUserManager();
        UserCredentials credentials = new UserCredentials(id, password, pin);
        boolean registered = false;

        try {
            IProcessComponent<Void> register = userManager.createRegisterProcess(credentials);
            register.execute();
            registered = userManager.isRegistered(id);

        } catch(Exception e){

            e.printStackTrace();
        }

        return registered;
    }

    public boolean login(String id, String password, String pin){

        IUserManager userManager = node.getUserManager();
        UserCredentials credentials = new UserCredentials(id, password, pin);
        boolean logged = false;

        try {
            IProcessComponent<Void> login = userManager.createLoginProcess(credentials, new FileAgent());
            login.execute();
            logged = userManager.isLoggedIn();

        } catch(Exception e){

            e.printStackTrace();
        }

        return logged;
    }

    public boolean loginCommon(){

        return login("common", "common", "1234");
    }

    public boolean logout(){

        IUserManager userManager = node.getUserManager();
        boolean loggedOut = false;

        try {
            userManager.createLogoutProcess().execute();
            loggedOut = !userManager.isLoggedIn();

        } catch(Exception e){

            e.printStackTrace();
        }

        return loggedOut;
    }

    public boolean addTextFile(String name, String content){

        IFileManager fileManager = node.getFileManager();
        File file = new File(new FileAgent().getRoot(), name);

        try {
            FileUtils.write(file, content);
            fileManager.createAddProcess(file).execute();

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean addFile(String name){

        IFileManager fileManager = node.getFileManager();
        File file = new File(new FileAgent().getRoot(), name);

        try {
            fileManager.createAddProcess(file).execute();

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean updateFile(String name){

        IFileManager fileManager = node.getFileManager();
        File file = new File(new FileAgent().getRoot(), name);

        try {
            fileManager.createUpdateProcess(file).execute();

        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }

        return true;
    }

    public boolean downloadFile(String name){

        IFileManager fileManager = node.getFileManager();
        File file = new File(new FileAgent().getRoot(), name);

        try {
            downloading = fileManager.createDownloadProcess(file);
            downloading.execute();

        } catch (Exception e) {

//            e.printStackTrace();
        }

        return true;
    }

    public boolean checkProgress(){

        boolean finished = downloading.getState() == ProcessState.EXECUTION_SUCCEEDED;
        if(finished) downloading = null;
        return finished;
    }

    static void setHost(String host){

        P2PService.host = host;
    }

}
