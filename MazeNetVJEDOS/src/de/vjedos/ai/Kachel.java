package de.vjedos.ai;

import de.vjedos.model.TreasureType;

/**
 *
 * @author prior
 */
public class Kachel {
	public boolean links;
	public boolean rechts;
	public boolean oben;
	public boolean unten;
	public TreasureType schatz;

	public Kachel(Kachel copy) {
		this.links = copy.links;
		this.rechts = copy.rechts;
		this.oben = copy.oben;
		this.unten = copy.unten;
	}

	public Kachel(boolean links, boolean rechts, boolean oben, boolean unten) {
		this.links = links;
		this.rechts = rechts;
		this.oben = oben;
		this.unten = unten;
	}

	public Kachel getGedrehtUm90Grad() {
		Kachel s = new Kachel(unten, oben, rechts, links);
		s.schatz = this.schatz;
		return s;
	}
}