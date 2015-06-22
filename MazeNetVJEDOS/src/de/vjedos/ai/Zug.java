package de.vjedos.ai;

public class Zug {
	public Weg weg;
	public int orientierung;
	public int richtung;
	public int index;
	public Kachel kachel;

	public Zug(Weg weg, Kachel kachel) {
		this.weg = new Weg(weg);
		this.orientierung = -1;
		this.richtung = -1;
		this.index = -1;
		this.kachel = new Kachel(kachel);
	}

	public Zug(Weg weg, int orientierung, int richtung, int index, Kachel kachel) {
		this.weg = new Weg(weg);
		this.orientierung = orientierung;
		this.richtung = richtung;
		this.index = index;
		this.kachel = new Kachel(kachel);
	}

	public boolean isValid() {
		return 	   (orientierung != -1)
				&& (richtung != -1) 
				&& (index != -1)
				&& (weg != null) 
				&& (kachel != null);
	}
}
