package models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import Dao.DBConnect;

public class LoginModel extends DBConnect {
 
	private Boolean admin;
 
	public Boolean isAdmin() {
		return admin;
	}
	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}
	
	public int getCredentials(String username, String password){
           int user_id=-1;
        	String query = "SELECT * FROM vp_users WHERE username = ? and password = ?;";
            try(PreparedStatement stmt = connection.prepareStatement(query)) {
               stmt.setString(1, username);
               stmt.setString(2, password);
               ResultSet rs = stmt.executeQuery();
                if(rs.next()) { 
                	//if(password.equals(rs.getString("password")) && username.equals(rs.getString("username"))) {
                	user_id = rs.getInt("uid");
                	System.out.println("here"+user_id);
                	setAdmin(rs.getBoolean("admin"));
                	return user_id;
                	
                	}
             }catch (SQLException e) {
            	e.printStackTrace();   
             }
			return user_id;
    }

}//end class