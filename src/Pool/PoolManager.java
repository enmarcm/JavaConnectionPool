

package Pool;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import Connection.DBConnection;
public class PoolManager {
//	Las 3 propiedades estaticas las llamamos con un metodo que nos aseguramos que solo se llamen una vez con la propiedad iniciado
	private static int conexionesIniciales;
	private static int conexionesMaximas;
	private static int gradoCrecimiento;
	private static boolean iniciado = false;
	
//	Entregadas la usamos cada vez que damos o retornamos una conexion
	private static int entregadas = 0;
	
//	Esto actua como llave para que solo un hilo acceda una vez a cierta propiedad
	private static final Object lock = new Object();
	
//	Aqui se guarda la instancia de Pool Como tal, cuando la creemos
	private Pool pool = null;
	
//	Y esta es la instancia del PoolManager
	private static PoolManager instance = null;
		
//	Variable para los intentos al intentar conectar, valor inicial de 0
	private ThreadLocal<Integer> intentos = ThreadLocal.withInitial(()->0);
	
	private PoolManager() {
	}
	
	public synchronized static PoolManager getInstance() {
		if(instance == null) {
			instance = new PoolManager();
		}
		
		return instance;
	}
	
//	Con esto obtenemos la instancia del Pool, ademas, si es la primera, se encarga de darle sus valores iniciales al Arraylist, llamando a Init Pool, este verifica las propiedades y agrega las conexiones a la Lista
	public synchronized void createPool() {
		
		if(!iniciado) {
			this.pool = Pool.getInstance();
			this.initPool();
			
			synchronized(lock) {
				iniciado = true;
			}
			
		}	
	}

	
//	Si las entregadas y el grado de crecimiento lo permiten, se llama a addCon para quea agregue mas conexiones
	synchronized public Connection getCon() {
		Connection con = null;
		
		while(this.intentos.get() < 3) {
			if(!this.pool.getPoolConexiones().isEmpty()) {
				con = this.pool.getPoolConexiones().remove(0);
				synchronized(lock) {
					entregadas +=1;
					this.intentos.set(4);
				}
				
				return con;
			}else if(entregadas + gradoCrecimiento <= conexionesMaximas) {				
				System.out.println("------------");
				System.out.println("Entregadas son: "+entregadas);
				System.out.println("El tamaño del pool es: "+ this.pool.getPoolConexiones().size());
				
				this.addCon();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					System.out.println("No se pudo dormir el hilo "+e.getMessage());
				}
				this.intentos.set(this.intentos.get() + 1);
				con = this.getCon();
		}else {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				System.out.println("No se pudo dormir el hilo "+e.getMessage());
			}
			this.intentos.set(this.intentos.get() + 1);
			System.out.println("No se pudo dar una conexion. Intento #"+this.intentos.get());
		}

	}
		return con;
	}
	
	
//	Devolvemos una conexion a la Lista Enlazada
	public  void returnCon(Connection con) {
		pool.getPoolConexiones().add(con);
		synchronized(lock) {
			entregadas --;
		}
	}
	
	//CON ESTA EJECUTAMOS LOS QUERYS
	synchronized public ResultSet executeQuery(String query) {
		Connection con = this.getCon();
		ResultSet rs = null;
		
		try {
			Statement st = con.createStatement();
			rs = st.executeQuery(query);
				
			if(rs.next()) {
				this.returnCon(con);
			}
		}catch(Exception e) {
			System.err.println(e.getMessage());
		}
		
		return rs;
	}
	
//	Esto agrega conexiones al Pool, es decir, a la Lista enlzada, segun la cantidad de crecimiento establecido
	public void addCon() {
		for(int i =0; i<gradoCrecimiento;i++) {
			Connection con = new DBConnection().getConnection();
			this.pool.getPoolConexiones().add(con);
		}
	}
	
//	Estas son privadas porque solo se usan de manera interna
//	Si no esta iniciado aun, es decir, es el primer createPool, carga propeidades y agrega conexiones iniciales
	private  void initPool() {
		if(!iniciado) {
			this.loadProperties();
			this.loadInit();
		}
		
	}
	
//	Leemos las propiedades del archivo config
	private  void loadProperties() {
		Properties p = new Properties();
		try {
			InputStream entrada = new FileInputStream("src/Pool/config.properties");
			p.load(entrada);
			synchronized (lock) {
				conexionesIniciales = Integer.parseInt(p.getProperty("TC"));
				conexionesMaximas = Integer.parseInt(p.getProperty("MC"));
				gradoCrecimiento = Integer.parseInt(p.getProperty("GR"));
			}
		} catch (FileNotFoundException e) {
			System.err.println("No pudo cargar el archivo. Error: "+e.getMessage());
		} catch(Exception e) {
			System.err.println("Ocurrio un error: "+e.getMessage());
		}
	}

//	Agregamos conexiones iniciales 
	private void loadInit() {
		for(int i=0;i<conexionesIniciales;i++) {
			Connection con = new DBConnection().getConnection();
			this.pool.getPoolConexiones().add(con);
		}
	}
		
		
// Saber las entregadas
	public static int getEntregadas() {
		return entregadas;
	}
	
}
