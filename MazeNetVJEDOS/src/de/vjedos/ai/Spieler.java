package de.vjedos.ai;

public class Spieler {
	public int spalte;
	public int zeile;

	public Spieler(int zeile, int spalte) {
		this.spalte = spalte;
		this.zeile = zeile;
	}

	public Spieler(Spieler s) {
		this.spalte = s.spalte;
		this.zeile = s.zeile;
	}

	public boolean equals(Spieler s) {
		if (s == null)
			return false;
		System.out.println(zeile + "==" + s.zeile + " && " + spalte + "=="
				+ s.spalte);
		return s.zeile == zeile && s.spalte == spalte;
	}
	
}

