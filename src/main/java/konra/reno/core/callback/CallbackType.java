package konra.reno.core.callback;

public enum CallbackType {
    HEAD_EXCHANGE,  // Registered in BlockHandler contructor
    TRANSACTION,    // Registered in TransactionHandler contructor
    MINE_NEW_BLOCK  // Registered in MinerService constructor
}
