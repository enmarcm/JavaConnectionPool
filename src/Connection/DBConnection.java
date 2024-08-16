package Connection;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
	Properties prop = new Properties();
	InputStream entrada = null;
	private String url;
	private String user;
	private String password;
	private String urlCredenciales = "src/Connection/credentials.properties";
	private Connection con = null;
	
	public DBConnection(){
		try {
			entrada = new FileInputStream(this.urlCredenciales);
			prop.load(entrada);
			
			this.url = prop.getProperty("url") + prop.getProperty("bdd");
			this.user = prop.getProperty("user");
			this.password = prop.getProperty("password");
			
			
		} catch (FileNotFoundException e) {
			System.err.println("No se encontro el archivo en la ruta'"+this.urlCredenciales+"'" + "Error: "+e.getMessage());
		} catch (IOException e) {
			System.err.println("No se pudo cargar el archivo de entrada. Error: "+e.getMessage());
		} catch (Exception e) {
			System.err.println("Error: " +e.getMessage());
		}
	}
	
	public Connection getConnection() {
		try {
			this.con = DriverManager.getConnection(this.url, this.user, this.password);
		} catch (SQLException e) {
			System.err.println("No se pudo conectar a la BDD. Error: " + e.getMessage());
		} 
		
		return this.con;
	}
	
	public void desconex() {
		try {
			this.con.close();
		} catch (SQLException e) {
			System.out.println("No se pudo desconectar de la BDD. Error: "+e.getMessage());
		}
		this.con = null;
	}
	

	
}
