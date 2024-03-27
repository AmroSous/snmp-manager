package najah.network.models;

import najah.network.interfaces.IDataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.net.SocketTimeoutException;
import najah.network.utils.ServerRequestException;

/**
 *
 * @author Network2 Group
 */
public class DataService implements IDataService {

    @Override
    public String getRequest(String urlString, Map<String, String> params) 
            throws ServerRequestException, SocketTimeoutException, IOException {
        
        String result = null;
        
        StringBuilder builder = new StringBuilder(urlString + "?");
        params.forEach((k, v) -> {
            builder.append(k).append("=").append(v).append("&");
        });
        builder.deleteCharAt(builder.length() - 1);
        
        URL url = new URL(builder.toString());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setUseCaches(false);
        connection.setConnectTimeout(4000);
        connection.setReadTimeout(4000);

        connection.connect();
        int status = connection.getResponseCode();

        if (status == HttpURLConnection.HTTP_OK) {
            StringBuilder responseContent;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                responseContent = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    responseContent.append(line);
                }
            }
            result = responseContent.toString();
        } else {
            throw new ServerRequestException("Request Error.", status);
        }

        connection.disconnect();
        return result;
    }
    
    @Override
    public String postRequest(String urlStr, Map<String, String> data) 
            throws SocketTimeoutException, IOException, ServerRequestException {
        
        HttpURLConnection conn;
        String result;
        URL url = new URL(urlStr);
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setUseCaches(false);
        conn.setConnectTimeout(4000);
        conn.setReadTimeout(4000);
        
        ObjectMapper mapper = new ObjectMapper();
        String jsonData = mapper.writeValueAsString(data);
        
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Read the response
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            result = response.toString();
        }
        
        conn.disconnect();
        return result;
    }
}
