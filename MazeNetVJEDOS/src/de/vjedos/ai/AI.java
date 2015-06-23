package de.vjedos.ai;

import java.util.ArrayList;
import java.util.List;

import de.vjedos.client.Client;
import de.vjedos.model.AIOutModel;
import de.vjedos.model.BoardType;
import de.vjedos.model.CardType;
import de.vjedos.model.MazeCom;
import de.vjedos.model.MazeComType;
import de.vjedos.model.PositionType;
import de.vjedos.model.TreasureType;
import de.vjedos.model.TreasuresToGoType;

public class AI extends SpielStrategie
{
	public Spieler spieler_alt;
	
	public AIOutModel move( BoardType board,  List<TreasureType> found, TreasureType treasure, List<TreasuresToGoType> todo, int id)
	{		
		AIOutModel model = new AIOutModel();
		if(id == 1)
		{
			model.card_column = 1;
			model.card_row = 0;
			model.player_column = 0;
			model.player_row = 0;
			model.shiftCard = board.getShiftCard();
		}
		else if(id == 2)
		{
			model.card_column = 5;
			model.card_row = 0;
			model.player_column = 6;
			model.player_row = 6;
			model.shiftCard = board.getShiftCard();			
		}
		else if(id == 3)
		{
			model.card_column = 1;	
			model.card_row = 0;
			model.player_column = 0;
			model.player_row = 6;
			model.shiftCard = board.getShiftCard();
		}
		else if(id == 4)
		{
			model.card_column = 5;
			model.card_row = 0;
			model.player_column = 6;
			model.player_row = 6;
			model.shiftCard = board.getShiftCard();
		}
		
		return model;
//		
//		
//		//x,y der Karte, Spielers und Shft Karte 
//		schatz = null;
//		freieKachel = null; 
//		brett = new Kachel[7][7];
//		schaetze = new ArrayList<>();
//        for(int i = 0; i < 7; i++) {
//        	for(int j = 0; j < 7; j++) {
//        		CardType karte = board.getRow().get(i).getCol().get(j);
//        		brett[i][j] = new Kachel(karte.getOpenings().isLeft(), karte.getOpenings().isRight(), karte.getOpenings().isTop(), karte.getOpenings().isBottom());
//                if(karte.getTreasure() != null) {
//                	TreasureType tr = karte.getTreasure();
//                    if(karte.getTreasure() == treasure) {
//                   		schatz = tr;
//                   		schatzZeile=i;
//                   		schatzSpalte=j;
//                    }
//                    brett[i][j].schatz = tr;
//                    schaetze.add(tr);
//                }
//                if(karte.getPin().getPlayerID().contains(id)){
//                 	System.out.println("\tSpieler auf der Position" + i + "," + j);
//                 	spieler = new Spieler(i, j);
//                }
//        	}
//        	
//        }
//        CardType frei = board.getShiftCard();
//        freieKachel = new Kachel(frei.getOpenings().isLeft(), frei.getOpenings().isRight(), frei.getOpenings().isTop(), frei.getOpenings().isBottom());
//        freieKachel.schatz = frei.getTreasure();
//        System.out.println("\tfreier schatz:" + frei.getTreasure());
//        if(frei.getTreasure() != null) {
//        	TreasureType tr = frei.getTreasure();
//            if(schatz == null) {
//            	if(frei.getTreasure() == treasure) {
//            		schatz = tr;
//                    schatzZeile = -1;
//                    schatzSpalte = -1;
//            	}
//                schaetze.add(tr);
//            }
//        }
//        System.out.println("\tFreie Karte is: L: " + freieKachel.links + " T: " + freieKachel.oben + " R: " + freieKachel.rechts + " B: " + freieKachel.unten + " Trs: " + freieKachel.schatz);
//                                          
//        for(int i = 0; i < 7; i++) {
//        	for(int j = 0; j < 7; j++) {
//        		if(brett[i][j].schatz == schatz) {
//        			schatzZeile = i;
//                    schatzSpalte = j;
//        		}
//        	}
//        }
//        System.out.println("\tSchatz geufnden : " + schatzSpalte + "," + schatzZeile);
//        PositionType pt = board.getForbidden();
//        if(pt != null) {
//        	geblockteZeile = pt.getRow();
//        	geblockteSpalte = pt.getCol();
//        	System.out.println("\tVerboten/Blockiert:" + geblockteZeile + "," + geblockteSpalte);
//        }
//		
//		
//		//********************************************************************************************
//    	spieler_alt=null;
//        SpielStrategie erg = null;
//    	boolean erreichbar = false;
//    	int abstand = -1;
//    	Weg weg = null;
//    	System.out.println("Beste m�glichkeit ausprobieren;):");
//    	for (int dir = SpielStrategie.DIR_OBEN; dir < SpielStrategie.DIR_RECHTS; dir++) 
//    	{
//    		for (int index = 1; index < 6; index += 2) 
//    		{
//    			System.out.println("\tPosition:"+ SpielStrategie.dir_array[dir] + ", Index " + index);
//    			if (pruefeZugGueltigkeit(index, dir)) {
//    				for (int j = 0; j < 4; j++) {
//    					SpielStrategie copy = new SpielStrategie(brett, spieler_alt);
//    					freieKachel = freieKachel.getGedrehtUm90Grad();
//
//
//    					weg = copy.findeKuerzestenWeg();
//    					copy.spieler.spalte = weg.zielSpalte;
//    					copy.spieler.zeile = weg.zielZeile;
//    					int meinAbstand = copy.getAbstandSpielerSchatz();
//    					System.out.println("\t\tAbstand mit Drehung "
//    							+ (j * 90) + "�:" + meinAbstand);
//    					if (abstand == -1 || meinAbstand < abstand) {
//    						erg = copy;
//    						abstand = meinAbstand;
//    					}
//
//    					if (copy.spieler.spalte == copy.schatzSpalte
//    							&& copy.spieler.zeile == copy.schatzZeile) {
//    						erg = copy;
//    						erreichbar = true;
//    						//Innere Schleifef abbrechen ????
//    						break;
//    					}
//    				}
//    			}
//    		}
//    	}
//    	if (erg.spieler.equals(spieler_alt)) {
//    		System.out.println("->zufallllllll");
//    		SpielStrategie spiel_zufall = new SpielStrategie(brett, spieler_alt);
//    		spiel_zufall.movePlayerRandomly();
//    		spiel_zufall.setzeZufaellig();;
//    		erg = spiel_zufall;
//    	}
//    	spieler_alt = spieler;
//    	//**********************************************************************
//    	//Die �bergabe die du brauchst???????
//    	AIOutModel aiout = new AIOutModel();
//    	aiout.player_column=spieler.spalte;
//    	aiout.player_row=spieler.zeile;
//    	aiout.card_row=schatzZeile;
//    	aiout.card_column=schatzSpalte;
//    	aiout.shiftCard=board.getShiftCard();
//		return aiout;
	}
	
}


