import java.util.*;

public class Backtracking extends Problem {

	public HashMap<Integer, LinkedList<Integer>> paths; // en position 0, le départ; en dernière position, la dernière
														// case explorée
	public HashMap<Integer, List<Integer>> voisinsVides; // voisins non encore visités; clé = un sommet, valeur = les
															// sommets adjacents non occupés
	public LinkedList<Integer> aExplorer; // chemins non encore complétés
	public int nbSolutions; // Pour une grille, le nombre de solutions existantes

	public Backtracking(int d) {
		super(d);
		this.paths = new HashMap<>();
		this.voisinsVides = new HashMap<>();
		for (int v = 0; v < n; v++) {
			voisinsVides.put(v, new LinkedList<>());
			for (int w : voisins.get(v)) {
				voisinsVides.get(v).add(w);
			}
		}
		this.aExplorer = new LinkedList<>();
		this.nbSolutions = 0;

	}

	void fillBlanks() {
		// attribue -1 comme numéro à toutes les cases n'étant pas un départ/arrivée, dans la map des
		// numéros
		for (int i = 0; i < d * d; i++) {
			if (!aRelier.containsKey(i)) {
				numeros.put(i, -1);
			}
		}
	}

	public void initialize() {
		// Une fois qu'on a configuré le problème, le Backtracking s'initialise
		// On remplit les cases vides avec -1 par défaut, et on initialise tout chemin i
		// avec son départ, en considérant i faisant désormais partie de aExplorer
		// On actualise les voisins vides, en retirant dans toutes les valeurs le départ
		// de i et son arrivee
		fillBlanks();
		
		//Réinitialisation des voisins vides
		this.voisinsVides = new HashMap<>();
		for (int v = 0; v < n; v++) {
			voisinsVides.put(v, new LinkedList<>());
			for (int w : voisins.get(v)) {
				voisinsVides.get(v).add(w);
			}
		}

		for (int i = 1; i < nbARelier + 1; i++) {
			Integer extremité = depart.get(i);
			paths.put(i, new LinkedList<>());
			paths.get(i).add(extremité);
			aExplorer.add(i);
			voisins.forEach((k, v) -> {
				if (v.contains(extremité)) {
					voisinsVides.get(k).remove(extremité);

				}
			});
			Integer fin = arrivee.get(i);
			voisins.forEach((k, v) -> {
				if (v.contains(fin)) {
					voisinsVides.get(k).remove(fin);

				}
			});

		}
	}

	public boolean solve() {
		// Cas de base : grille pleine et tous les chemins sont complets (<=> il ne
		// reste plus de chemin aExplorer et aucune case n'a la valeur par défaut -1)
		if (!numeros.containsValue(-1) && aExplorer.isEmpty())
			return true;

		// Cas où tous les chemins sont complets mais il reste des cases vides
		if (aExplorer.isEmpty())
			return false;

		// Récupération du sommet prioritaire parmis les extrémités de chemins non finis
		// (i € aExplorer) : le moins de voisins possible et en cas de conflit, celui
		// qui a parcouru jusque là le plus court chemin.
		int j = aExplorer.getFirst();
		Integer c = paths.get(j).getLast();

		for (int i : aExplorer) {
			Integer tmp = paths.get(i).getLast();
			int voisinsC = voisinsVides.get(c).size();
			int voisinsTmp = voisinsVides.get(tmp).size();

			if (voisinsTmp < voisinsC && voisinsTmp > 0) {
				c = tmp;
				j = i;
			} else if (voisinsTmp == voisinsC) {
				int longueurCheminTmp = paths.get(i).size();
				int longueurCheminC = paths.get(j).size();
				if (longueurCheminTmp < longueurCheminC) {
					c = tmp;
					j = i;
				}
			}
		}
		// On explore donc le "bout de chemin" c, correspondant au numéro j.

		int fin = arrivee.get(j);

		for (int v : voisins.get(c)) {
			// premier cas : le voisin de c exploré est inoccupé ; on l'ajoute au chemin
			// courant, on actualise son numéro et toutes les listes de voisins vides,
			// et on résout récursivement.

			if (voisinsVides.get(c).contains(v)) {
				paths.get(j).add(v);
				numeros.put(v, j);
				// System.out.println("Voisin exploré :" + v);
				// System.out.println("Il a pour voisins : "+voisins.get(v));

				for (int v2 : voisins.get(v)) {
					int index = voisinsVides.get(v2).indexOf(v);
					voisinsVides.get(v2).remove(index);
				}

				if (solve()) {
					return true;
				} else {
					// Si la résolution récursive n'est pas possible : REBROUSSEMENT
					paths.get(j).removeLast();
					numeros.put(v, -1);
					for (int v2 : voisins.get(v)) {
						voisinsVides.get(v2).add(v);
					}
				}
			} else if (v == fin) {
				// Second cas : le voisin de c exploré est la fin du chemin en cours ; on
				// l'ajoute au chemin courant, et on considère qu'il n'est plus nécessaire
				// d'explorer le chemin j (on enlève donc j de aExplorer), et on résout
				// récursivement.
				paths.get(j).add(fin);
				int indexj = aExplorer.indexOf(j);
				aExplorer.remove(indexj);
				if (solve()) {
					return true;
				} else {
					// REBROUSSEMENT
					paths.get(j).removeLast();
					aExplorer.add(j);
				}
			}
		}
		return false;

	}

