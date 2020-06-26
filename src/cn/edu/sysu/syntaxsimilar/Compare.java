package cn.edu.sysu.syntaxsimilar;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import cn.edu.sysu.config.ConfigOperation;
import java.util.HashMap;
import java.io.IOException;

public class Compare {

    ArrayList<Text> project;

    List<String> databasePaths;

    int minNumLines;

    Output result;

    String databaseDir;
    
    

    public Compare() {
		super();
	}

	public Compare(int numLinesMatch, String databaseDirIn) {
        databaseDir = databaseDirIn;
        minNumLines = numLinesMatch;
    }

    public void installTextFiles(List<String> db_PathList) {
        databasePaths = db_PathList;
    }
    public void installTextFiles(ArrayList<Text> projectList, List<String> db_PathList) {
        project = projectList;
        databasePaths = db_PathList;
    }


 
    public double codeFragmentHashCompare(ArrayList<Long> fragmentHash1, ArrayList<Long> fragmentHash2){
        HashMap<Coordinate, Long> coorMap = new HashMap<Coordinate, Long>();
        ArrayList<Coordinate> coorList = new ArrayList<Coordinate>();
        
        int sizeX = fragmentHash1.size();
        int sizeY = fragmentHash2.size();
        
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                if (fragmentHash1.get(x).equals(fragmentHash2.get(y))) {
                    Coordinate coor = new Coordinate(x,y);
                    coorMap.put(coor, fragmentHash1.get(x));
                    coorList.add(coor);
                }
            }
        }
        
     // used to store the NG-clones
        ArrayList<Chain> chainList = new ArrayList<Chain>();

        // Detect NG-clones
        while (coorList.size() != 0) {
            Coordinate thisCoor = coorList.get(0);

            int longestLength = 1;

            // see how far we can go
            Coordinate coorNext;
            int currentX = thisCoor.x;
            int currentY = thisCoor.y;
            while (true) {
                // try extend the chain
                coorNext = new Coordinate(currentX + 1, currentY + 1);
                if (coorMap.get(coorNext) != null) {

                    // remove this since it is 
                    // part of a NG-chain
                    coorList.remove(coorNext);

                    // can extend chain
                    currentX = currentX + 1;
                    currentY = currentY + 1;
                    longestLength++;
                    continue;
                } else {
                    // there is a gap
                    break;
                }
            }

            // add to chain list
            Chain c = new Chain(thisCoor.x, thisCoor.y, 
                    currentX, currentY, longestLength);
            //System.out.println(thisCoor.x + "!" + thisCoor.y);
            //System.out.println(currentX + "!" + currentY);
            //System.out.println(longestLength);
            chainList.add(c);
            // remove this point
            coorList.remove(thisCoor);
        }

        // now have a list of NG chains
        // create a hashmap of gaps
        int gapSize = Integer.parseInt(ConfigOperation.getConfigProperties("gapSize"));
        HashMap<Chain, ArrayList<Chain>> gapMap = new HashMap<Chain, ArrayList<Chain>>();
        for (int i = 0; i < chainList.size(); i++) {
            Chain c_i = chainList.get(i);

            // search for connections
            for (int j = i + 1; j < chainList.size(); j++) {
                Chain c_j = chainList.get(j);
                
                // check if the two are near
                int dx = distance(c_i.x2, c_j.x1);
                int dy = distance(c_i.y2, c_j.y1);
                if ( ((0 < dx) && (dx <= gapSize)) &&
                        ((0 < dy) && (dy <= gapSize)) ) {
                    // satistified gap requirement
                    ArrayList<Chain> c_i_list = gapMap.get(c_j);
                    if (c_i_list == null) {
                        c_i_list = new ArrayList<Chain>();
                        c_i_list.add(c_j);
                        gapMap.put(c_i, c_i_list);
                    } else {
                        c_i_list.add(c_j);
                    }
                } else {
                    // optimization
                    if (distance(c_i.x2, c_j.y1) > gapSize) {
                        continue;
                    }
                }
            }
        }

        // now have a list of gaps
        // construct the longest chain
        ArrayList<ArrayList<Chain>> masterList = buildChains(chainList, gapMap);
        int maxLength = 0;
        
        for (int i = 0; i<masterList.size();i++) {
        	int length = 0;
        	ArrayList<Chain> list = masterList.get(i);
        	
        	for(int k=0;k<list.size();k++){        		
        		length = length + list.get(k).size;
        	}
        	if(length > maxLength){
        		maxLength = length;
        	}
        	
        }
        
        //System.out.println(maxLength);
        if(sizeX != 0 && sizeY != 0){
        	double syntaxSimilar = 0.0;
        	if(sizeX >= sizeY){
        		syntaxSimilar = (double)maxLength/(double)sizeX;
        	}else{
        		syntaxSimilar = (double)maxLength/(double)sizeY;
        	}
        	
        	//System.out.println("syntaxSimilar:"+syntaxSimilar);
            return syntaxSimilar;     
        }else{
        	return 0.0;
        }
        
           
}

    
    private int distance(int a, int b) {
        return (b-a);
    }



    private ArrayList<ArrayList<Chain>> buildChains(ArrayList<Chain> chainList, HashMap<Chain, ArrayList<Chain>> gapMap) {
        
        ArrayList<ArrayList<Chain>> masterChain = new ArrayList<ArrayList<Chain>>();

        for (int i = 0; i < chainList.size(); i++) {
            Chain thisChain = chainList.get(i);

            ArrayList<Chain> builtChain = new ArrayList<Chain>();
            builtChain.add(thisChain);

            extendChain(builtChain, gapMap, masterChain);
        }

        return masterChain;
    }
    
    private void extendChain (ArrayList<Chain> builtChain, HashMap<Chain, ArrayList<Chain>> gapMap,ArrayList<ArrayList<Chain>> masterChain) {
        
        Chain thisChain = builtChain.get(builtChain.size()-1);
        ArrayList<Chain> connectedList = gapMap.get(thisChain);
        if (connectedList != null) {
            for (int i = 0 ; i < connectedList.size(); i++) {
                ArrayList<Chain> newList = new ArrayList<Chain>(builtChain);
                newList.add(connectedList.get(i));
                extendChain(newList, gapMap, masterChain);
            }
        } else {
            masterChain.add(builtChain);
        }
    }

    static class Coordinate {
        public int x, y;
        Coordinate(int x, int y) { this.x = x; this.y = y; }
        public boolean equals(Object obj) {
            Coordinate other = (Coordinate) obj;
            return ((this.x == other.x) && (this.y == other.y));
        }
        public int hashCode() {
            return this.x ^ this.y;
        }
    } 

    static class Chain {
        public int x1, y1, x2, y2;
        public int size;
        Chain(int x1, int y1, int x2, int y2, int size) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.size = size;
        }
        public boolean equals(Object obj) {
            Chain other = (Chain) obj;
            return ((this.x1 == other.x1) && (this.y1 == other.y1) &&
                    (this.x2 == other.x2) && (this.y2 == other.y2) &&
                    this.size == other.size);
        }
        public int hashCode() {
            return this.x1 ^ this.y1 + this.x2 ^ this.y2 + size;
        }
    }
    
    static class Gap {
        Chain c1;
        Chain c2;
        Gap(Chain c1, Chain c2) {
            this.c1 = c1;
            this.c2 = c2;
        }
        public boolean equals(Object obj) {
            Gap other = (Gap) obj;
            return ((this.c1 == other.c1) && (this.c2 == other.c2));
        }
        public int hashCode() {
            return c1.hashCode() + c2.hashCode();
        }
    }
    
    public static void main(String[] args){
    	ArrayList<Long> fragmentHash1 = new ArrayList<Long>();
    	ArrayList<Long> fragmentHash2 = new ArrayList<Long>();
    	
    		fragmentHash1.add((long) 5);
    		fragmentHash1.add((long)3);
    		fragmentHash1.add((long)2);
    		fragmentHash1.add((long)1);
   		    fragmentHash1.add((long)3);
    		fragmentHash1.add((long)5);
    		fragmentHash1.add((long)3);
    		fragmentHash1.add((long)2);
    		fragmentHash1.add((long)1);
    		fragmentHash1.add((long)3);
    		
    		fragmentHash2.add((long)5);
    		fragmentHash2.add((long)3);
    		fragmentHash2.add((long)2);
    		fragmentHash2.add((long)0);
    		fragmentHash2.add((long)1);
    		fragmentHash2.add((long)5);
    		fragmentHash2.add((long)3);
    		fragmentHash2.add((long)2);
    		fragmentHash2.add((long)1);
    		fragmentHash2.add((long)3);
    		
    		Compare comp = new Compare(3,"");
    		comp.codeFragmentHashCompare(fragmentHash1,fragmentHash2);
    }
}



