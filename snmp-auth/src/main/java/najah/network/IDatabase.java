package najah.network;

/**
 *
 * @author Network2 Group
 */
public interface IDatabase {
    boolean isValidUserByName(String username, String password);
    boolean isValidUserById(int id, String password);
}
