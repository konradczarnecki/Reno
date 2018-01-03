package konra.reno.blockchain;

import konra.reno.util.Crypto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ChainChunk {

    @Getter @Setter private int id;
    @Getter @Setter private List<Block> blocks;
    @Getter @Setter private String previousChunkHash;
    @Getter @Setter private ChunkState state;

    public String hash() {

        return Crypto.hashHex(id + blocksHash() + previousChunkHash);
    }

    public String blocksHash() {

        StringBuilder sb = new StringBuilder();
        for(Block b: blocks) sb.append(b.hash());
        return Crypto.hashHex(sb.toString());
    }

    public boolean equals(Object o) {

        if(o == null || !(o instanceof ChainChunk)) return false;

        ChainChunk otherChunk = (ChainChunk) o;

        return (otherChunk.id == id && otherChunk.blocksHash().equals(blocksHash()) &&
                otherChunk.previousChunkHash.equals(previousChunkHash) && otherChunk.state == state);

    }

    public enum ChunkState {
        HEAD("H"),
        COMPLETE("C");

        private String val;

        ChunkState(String val) {
            this.val = val;
        }

        public String getVal() {
            return val;
        }
    }
}
