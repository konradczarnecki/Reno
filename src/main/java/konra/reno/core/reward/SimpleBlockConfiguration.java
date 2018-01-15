package konra.reno.core.reward;

import konra.reno.core.Block;
import org.springframework.stereotype.Service;

@Service
public class SimpleBlockConfiguration implements BlockConfiguration {

    @Override
    public double getReward(Block block) {

        if(block.getId() < 1000) return 1000;
        else if(block.getId() < 1000000) return 100;
        else return 10;
    }

    @Override
    public int getDifficulty(Block block) {

        int difficulty = 7;
        if(block.getId() < 10000) difficulty = 6;
        if(block.getId() < 1000) difficulty = 5;
        if(block.getId() < 100) difficulty = 4;
        if(block.getId() < 10) difficulty = 3;
        if(block.getId() == 1) difficulty = 0;

        return difficulty;
    }
}
