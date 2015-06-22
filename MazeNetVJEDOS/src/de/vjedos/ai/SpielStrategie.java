package de.vjedos.ai;

import java.util.*;

import de.vjedos.client.Client;
import de.vjedos.model.TreasureType;

public class SpielStrategie {
	public static final int DIR_OBEN = 0;
	public static final int DIR_UNTEN = 1;
	public static final int DIR_LINKS = 2;
	public static final int DIR_RECHTS = 3;
	public static final String[] dir_array = new String[] { "oben", "unten",
			"links", "rechts" };

	protected Client hauptClient;
	protected Kachel[][] brett;
	protected Kachel freieKachel;
	protected TreasureType schatz;
	protected Spieler spieler;
	protected List<TreasureType> schaetze;
	protected int schatzZeile, schatzSpalte;
	protected TreasureType freierSchatz;

	public Kachel momentaneKachel;
	public int momentaneSpaltenPosition, momentaneZeilenPosition;
	public int geblockteZeile, geblockteSpalte;

	public SpielStrategie() {

	}

	public SpielStrategie(SpielStrategie copy) 
	{
		this.schaetze = new ArrayList<>();
		this.hauptClient = copy.hauptClient;
		this.brett = new Kachel[7][7];
		for(int i = 0; i < 7; i++){
			System.arraycopy(copy.brett[i], 0, brett[i], 0, 7);
		}
////		copy.schaetze.stream().forEach((t) -> {
////				schaetze.add(t);
//		});
		this.freieKachel = copy.freieKachel;
		this.schatz = copy.schatz;
		this.spieler = new Spieler(copy.spieler);
		this.schatzZeile = copy.schatzZeile;
		this.schatzSpalte = copy.schatzSpalte;
		this.momentaneSpaltenPosition = copy.momentaneSpaltenPosition;
		this.momentaneZeilenPosition = copy.momentaneZeilenPosition;
		this.momentaneKachel = copy.momentaneKachel;
		this.geblockteSpalte = copy.geblockteSpalte;
		this.geblockteZeile = copy.geblockteZeile;
		
	}
	
	public boolean pruefeZugGueltigkeit(int index, int dir) {
		int zeile = -1, spalte = -1;
		if (dir == DIR_LINKS || dir == DIR_RECHTS) {
			zeile = index;
		} else
			spalte = index;
		if (dir == DIR_LINKS)
			spalte = 0;
		if (dir == DIR_RECHTS)
			spalte = 6;
		if (dir == DIR_OBEN)
			zeile = 0;
		if (dir == DIR_UNTEN)
			zeile = 6;
		return !(zeile == geblockteZeile && spalte == geblockteSpalte);
	}

	private int tiefe = 0;

	public boolean pruefeErreichbarkeitSchatz() {
		long zeit = System.currentTimeMillis();
		boolean[][] besucht = new boolean[7][7];
		tiefe = 0;

		boolean f = pruefeErreichbarkeitKachelVon(spieler.zeile,
				spieler.spalte, schatzZeile, schatzSpalte, besucht);

		return f;
	}

	public boolean pruefeErreichbarkeitKachelVon(int ausgangsZeile,
			int ausgangsSpalte, int zielZeile, int zielSpalte) {
		boolean[][] besucht = new boolean[7][7];
		return pruefeErreichbarkeitKachelVon(ausgangsZeile, ausgangsSpalte,
				zielZeile, zielSpalte, besucht);
	}

