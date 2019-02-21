Ce projet d'informatique "Numberlink" (INF421) a été réalisé par Madeleine Fabre et Paul Jacob.

Voici une description des classes et méthodes programmées.


--------------------

 "Test" contient la fonction main, et a servi durant la construction du projet à effectuer divers tests. Des problèmes et des instructions sont préremplis dessus (voir la suite pour comprendre leur structure). 


--------------------

 "Graph" décrit une modélisation naturelle des graphes non orientés, avec un nombre de sommets "n" donnés (les sommets seront les entiers de 0 à (n-1)), ainsi qu'une HashMap "voisins" pour représenter les voisins de tous ces sommets.


--------------------

 "Grid", qui étend la classe "Graph", est le cas particulier de graphe adapté à notre problème, où les sommets sont les cases de la grille et où on spécifie la dimension d de la grille. 
Alors le nombre de sommets est n = d*d, et les voisins des sommets sont les voisins naturels de chaque case sur la grille. Un constructeur est préparé pour directement initialiser une grille avec les bons voisins.
La méthode "Case" effectue simplement l'opération qui trouve le numéro de la case en fonction de ses coordonnées.


--------------------

 "Problem" étend la classe grille. On ajoute le concept des sommets à relier, avec différentes HashMap pour garder toute l'information à chaque fois.
La Hashmap "numeros" retient, pour un sommet à relier à un autre, son numéro de chemin.
La Hashmap "aRelier" retient, pour un sommet à relier à un autre, le sommet auquel il doit être relié.
La Hashmap "depart" retient, pour un numéro de chemin donné, son sommet de départ (arbitraire).
La Hashmap "arrivee" retient, pour un numéro de chemin donné, son sommet d'arrivée (arbitraire).


C'est dans cette dernière classe qu'on trouve les méthodes de la partie du sujet consacrée à la résolution par problème SAT.

Le constructeur "Problem" initialise un problème Numberlink mais sans sommets à relier au départ.
L'appel de "p.ajoute(x,y)", à effectuer autant de fois que nécéssaire, permet der signifier que les sommets x et y sont à relier pour le problème p.
On peut noter x et y sous la forme "Case(a,b)" de manière plus visuelle.

C'est donc avec ce constructeur et cette méthode qu'on construit un problème Numberlink (voir classe "Test" pour des exemples).


L'appel de "p.print()" imprime la grille représentant le problème p.


TASK 1

- L'appel de "p.solveurPaths()" retourne un tableau de chemins, qui représente les chemins obtenus par résolution SAT en fonction des contraintes données par la Task 1 de l'énoncé.

- L'appel de "p.printSolutionPaths()" imprime ces chemins, ainsi que la grille résolue.


TASK 2

- L'appel de "p.solveurEdges()" retourne le résultat de la résolution SAT du problème p sous forme de "int[]" obtenu par les contraintes de la Task 2 de l'énoncé.
On peut lui spécifier un solveur déjà rempli, ce qui servira pour éliminer les cycles.

TASK 3

- L'appel de "Cycle(solution)" où "solution" est un résultat donné par "p.solveurEdges()" regarde si ce résultat comporte un cycle, et si c'est le cas, renvoie des clauses à rajouter pour que ce cycle soit impossible.

- L'appel de "p.solveurEdgesWithoutCycle()" se base sur ces deux dernières méthodes pour fournir une solution sans cycle au problème p, basée sur les contraintes de la Task 2.

- L'appel de "p.printSolutionEdges()" imprime les listes d'arêtes triées par chemin, ainsi que la grille résolue.

TASK 4

- L'appel de "p.solveurVertices()" retourne le résultat de la résolution SAT du problème p sous forme de "int[]" obtenu par les contraintes de la Task 4 de l'énoncé.
On peut lui spécifier un solveur déjà rempli, ce qui servira pour éliminer les cycles.

