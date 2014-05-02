/**
 * 
 */
package hotelmania.group2.dao;

/**
 * @author user
 *
 */
public class ClientDAO {
	
	private static ClientDAO instance; 
	
	private ClientDAO(){
		
	}

	public static ClientDAO getInstance() {
		if (instance==null) {
			instance = new ClientDAO();
		}
		return instance;
	}

}
