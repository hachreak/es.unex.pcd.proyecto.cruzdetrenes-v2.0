package es.unex.pcd.leorossi.cruze_de_trenes;

import java.util.LinkedList;
import java.util.Queue;

public class Main {

	public static void main(String[] args) {		
		for(int i=0; i<10; i++){
			Tren t = new Tren(i, (int) (Math.random() * TrainController.NUM_ESTACIONES), (int) (Math.random() * TrainController.NUM_VIA_CADA_ESTACION), TipoTren.getRandom());
			t.start();
		}
		TrainController.getControlador().start();
		
//		new Tren(1, (int) 0, (int) 0, TipoTren.PASAJEROS).start();
//		new Tren(2, (int) 0, (int) 0, TipoTren.PASAJEROS).start();
//		new Tren(3, (int) 0, (int) 0, TipoTren.PASAJEROS).start();
//		new Tren(4, (int) 0, (int) 0, TipoTren.PASAJEROS).start();
//		TrainController.getControlador().start();
	}

}
