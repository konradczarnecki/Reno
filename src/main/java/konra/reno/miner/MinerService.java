package konra.reno.miner;

import konra.reno.core.block.Block;
import konra.reno.core.CoreService;
import konra.reno.core.callback.CallbackHandler;
import konra.reno.core.callback.CallbackType;
import konra.reno.core.block.BlockConfiguration;
import konra.reno.miner.txpicker.TxPicker;
import konra.reno.transaction.Transaction;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class MinerService {

    CoreService core;
    TxPicker picker;
    ScheduledExecutorService exec;

    boolean doMine;
    Block minedBlock;

    @Autowired
    public MinerService(CoreService core,
                        TxPicker picker) {

        this.core = core;
        this.picker = picker;
        this.exec = Executors.newScheduledThreadPool(10);
        core.getCallbackHandler().register(CallbackType.MINE_NEW_BLOCK, this::restartMining);
    }

    public void startMining(String minerAddress, String message) {

        Block head = core.getHeadBlock();
        minedBlock = new Block(head);
        minedBlock.setMiner(minerAddress);
        minedBlock.setMessage(message);

        Set<Transaction> txs = picker.pick(core.getTransactionPool().txsInPool());
        minedBlock.setTransactions(txs);

        doMine = true;
        exec.execute(this::mine);
    }

    private void mine() {

        while(doMine) {

            minedBlock.bumpNonce();

            if(minedBlock.prove(core.getBlockConfiguration().getDifficulty(minedBlock))){
                core.processNewBlocks(Collections.singletonList(minedBlock));
                break;
            }
        }
    }

    public void stopMining() {

        doMine = false;
    }

    public void restartMining() {

        if(!doMine) return;
        stopMining();
        startMining(minedBlock.getMiner(), minedBlock.getMessage());
    }
}
