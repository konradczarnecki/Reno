package konra.reno.core.reward;

import konra.reno.core.Block;
import org.springframework.stereotype.Service;

@Service
public class SimpleRewardConfig implements RewardConfig {

    public double getReward(Block block) {

        if(block.getId() < 1000) return 1000;
        else if(block.getId() < 1000000) return 100;
        else return 10;
    }
}
