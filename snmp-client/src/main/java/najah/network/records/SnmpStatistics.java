package najah.network.records;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Network2 Group
 */
public record SnmpStatistics(
        @JsonProperty("status") String status,
        @JsonProperty("snmpStatistics") List<Map<String, Object>> snmpStatistics) {
}
