package konra.reno.core.block;

import konra.reno.core.CoreConfig;
import konra.reno.core.persistance.BlockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("adjustingConfig")
public class AdjustingBlockConfiguration implements BlockConfiguration {

    BlockRepository repository;
    CoreConfig config;

    @Autowired
    public AdjustingBlockConfiguration(BlockRepository repository, CoreConfig config) {
        this.repository = repository;
        this.config = config;
    }

    @Override
    public long getReward(Block block) {

        if(block.getId() < 1000) return 1000000000;
        else if(block.getId() < 100000) return 100000000;
        else return 10000000;
    }

    @Override
    public int getDifficulty(Block block) {

        if(block.getId() == 1) return 4;

        int prevBlockDiff = repository.findBlockById(block.getId() - 1).getDifficulty();
        int newDiff = prevBlockDiff;

        if(block.getId() % 10 != 0) return prevBlockDiff;

        List<Block> previousBlocks = repository.findBlocksByIdBetween(block.getId() - 10, block.getId());

        long meanBlockTime = 0;

        for(int i = 1; i < previousBlocks.size(); i++)
            meanBlockTime += previousBlocks.get(i).getTimestamp() - previousBlocks.get(i-1).getTimestamp();

        meanBlockTime = meanBlockTime / (previousBlocks.size() - 1);

        if(meanBlockTime > config.getBaseBlockTime()) {

            while(meanBlockTime / config.getBaseBlockTime() > 10) {
                newDiff--;
                meanBlockTime /= 16;
            }

        } else {

            while(config.getBaseBlockTime() / meanBlockTime > 10) {
                newDiff++;
                meanBlockTime *= 16;
            }
        }

        return newDiff;
    }
}
