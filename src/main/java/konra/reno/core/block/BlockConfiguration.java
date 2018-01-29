package konra.reno.core.block;

public interface BlockConfiguration {

    long getReward(Block block);
    int getDifficulty(Block block);
}
