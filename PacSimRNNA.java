import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Queue;

import pacsim.BFSPath;
import pacsim.PacAction;
import pacsim.PacCell;
import pacsim.PacFace;
import pacsim.PacUtils;
import pacsim.PacmanCell;
import pacsim.PacSim;

/*
 * University of Central Florida
 * CAP4630 - Fall 2018
 * Author: Kobee Raveendran
 */

class Candidate
{
    private int cost;
    private ArrayList<Point> path;
    private HashMap<Point, Integer> map;

    public Candidate()
    {
        cost = 0;
        path = new ArrayList<Point>();
        map = new HashMap<Point, Integer>();

    }

    public Candidate(int cost)
    {
        this.cost = cost;
        path = new ArrayList<Point>();
        map = new HashMap<Point, Integer>();
    }

    public int getPathLength()
    {
        return this.path.size();
    }

    public int getCost()
    {
        return this.cost;
    }

    public void setCost(int cost)
    {
        this.cost = cost;
    }

    public int getPointCost(int i)
    {
        Point point = this.path.get(i);
        return map.get(point);
    }

    public int getPointCost(Point point)
    {
        return map.get(point);
    }

    public void appendPointCost(int i, int cost)
    {
        Point point = this.path.get(i);
        map.put(point, cost);
    }

    public void setPointCost(int i, int cost)
    {
        Point point = this.path.get(i);
        map.put(point, cost);
    }

    public void setPointCost(Point point, int cost)
    {
        map.put(point, cost);
    }

    public Point getPoint(int i)
    {
        return this.path.get(i);
    }

    public int getX(int i)
    {
        return this.path.get(i).x;
    }

    public int getY(int i)
    {
        return this.path.get(i).y;
    }

    public List<Point> getPath()
    {
        return this.path;
    }

    public void setPath(ArrayList<Point> path)
    {
        this.path = path;
    }

    public void addToPath(Point point)
    {
        this.path.add(point);
    }

    public HashMap<Point, Integer> getMap()
    {
        return this.map;
    }
}

public class PacSimRNNA implements PacAction
{

    private ArrayList<Point> path;
    private int simTime;

    public PacSimRNNA(String fname)
    {
        PacSim sim = new PacSim(fname);
        sim.init(this);
    }

    public static void main(String[] args)
    {
        System.out.println("\nTSP using Repetitive Nearest Neighbor Algorithm by Kobee Raveendran:");
        System.out.println("\nMaze : " + args[0] + "\n");

        new PacSimRNNA(args[0]);
    }

    @Override
    public void init()
    {
        simTime = 0;
        path = new ArrayList<Point>();
    }

