package de.vjedos.ai;

public class Weg {
    public int ausgangsZeile;
    public int ausgangsSpalte;
    public int zielZeile;
    public int zielSpalte;
   
    public Weg(int ausgangsZeile, int ausgangsSpalte, int zielZeile, int zielSpalte){
                   this.ausgangsZeile = ausgangsZeile;
                   this.ausgangsSpalte = ausgangsSpalte;
                   this.zielZeile = zielZeile;
                   this.zielSpalte = zielSpalte;
    }
   
    public Weg(Spieler spieler, int zielZeile, int zielSpalte){
                   this.ausgangsZeile = spieler.zeile;
                   this.ausgangsSpalte = spieler.spalte;
                   this.zielZeile = zielZeile;
                   this.zielSpalte = zielSpalte;
    }
   
    public Weg(Weg copy) {
                   this.ausgangsZeile = copy.ausgangsZeile;
                   this.ausgangsSpalte = copy.ausgangsSpalte;
                   this.zielZeile = copy.zielZeile;
                   this.zielSpalte = copy.zielSpalte;
    }

    public int getAbstand(){
                   return Math.abs(ausgangsZeile - zielZeile) + Math.abs(ausgangsSpalte - ausgangsSpalte);
    }
}
