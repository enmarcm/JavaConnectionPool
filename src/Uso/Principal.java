package Uso;


import Pool.PoolManager;

public class Principal {
	public static void main(String[] args) {
		Hilo[] hilo = new Hilo[25];
		
		for(int i=0; i<hilo.length;i++) {
			hilo[i] = new Hilo();
			hilo[i].setName(""+(i+1));
			hilo[i].start();

		}

	}
}

class Hilo extends Thread{	
	private PoolManager pg = PoolManager.getInstance();
	@Override
	
	public void run() {
		pg.createPool();
		pg.getCon();
		System.out.println(PoolManager.getEntregadas());
		
		
//		var a = pg.executeQuery("SELECT * FROM prueba LIMIT 1");
//		try {
//			System.out.println(a.getString(2));
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			System.err.println("fue aqui" + e.getMessage());
//		}
		
		
	}
	
}



