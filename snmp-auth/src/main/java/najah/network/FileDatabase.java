package najah.network;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Network2 Group
 */
public class FileDatabase implements IDatabase {
    
    private List<User> users; 
    private static final Logger logger = LogManager.getLogger(FileDatabase.class);
    
    public FileDatabase(String filename) throws IOException {
        ObjectMapper mapper = new ObjectMapper();       
        try {
            InputStream inputStream = Thread.currentThread()
                    .getContextClassLoader().getResourceAsStream(filename);
            if (inputStream != null) {
                users = mapper.readValue(
                        inputStream, new TypeReference<List<User>>(){});
            } else {
                throw new FileNotFoundException("Resource not found: " + filename);
            }
        } catch (IOException e) {
            logger.error("Exceptional Error: ", e);
        }
    }
    
    @Override
    public boolean isValidUserByName(String username, String password) {
        return users.stream().anyMatch(u -> 
                u.username().equals(username) && u.password().equals(password));
    }

    @Override
    public boolean isValidUserById(int id, String password) {
        return users.stream().anyMatch(u -> 
                u.id() == id && u.password().equals(password));
    }
    
}
