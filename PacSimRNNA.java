import java.awt.Point;
import java.util.ArrayList;
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

class PopulationNode
{
    private int cost;
    private ArrayList<Point> path;
    private HashMap<Point, Integer> map;

    public PopulationNode()
    {
        cost = 0;
        path = new ArrayList<Point>();
        map = new HashMap<Point, Integer>();

    }

    public PopulationNode(int cost)
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

    public static int[][] createCostMatrix(PacmanCell pc, PacCell[][] grid)
    {
        // retrieve nodes of fully-connected graph for RNNA cost matrix (the food points)
        List<Point> food = PacUtils.findFood(grid);
        int foodCount = food.size();

        int[][] matrix = new int[foodCount + 1][foodCount + 1];

        // from Pacman to each food cell
        /*
        for(int i = 0; i < foodCount; i++)
        {
            int cost = BFSPath.getPath(grid, pc.getLoc(), food.get(i)).size();
            matrix[0][i + 1] = cost;
            matrix[i + 1][0] = cost;
        }
        */
        // populate cost matrix from every node to every other node (with zeros in diagonal)
        for(int i = 0; i < foodCount; i++)
        {
            // fill in columns B - ... (from one food cell to all other food cells)
            for(int j = 0; j < i; j++)
            {
                int cost = BFSPath.getPath(grid, food.get(i), food.get(j)).size();

                matrix[i + 1][j + 1] = cost;
                matrix[j + 1][i + 1] = cost;
            }
            
            int costFromPac = BFSPath.getPath(grid, pc.getLoc(), food.get(i)).size();
            
            // fill in column A and row A (from Pacman to each food cell)
            matrix[0][i + 1] = costFromPac;
            matrix[i + 1][0] = costFromPac;          
        }

        // print cost matrix
        System.out.println("Cost table:");
        
        for(int i = 0; i < matrix.length; i++)
        {
            for(int j = 0; j < matrix.length; j++)
            {
                System.out.print("\t" + matrix[i][j]);
            }
            
            System.out.println();
            
        }

        return matrix;
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
            int[][] costMatrix = createCostMatrix(pc, grid);
            
            // create and print food array
            List<Point> foodArray = PacUtils.findFood((PacCell[][]) state);

            System.out.println("Food Array:\n");

            for(int i = 0; i < foodArray.size(); i++)
            {
                System.out.println(i + " : (" + foodArray.get(i).x + "," + foodArray.get(i).y + ")");
            }

            // plan generation timer

            long startTime = System.currentTimeMillis();
            ArrayList<PopulationNode> population = new ArrayList<>();
            int numNodes = foodArray.size();

            for(int i = 0; i < numNodes; i++)
            {
                System.out.println("Population at step " + (i + 1) + " :");
                
                PopulationNode currNode = new PopulationNode();

                // first step (from pacman to each of the food cells)
                if(i == 0)
                {
                    currNode.addToPath(new Point(foodArray.get(i).x, foodArray.get(i).y));
                    currNode.setCost(costMatrix[0][i]);
                    currNode.setPointCost(0, costMatrix[0][i]);
                    
                    System.out.print(i + " : cost=" + currNode.getCost());
                    //Point currPoint = new Point(currNode.getX(0), currNode.getY(0));
                    System.out.print(" : [(" + currNode.getX(0) + "," + currNode.getY(0) + ")");
                    System.out.println("," + currNode.getPointCost(0) + "]");
                }

                else
                {
                    // TODO: perform RNNA to determine path to the remaining food cells
                    Queue<Point> rnnaq = new LinkedList<>();

                    // check cost to reach each other food cell and choose the one(s) with the lowest cost
                    for(int j = 1; j < costMatrix.length; j++)
                    {
                        PopulationNode leastCostNode = new PopulationNode();
                        //currNodeCosts = costMatrix[j][0];

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

                        currNode.addToPath(nearestFood);
                        currNode.setPointCost(nearestFood, minCost);
                        currNode.setCost(currNode.getCost() + minCost);
                        
                    }
                    
                    System.out.print(i + " : cost=" + currNode.getCost());

                    for(int j = 0; j < currNode.getPathLength(); j++)
                    {
                        System.out.print("[(" + currNode.getX(j) + currNode.getY(j) + ")]");
                        System.out.print("," + currNode.getPointCost(j) + "]");    
                    }
                    
                    System.out.print("\n");
                }

                population.add(currNode);
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
