package es.unex.pcd.leorossi.cruze_de_trenes;

public enum TipoTren {
	MERCANCIAS, PASAJEROS;
	
	public static TipoTren getRandom() {
		return values()[(int) (Math.random() * values().length)];
	}
}