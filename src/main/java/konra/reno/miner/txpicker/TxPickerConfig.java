package konra.reno.miner.txpicker;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("txpicker")
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TxPickerConfig {

    int maxTxPerBlock = 10;
    PickerStrategy strategy = PickerStrategy.HIGHIEST_FEE;
}