	private boolean pruefeErreichbarkeitKachelVon(int ausgangsZeile,
			int ausgangsSpalte, int zielZeile, int zielSpalte,
			boolean[][] besucht) {
		if (besucht[ausgangsZeile][ausgangsSpalte])
			return false;
		tiefe++;
		Kachel t = brett[ausgangsZeile][ausgangsSpalte];
		besucht[ausgangsZeile][ausgangsSpalte] = true;
		if (ausgangsZeile == zielZeile && ausgangsSpalte == zielSpalte) {
			return true;
		} else {
			if (ausgangsZeile > 0 && t.oben
					&& brett[ausgangsZeile - 1][ausgangsSpalte].unten) {
				if (pruefeErreichbarkeitKachelVon(ausgangsZeile - 1,
						ausgangsSpalte, zielZeile, zielSpalte, besucht)) {
					return true;
				}
			}
			if (ausgangsZeile < 6 && t.unten
					&& brett[ausgangsZeile + 1][ausgangsSpalte].oben) {
				if (pruefeErreichbarkeitKachelVon(ausgangsZeile + 1,
						ausgangsSpalte, zielZeile, zielSpalte, besucht)) {
					return true;
				}
			}
			if (ausgangsSpalte > 0 && t.links
					&& brett[ausgangsZeile][ausgangsSpalte - 1].rechts) {
				if (pruefeErreichbarkeitKachelVon(ausgangsZeile,
						ausgangsSpalte - 1, zielZeile, zielSpalte, besucht)) {
					return true;
				}
			}
			if (ausgangsSpalte < 6 && t.rechts
					&& brett[ausgangsZeile][ausgangsSpalte + 1].links) {
				if (pruefeErreichbarkeitKachelVon(ausgangsZeile,
						ausgangsSpalte + 1, zielZeile, zielSpalte, besucht)) {
					return true;
				}
			}
			return false;
		}
	}

	public Weg findeKuerzestenWeg() {
		Weg erg = new Weg(spieler, 0, 0);

		if (pruefeErreichbarkeitSchatz()) {
			erg.zielZeile = schatzZeile;
			erg.zielSpalte = schatzSpalte;
			System.out.println("Schatz ist erreichbar!");
			return erg;
		}

		int minDistance = Integer.MAX_VALUE;

		for (int zeile = 0; zeile < 7; zeile++) {
			for (int spalte = 0; spalte < 7; spalte++) {
				if (pruefeErreichbarkeitKachelVon(spieler.zeile,
						spieler.spalte, zeile, spalte)) {
					int distance = getAbstandVonZu(zeile, spalte, schatzZeile,
							schatzSpalte);
					if (distance < minDistance) {
						minDistance = distance;
						erg.zielZeile = zeile;
						erg.zielSpalte = spalte;
					}
				}
			}
		}

		return erg;
	}

	public Zug bestenZugFinden() {
		Zug minZug = new Zug(findeKuerzestenWeg(), freieKachel);

		for (int index = 1; index < 7; index += 2) {
			for (int richtung = 0; richtung < 4; richtung++) {
				for (int orientation = 0; orientation < 4; orientation++) {
					SpielStrategie game = new SpielStrategie(this);
					game.freieKarteSetzen(index, richtung);
					Weg weg = game.findeKuerzestenWeg();
					if (weg.getAbstand() < minZug.weg.getAbstand()) {
						minZug.weg = weg;
						minZug.index = index;
						minZug.richtung = richtung;
						minZug.orientierung = orientation;
						if (minZug.weg.getAbstand() == 1) {
							return minZug;
						}
					}
					freieKachel = freieKachel.getGedrehtUm90Grad();
				}
			}
		}
		return minZug;
	}

