package konra.reno.core.callback;

public enum CallbackType {
    HEAD_EXCHANGE,  // Registered in BlockHandler constructor
    TRANSACTION,    // Registered in TransactionHandler constructor
    MINE_NEW_BLOCK  // Registered in MinerService constructor
}
