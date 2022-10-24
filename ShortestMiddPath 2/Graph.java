package ShortestMiddPath;

/*
* Author: Shelby Kimmel
* Creates a adjacency list object to store information about the graph of roads, and contains the main functions used to
* run the Bellman Ford algorithm

*/

import java.io.File;
import java.util.Scanner;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.lang.Math;
import java.io.File;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Graph {

	// Object that contains an adjacency list of a road network and a dictionary from elements of the list to indeces from 0 to |V|-1, since the roads are labeled in the data by arbitrary indices. Because we are considering a walking application, we construct the adjacency list so that if there is an edge {u,v}, then u appears in the list of v's neighbors, and v appears in the list of u's neighbors. This means that the adjacency matrix and the reverse adjacency matrix are the same. In other words, the adjacency matrix that is already written here is a reverse adjacency matrix.
	HashMap<Integer, ArrayList<Road>> adjList;
	HashMap<Integer,Integer> nodeDict;


	public Graph(String file) throws IOException{
		// We will store the information about the road graph in an adjacency list
		// We will use a HashMap to store the Adjacency List, since each vertex in the graph has a more or less random integer name.
		// Each element of the HashMap will be an ArrayList containing all roads (edges) connected to that vertex
		adjList = new HashMap<>();
		nodeDict = null;

		// Based on https://stackoverflow.com/questions/49599194/reading-csv-file-into-an-arrayliststudent-java
		String line = null;
		BufferedReader br = new BufferedReader(new FileReader(file));
		if ((line=br.readLine())==null){
			return;
		}
		while ((line = br.readLine())!=null) {
			String[] temp = line.split(",");
			//Assume all roads are two-way, and using ArcMiles as distance:
			this.addToAdjList(new Road(Integer.parseInt(temp[60]),Integer.parseInt(temp[61]),temp[9],Double.parseDouble(temp[31])));
			this.addToAdjList(new Road(Integer.parseInt(temp[61]),Integer.parseInt(temp[60]),temp[9],Double.parseDouble(temp[31])));
		}


		//For dynamic programming, we will have an array with indeces 0 to |V|-1,
		// where |V| is the number of vertices. Thus we need to associate each element of adjList with a number between 0 and |V|-1
		// We will use a Dictionary (HashMap) to do this.
		nodeDict = new HashMap<>();
		int j = 0;
		for (Integer nodeName: adjList.keySet()){
			nodeDict.put(nodeName, j);
			j++;
		}

	}


	// get functions
	public HashMap<Integer, ArrayList<Road>> getAdjList(){
		return adjList;
	}
	public HashMap<Integer,Integer> getDict(){
		return nodeDict;
	}


	public synchronized void addToAdjList(Road road) {
		//Adds the Road (edge) to the appropriate list of the adjacency list.
		//This method is used by the constructor method
		//Based on https://stackoverflow.com/questions/12134687/how-to-add-element-into-arraylist-in-hashmap
		Integer node = road.getStart();
    	ArrayList<Road> roadList = this.getAdjList().get(node);

    	// if node is not already in adjacency list, we create a list for it
    	if(roadList == null) {
    	    roadList = new ArrayList<Road>();
    	    roadList.add(road);
   		    this.getAdjList().put(node, roadList);
  	  	}
  	  	else {
        	// add to appropriate list if item is not already in list
        	if(!roadList.contains(road)) roadList.add(road);
    	}

    }

    public Double[][] ShortestDistance(Integer startNode){
    	// This method should create the array storing the objective function values of subproblems used in Bellman Ford.
			Integer start = nodeDict.get(startNode);

			//Initialize Array
			Double[][] A = new Double[nodeDict.size()][nodeDict.size()];
			for(int x = 0; x < nodeDict.size(); x++){
				for(int y = 0; y < nodeDict.size(); y++){
					A[x][y] = Double.POSITIVE_INFINITY;
				}
			}
			A[start][0] = 0.0;

			//Fill in Array
			double distance = 0.0;
			for (int i = 1; i < nodeDict.size(); i++){
					//Loop through vertices
					for (Integer v: nodeDict.keySet()){
							ArrayList<Road> ulist = adjList.get(v);
							//Add a road to the vertice
							Road r = new Road(v,v,"self",0.0);
							ulist.add(r);
							//Loop through list of adjacent vertices
						  for(int u = 0; u < ulist.size(); u++){
								Integer vv = nodeDict.get(ulist.get(u).endNode);
								Integer uu = nodeDict.get(ulist.get(u).startNode);
								distance = ulist.get(u).getMiles();
								//Change value in A if smaller path exists
						  	if(A[uu][i-1] + distance < A[vv][i]){
							 		A[vv][i] = A[uu][i-1] + distance;
								}
							}
							ulist.remove(ulist.size()-1);
					 }
			 }
		return A;
    }

    public String ShortestPath(Integer endNode, Double[][] dpArray){
		// This method should work backwards through the array you created in ShortestDistance and output the
		// sequence of streets you should take to get from your starting point to your ending point.
		Integer v = nodeDict.get(endNode);
		ArrayList<Double> P = new ArrayList<Double>();
		ArrayList<Road> p1 = new ArrayList<Road>();
		System.out.print(endNode);
		//Check if there is a path
		if(dpArray[v][dpArray.length-1] == Double.POSITIVE_INFINITY){
			return "No Path";
		}
		//Finds path
		else{
			int i = nodeDict.size()-1;
			//Loop through i
			while (i > 0){
				if(dpArray[v][i] != dpArray[v][i-1]){
					ArrayList<Road> ulist = adjList.get(endNode);
					//Loop through the vertices
					for(int ui = 0; ui < ulist.size(); ui++){
							double distance = ulist.get(ui).getMiles();
							Integer u = nodeDict.get(ulist.get(ui).endNode);
								//Adds to path
								if(dpArray[v][i] == dpArray[u][i-1] + distance){
									P.add(0,distance);
									p1.add(0, ulist.get(ui));
									v = u;
									endNode = ulist.get(ui).endNode;
									break;
								}
						}
					}
				i--;
			}
		}
		//Make string x
		double Distance = 0.0;
		String x="";
		for(int pi = 0; pi < P.size(); pi++){
			Distance += P.get(pi);
			x += " " + p1.get(pi).name;
		}

		x += " Distance:" + Distance;
	return x;



	}
}
