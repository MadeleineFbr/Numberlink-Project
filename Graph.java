import java.util.List;
import java.util.HashMap;
import java.util.LinkedList;

public class Graph {  //Classe Graphe

	int n; //Nombre de sommets
	HashMap<Integer, List<Integer>> voisins = new HashMap<Integer, List<Integer>>(); //Voisins de chaque sommet i pour i entre 0 et n-1
	
	
	Graph(int k) {
		n = k;
		for (int i=0 ; i<k ; i++) {
			voisins.put(i, new LinkedList<Integer>());
		}
	}
	
	

	List<Integer> voisins( int i) {
		return this.voisins.get(i);
	}

	void add_edge(int i, int j) { //Ajoute une arete entre (i,j)
		this.voisins.get(i).add(j);
		this.voisins.get(j).add(i);
	}
	
	void remove_edge(Graph g, int i, int j) { //Retire une arete entre (i,j) si celle-ci existe
		this.voisins.get(i).remove(j);
		this.voisins.get(j).remove(i);
	}
	

}
