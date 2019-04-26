package models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Dao.DBConnect;

public class CustomerModel extends DBConnect implements User<ParkingLot>{

	private String firstName;
	private String lastName;
	private int id;
	private int cid;
    private ParkingLot pt;
	
	public CustomerModel() {
		
	
	}
 
	public CustomerModel(String firstName, String lastName) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
 	public void setCid(int cid) {
		this.cid = cid;
	}


	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	
	public List<CustomerModel> getBookings(int cid) {
		List<CustomerModel> accounts  = new ArrayList<>();
		String query = "SELECT lot,level FROM bookings join users WHERE uid = ?;";
		try(PreparedStatement statement = connection.prepareStatement(query)){
            statement.setInt(1, cid);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()) {
            	CustomerModel account = new CustomerModel();
                //grab record data by table field name into CustomerModel account object
            
            }
        } catch(SQLException e){
            System.out.println("Error fetching Accounts: " + e);
        }
		return accounts; //return arraylist
	}

	@Override
	public ParkingLot getCustomerInfo() {
		// TODO Auto-generated method stub
		return pt;
	}
}