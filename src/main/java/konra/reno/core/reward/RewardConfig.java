package konra.reno.core.reward;

import konra.reno.core.Block;

public interface RewardConfig {

    double getReward(Block block);
}
