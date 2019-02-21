import java.util.HashMap;

public class Test {

	public static void main(String[] args) {
		
		//Voici un espace pour tester nos programmes.
		//Vous trouverez ici des exemples de problèmes Numberlink à résoudre (p, p1, p2, p3, p4 et p5 pour la partie 2.1, et b, b1, b2, b3, b4 et b5 pour la partie 2.2) ainsi que des exemples d'appels de méthodes pour chaque question.
		//Pour visualiser les grilles, utilisez la méthode print() (ex : p.print() pour afficher p).
		//Chaque construction de problème est suivie des temps de résolution des différentes méthodes.
		//Vous pouvez décommenter la méthode que vous souhaitez tester, et librement changer le numéro du problème à résoudre.
		//A la fin de la fonction main, un espace pour chronométrer une méthode est proposé.
		
		
		

		// PARTIE 2.1 : Résolution par satisfiabilité

		// Création de plusieurs problèmes de taille différentes

		Problem p = new Problem(7);
		p.ajoute(p.Case(5, 2), p.Case(2, 4));
		p.ajoute(p.Case(6, 0), p.Case(1, 4));
		p.ajoute(p.Case(1, 1), p.Case(2, 3));
		p.ajoute(p.Case(0, 3), p.Case(6, 4));
		p.ajoute(p.Case(3, 3), p.Case(1, 5));

		// SolveurPaths : 1.411 secondes
		// SolveurEdges : 0.005 secondes.
		// SolveurEdgesWithoutCycle : 0.166 secondes.
		// SolveurVertices : 0.224 secondes.
		// SolveurVerticesWithoutCycle : 0.465 secondes.

		Problem p1 = new Problem(3);
		p1.ajoute(p1.Case(0, 0), p1.Case(2, 2));

		// SolveurPaths : 0.065 secondes
		// SolveurEdges : 0.001 secondes.
		// SolveurEdgesWithoutCycle : 0.002 secondes.
		// SolveurVertices : 0.002 secondes.
		// SolveurVerticesWithoutCycle : 0.003 secondes.

		Problem p2 = new Problem(5);
		p2.ajoute(p2.Case(0, 0), p2.Case(2, 2));

		// SolveurPaths : 0.110 secondes.
		// SolveurEdges : 0.065 secondes.
		// SolveurEdgesWithoutCycle : 0.084 secondes.
		// SolveurVertices : 0.077 secondes.
		// SolveurVerticesWithoutCycle : 0.077 secondes.

		Problem p3 = new Problem(11);
		p3.ajoute(p3.Case(0, 0), p3.Case(2, 2));

		// SolveurPaths : Trop long
		// SolveurEdges : 0.098 secondes.
		// SolveurEdgesWithoutCycle : Trop long
		// SolveurVertices : 0.034 secondes.
		// SolveurVerticesWithoutCycle : 0.63 secondes.

		Problem p4 = new Problem(10);
		p4.ajoute(p4.Case(2, 8), p4.Case(7, 9));
		p4.ajoute(p4.Case(4, 8), p4.Case(6, 9));
		p4.ajoute(p4.Case(4, 9), p4.Case(3, 6));
		p4.ajoute(p4.Case(0, 9), p4.Case(3, 3));
		p4.ajoute(p4.Case(3, 1), p4.Case(0, 5));
		p4.ajoute(p4.Case(0, 0), p4.Case(8, 9));
		p4.ajoute(p4.Case(2, 3), p4.Case(4, 1));
		p4.ajoute(p4.Case(4, 2), p4.Case(3, 4));
		p4.ajoute(p4.Case(8, 8), p4.Case(4, 6));
		p4.ajoute(p4.Case(7, 2), p4.Case(7, 7));

		// SolveurPaths : 61 secondes
		// SolveurEdges : 0.221 secondes
		// SolveurEdgesWithoutCycle : 0.517 secondes.
		// SolveurVertices : 0.854 secondes.
		// SolveurVerticesWithoutCycle : 1.077 secondes.

		Problem p5 = new Problem(7);
		p5.ajoute(p5.Case(0, 0), p5.Case(6, 6));
		p5.ajoute(p5.Case(0, 1), p5.Case(5, 6));
		p5.ajoute(p5.Case(1, 1), p5.Case(5, 5));

		// SolveurPaths : 1.608 secondes.
		// SolveurEdges : 0.088 secondes.
		// SolveurEdgesWithoutCycle : O.1 secondes.
		// SolveurVertices : 0.098 secondes.
		// SolveurVerticesWithoutCycle : 0.149 secondes.

		// Liste des questions. Changer "p" en "p2 ou "p3" ou en tout problème que vous
		// souhaitez (il faudra alors le créer avant avec le constructeur de "Problem"
		// et la méthode "ajoute".

		// TASK 0

		//p.print();
		//System.out.println();

		// TASK 1

		//p.solveurPaths();

		//p.printSolutionPaths();

		// TASK 2 & 3

		//p.solveurEdges();

		//p.solveurEdgesWithoutCycle();

		//p.printSolutionEdges();

		// TASK 4

		//p.solveurVertices();

		//p.solveurVerticesWithoutCycle();

		//p.printSolutionVertices();

		// TASK 5

		//p.uniciteSolution();

		// PARTIE 2.2 : Résolution par algorithme de rebroussement
		
		//Création des mêmes problèmes adaptés à la classe Backtracking

		Backtracking b = new Backtracking(7);
		b.ajoute(b.Case(5, 2), b.Case(2, 4));
		b.ajoute(b.Case(6, 0), b.Case(1, 4));
		b.ajoute(b.Case(1, 1), b.Case(2, 3));
		b.ajoute(b.Case(0, 3), b.Case(6, 4));
		b.ajoute(b.Case(3, 3), b.Case(1, 5));

		// Solve : 0.093 secondes.
		// SolveAndCount : 1.919 secondes.

		Backtracking b1 = new Backtracking(3);
		b1.ajoute(b1.Case(0, 0), b1.Case(2, 2));

		// Solve : 0.048 secondes.
		// SolveAndCount : 0.05 secondes.

		Backtracking b2 = new Backtracking(5);
		b2.ajoute(b2.Case(0, 0), b2.Case(2, 2));

		// Solve : 0.057 secondes.
		// SolveAndCount : 0.1 secondes.

		Backtracking b3 = new Backtracking(11);
		b3.ajoute(b3.Case(0, 0), b3.Case(2, 2));

		// Solve : Trop long
		// SolveAndCount : Trop long

		Backtracking b4 = new Backtracking(10);
		b4.ajoute(b4.Case(2, 8), b4.Case(7, 9));
		b4.ajoute(b4.Case(4, 8), b4.Case(6, 9));
		b4.ajoute(b4.Case(4, 9), b4.Case(3, 6));
		b4.ajoute(b4.Case(0, 9), b4.Case(3, 3));
		b4.ajoute(b4.Case(3, 1), b4.Case(0, 5));
		b4.ajoute(b4.Case(0, 0), b4.Case(8, 9));
		b4.ajoute(b4.Case(2, 3), b4.Case(4, 1));
		b4.ajoute(b4.Case(4, 2), b4.Case(3, 4));
		b4.ajoute(b4.Case(8, 8), b4.Case(4, 6));
		b4.ajoute(b4.Case(7, 2), b4.Case(7, 7));

		// Solve : Trop long
		// SolveAndCount : Trop long

		Backtracking b5 = new Backtracking(7);
		b5.ajoute(b5.Case(0, 0), b5.Case(6, 6));
		b5.ajoute(b5.Case(0, 1), b5.Case(5, 6));
		b5.ajoute(b5.Case(1, 1), b5.Case(5, 5));

		// Solve : 0.078 secondes.
		// SolveAndCount : 64.782 secondes.
		
		
		// TASK 6
		
		//b.initialize();
		//b.print();
		//b.solve();
		//b.print();
		
		// TASK 7
		
		//b.initialize();
		//b.print();
		//b.solveAndCount();
		//System.out.println(b.nbSolutions);
		

		// CHRONOMETRAGE D'UNE METHODE		

		double tempsDebut = System.currentTimeMillis();

		// Insérer ici une méthode à chronométrer

		double tempsFin = System.currentTimeMillis();
		double seconds = (tempsFin - tempsDebut) / 1000F;
		System.out.println("Opération effectuée en: " + Double.toString(seconds) + " secondes.");

	}

}