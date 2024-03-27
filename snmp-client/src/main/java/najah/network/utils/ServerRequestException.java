package najah.network.utils;

/**
 *
 * @author Network2 Group
 */
public class ServerRequestException extends Exception {
    
    private final int status;
    private final String msg;
    
    public ServerRequestException(String msg, int status) {
        this.status = status;
        this.msg = msg;
    }
    
    @Override
    public String getMessage() {
        return this.msg;
    }
    
    public int getStatus() {
        return this.status;
    }
}
