import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

import pacsim.BFSPath;
import pacsim.PacAction;
import pacsim.PacCell;
import pacsim.PacFace;
import pacsim.PacUtils;
import pacsim.PacmanCell;

/*
 * University of Central Florida
 * CAP4630 - Fall 2018
 * Author: Kobee Raveendran
 */

private class PopulationNode
{
    private int cost;
    private List<Point> path;
    private HashMap<Point, Integer> map;

    public PopulationNode()
    {
        cost = 0;
        path = new ArrayList<Point>();
        map = new HashMap<>();

    }

    private getPathLength()
    {
        return this.path.size();
    }

    private getCost()
    {
        return this.cost;
    }

    private setCost(int cost)
    {
        this.cost = cost;
    }

    private getPointCost(int i)
    {
        Point point = this.path.get(i);
        return map.get(point);
    }

    private appendPointCost(int i, int cost)
    {
        Point point = this.path.get(i);
        map.put(point, cost);
    }

    private setPointCost(int i, int cost)
    {
        Point point = this.path.get(i);
        map.put(point, cost);
    }

    private getPoint(int i)
    {
        return this.path.get(i);
    }

    private getX(int i)
    {
        return this.path.get(i).x;
    }

    private getY(int i)
    {
        return this.path.get(i).y;
    }

    private getPath()
    {
        return this.path;
    }

    private setPath(List<Point> path)
    {
        this.path = path;
    }

    private addToPath(Point point)
    {
        this.path.add(point);
    }

    private getStep(int step)
    {
        return path.get(step);
    }
}

public class PacSimRNNA implements PacAction
{

    private List<Point> path;
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
        path = new ArrayList();
    }

    public static int[][] createCostMatrix(PacmanCell pc, PacCell[][] grid)
    {
        // retrieve nodes of fully-connected graph for RNNA cost matrix (the food points)
        List<Point> food = PacUtils.findFood(grid);
        int foodCount = food.size();

        int[][] matrix = new int[foodCount + 1][foodCount + 1];

        // populate cost matrix from every node to every other node (with zeros in diagonal)
        for(int i = 0; i <= foodCount; i++)
        {
            // fill in columns B - ... (from one food cell to all other food cells)
            for(int j = 1; j < i; j++)
            {
                int cost = BFSPath.getPath(grid, food.get(i), food.get(j)).size();

                matrix[i][j] = cost;
                matrix[j][i] = cost;
            }
            
            int costFromPac = BFSPath.getPath(grid, pc.getLoc(), food.get(i)).size();
            
            // fill in column A and row A (from Pacman to each food cell)
            matrix[0][i] = costFromPac;
            matrix[i][0] = costFromPac;          
        }

        // print cost matrix
        System.out.println("Cost table:");
        
        for(int i = 0; i < matrix.length; i++)
        {
            for(int j = 0; j < matrix.length; j++)
            {
                System.out.print("   " + matrix[i][j]);
            }
            
            System.out.println();
            
        }

        return matrix;
    }

    @Override
    public PacFace action(Object state)
    {
        PacCell[][] grid = (PacCell[][]) state;
        PacmanCell pc = PacUtils.findPackman(grid);

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
            List<Point> foodArray = PacUtils.findFood(state);

            System.out.println("Food Array:\n");

            for(i = 0; i < foodArray.size(); i++)
            {
                System.out.println(i + " : (" + foodArray.get(i).x + "," + foodArray.get(i).y + ")");
            }

            // TODO: generate solution plan using RNNA
            
            // from pacman to food pellets
            /*
            int numNodes = costMatrix.length;
            int[] pacToFoodCost = new int[numNodes];
            Queue<PacCell> nodes = new Queue(numNodes);
            
            for(i = 0; i < numNodes + 1; i++)
            {
                pacToFoodCost[i] = costMatrix[0][i];
                nodes.add(foodArray.get(i));
            }
            */

            // plan generation timer

            for(i = 0; i < numNodes; i++)
            {
                System.out.println("Population at step " + i + " : ");

                ArrayList<PopulationNode> population = new ArrayList<>();
                PopulationNode currNode = new PopulationNode();

                // first step (from pacman to each of the food cells)
                if(i == 0)
                {
                    currNode.addToPath(new Point(foodArray.get(i).x, foodArray.get(i).y));
                    currNode.setCost(costMatrix[0][i]);
                    
                    System.out.print(i + " : cost=" + currNode.getCost());
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
                        currNodeCosts = costMatrix[j][0];

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

                        Point nearestFood = foodArray(minIndex);

                        currNode.addToPath(new Point(nearestFood));
                        currNode.setPointCost(currNode.getPathLength(), minCost);
                        
                    }
                    continue;
                }
            }

            // generate plan here
            System.out.println("Number of food dots collected.");

            System.out.println("Time to generate plan: " + (int) timeElapsed + " msec");
        }

        Point next = path.remove(0);
        PacFace face = PacUtils.direction(pc.getLoc(), next);

        System.out.println("%5d : From [ %2d, %2d ] go ");

        
    }

}