	public boolean solveAndCount() {
		// Le cas de base est modifié : au lieu de renvoyer true, on incrémente le
		// compteur de solutions et on renvoie false. Le reste de l'algorithme ne change
		// pas.
		if (!numeros.containsValue(-1) && aExplorer.isEmpty()) {
			nbSolutions++;

			// Décommenter ces lignes pour afficher la solution supplémentaire :
			// this.print();
			// System.out.println("Youpi ! Une solution de plus :D");

			return false;
		}

		// Cas où tous les chemins sont complets mais il reste des cases vides
		if (aExplorer.isEmpty())
			return false;

		// Récupération du sommet prioritaire parmis les extrémités de chemins non finis
		// (i € aExplorer) : le moins de voisins possible et en cas de conflit, celui
		// qui a parcouru jusque là le plus court chemin.
		int j = aExplorer.getFirst();
		Integer c = paths.get(j).getLast();

		for (int i : aExplorer) {
			Integer tmp = paths.get(i).getLast();
			int voisinsC = voisinsVides.get(c).size();
			int voisinsTmp = voisinsVides.get(tmp).size();

			if (voisinsTmp < voisinsC && voisinsTmp > 0) {
				c = tmp;
				j = i;
			} else if (voisinsTmp == voisinsC) {
				int longueurCheminTmp = paths.get(i).size();
				int longueurCheminC = paths.get(j).size();
				if (longueurCheminTmp < longueurCheminC) {
					c = tmp;
					j = i;
				}
			}
		}
		// On explore donc le "bout de chemin" c, correspondant au numéro j.

		int fin = arrivee.get(j);

		for (int v : voisins.get(c)) {
			// premier cas : le voisin de c exploré est inoccupé ; on l'ajoute au chemin
			// courant, on actualise son numéro et toutes les listes de voisins vides,
			// et on résout récursivement.

			if (voisinsVides.get(c).contains(v)) {
				paths.get(j).add(v);
				numeros.put(v, j);
				// System.out.println("Voisin exploré :" + v);
				// System.out.println("Il a pour voisins : "+voisins.get(v));

				for (int v2 : voisins.get(v)) {
					int index = voisinsVides.get(v2).indexOf(v);
					voisinsVides.get(v2).remove(index);
				}

				if (solveAndCount()) {
					return true;
				} else {
					// Si la résolution récursive n'est pas possible : REBROUSSEMENT
					paths.get(j).removeLast();
					numeros.put(v, -1);
					for (int v2 : voisins.get(v)) {
						voisinsVides.get(v2).add(v);
					}
				}
			} else if (v == fin) {
				// Second cas : le voisin de c exploré est la fin du chemin en cours ; on
				// l'ajoute au chemin courant, et on considère qu'il n'est plus nécessaire
				// d'explorer le chemin j (on enlève donc j de aExplorer), et on résout
				// récursivement.
				paths.get(j).add(fin);
				int indexj = aExplorer.indexOf(j);
				aExplorer.remove(indexj);
				if (solveAndCount()) {
					return true;
				} else {
					// REBROUSSEMENT
					paths.get(j).removeLast();
					aExplorer.add(j);
				}
			}
		}
		return false;

	}

	public Backtracking solveMoreThan(int k) {
		// Cette méthode renvoie une solution de la grille si il existe plus de k
		// solutions possibles au problème. Autrement, il renvoie une solution vide
		// (null)
		Backtracking bt = this;
		this.solveAndCount();
		if (this.nbSolutions >= k) {
			bt.solve();
			return bt;
		} else {
			return null;
		}
	}

	public void print() {
		// La méthode print est similaire à celle de Problem, en revanche elle diffère
		// par son traitement des numéros des cases (on attribue "0" à l'affichage si sa
		// valeur est la valeur par défaut -1)
		for (int i = 0; i < d; i++) {
			LinkedList<Integer> ligne = new LinkedList<Integer>();
			for (int j = 0; j < d; j++) {

				if (numeros.get(Case(i, j)) < 0) {
					ligne.add(0);

				} else {
					ligne.add(numeros.get(Case(i, j)));
				}

			}
			System.out.println(ligne);
		}
	}

}
