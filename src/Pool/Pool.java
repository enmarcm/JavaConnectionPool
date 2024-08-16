package Pool;
import java.util.ArrayList;
import java.sql.Connection;


public class Pool {
//	Este ArrayList tendra las conexiones
	private ArrayList<Connection> PoolConexiones = new ArrayList<Connection>();
	private static Pool instance = null;
	
	private Pool() {
	}
	
//	Aqui primero verifica si esta creada, si no esta creada, entonces lo crea y lo retorna
	public static synchronized Pool getInstance() {
		if(instance == null) {
			instance = new Pool();
		}
		
		return instance;
	}
	
	public ArrayList<Connection> getPoolConexiones() {
		return this.PoolConexiones;
	}
}
