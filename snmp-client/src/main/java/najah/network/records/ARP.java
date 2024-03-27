package najah.network.records;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Network2 Group
 */
public record ARP(
        @JsonProperty("status") String status,
        @JsonProperty("arpEntries") List<Map<String, Object>> arpEntries) {
}
