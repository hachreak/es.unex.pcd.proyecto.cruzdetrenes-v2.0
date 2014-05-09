package es.unex.pcd.leorossi.cruze_de_trenes;

public class Sensor {

	private Tren tren = null;

	public void activarSensor(Tren tren) throws InterruptedException{
		this.tren  = tren;
		TrainController.getControlador().sensorActivated(this);
	}

	public Tren getTrain() {
		return tren;
	}
}