	public void freieKarteSetzen(int index, int richtung) {
		this.momentaneKachel = this.freieKachel;// Alte Karte speichern f�r
												// senden von ZugMessage
		if (index % 2 == 1 && index < 7) {// Nummer muss im Bereich liegen
			if (richtung == DIR_LINKS) {// Von Links
				Kachel t = brett[index][6];// Karte die rausf�llt speichern
				for (int i = 5; i >= 0; i--) {// Alle einen aufschieben
					brett[index][i + 1] = brett[index][i];
				}
				brett[index][0] = freieKachel;// Ganz links freies St�ck
												// anwenden
				this.momentaneSpaltenPosition = 0;
				this.momentaneZeilenPosition = index;
				if (schatzZeile == index) {// Wenn das gesuchte st�ck auch
											// ver�ndert wurde, entsprechend
											// tuen
					schatzSpalte++;// Aufschieben
					if (schatzSpalte == 7) {// Wenn es herausf�llt
						schatzZeile = -1;
						schatzSpalte = -1;
					}
				}
				if (spieler.zeile == index) {
					System.out.println("Verschiebe Spieler 1 nach rechts");
					spieler.spalte++;
					if (spieler.spalte == 7) {
						spieler.spalte = 0;
					}
				}
				freieKachel = t;// Freies St�ck auf herausgefallenes setzen
			}
			if (richtung == DIR_RECHTS) {
				Kachel t = brett[index][0];
				for (int i = 1; i < 7; i++) {
					brett[index][i - 1] = brett[index][i];
				}
				brett[index][6] = freieKachel;
				this.momentaneSpaltenPosition = 6;
				this.momentaneZeilenPosition = index;
				if (schatzZeile == index) {
					schatzSpalte--;
					if (schatzSpalte == -1) {
						schatzZeile = -1;
						schatzSpalte = -1;
					}
				}
				if (spieler.zeile == index) {
					System.out.println("Verscheibe spieler 1 nach links");
					spieler.spalte--;
					if (spieler.spalte == -1) {
						spieler.spalte = 6;
					}
				}
				freieKachel = t;
			}
			if (richtung == DIR_OBEN) {
				Kachel t = brett[0][index];
				for (int i = 5; i >= 0; i--) {
					brett[i + 1][index] = brett[i][index];
				}
				brett[0][index] = freieKachel;
				this.momentaneSpaltenPosition = index;
				this.momentaneZeilenPosition = 0;
				if (schatzSpalte == index) {
					schatzZeile++;
					if (schatzZeile == 7) {
						schatzZeile = -1;
						schatzSpalte = -1;
					}
				}
				if (spieler.spalte == index) {
					System.out.println("Verschiebe Spieler 1 nach unten");
					spieler.zeile++;
					if (spieler.zeile == 7) {
						spieler.zeile = 0;
					}
				}
				freieKachel = t;
			}
			if (richtung == DIR_UNTEN) {
				Kachel t = brett[0][index];
				for (int i = 1; i < 7; i++) {
					brett[i - 1][index] = brett[i][index];
				}
				brett[6][index] = freieKachel;
				this.momentaneSpaltenPosition = index;
				this.momentaneZeilenPosition = 6;
				if (schatzSpalte == index) {
					schatzZeile--;
					if (schatzZeile == -1) {
						schatzZeile = -1;
						schatzSpalte = -1;
					}
				}
				if (spieler.spalte == index) {
					spieler.zeile--;
					if (spieler.zeile == -1)
						spieler.zeile = 6;
				}
				freieKachel = t;
			}
		} else {
			throw new IllegalArgumentException(
					"You cannot apply a card to this zeile/spalte");
		}
	}

	public void movePlayerRandomly() {
		if (pruefeErreichbarkeitKachelVon(spieler.zeile, spieler.spalte,
				spieler.zeile - 1, spieler.spalte)) {
			System.out.println("\tnach oben");
			spieler.zeile--;
		} else if (pruefeErreichbarkeitKachelVon(spieler.zeile, spieler.spalte,
				spieler.zeile + 1, spieler.spalte)) {
			System.out.println("\tnach unten");
			spieler.zeile++;
		} else if (pruefeErreichbarkeitKachelVon(spieler.zeile, spieler.spalte,
				spieler.zeile, spieler.spalte - 1)) {
			System.out.println("\tnach links");
			spieler.spalte--;
		} else if (pruefeErreichbarkeitKachelVon(spieler.zeile, spieler.spalte,
				spieler.zeile, spieler.spalte + 1)) {
			System.out.println("\tnach rechts");
			spieler.spalte++;
		}
	}

	public boolean SpielerAufFreierKarte() {
		return spieler.zeile == -1 && spieler.spalte == -1;
	}

	public int getAbstandSpielerSchatz() {
		return Math.abs(spieler.spalte - schatzSpalte)
				+ Math.abs(spieler.zeile - schatzZeile);
	}

	private int getAbstandVonZu(int ausgangsZeile, int ausgangsSpalte,
			int zielZeile, int zielSpalte) {
		return Math.abs(ausgangsZeile - zielZeile)
				+ Math.abs(ausgangsSpalte - zielSpalte);
	}

	public void setzeZufaellig() {
		int rand4 = (int) (Math.random() * 4);
		int dir = (int) (Math.random() * 4);
		int index = (int) (Math.random() * 3) * 2 + 1;
		for (int i = 0; i < rand4; i++) {
			this.freieKachel = this.freieKachel.getGedrehtUm90Grad();
		}
		if ((dir == DIR_LINKS || dir == DIR_RECHTS) && spieler.zeile % 2 == 1)
			index = spieler.zeile;
		if ((dir == DIR_UNTEN || dir == DIR_OBEN) && spieler.spalte % 2 == 1)
			index = spieler.spalte;
		this.freieKarteSetzen(index, dir);
	}
}