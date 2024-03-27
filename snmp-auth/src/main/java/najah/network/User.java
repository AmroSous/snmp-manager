package najah.network;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author Network2 Group
 */
public record User(
        @JsonProperty("username") String username, 
        @JsonProperty("ID") int id, 
        @JsonProperty("password") String password) {
}
