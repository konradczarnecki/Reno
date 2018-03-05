package konra.reno.core.block;

import org.springframework.stereotype.Service;

@Service("simpleConfig")
public class SimpleBlockConfiguration implements BlockConfiguration {

    @Override
    public long getReward(Block block) {

        if(block.getId() < 1000) return 1000000000;
        else if(block.getId() < 1000000) return 100000000;
        else return 10000000;
    }

    @Override
    public int getDifficulty(Block block) {

        int difficulty = 12;
        if(block.getId() < 100000) difficulty = 10;
        if(block.getId() < 1000) difficulty = 8;
        if(block.getId() < 100) difficulty = 6;
        if(block.getId() < 10) difficulty = 4;
        if(block.getId() == 1) difficulty = 0;

        return difficulty;
    }
}
