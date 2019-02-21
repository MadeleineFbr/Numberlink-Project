import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Grid extends Graph {
	
	int d;

	Grid(int dimension) {

		super(dimension * dimension); //initialise le nombre de sommets à d^2 et les listes de voisins vides
		d = dimension;

		for (int i = 1; i < d - 1; i++) { //sommets du centre, on leur spécifie leurs voisins
			for (int j = 1; j < d - 1; j++) {
				voisins.get(Case(i,j)).add(Case(i-1,j));
				voisins.get(Case(i,j)).add(Case(i+1,j));
				voisins.get(Case(i,j)).add(Case(i,j-1));
				voisins.get(Case(i,j)).add(Case(i,j+1));
			}
		}

		for (int i = 1; i < d - 1; i++) { //sommets des bords gauche et droite, on leur spécifie leurs voisins
			voisins.get(Case(i,0)).add(Case(i-1,0));
			voisins.get(Case(i,0)).add(Case(i+1,0));
			voisins.get(Case(i,0)).add(Case(i,1));

			voisins.get(Case(i,d-1)).add(Case(i-1,d-1));
			voisins.get(Case(i,d-1)).add(Case(i+1,d-1));
			voisins.get(Case(i,d-1)).add(Case(i,d-2));
		}

		for (int j = 1; j < dimension - 1; j++) { //sommets des bords haut et bas, on leur spécifie leurs voisins
			voisins.get(Case(0,j)).add(Case(0,j-1));
			voisins.get(Case(0,j)).add(Case(0,j+1));
			voisins.get(Case(0,j)).add(Case(1,j));

			voisins.get(Case(d-1,j)).add(Case(d-1,j-1));
			voisins.get(Case(d-1,j)).add(Case(d-1,j+1));
			voisins.get(Case(d-1,j)).add(Case(d-2,j));
		}
		
		voisins.get(Case(0,0)).add(Case(0,1));
		voisins.get(Case(0,0)).add(Case(1,0)); //sommets des 4 coins, on leur spécifie leurs voisins
		
		voisins.get(Case(0,d-1)).add(Case(1,d-1));
		voisins.get(Case(0,d-1)).add(Case(0,d-2));
		
		voisins.get(Case(d-1,0)).add(Case(d-2,0));
		voisins.get(Case(d-1,0)).add(Case(d-1,1));
		
		voisins.get(Case(d-1,d-1)).add(Case(d-2,d-1));
		voisins.get(Case(d-1,d-1)).add(Case(d-1,d-2));
		
	}
	
	public int Case(int i,int j) { //Renvoie le numéro du sommet associé à la case de coordonnées (i,j)
		return i*d+j;
	}
	
}