- L'appel de "CycleVertice(solution)" où "solution" est un résultat donné par "p.solveurVertices()" regarde si ce résultat comporte un cycle, et si c'est le cas, renvoie des clauses à rajouter pour que ce cycle soit impossible.

- L'appel de "p.solveurVerticesWithoutCycle()" se base sur ces deux dernières méthodes pour fournir une solution sans cycle au problème p, basée sur les contraintes de la Task 4.
On peut lui rajouter une clause de plus en argument, ce qui servira pour trouver une éventuelle 2ème solution.

- L'appel de "p.printSolutionEdges()" imprime une grille avec les configurations des différents sommets, ainsi que la grille résolue.

TASK 5

- L'appel de "p.uniciteSolution()" se base sur la résolution donnée par la question 4, pour trouver une éventuelle 2ème solution en imposant à un des sommets d'être dans une configuration différente. 
Il renvoie un booléen "true" si il existe une unique solution, "false" si il n'y a pas ou plusieurs solutions.

--------------------

 "Backtracking" est une classe qui étend la classe "Problem". C'est la classe qui permet de modéliser la résolution des problèmes Numberlink par algorithmes de rebroussement, en faisant progresser les chemins de proche en proche.
La HashMap "Paths" retient, pour un numéro du chemin donné, la succession des sommets qui forment le chemin en cours pour ce numéro.
La HashMap "VoisinsVides" retient, pour un sommet donné, ses voisins qui ne sont pas encore sur un chemin.
La LinkedList "aExplorer" liste tous les chemins qui n'ont pas encore relié leur départ à leur arrivée.
L'entier "nbSolutions" retient le nombre de solutions déjà trouvé pour un problème donné et est utilisé dans la méthode "solveAndCount".


/!\ Contrairement aux instances de la classe "Problem", la HashMap "numeros" est ici vouée à évoluer au cours de la résolution, et contiendra les numéros de tous les sommets appartenant à des chemins en cours (ou par défaut -1 pour un sommet n'étant pas encore sur un chemin, via la méthode fillBlanks()).


Le constructeur "Backtracking" initialise une instance de la classe de la même manière que celui de "Problem", en initialisant de plus la HashMap "chemins" avec des listes vides, la HashMap "voisinsVides" en copiant "voisins", et la LinkedList "aExplorer" avec une liste vide.

On utilisera également la méthode "ajoute" de la superclasse pour rajouter des points à relier au problème.

INITIALISATION DU PROBLEME :

- L'appel à "p.fillBlanks()" permet d'attribuer par défaut -1 dans numeros à tous les sommets vides de p.
- L'appel à "p.initialize()" permet d'initialiser le problème (qui a été défini avec les méthodes de la superclasse) en attribuant un départ à chaque chemin dans paths, en suprimant les extrémités de chemins de voisinsVides, et en définissant l'ensemble des chemins à explorer.

TASK 6

- L'appel à "p.solve()", à effectuer après "p.initialize()", met en oeuvre la résolution du problème p avec l'algorithme de rebroussement. Il renvoie "true" si et seulement si l'algorithme admet une solution, et modifie par effet de bord la HashMap des numéros des sommets.

TASK 7

- L'appel à "p.solveAndCount()", à effectuer après "p.initialize()", met en oeuvre la résolution du problème p avec l'algorithme de rebroussement, et continue la résolution à la recherche d'autre solutions en les comptant. Il modifie par effet de bord l'entier "nbSolutions" et s'arrête une fois qu'il a exploré toutes les possibilités de chemins du problème.


- L'appel à "p.solveMoreThan(k)" renvoie une solution de la grille si il existe plus de k solutions possibles au problème, au format Backtracking. Autrement, il renvoie une solution vide (null).


On réimplante également la méthode "print()" pour imprimer la grille d'une instance de la classe Backtracking, en prenant en compte l'attribution par défaut d'un -1 sur les cases vides. 


  


