package Dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnect {

	protected Connection connection;

	public Connection getConnection() {
		return connection;
	}

	// Varibales for connection
	private static String url = "jdbc:mysql://www.papademas.net:3307/fp510";
	private static String username = "fpuser";
	private static String password = "510";

	// Function to connect to DB
	public DBConnect() {
		try {
			connection = DriverManager.getConnection(url, username, password);
		} catch (SQLException e) {
			System.out.println("Error creating connection to database: " + e.getMessage());
			System.exit(-1);
		}
	}

	// Function to change parking availability by admin
	public void toggleUpdate(String level, String lot) {
		String query = "update  vp_parking_lot  set " + level + "=" + "1-" + level + " WHERE level =" + lot;

		System.out.println(query);
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			int rs = stmt.executeUpdate();
		} catch (SQLException e) {
			System.out.println("exception"+e.getMessage());
		}
	}
	
	public boolean existLevel(String lvl) {
		String max_level = getMaxId();
		System.out.println("Values lvl "+lvl+" max lvl"+max_level);
		int lvlInt = Integer.parseInt(lvl);
		int maxlvlInt = Integer.parseInt(max_level);
		
		if(lvlInt<=maxlvlInt) {
			return true;
		}
		else {
			return false;
		}
	}

	// Function to add a level in parking
	public void addOneLevel() {
		String id = getMaxId();
		int idInt = Integer.parseInt(id);
		System.out.println(id);
		if (!id.equals("error")) {
			String query = "INSERT INTO `vp_parking_lot`(`level`, `A`, `B`, `C`, `D`, `E`) VALUES (" + (idInt + 1)
					+ ",'0','0','0','0','0')";
			System.out.println(query);
			try (PreparedStatement stmt = connection.prepareStatement(query)) {
				int rs = stmt.executeUpdate();
			} catch (SQLException e) {
				System.out.println("exception");
			}
		}
	}

	// Function to get maximum level from parking to delete the last row
	// Auto-incrementing the table rows and deleting last row is not a good option
	// as when after row 5 is deleted and
	// a new row is added, the new row will be 6 which will cause confusions and
	// improper system management
	public String getMaxId() {
		String query1 = "SELECT MAX(level) from vp_parking_lot";
		System.out.println(query1);
		try (PreparedStatement stmt = connection.prepareStatement(query1)) {
			System.out.println("connected");
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String maxid = rs.getString(1);
				return maxid;
			}
		} catch (SQLException e) {
			System.out.println("Error" + e.getMessage());
		}
		return "error";
	}

	// Function that deletes the level from parking lot
	public void deleteOneLevel() {
		String id = getMaxId();
		int idInt = Integer.parseInt(id);
		System.out.println(id);
		if (!id.equals("error")) {
			String query = "Delete from `vp_parking_lot` where level =" + idInt;
			System.out.println(query);
			try (PreparedStatement stmt = connection.prepareStatement(query)) {

				int rs = stmt.executeUpdate();

			} catch (SQLException e) {
				System.out.println("exception");
			}
		}
	}

	// Function that checks whether the deletion is allowed i.e no 1's exist in that
	// row
	public int canDelete() {
		String id = getMaxId();
		int idInt = Integer.parseInt(id);
		String query = "SELECT * from vp_parking_lot  where level =" + idInt;
		System.out.println(query);
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int ide = rs.getInt(1);
				int a = rs.getInt(2);
				int b = rs.getInt(3);
				int c = rs.getInt(4);
				int d = rs.getInt(5);
				int e = rs.getInt(6);
				System.out.print("Lvl" + ide + " A" + a + " B" + b + " C" + c + " D" + d + " E" + e);
				if (a + b + c + d + e == 0)
					return 1;
				else
					return 0;
			}
		} catch (SQLException e) {
			System.out.println("exception");
		}
		return -1;
	}

	// Function that inserts new user into the DB
	public boolean insertUser(String username, String password, String fname, String lname) {
		// the mysql insert statement
		String query = " insert into vp_users (username, password, firstName, lastName)" + " values (?, ?, ?, ?)";
		// create the mysql insert preparedstatement
		PreparedStatement preparedStmt;
		try {
			preparedStmt = connection.prepareStatement(query);
			preparedStmt.setString(1, username);
			preparedStmt.setString(2, password);
			preparedStmt.setString(3, fname);
			preparedStmt.setString(4, lname);
			// execute the preparedstatement
			preparedStmt.execute();
			return true;
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return false;
	}

	// Function that checks whether the booking can be made i.e 0 exists in the
	// desired spot
	public boolean canBook(String lvl, String lt) {
		String value = "";
		String query = "SELECT " + lvl + " FROM `vp_parking_lot` WHERE level = " + lt;
		System.out.println(query);
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				value = rs.getString(1);
			}

		} catch (SQLException e) {
			System.out.println("exception" + e.getMessage());

		}
		if (Integer.parseInt(value) == 0) {
			return true;
		} else {
			return false;
		}
	}

	// Function that makes sure only one booking is made by the user
	public int onlyOneBooking(int id) {
		int count = 0;
		String query = "SELECT COUNT(*) FROM `vp_bookings` WHERE uid = " + id;
		System.out.println(query);
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				count = rs.getInt(1);
			}

		} catch (SQLException e) {
			System.out.println("exception");
		}
		return count;
	}

	// Function that actually books the slot
	public boolean bookTheSlot(String lvl, String lt, int userId) {
		toggleUpdate(lvl, lt);
		String query = "INSERT into vp_bookings(uid,lvl,lot) values(" + userId + "," + lt + ",'" + lvl + "')";
		System.out.println(query);
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			int rs = stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.out.println("exception" + e.getMessage());
		}
		return false;
	}

	// Function that will retrieve the booked slot
	public String getBookedSlot(int user_id) {
		String query = "Select lvl,lot from vp_bookings where uid =" + user_id;
		System.out.println(query);
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String level = rs.getString(1);
				String lot = rs.getString(2);
				System.out.println(level + lot);
				return (level + lot);
			}
		} catch (SQLException e) {
			System.out.println("exception" + e.getMessage());
		}
		return "No records";
	}

	// Function that will make the actual deletion of booking
	public boolean confirmCancel(int user_id) {
		String query = "DELETE from vp_bookings where uid =" + user_id;
		System.out.println(query);
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			int rs = stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.out.println("exception" + e.getMessage());
		}
		return false;
	}
	
	//Function that will delete booking from table when admin changes the status of bookings
	public boolean deleteTheirBooking(String lvl, String lt) {
		System.out.println(lt);
		//int ltInt = Integer.parseInt(lt);
		String query = "DELETE from vp_bookings where lot = ?  AND lvl ="+lt;
		System.out.println(query);
		try (PreparedStatement stmt = connection.prepareStatement(query)) {
			stmt.setString(1,lvl);
			int rs = stmt.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.out.println("exception" + e.getMessage());
		}
		return false;
	}
}
