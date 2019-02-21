import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

public class Problem extends Grid {

	HashMap<Integer, Integer> numeros = new HashMap<Integer, Integer>(); // Pour un sommet à relier à un autre, indique
																			// son numéro (exemple avec l'énoncé : 3 et
																			// 46 renvoient au numéro 4
	HashMap<Integer, Integer> aRelier = new HashMap<Integer, Integer>(); // Pour un sommet à relier à un autre, indique
																			// l'autre sommet (exemple avec l'énoncé : 3
																			// renvoie vers 46, et inversement)
	HashMap<Integer, Integer> depart = new HashMap<Integer, Integer>(); // Pour un numéro donné, renvoie le départ du
																		// chemin à construire (exemple avec l'énoncé :
																		// 4 renvoie vers 3)
	HashMap<Integer, Integer> arrivee = new HashMap<Integer, Integer>(); // Pour un numéro donné, renvoie le départ du
																			// chemin à construire (exemple avec
																			// l'énoncé : 4 renvoie vers 46)
	int nbARelier;

	Problem(int d) { // Construit un problème initialement vide de taille d
		super(d);
		numeros = new HashMap<Integer, Integer>();
		aRelier = new HashMap<Integer, Integer>();
		depart = new HashMap<Integer, Integer>();
		arrivee = new HashMap<Integer, Integer>();
		nbARelier = 0;
	}

	void ajoute(int sommet0, int sommet1) { // Ajoute deux sommets à relier au problème

		numeros.put(sommet0, nbARelier + 1);
		numeros.put(sommet1, nbARelier + 1);
		depart.put(nbARelier + 1, sommet0);
		arrivee.put(nbARelier + 1, sommet1);
		aRelier.put(sommet0, sommet1);
		aRelier.put(sommet1, sommet0);
		nbARelier++;

	}

	void print() { // Imprime la carte d'un problème (non résolu)

		for (int i = 0; i < d; i++) {
			LinkedList<Integer> ligne = new LinkedList<Integer>();
			for (int j = 0; j < d; j++) {

				if (aRelier.containsKey(Case(i, j)) || aRelier.containsValue(Case(i, j))) {
					ligne.add(numeros.get(Case(i, j)));
				} else {
					ligne.add(0);
				}

			}
			System.out.println(ligne);
		}
	}

	// TASK 1 : Résout un problème avec la méthode des chemins donnée par l'énoncé

