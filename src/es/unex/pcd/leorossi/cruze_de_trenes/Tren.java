package es.unex.pcd.leorossi.cruze_de_trenes;

import java.util.Random;

public class Tren extends Thread {

	private static final long retardoVisualizacion = 1000;
	private int idTren;
	private int numEstacion;
	private int numViaEstacion;
	private TipoTren tipoTren;

	public Tren(int idTren, int numEstacion, int numViaEstacion,
			TipoTren tipoTren) {
		super();
		this.idTren = idTren;
		this.numEstacion = numEstacion;
		this.numViaEstacion = numViaEstacion;
		this.tipoTren = tipoTren;
	}

	public int getIdTren() {
		return idTren;
	}

	public int getNumEstacion() {
		return numEstacion;
	}

	public int getNumViaEstacion() {
		return numViaEstacion;
	}

	public TipoTren getTipoTren() {
		return tipoTren;
	}

	protected void cruzarVia() {
//		System.out.println("Sta passando il treno: "+this.toString());
		retardoVisualizacionAmigable();
	}

	public void entrarEnVia() throws InterruptedException {
		// requiero el acceso (me pare hasta que la transición no es libre)
		Sensor s = TrainController.getControlador().accederALaVia(this);
		// ahora es mi turno, cruz la via!
		cruzarVia();
		// Crucé la intersección, ahora està libre
		s.activarSensor(this);
	}

	@Override
	public void run() {
		
		// El tren, tras un tiempo a priori desconocido (cuasialeatorio -entre
		// 2000 y 7000 ms-), pide acceso a la via
		try {
			int retardo = 2000 + (new Random()).nextInt(5001);
			sleep(retardo);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Pide acceso a la via
		try {
			entrarEnVia();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "Tren " + idTren + "("+numViaEstacion+""+ ((tipoTren == TipoTren.MERCANCIAS) ? "M" : "P")+ ")"+ " estacion " + numEstacion;
	}
	
	public void retardoVisualizacionAmigable() {
		try {
			Thread.sleep(retardoVisualizacion);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