    @Override
    public PacFace action(Object state)
    {
        PacCell[][] grid = (PacCell[][]) state;
        PacmanCell pc = PacUtils.findPacman(grid);

        if (pc == null)
        {
            return null;
        }

        // decision to be made when deciding which food to go for next
        if (path.isEmpty())
        {
            // create and print cost matrix

            List<Point> food = PacUtils.findFood(grid);
            int foodCount = food.size();

            int[][] costMatrix = new int[foodCount + 1][foodCount + 1];

            // populate cost matrix from every node to every other node (with zeros in diagonal)
            for(int i = 0; i < foodCount; i++)
            {
                // fill in columns B - ... (from one food cell to all other food cells)
                for(int j = 0; j < i; j++)
                {
                    int cost = BFSPath.getPath(grid, food.get(i), food.get(j)).size();

                    costMatrix[i + 1][j + 1] = cost;
                    costMatrix[j + 1][i + 1] = cost;
                }
                
                int costFromPac = BFSPath.getPath(grid, pc.getLoc(), food.get(i)).size();
                
                // fill in column A and row A (from Pacman to each food cell)
                costMatrix[0][i + 1] = costFromPac;
                costMatrix[i + 1][0] = costFromPac;          
            }

            // print cost matrix
            System.out.println("Cost table:");
            
            for(int i = 0; i < costMatrix.length; i++)
            {
                for(int j = 0; j < costMatrix.length; j++)
                {
                    System.out.print("\t" + costMatrix[i][j]);
                }
                
                System.out.println();
                
            }
            
            // create and print food array
            List<Point> foodArray = PacUtils.findFood((PacCell[][]) state);

            System.out.println("Food Array:\n");

            for(int i = 0; i < foodArray.size(); i++)
            {
                System.out.println(i + " : (" + foodArray.get(i).x + "," + foodArray.get(i).y + ")");
            }

            // plan generation timer

            long startTime = System.currentTimeMillis();
            ArrayList<ArrayList<Candidate>> populationList = new ArrayList<>();

            for(int i = 0; i < foodArray.size(); i++)
            {
                System.out.println("Population at step " + (i + 1) + " :");
                
                //Candidate currCandidate = new Candidate();

                // determine how many entries this step needs
                ArrayList<Candidate> candidateList = new ArrayList<>();
                
                int numEntries = Math.max(foodArray.size(), candidateList.size());

                // first step (from pacman to each of the food cells)
                if(i == 0)
                {
                    ArrayList<Candidate> popList = new ArrayList<>();

                    for(int j = 0; j < foodArray.size(); j++)
                    {
                        Candidate currCandidate = new Candidate();
                        Point tempPoint = new Point(foodArray.get(j).x, foodArray.get(j).y);
                        currCandidate.addToPath(tempPoint);
                        //currCandidate.setCost(costMatrix[0][j + 1]);
                        currCandidate.setPointCost(tempPoint, costMatrix[0][j + 1]);
                        currCandidate.setCost(currCandidate.getPointCost(tempPoint));
                        
                        candidateList.add(currCandidate);
                    }

                    //HashMap<Point, Integer> map = currCandidate.getMap();
                    populationList.add(candidateList);

                    Collections.sort(candidateList, new Comparator<Candidate>() {
                        @Override
                        public int compare(Candidate cand1, Candidate cand2)
                        {
                            if(cand1.getCost() > cand2.getCost())
                            {
                                return 1;
                            }
                            else if(cand1.getCost() < cand2.getCost())
                            {
                                return -1;
                            }
                            else
                            {
                                return 0;
                            }
                        }
                    });

                    int index = 0;
                    for(Candidate cand : candidateList)
                    {
                        // NOTE FOR LATER: make sure to update cost retrieved by getCost() (seems to be stuck at 5 [first cost])
                        System.out.print(index + " : cost=" + cand.getCost());
                        System.out.print(" : [(" + cand.getX(0) + "," + cand.getY(0) + ")");
                        System.out.println("," + cand.getCost() + "]");
                        index++;
                    }

                    // new

                }

                // usual RNNA steps  (from a food cell to the other food cells)
                else
                {
                    // TODO: perform RNNA to determine path to the remaining food cells
                    //Queue<Point> rnnaq = new LinkedList<>();
                    ArrayList<Candidate> currCandidateList = populationList.get(i - 1);
                    // find costs to each other food cell and generate list with costs
                    for(int j = 1; j < currCandidateList.size(); j++)
                    {
                        Candidate currCandidate = currCandidateList.get(j);
                        //currCandidateCosts = costMatrix[j][0];

                        int minCost = Integer.MAX_VALUE;
                        int minIndex = j;

                        // find min cost among other food cells
                        for(int k = 0; k < foodArray.size() && k + 1 != j; k++)
                        {
                            if(costMatrix[j][k] < minCost)
                            {
                                minCost = costMatrix[j][k];
                                minIndex = k;
                            }
                        }

                        Point nearestFood = foodArray.get(minIndex);

                        currCandidate.addToPath(nearestFood);
                        currCandidate.setPointCost(nearestFood, minCost);
                        currCandidate.setCost(currCandidate.getCost() + minCost);
                        
                    }

                    for(int j = 0; j < currCandidateList.size(); j++)
                    {
                        Candidate currCandidate = currCandidateList.get(j);
                        System.out.print(j + " : cost=" + currCandidate.getCost());
                        System.out.print("[(" + currCandidate.getX(j) + currCandidate.getY(j) + ")," + currCandidate.getPointCost(j) + "]");
                        System.out.print("," + currCandidate.getPointCost(j) + "]");    
                    }
                    
                    System.out.print("\n");
                }
            }

            // generate plan here
            System.out.println("Number of food dots collected.");

            long timeElapsed = System.currentTimeMillis() - startTime;

            System.out.println("Time to generate plan: " + (int) timeElapsed + " msec");
        }

        Point next = path.remove(0);
        PacFace face = PacUtils.direction(pc.getLoc(), next);

        System.out.println("%5d : From [ %2d, %2d ] go ");

        return face;
        
    }

}
