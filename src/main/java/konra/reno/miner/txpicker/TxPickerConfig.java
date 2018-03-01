package konra.reno.miner.txpicker;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("txpicker")
@Getter @Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TxPickerConfig {

    int maxTxPerBlock;
    PickerStrategy strategy;
}
