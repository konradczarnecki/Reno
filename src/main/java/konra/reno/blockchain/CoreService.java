package konra.reno.blockchain;

import konra.reno.util.Crypto;
import konra.reno.util.FileService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
public class CoreService {

    private static final Logger log = LoggerFactory.getLogger(CoreService.class);

    private FileService fileService;
    private Crypto crypto;
    private BlockRepository blockRepository;

    private Map<String, String> config;
    private ScheduledExecutorService exec;

    @Getter private long headBlockId;
    private Runnable syncCallback;

    @Autowired
    public CoreService(Crypto crypto, FileService fileService, BlockRepository blockRepository) {

        this.crypto = crypto;
        this.fileService = fileService;
        this.blockRepository = blockRepository;
        this.config = new HashMap<>();
        exec  = Executors.newScheduledThreadPool(10);
    }

    @Transactional
    public boolean startBlockchain() {

        Block initialBlock = new Block(null);
        blockRepository.save(initialBlock);
        headBlockId = 1;

        return true;
    }

    public void registerSyncCallback(Runnable callback) {

        syncCallback = callback;
    }

    public void setHeadBlockId(long id) {

        headBlockId = id;
        syncCallback.run();
    }

    public List<Block> getBlocks(long fromId, long toId) {

        return blockRepository.findBlocksByIdBetween(fromId, toId);
    }
}
