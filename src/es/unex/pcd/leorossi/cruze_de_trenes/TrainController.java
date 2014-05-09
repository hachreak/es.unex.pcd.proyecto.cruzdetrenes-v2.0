package es.unex.pcd.leorossi.cruze_de_trenes;

import java.util.ArrayList;
import java.util.List;

import javax.management.MXBean;

public class TrainController extends Thread {

	public static final int NUM_ESTACIONES = 2;
	public static final int NUM_VIA_CADA_ESTACION = 3;
	private static final int MAX_TRAIN = 3;

	private static TrainController controlador = null;
	private List<Sensor> sensors = new ArrayList<Sensor>();
	private List<ArrayList<Semaphore>> estaciones = new ArrayList<ArrayList<Semaphore>>();

	private Integer sensoDiMarcia = 0;

	private int trainGoing = 0;
	private java.util.concurrent.Semaphore waitNewTrain = new java.util.concurrent.Semaphore(0);
	private java.util.concurrent.Semaphore waitTrainPassSensor = new java.util.concurrent.Semaphore(
			0);
	private Integer lastVia = 0;
	private boolean passed = false;
	private int newTrain = 0;
	private int total = 0;


	/**
	 * Init controller and stations with semaphores
	 */
	public TrainController() {
		for (int i = 0; i < NUM_ESTACIONES; i++) {
			sensors.add(new Sensor());
			ArrayList<Semaphore> estacion = new ArrayList<Semaphore>();
			for (int j = 0; j < (NUM_VIA_CADA_ESTACION * 2); j++) {
				estacion.add(new Semaphore());
			}
			estaciones.add(estacion);
		}
	}

	/**
	 * Retorna un particular semaphore
	 * @param tren
	 * @return
	 */
	private Semaphore getSemaphore(Tren tren) {
		return getSemaphore(tren.getNumEstacion(), tren.getNumViaEstacion(),
				tren.getTipoTren());

	}

	/**
	 * Retorna un particular semaphore
	 * @param station
	 * @param via
	 * @param tipo
	 * @return
	 */
	private Semaphore getSemaphore(int station, int via, TipoTren tipo) {
		int i = (tipo.equals(TipoTren.MERCANCIAS) ? NUM_VIA_CADA_ESTACION : 0);
		return estaciones.get(station).get(via + i);
	}

	/**
	 * Train require to cross
	 * 
	 * @param type
	 * @throws InterruptedException
	 */
	public Sensor accederALaVia(Tren tren) throws InterruptedException {

		// tren añadido a la cola del semáforo correspondiente
		synchronized (this) {
			Semaphore s = getSemaphore(tren);
			s.addQueue(tren);
			System.out.println("[Treno] tren añadido a la cola del semáforo correspondiente... "+tren);
		}

		// notificar al controlador que tiene un nuevo tren
		synchronized (waitNewTrain) {
			newTrain++;
			waitNewTrain.notify();
		}
		
		// El tren entra en modo de espera se pone la luz verde
		synchronized (tren) {
//			System.out.println("[Treno] espera.. " + tren);
			tren.wait();
		}
//		System.out.println("[Treno] puede ir.. " + tren);
		// El tren puede ir
		return sensors.get(tren.getNumEstacion());
	}

	/**
	 * Called after a sensor has been activated
	 * @param sensor
	 */
	public void sensorActivated(Sensor sensor) {
		synchronized (waitTrainPassSensor) {
			System.out.println("[Treno] ha pasado la cruz.. " + sensor.getTrain());
			passed = true;
			waitTrainPassSensor.notify();
			total++;
			System.out.println("número total de trenes que pasaron: " + total);
		}
		//
	}

	@Override
	public void run() {
		while (true) {
			try {
				// Espero nuevo tren
				synchronized (waitNewTrain) {
					while (newTrain == 0) {
						waitNewTrain.wait();
					}
					newTrain--;
				}

				// Asignar turno
				asignarTurno();
				
				// Espero que el tren pasa
				synchronized (waitTrainPassSensor) {
					while (!passed)
						waitTrainPassSensor.wait();
					passed = false;
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	synchronized private boolean asignarTurno() {
		// synchronized (this) {
		if (trainGoing > MAX_TRAIN) {
			 System.out.println("[Controller] Cambio!");
			// cambio senso di marcia
			sensoDiMarcia = (sensoDiMarcia + 1) % NUM_ESTACIONES;
			lastVia = 0;
			// azzero contatore
			trainGoing = 0;
		}

		// }
		for (int i = 0; i < NUM_ESTACIONES; i++) {
			int stazione = (sensoDiMarcia + i) % NUM_ESTACIONES;
			if(i > 0){
				// azzero contatore perchè cambio stazione
				trainGoing = 0;
			}
			for (int j = 0; j < NUM_VIA_CADA_ESTACION; j++) {
				int via = (lastVia + j) % NUM_VIA_CADA_ESTACION;
				Semaphore sp = getSemaphore(stazione, via, TipoTren.PASAJEROS);
				Semaphore sm = getSemaphore(stazione, via, TipoTren.MERCANCIAS);

				if (sp.size() > 0) {
//					System.out.println("[Controllore] libero treno, stazione("
//							+ stazione + ") - " + via + "P");
					sensoDiMarcia = stazione;
					lastVia = via;
					trainGoing++;
					Tren t = sp.getFirst();
					synchronized (t) {
						t.notify();
					}
					return true;
				} else if (sm.size() > 0) {
//					System.out.println("[Controllore] libero treno: stazione("
//							+ stazione + ") - " + via + "M");
					trainGoing++;
					lastVia = via;
					Tren t = sm.getFirst();
					synchronized (t) {
						t.notify();
					}
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Lazy init
	 * @return
	 */
	public static TrainController getControlador() {
		// Lazy initialization
		if (controlador == null) {
			controlador = new TrainController();
		}
		return controlador;
	}

}
