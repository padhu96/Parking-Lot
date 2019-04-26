package application;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.concurrent.atomic.AtomicLong;

import Dao.DBConnect;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public class DynamicTable    {

	// TABLE VIEW AND DATA OBJECT CREATIONS
	@SuppressWarnings("rawtypes")
	private ObservableList<ObservableList> data;
	@SuppressWarnings("rawtypes")
	private TableView tableview;
	private Stage secondStage;
	// CONNECTION 2 DATABASE
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void buildData(String sql,boolean isButtonReq) {
		tableview = new TableView();
		DBConnect c = new DBConnect();
		data = FXCollections.observableArrayList();
		try {
			Connection conn = c.getConnection();
			// SQL FOR SELECTING DATA
			String SQL = sql;
			// ResultSet object
			ResultSet rs = conn.createStatement().executeQuery(SQL);

			/**********************************
			 * TABLE COLUMN ADDED DYNAMICALLY *
			 **********************************/
			for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {

				// We are using non property style for making dynamic table
				final int j = i;
				TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));

				/**
				 * Build an ObservableList for column headings as you iterate thru meta data
				 * setup callback Api for column retrievals, works with call method to return
				 * heading names
				 */
				col.setCellValueFactory(
						new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {

							public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
								return new SimpleStringProperty(param.getValue().get(j).toString());
							}

						});
				// add each column name to tableview object
				tableview.getColumns().add(col);
				// display column names to console as they are added to table dynamically
				System.out.println("Column [" + i + "] added [" + rs.getMetaData().getColumnName(i + 1) + "]");
			}

			/********************************************
			 * Data added to ObservableList dynamically *
			 ********************************************/
			int ridx = 0; // track a row index to display to console added rows to table
			while (rs.next()) {
				// Iterate Row
				ObservableList<String> row = FXCollections.observableArrayList();
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					// Iterate Column, grab data
					row.add(rs.getString(i));
				}
				System.out.println("Row [" + (ridx++) + "] added " + row);
				data.add(row);
			}
			// automatically adjust width of columns depending on their content
			tableview.setColumnResizePolicy((param) -> true);
			Platform.runLater(() -> customResize(tableview));

			// add data to tableview object
			tableview.setItems(data);

			 Scene secondScene = new Scene(tableview, 600, 400);
             
			    secondStage  = new Stage();
	            secondStage.setTitle("New Stage");
	            if(isButtonReq)
	            {
	            	Group root = (Group)secondScene.getRoot();
	            	Label label1 = new Label("Name:");
	            	TextField textField = new TextField ();
	            	root.getChildren().addAll(label1,textField);
	            }
	   		 
	   		  
	            secondStage.setScene(secondScene);
	             
	            secondStage.show();
		
			 Main.stage.setOnCloseRequest((WindowEvent event1) -> {
			       
				System.out.println("Main window closed");
				  
			 });
			
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error on Building Data");
		}
	}
	
	public void customResize(TableView<?> view) {

		AtomicLong width = new AtomicLong();
		view.getColumns().forEach(col -> {
			width.addAndGet((long) col.getWidth());
		});
		double tableWidth = view.getWidth();

		if (tableWidth > width.get()) {
			view.getColumns().forEach(col -> {
				col.setPrefWidth(col.getWidth() + ((tableWidth - width.get()) / view.getColumns().size()));
			});
		}
	}
}