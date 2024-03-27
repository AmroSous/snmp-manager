package najah.network.interfaces;

import java.io.IOException;
import java.util.Map;
import najah.network.utils.ServerRequestException;

/**
 *
 * @author Network2 Group
 */
public interface IDataService {
    
    /**
     * Method to get data from URL, and return the response in string.
     * 
     * @param url URL to send the request with any parameters.
     * @param params query parameters to send with URL.
     * @return String, the response of request.
     * @throws ServerRequestException
     * @throws IOException 
     */
    String getRequest(String url, Map<String, String> params) throws ServerRequestException, IOException;
    
    /**
     * Method to send POST request to a specific URL, takes data to append in request body.
     * 
     * @param url URL to send the post request.
     * @param data Data (key-value pairs) to send in post.
     * @return the response from the server.
     * @throws IOException
     * @throws ServerRequestException 
     */
    String postRequest(String url, Map<String, String> data) throws IOException, ServerRequestException;
}
