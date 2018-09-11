import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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

class Candiate
{
    private int cost;
    private ArrayList<Point> path;
    private HashMap<Point, Integer> map;
    private List<Point> remainingFood;
    public Candidate(List<Point> food)
    {
        cost = 0;
        path = new ArrayList<Point>();
        map = new HashMap<Point, Integer>();
        remainingFood = new ArrayList<>();
        for(int z = 0; z < food.size(); z++)
        {
            remainingFood.add(food.get(z));
        }
    }
    public void removeFood(Point point)
    {
        this.remainingFood.remove(point);
    }
    public List<Point> getRemainingFood()
    {
        return this.remainingFood;
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
    public int getPointCost(Point point)
    {
       return map.get(point);
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

    public ArrayList<Object> nearestNeighbor(Point pcLoc, int[][] costMatrix, Candidate currCandidate, HashMap<Point, Integer> pointToIndex)
    {
        int currFoodCell = currCandidate.getRemainingFood().indexOf(pcLoc);
        // maybe consider moving this
        currCandidate.removeFood(pcLoc);

        int minCost = Integer.MAX_VALUE;
        int minIndex = 0;

        List<Point> food = currCandidate.getRemainingFood();

        for (int i = 0; i < food.size(); i++)
        {
            if (!currCandidate.getPath().contains(food.get(i)) && costMatrix[currFoodCell + 1][pointToIndex.get(food.get(i))] < minCost)
            {
                minCost = costMatrix[currFoodCell + 1][pointToIndex.get(food.get(i))];
                // index in the food array
                minIndex = i;
            }
        }

        ArrayList<Object> retval = new ArrayList<>();
        retval.add(minCost);
        retval.add(food.get(minIndex));

        for (int i = 0; i < food.size(); i++)
        {
            if (i != minIndex && costMatrix[currFoodCell + 1][pointToIndex.get(food.get(i))] == minCost)
            {
                retval.add(food.get(i));
            }
        }

        return retval;
    }

    
}