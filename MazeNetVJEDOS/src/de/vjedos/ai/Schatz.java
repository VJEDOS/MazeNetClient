package de.vjedos.ai;

import de.vjedos.model.TreasureType;


public class Schatz
{
	public int spalte;
	public int zeile;
	public TreasureType typ;
	
	public Schatz(TreasureType typ, int zeile, int spalte) {
		this.typ = typ;
		this.zeile = zeile;
		this.spalte = spalte;
	}
}
