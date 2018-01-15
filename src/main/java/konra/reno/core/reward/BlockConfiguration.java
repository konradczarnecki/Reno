package konra.reno.core.reward;

import konra.reno.core.Block;

public interface BlockConfiguration {

    double getReward(Block block);
    int getDifficulty(Block block);
}