	int[][] solveurPaths() {

		ISolver solver = SolverFactory.newDefault();

		// CONDITION 1 : Chaque sommet est sur une seule position d'un seul chemin

		for (int v = 0; v < n; v++) {
			int[] clauseOu = new int[n * nbARelier];
			for (int i = 0; i < nbARelier; i++) {
				for (int p = 0; p < n; p++) {
					clauseOu[i * n + p] = variable(v, i, p);
				}
			}
			try {
				solver.addClause(new VecInt(clauseOu));
			} catch (ContradictionException e) {
				e.printStackTrace();
			}
			for (int i = 0; i < nbARelier; i++) {
				for (int i2 = i + 1; i2 < nbARelier; i2++) {
					for (int p = 0; p < n; p++) {
						for (int p2 = 0; p2 < n; p2++) {

							try {
								solver.addClause(new VecInt(new int[] { -variable(v, i, p), -variable(v, i2, p2) }));
							} catch (ContradictionException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			for (int i = 0; i < nbARelier; i++) {
				for (int p = 0; p < n; p++) {
					for (int p2 = p + 1; p2 < n; p2++) {
						try {
							solver.addClause(new VecInt(new int[] { -variable(v, i, p), -variable(v, i, p2) }));
						} catch (ContradictionException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		// CONDITION 2 : Chaque position d'un chemin est occupée par un unique sommet
		// (ou le sommet fantome)

		for (int i = 0; i < nbARelier; i++) {
			for (int p = 0; p < n; p++) {
				int[] clauseOu = new int[n + 1];
				for (int v = 0; v <= n; v++) {
					clauseOu[v] = variable(v, i, p);
					for (int v1 = 0; v1 < v; v1++) {
						try {
							solver.addClause(new VecInt(new int[] { -variable(v, i, p), -variable(v1, i, p) }));
						} catch (ContradictionException e) {
							e.printStackTrace();
						}
					}
				}
				try {
					solver.addClause(new VecInt(clauseOu));
				} catch (ContradictionException e) {
					e.printStackTrace();
				}
			}
		}

		// CONDITION 3 : Si le chemin est fini avant p, il est fini avant p+1

		for (int i = 0; i < nbARelier; i++) {
			for (int p = 0; p < n - 1; p++) {
				try {
					solver.addClause(new VecInt(new int[] { -variable(n, i, p), variable(n, i, p + 1) }));
				} catch (ContradictionException e) {
					e.printStackTrace();
				}
			}
		}

		// CONDITION 4 : Les sommets consécutifs d'un chemin sont voisins (sauf cas
		// sommet fantome)

		for (int v = 0; v < n; v++) {
			List<Integer> vois = voisins(v);
			int[] tab = new int[vois.size() + 1];
			int k = 0;
			for (int voisin : vois) {
				tab[k] = voisin;
				k++;
			}
			tab[vois.size()] = n;
			for (int i = 0; i < nbARelier; i++) {
				for (int p = 0; p < n - 1; p++) {
					int[] variables = new int[vois.size() + 2];
					for (int j = 0; j < vois.size() + 1; j++) {
						variables[j] = variable(tab[j], i, p + 1);
					}
					variables[vois.size() + 1] = -variable(v, i, p);
					try {
						solver.addClause(new VecInt(variables));
					} catch (ContradictionException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// CONDITION 5 : le départ du chemin est en position 0, l'arrivée est suivie du
		// fantome

		for (int i = 0; i < nbARelier; i++) {
			try {
				solver.addClause(new VecInt(new int[] { variable(depart.get(i + 1), i, 0) }));
			} catch (ContradictionException e) {
				e.printStackTrace();
			}
			for (int p = 0; p < n - 1; p++) {
				int[] tab = new int[2];
				tab[0] = -variable(arrivee.get(i + 1), i, p);
				tab[1] = variable(n, i, p + 1);
				try {
					solver.addClause(new VecInt(tab));
				} catch (ContradictionException e) {
					e.printStackTrace();
				}
			}
		}

		// CONDITION 6 : l'arrivée est bien dans le bon chemin correspondant

		for (int i = 0; i < nbARelier; i++) {
			int[] tab = new int[n];
			for (int p = 0; p < n; p++) {
				tab[p] = variable(arrivee.get(i + 1), i, p);
			}
			try {
				solver.addClause(new VecInt(tab));
			} catch (ContradictionException e) {
				e.printStackTrace();
			}
		}

		// RESOLUTION

		try {
			if (solver.isSatisfiable()) {
				int[] solution = solver.model();
				int[][] chemins = new int[nbARelier][n];
				for (int i = 0; i < nbARelier; i++) {
					for (int p = 0; p < n; p++) {
						chemins[i][p] = n;
					}
				}
				for (int v = 0; v < n; v++) {
					for (int i = 0; i < nbARelier; i++) {
						for (int p = 0; p < n; p++) {
							if (solution[variable(v, i, p) - 1] > 0) {

								chemins[i][p] = v;
							}
						}
					}
				}

				return chemins;

			}
		} catch (TimeoutException e1) {
			System.out.println("Timeout, sorry!");
		}
		return null;
	}

	int variable(int v, int i, int p) { // La variable x_v,i,p de la question 1
		return v * (n * nbARelier) + i * n + p + 1;
	}

	void printSolutionPaths() { // Une fonction qui imprime la solution du problème, basée sur la résolution de
		// la question 1
		int[][] solution = this.solveurPaths();
		if (solution == null) {
			System.out.println("Unsatisfiable problem!");
			return;
		}
		System.out.println("Satisfiable problem!");
		System.out.println();
		for (int i = 0; i < nbARelier; i++) {
			System.out.println(Arrays.toString(solution[i]));
		}

		System.out.println();

		int[][] carte = new int[d][d];
		int numChemin = 1;
		for (int[] chemin : solution) {
			for (int point : chemin) {
				if (point != n) {
					int a = point / d;
					int b = point % d;
					carte[a][b] = numChemin;
				}
			}
			numChemin++;
		}
		for (int i = 0; i < d; i++) {
			System.out.println(Arrays.toString(carte[i]));
		}
		System.out.println();
	}

	// TASK 2 : Solveur basé sur les bords

	int[] solveurEdges() {
		return solveurEdges(SolverFactory.newDefault());
	}

	int[] solveurEdges(ISolver solver) { // Cette fonction résout le problème avec l'aide des variables x_e,i définies
											// par l'énoncé. Ici, les variables seront en réalités des variables x_x,y,i
											// avec x voisin de y et x<y, mais celà revient au même. On ajoute la
											// possibilité de donner un solveur en argument (ceci servira pour
											// la Task 3).

		// CONDITION 1 : Chaque arete est sur un seul chemin au plus

		for (int x = 0; x < n; x++) {
			for (int y : voisins(x)) {
				for (int i = 0; i < nbARelier; i++) {
					for (int j = 0; j < i; j++) {
						try {
							solver.addClause(new VecInt(new int[] { -variableEdges(min(x, y), max(x, y), i),
									-variableEdges(min(x, y), max(x, y), j) }));
						} catch (ContradictionException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		// Tri des sommets : les sommets qui n'auront qu'une arete sur un chemin, et
		// ceux qui en auront deux sur un chemin.

		LinkedList<Integer> sommetsLimites = new LinkedList<Integer>();
		LinkedList<Integer> sommetsNormaux = new LinkedList<Integer>();

		for (int i = 0; i < n; i++) {

			if (aRelier.containsKey(i)) {
				sommetsLimites.add(i);
			} else {
				sommetsNormaux.add(i);
			}
		}

		// CONDITION 2 : Chaque sommet limite est contenu dans 1 arete du chemin i et
		// dans aucun autre chemin

		for (int x : sommetsLimites) {
			int bonChemin = numeros.get(x) - 1;
			for (int i = 0; i < nbARelier; i++) {
				if (i == bonChemin) {
					int[] tabAretes = new int[voisins(x).size()];
					for (int j = 0; j < voisins(x).size(); j++) {
						int y = voisins(x).get(j);
						tabAretes[j] = variableEdges(min(x, y), max(x, y), i);
					}
					try {
						solver.addClause(new VecInt(tabAretes));
					} catch (ContradictionException e) {
						e.printStackTrace();
					}
					for (int j = 0; j < voisins(x).size(); j++) {
						for (int k = 0; k < j; k++) {
							try {
								solver.addClause(new VecInt(new int[] { -tabAretes[j], -tabAretes[k] }));
							} catch (ContradictionException e) {
								e.printStackTrace();
							}
						}
					}
				} else {
					for (int y : voisins(x)) {
						try {
							solver.addClause(new VecInt(new int[] { -variableEdges(min(x, y), max(x, y), i) }));
						} catch (ContradictionException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		// CONDITION 3 : Tout sommet non limite d'un chemin est parcouru par 2 aretes
		// sur un seul chemin

		for (int i = 0; i < nbARelier; i++) { // Pour tout x, il n'existe pas (e0, e1) et (i,j) avec i != j tel que
												// x_e0,i et x_e1,j
			for (int j = 0; i > j; j++) {
				for (int x : sommetsNormaux) {
					for (int y : voisins(x)) {
						for (int z : voisins(x)) {
							int va = variableEdges(min(x, y), max(x, y), i);
							int vb = variableEdges(min(x, z), max(x, z), j);
							try {
								solver.addClause(new VecInt(new int[] { -va, -vb }));
							} catch (ContradictionException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}

		for (int x : sommetsNormaux) { // Pour tout x, un (x_e,i) est vrai, et à ce moment là, un autre (x_e0,i) est
										// vrai pour le meme i
			int[] clauseOu = new int[nbARelier * voisins(x).size()];
			int compteur = 0;
			for (int i = 0; i < nbARelier; i++) {
				for (int y : voisins(x)) {
					clauseOu[compteur] = variableEdges(min(x, y), max(x, y), i);
					compteur++;
					int[] aumoins2Voisins = new int[voisins(x).size()];
					int compteur2 = 0;
					for (int z : voisins(x)) {
						if (z != y) {
							aumoins2Voisins[compteur2] = variableEdges(min(x, z), max(x, z), i);
						} else {
							aumoins2Voisins[compteur2] = -variableEdges(min(x, y), max(x, y), i);
						}
						compteur2++;
					}
					try {
						solver.addClause(new VecInt(aumoins2Voisins));
					} catch (ContradictionException e) {
						e.printStackTrace();
					}
				}
			}
			try {
				solver.addClause(new VecInt(clauseOu));
			} catch (ContradictionException e) {
				e.printStackTrace();
			}
		}

		for (int x : sommetsNormaux) { // Pas 3 aretes ou plus partant d'un meme sommet sur un meme chemin
			for (int i = 0; i < nbARelier; i++) {
				for (int y : voisins(x)) {
					for (int z : voisins(x)) {
						for (int t : voisins(x)) {

							if (y != z && y != t && z != t) {

								int vy = variableEdges(min(x, y), max(x, y), i);
								int vz = variableEdges(min(x, z), max(x, z), i);
								int vt = variableEdges(min(x, t), max(x, t), i);

								try {
									solver.addClause(new VecInt(new int[] { -vy, -vz, -vt }));
								} catch (ContradictionException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		}

		// RESOLUTION DU SOLVER

		try {
			if (solver.isSatisfiable()) {
				int[] solution = solver.model();

				return solution;
			}
		} catch (TimeoutException e1) {
			System.out.println("Timeout, sorry!");
		}
		return null;
	}

	private int max(int x, int z) {
		if (x > z)
			return x;
		return z;
	}

	private int min(int x, int z) {
		if (x > z)
			return z;
		return x;
	}

	int variableEdges(int x, int y, int i) { // Pour une arete e = (x,y) donnée, renvoie un entier spécifiquement choisi
												// pour représenter x_e,i
		if (!voisins(x).contains(y)) {
			return 0;
		}
		if (x > y) {
			return 0;
		}
		if (x < y) {
			return x * n * nbARelier + y * nbARelier + i + 1;
		}
		return y * n * nbARelier + x * nbARelier + i + 1;
	}

	// TASK 3 : Empécher les cycles

	LinkedList<int[]> Cycle(int[] solution) { // Cette fonction teste s'il y a un cycle dans notre solution, et si oui,
												// ajoute un ensemble de clauses qui interdit aux sommets dans le cycle
												// d'être sur le meme chemin.

		HashMap<Integer, LinkedList<Integer>> pointeVers = new HashMap<Integer, LinkedList<Integer>>(); // Cette Hashmap
																										// répertoriera
																										// tous les
																										// sommets
																										// reliés, à la
																										// clé, elle
																										// nous servira
																										// pour chercher
																										// les cycles

		LinkedList<int[]> clausesARajouter = new LinkedList<int[]>(); // Cette liste contiendra les clauses à ajouter au
																		// solveur

		for (int k = 0; k < n; k++) { // On remplit la Hashmap
			pointeVers.put(k, new LinkedList<Integer>());
		}

		for (int variable : solution) {
			if (variable > 0 && variable <= n * n * nbARelier && -variable <= n * n * nbARelier) {

				int lol = variable - 1;
				int x = lol / (n * nbARelier);
				int y = (lol % (n * nbARelier)) / nbARelier;

				pointeVers.get(x).add(y);
				pointeVers.get(y).add(x);

			}
		}

		for (int k = 0; k < n; k++) { // Pour un sommet k donné, on regarde s'il est dans un cycle
			int sommetDepart = k;
			int sommetPrecedent = k;
			int sommetCourant = pointeVers.get(k).get(0);
			LinkedList<Integer> chemin = new LinkedList<Integer>();

			chemin.add(k);
			chemin.add(sommetCourant);

			while (sommetCourant != sommetDepart && !aRelier.containsKey(sommetCourant)) { // On part de k et on remplit
																							// le chemin de proche en
																							// proche

				if (pointeVers.get(sommetCourant).get(0) != sommetPrecedent) {
					sommetPrecedent = sommetCourant;
					chemin.add(pointeVers.get(sommetCourant).get(0));
					sommetCourant = pointeVers.get(sommetCourant).get(0);
				} else if (pointeVers.get(sommetCourant).get(1) != sommetPrecedent) {

					sommetPrecedent = sommetCourant;
					chemin.add(pointeVers.get(sommetCourant).get(1));
					sommetCourant = pointeVers.get(sommetCourant).get(1);

				} else {
					break;
				}

			}

			if (sommetCourant == sommetDepart) { // Si on retombe sur le départ (sommet k), on a un cycle. Alors on
													// ajoute des clauses pour imposer aux sommets du cycle de ne pas
													// etre ensemble.

				for (int i = 0; i < nbARelier; i++) {

					int[] clause = new int[chemin.size() - 1];

					for (int m = 0; m < chemin.size() - 1; m++) {
						clause[m] = -variableEdges(min(chemin.get(m), chemin.get(m + 1)),
								max(chemin.get(m), chemin.get(m + 1)), i);
					}

					clausesARajouter.add(clause);

				}

				return clausesARajouter;
			}

		}

		return clausesARajouter;
	}

	int[] solveurEdgesWithoutCycle() { // Utilise la méthode de la question 2, et la réitère à chaque fois qu'on trouve
										// un cycle en rajoutant les clauses données par la fonction Cycle

		ISolver solver = SolverFactory.newDefault();
		int[] solutionProvisoire = solveurEdges(solver);

		if (solutionProvisoire == null) {
			return null;
		}
		LinkedList<int[]> cycle = Cycle(solutionProvisoire);
		LinkedList<int[]> clausesARajouter = new LinkedList<int[]>();
		solver = SolverFactory.newDefault();

		while (!cycle.isEmpty()) {

			clausesARajouter.addAll(cycle);
			for (int[] clause : clausesARajouter) {

				try {
					solver.addClause(new VecInt(clause));
				} catch (ContradictionException e) {
					e.printStackTrace();
				}
			}
			cycle = Cycle(solveurEdges(solver));

			solver = SolverFactory.newDefault(); // On est obligés de remettre à chaque fois le solveur à 0, car la
													// fonction solveurEdges le modifie par effet de bord
		}

		for (int[] clause : clausesARajouter) {

			try {
				solver.addClause(new VecInt(clause));
			} catch (ContradictionException e) {
				e.printStackTrace();
			}
		}

		return solveurEdges(solver);

	}

	void printSolutionEdges() { // Imprime la solution du problème avec cette dernière résolution.

		int[] solution = this.solveurEdgesWithoutCycle();
		if (solution == null) {
			System.out.println("Unsatisfiable problem!");
			return;
		}

		System.out.println("Satisfiable problem!");
		System.out.println();

		LinkedList<LinkedList<LinkedList<Integer>>> chemins = new LinkedList<LinkedList<LinkedList<Integer>>>();

		for (int i = 0; i < nbARelier; i++) {
			chemins.add(new LinkedList<LinkedList<Integer>>());
		}

		for (int variable : solution) {
			if (variable > 0 && variable <= n * n * nbARelier && -variable <= n * n * nbARelier) {

				int lol = variable - 1;
				int x = lol / (n * nbARelier);
				int y = (lol % (n * nbARelier)) / nbARelier;
				int i = (lol % nbARelier);
				LinkedList<Integer> arete = new LinkedList<Integer>();
				arete.add(x);
				arete.add(y);
				chemins.get(i).add(arete);
			}
		}

		for (LinkedList<LinkedList<Integer>> chemin : chemins) {
			System.out.println(chemin);
		}

		System.out.println();

		int[][] carte = new int[d][d];
		int m = 0;
		for (LinkedList<LinkedList<Integer>> chemin : chemins) {
			m++;
			for (LinkedList<Integer> arete : chemin) {
				for (int sommet : arete) {
					carte[sommet / d][sommet % d] = m;
				}
			}
		}

		for (int i = 0; i < d; i++) {
			System.out.println(Arrays.toString(carte[i]));
		}

		System.out.println();
	}

	// TASK 4 : Solveur basé sur les configurations des sommets

	int[] solveurVertices() {
		return solveurVertices(SolverFactory.newDefault());
	}

	int[] solveurVertices(ISolver solver) { // Cette méthode met en place la résolution de la task 4 de l'énoncé. On a
											// 10 configurations "sigma" possibles (les 6 de l'énoncé auxquelles on en a
											// ajouté 4 pour les sommets limites d'un chemin, voir fonction
											// variableVertice).

		// CONDITION 1 : Pour v donné, un unique x_v,i,sigma est vrai

		for (int v = 0; v < n; v++) {

			for (int i = 0; i < nbARelier; i++) {
				for (int i2 = i + 1; i2 < nbARelier; i2++) {
					for (int sigma = 0; sigma < 10; sigma++) {
						for (int sigma2 = 0; sigma2 < 10; sigma2++) {

							try {
								solver.addClause(new VecInt(
										new int[] { -variableVertice(v, i, sigma), -variableVertice(v, i2, sigma2) }));
							} catch (ContradictionException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			for (int i = 0; i < nbARelier; i++) {
				for (int sigma = 0; sigma < 10; sigma++) {
					for (int sigma2 = sigma + 1; sigma2 < 10; sigma2++) {
						try {
							solver.addClause(new VecInt(
									new int[] { -variableVertice(v, i, sigma), -variableVertice(v, i, sigma2) }));
						} catch (ContradictionException e) {
							e.printStackTrace();
						}
					}
				}
			}

			int[] clauseOu = new int[10 * nbARelier];
			for (int i = 0; i < nbARelier; i++) {
				for (int sigma = 0; sigma < 10; sigma++) {
					clauseOu[i * 10 + sigma] = variableVertice(v, i, sigma);
				}
			}
			try {
				solver.addClause(new VecInt(clauseOu));
			} catch (ContradictionException e) {
				e.printStackTrace();
			}

		}

		// Tri des sommets

		LinkedList<Integer> sommetsLimites = new LinkedList<Integer>();
		LinkedList<Integer> sommetsNormaux = new LinkedList<Integer>();

		for (int i = 0; i < n; i++) {

			if (aRelier.containsKey(i)) {
				sommetsLimites.add(i);
			} else {
				sommetsNormaux.add(i);
			}
		}

		// CONDITION 2 : Si le sommet est un sommet limite, i = le numero du sommet en
		// question et sigma = 6, 7, 8 ou 9

		for (int v : sommetsLimites) {

			int[] tab = new int[10];

			for (int sigma = 0; sigma < 10; sigma++) {

				tab[sigma] = variableVertice(v, numeros.get(v) - 1, sigma);

			}

			try {
				solver.addClause(new VecInt(tab));
			} catch (ContradictionException e) {
				e.printStackTrace();
			}

			for (int i = 0; i < nbARelier; i++) {

				for (int sigma = 0; sigma < 6; sigma++) {
					try {
						solver.addClause(new VecInt(new int[] { -variableVertice(v, i, sigma) }));
					} catch (ContradictionException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// CONDITION 3 : Si le sommet est un sommet normal, sigma = 0, 1, 2, 3, 4 ou 5

		for (int v : sommetsNormaux) {
			for (int i = 0; i < nbARelier; i++) {

				for (int sigma = 6; sigma < 10; sigma++) {
					try {
						solver.addClause(new VecInt(new int[] { -variableVertice(v, i, sigma) }));
					} catch (ContradictionException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// CONDITION 4 : Si le sommet est en bordure haute, sigma = 0,4,5,6,8 ou 9

		for (int x = 0; x < d; x++) {
			for (int i = 0; i < nbARelier; i++) {

				for (int sigma : new int[] { 1, 2, 3, 7 }) {
					try {
						solver.addClause(new VecInt(new int[] { -variableVertice(x, i, sigma) }));
					} catch (ContradictionException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// CONDITION 5 : Si le sommet est en bordure basse, sigma = 0,2,3,6,7, ou 8

		for (int x = 0; x < d; x++) {
			for (int i = 0; i < nbARelier; i++) {

				for (int sigma : new int[] { 1, 4, 5, 9 }) {
					try {
						solver.addClause(new VecInt(new int[] { -variableVertice(x + d * (d - 1), i, sigma) }));
					} catch (ContradictionException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// CONDITION 6 : Si le sommet est en bordure gauche, sigma = 1,3,4,7,8, ou 9

		for (int x = 0; x < d; x++) {
			for (int i = 0; i < nbARelier; i++) {

				for (int sigma : new int[] { 0, 2, 5, 6 }) {
					try {
						solver.addClause(new VecInt(new int[] { -variableVertice(x * d, i, sigma) }));
					} catch (ContradictionException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// CONDITION 7 : Si le sommet est en bordure droite, sigma = 1 2 5 6 7 ou 9

		for (int x = 0; x < d; x++) {
			for (int i = 0; i < nbARelier; i++) {

				for (int sigma : new int[] { 0, 3, 4, 8 }) {
					try {
						solver.addClause(new VecInt(new int[] { -variableVertice(x * d + (d - 1), i, sigma) }));
					} catch (ContradictionException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// CONDITION 8 : Les raccords sont bien les bons pour chacune des configurations

		for (int x = 0; x < d; x++) { // Configuration sigma = 0
			for (int y = 1; y < d - 1; y++) {
				for (int i = 0; i < nbARelier; i++) {

					try {
						solver.addClause(new VecInt(new int[] { -variableVertice(x * d + y, i, 0),
								variableVertice(x * d + y - 1, i, 0), variableVertice(x * d + y - 1, i, 4),
								variableVertice(x * d + y - 1, i, 3), variableVertice(x * d + y - 1, i, 8) }));
					} catch (ContradictionException e) {
						e.printStackTrace();
					}

					try {
						solver.addClause(new VecInt(new int[] { -variableVertice(x * d + y, i, 0),
								variableVertice(x * d + y + 1, i, 0), variableVertice(x * d + y + 1, i, 2),
								variableVertice(x * d + y + 1, i, 5), variableVertice(x * d + y + 1, i, 6) }));
					} catch (ContradictionException e) {
						e.printStackTrace();
					}
				}
			}
		}

		for (int x = 1; x < d - 1; x++) { // Configuration sigma = 1
			for (int y = 0; y < d; y++) {
				for (int i = 0; i < nbARelier; i++) {

					try {
						solver.addClause(new VecInt(new int[] { -variableVertice(x * d + y, i, 1),
								variableVertice((x - 1) * d + y, i, 1), variableVertice((x - 1) * d + y, i, 4),
								variableVertice((x - 1) * d + y, i, 5), variableVertice((x - 1) * d + y, i, 9) }));
					} catch (ContradictionException e) {
						e.printStackTrace();
					}

					try {
						solver.addClause(new VecInt(new int[] { -variableVertice(x * d + y, i, 1),
								variableVertice((x + 1) * d + y, i, 1), variableVertice((x + 1) * d + y, i, 2),
								variableVertice((x + 1) * d + y, i, 3), variableVertice((x + 1) * d + y, i, 7) }));
					} catch (ContradictionException e) {
						e.printStackTrace();
					}
				}
			}
		}

		for (int x = 1; x < d; x++) { // Configuration sigma = 2
			for (int y = 1; y < d; y++) {
				for (int i = 0; i < nbARelier; i++) {

					try {
						solver.addClause(new VecInt(new int[] { -variableVertice(x * d + y, i, 2),
								variableVertice(x * d + y - 1, i, 0), variableVertice(x * d + y - 1, i, 4),
								variableVertice(x * d + y - 1, i, 3), variableVertice(x * d + y - 1, i, 8) }));
					} catch (ContradictionException e) {
						e.printStackTrace();
					}

					try {
						solver.addClause(new VecInt(new int[] { -variableVertice(x * d + y, i, 2),
								variableVertice((x - 1) * d + y, i, 1), variableVertice((x - 1) * d + y, i, 4),
								variableVertice((x - 1) * d + y, i, 5), variableVertice((x - 1) * d + y, i, 9) }));
					} catch (ContradictionException e) {
						e.printStackTrace();
					}
				}
			}
		}

		for (int x = 1; x < d; x++) { // Configuration sigma = 3
			for (int y = 0; y < d - 1; y++) {
				for (int i = 0; i < nbARelier; i++) {

					try {
						solver.addClause(new VecInt(new int[] { -variableVertice(x * d + y, i, 3),
								variableVertice(x * d + y + 1, i, 0), variableVertice(x * d + y + 1, i, 2),
								variableVertice(x * d + y + 1, i, 5), variableVertice(x * d + y + 1, i, 6) }));
					} catch (ContradictionException e) {
						e.printStackTrace();
					}

					try {
						solver.addClause(new VecInt(new int[] { -variableVertice(x * d + y, i, 3),
								variableVertice((x - 1) * d + y, i, 1), variableVertice((x - 1) * d + y, i, 4),
								variableVertice((x - 1) * d + y, i, 5), variableVertice((x - 1) * d + y, i, 9) }));
					} catch (ContradictionException e) {
						e.printStackTrace();
					}
				}
			}
		}

		for (int x = 0; x < d - 1; x++) { // Configuration sigma = 4
			for (int y = 0; y < d - 1; y++) {
				for (int i = 0; i < nbARelier; i++) {

					try {
						solver.addClause(new VecInt(new int[] { -variableVertice(x * d + y, i, 4),
								variableVertice(x * d + y + 1, i, 0), variableVertice(x * d + y + 1, i, 2),
								variableVertice(x * d + y + 1, i, 5), variableVertice(x * d + y + 1, i, 6) }));
					} catch (ContradictionException e) {
						e.printStackTrace();
					}

					try {
						solver.addClause(new VecInt(new int[] { -variableVertice(x * d + y, i, 4),
								variableVertice((x + 1) * d + y, i, 1), variableVertice((x + 1) * d + y, i, 2),
								variableVertice((x + 1) * d + y, i, 3), variableVertice((x + 1) * d + y, i, 7) }));
					} catch (ContradictionException e) {
						e.printStackTrace();
					}
				}
			}
		}

		for (int x = 0; x < d - 1; x++) { // Configuration sigma = 5
			for (int y = 1; y < d; y++) {
				for (int i = 0; i < nbARelier; i++) {

					try {
						solver.addClause(new VecInt(new int[] { -variableVertice(x * d + y, i, 5),
								variableVertice(x * d + y - 1, i, 0), variableVertice(x * d + y - 1, i, 4),
								variableVertice(x * d + y - 1, i, 3), variableVertice(x * d + y - 1, i, 8) }));
					} catch (ContradictionException e) {
						e.printStackTrace();
					}

					try {
						solver.addClause(new VecInt(new int[] { -variableVertice(x * d + y, i, 5),
								variableVertice((x + 1) * d + y, i, 1), variableVertice((x + 1) * d + y, i, 2),
								variableVertice((x + 1) * d + y, i, 3), variableVertice((x + 1) * d + y, i, 7) }));
					} catch (ContradictionException e) {
						e.printStackTrace();
					}
				}
			}
		}

		for (int x = 0; x < d; x++) { // Configuration sigma = 6
			for (int y = 1; y < d; y++) {
				for (int i = 0; i < nbARelier; i++) {

					try {
						solver.addClause(new VecInt(new int[] { -variableVertice(x * d + y, i, 6),
								variableVertice(x * d + y - 1, i, 0), variableVertice(x * d + y - 1, i, 4),
								variableVertice(x * d + y - 1, i, 3), variableVertice(x * d + y - 1, i, 8) }));
					} catch (ContradictionException e) {
						e.printStackTrace();
					}
				}
			}
		}

		for (int x = 1; x < d; x++) { // Configuration sigma = 7
			for (int y = 0; y < d; y++) {
				for (int i = 0; i < nbARelier; i++) {

					try {
						solver.addClause(new VecInt(new int[] { -variableVertice(x * d + y, i, 7),
								variableVertice((x - 1) * d + y, i, 1), variableVertice((x - 1) * d + y, i, 4),
								variableVertice((x - 1) * d + y, i, 5), variableVertice((x - 1) * d + y, i, 9) }));
					} catch (ContradictionException e) {
						e.printStackTrace();
					}
				}
			}
		}

		for (int x = 0; x < d; x++) { // Configuration sigma = 8
			for (int y = 0; y < d - 1; y++) {
				for (int i = 0; i < nbARelier; i++) {

					try {
						solver.addClause(new VecInt(new int[] { -variableVertice(x * d + y, i, 8),
								variableVertice(x * d + y + 1, i, 0), variableVertice(x * d + y + 1, i, 2),
								variableVertice(x * d + y + 1, i, 5), variableVertice(x * d + y + 1, i, 6) }));
					} catch (ContradictionException e) {
						e.printStackTrace();
					}
				}
			}
		}

		for (int x = 0; x < d - 1; x++) { // Configuration sigma = 9
			for (int y = 0; y < d; y++) {
				for (int i = 0; i < nbARelier; i++) {

					try {
						solver.addClause(new VecInt(new int[] { -variableVertice(x * d + y, i, 9),
								variableVertice((x + 1) * d + y, i, 1), variableVertice((x + 1) * d + y, i, 2),
								variableVertice((x + 1) * d + y, i, 3), variableVertice((x + 1) * d + y, i, 7) }));
					} catch (ContradictionException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// RESOLUTION

		try {
			if (solver.isSatisfiable()) {
				int[] solution = solver.model();
				return solution;
			}
		} catch (TimeoutException e1) {
			System.out.println("Timeout, sorry!");
		}
		return null;

	}

	int variableVertice(int v, int i, int sigma) { // La variable x_v,i,sigma de la question 4
		return 1 + v + i * n + sigma * n * nbARelier;

		/*
		 * Pour les sommets non limites SIGMA = 0 : - / SIGMA = 1 : | SIGMA = 2 : ┘
		 * SIGMA = 3 : └ / SIGMA = 4 : ┌ / SIGMA = 5 : ┐
		 * 
		 * Pour les sommets limites SIGMA = 6 : ← / SIGMA = 7 : ↑ / SIGMA = 8 : → /
		 * SIGMA = 9 : ↓
		 * 
		 */
	}

	LinkedList<int[]> CycleVertice(int[] solution) { // Cette fonction teste s'il y a un cycle dans notre solution, et
														// si oui,
		// ajoute un ensemble de clauses qui interdit aux sommets dans le cycle
		// d'être sur le meme chemin.

		HashMap<Integer, LinkedList<Integer>> pointeVers = new HashMap<Integer, LinkedList<Integer>>(); // Cette Hashmap
		// répertoriera
		// tous les
		// sommets
		// reliés, à la
		// clé, elle
		// nous servira
		// pour chercher
		// les cycles

		HashMap<Integer, Integer> configuration = new HashMap<Integer, Integer>(); // Cette Hashmap garde en mémoire les
																					// configurations actuelles des
																					// sommets

		LinkedList<int[]> clausesARajouter = new LinkedList<int[]>(); // Cette liste contiendra les clauses à ajouter au
		// solveur

		for (int k = 0; k < n; k++) { // On remplit la Hashmap pointeVers
			pointeVers.put(k, new LinkedList<Integer>());
		}

		for (int variable : solution) {
			if (variable > 0) {

				int lol = variable - 1;
				int v = lol % n;
				int sigma = lol / (n * nbARelier);

				configuration.put(v, sigma);

				switch (sigma) {
				case 0:
					pointeVers.get(v).add(v - 1);
					pointeVers.get(v).add(v + 1);
					break;
				case 1:
					pointeVers.get(v).add(v - d);
					pointeVers.get(v).add(v + d);
					break;
				case 2:
					pointeVers.get(v).add(v - d);
					pointeVers.get(v).add(v - 1);
					break;
				case 3:
					pointeVers.get(v).add(v - d);
					pointeVers.get(v).add(v + 1);
					break;
				case 4:
					pointeVers.get(v).add(v + d);
					pointeVers.get(v).add(v + 1);
					break;
				case 5:
					pointeVers.get(v).add(v + d);
					pointeVers.get(v).add(v - 1);
					break;
				case 6:
					pointeVers.get(v).add(v - 1);
					break;
				case 7:
					pointeVers.get(v).add(v - d);
					break;
				case 8:
					pointeVers.get(v).add(v + 1);
					break;
				case 9:
					pointeVers.get(v).add(v + d);
					break;
				default:
					break;
				}

			}
		}

		for (int k = 0; k < n; k++) { // Pour un sommet k donné, on regarde s'il est dans un cycle
			int sommetDepart = k;
			int sommetPrecedent = k;
			int sommetCourant = pointeVers.get(k).get(0);
			LinkedList<Integer> chemin = new LinkedList<Integer>();

			chemin.add(k);
			chemin.add(sommetCourant);

			while (sommetCourant != sommetDepart && !aRelier.containsKey(sommetCourant)) { // On part de k et on remplit
				// le chemin de proche en
				// proche

				if (pointeVers.get(sommetCourant).get(0) != sommetPrecedent) {
					sommetPrecedent = sommetCourant;
					chemin.add(pointeVers.get(sommetCourant).get(0));
					sommetCourant = pointeVers.get(sommetCourant).get(0);
				} else if (pointeVers.get(sommetCourant).get(1) != sommetPrecedent) {

					sommetPrecedent = sommetCourant;
					chemin.add(pointeVers.get(sommetCourant).get(1));
					sommetCourant = pointeVers.get(sommetCourant).get(1);

				} else {
					break;
				}

			}

			if (sommetCourant == sommetDepart) { // Si on retombe sur le départ (sommet k), on a un cycle. Alors on
				// ajoute des clauses pour imposer aux sommets du cycle de ne pas
				// etre ensemble.

				for (int i = 0; i < nbARelier; i++) {

					int[] clause = new int[chemin.size()];
					int indice = 0;

					for (int v : chemin) {
						clause[indice] = -variableVertice(v, i, configuration.get(v));
						indice++;
					}

					clausesARajouter.add(clause);

				}

				return clausesARajouter;
			}

		}

		return clausesARajouter;
	}

	int[] solveurVerticesWithoutCycle() {
		return solveurVerticesWithoutCycle(null);
	}

	int[] solveurVerticesWithoutCycle(int[] clauseSupplementaire) { // Utilise la méthode de la question 4, et la
																	// réitère à chaque fois qu'on
		// trouve
		// un cycle en rajoutant les clauses données par la fonction CycleVertice

		// La clause supplémentaire servira pour la question 5

		ISolver solver = SolverFactory.newDefault();
		int[] solutionProvisoire = solveurVertices(solver);

		if (solutionProvisoire == null) {
			return null;
		}
		LinkedList<int[]> cycle = CycleVertice(solutionProvisoire);
		LinkedList<int[]> clausesARajouter = new LinkedList<int[]>();
		if (clauseSupplementaire != null) {
			clausesARajouter.add(clauseSupplementaire);
		}
		solver = SolverFactory.newDefault();

		while (!cycle.isEmpty()) {

			clausesARajouter.addAll(cycle);
			for (int[] clause : clausesARajouter) {

				try {
					solver.addClause(new VecInt(clause));
				} catch (ContradictionException e) {
					e.printStackTrace();
				}
			}
			cycle = CycleVertice(solveurVertices(solver));

			solver = SolverFactory.newDefault(); // On est obligés de remettre à chaque fois le solveur à 0, car la
			// fonction solveurEdges le modifie par effet de bord
		}

		for (int[] clause : clausesARajouter) {

			try {
				solver.addClause(new VecInt(clause));
			} catch (ContradictionException e) {
				e.printStackTrace();
			}
		}

		return solveurVertices(solver);

	}

	void printSolutionVertices() { // Imprime 2 cartes représentant la solution,
		// La premiere avec les configurations des sommets
		// La 2ème avec les numéros des chemins traversant les sommets

		int[] solution = this.solveurVerticesWithoutCycle();
		if (solution == null) {
			System.out.println("Unsatisfiable problem!");
			System.out.println();
			return;
		}

		System.out.println("Satisfiable problem!");
		System.out.println();

		int[][] carte1 = new int[d][d];
		char[][] carte2 = new char[d][d];

		for (int var : solution) {

			if (var > 0) {

				int lol = var - 1;

				int v = (lol % n);
				int i = (lol % (n * nbARelier)) / n;
				int sigma = lol / (n * nbARelier);

				int x = (v / d);
				int y = (v % d);

				carte1[x][y] = i + 1;

				switch (sigma) {
				case 0:
					carte2[x][y] = '-';
					break;
				case 1:
					carte2[x][y] = '|';
					break;
				case 2:
					carte2[x][y] = '┘';
					break;
				case 3:
					carte2[x][y] = '└';
					break;
				case 4:
					carte2[x][y] = '┌';
					break;
				case 5:
					carte2[x][y] = '┐';
					break;
				case 6:
					carte2[x][y] = '←';
					break;
				case 7:
					carte2[x][y] = '↑';
					break;
				case 8:
					carte2[x][y] = '→';
					break;
				case 9:
					carte2[x][y] = '↓';
					break;
				default:
					carte2[x][y] = '?';
					break;
				}

			}

		}

		for (int i = 0; i < d; i++) {
			System.out.println(Arrays.toString(carte2[i]));
		}

		System.out.println();

		for (int i = 0; i < d; i++) {
			System.out.println(Arrays.toString(carte1[i]));
		}

		System.out.println();

	}

	// TASK 5 : Déterminer s'il y a 0, 1 ou plusieurs solutions à un problème donné
	
	boolean uniciteSolution() { // Cette fonction prend la solution donnée par le SAT-solver dans
								// solveurVertices, et impose au SAT-solver une clause supplémentaire (au moins
								// un des
								// x_v,i,sigma précédement vrais est faux, i.e. au moins un des sommets est dans
								// une configuration différente). Il refait tourner le SAT-solveur avec cette
								// clause en plus et regarde si la formule reste satisfiable. Si tel est le cas,
								// il y a une autre solution.

		int[] solution = this.solveurVerticesWithoutCycle();
		if (solution == null) {
			System.out.println("Meme pas une seule solution !");
			return false;
		}

		int compteur = 0;

		for (int var : solution) {

			if (var > 0) {
				compteur++;
			}
		}

		int[] clauseSupplementaire = new int[compteur];
		int k = 0;

		for (int var : solution) {

			if (var > 0) {
				clauseSupplementaire[k] = -var;
				k++;
			}
		}

		int[] deuxiemesolution = solveurVerticesWithoutCycle(clauseSupplementaire);

		if (deuxiemesolution != null) {
			System.out.println("Plusieurs solutions !");
			System.out.println();
			return false;
		} else {
			System.out.println("Une unique solution!");
			return true;
		}

	}

}
