package konra.reno.core.block;

public interface BlockConfiguration {

    long getReward(Block block);
    int getDifficulty(Block block);

    default void setDifficulty(Block block) {
        block.setDifficulty(getDifficulty(block));
    }
}
