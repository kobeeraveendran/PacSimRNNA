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
    private List<Point> remainingFood;

    public Candidate(List<Point> food)
    {
        cost = 0;
        path = new ArrayList<Point>();
        map = new HashMap<Point, Integer>();

        remainingFood = new ArrayList<>();

        for(int i = 0; i < food.size(); i++)
        {
            remainingFood.add(food.get(i));
        }
    }

    public Candidate(int cost, List<Point> food)
    {
        this.cost = cost;
        path = new ArrayList<Point>();
        map = new HashMap<Point, Integer>();
        remainingFood = food;
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

public class PacSimRNNAOld implements PacAction
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
        // note to self: pcLoc is not Pacman's actual current location, but which food dot he will go to in step 1, 
        // or the next planned step in any of the other population steps

        // find lowest cost from Pacman's current location (one of the food cells) to another food cell
        int currFoodCell = currCandidate.getRemainingFood().indexOf(pcLoc);

        System.out.println("Current loc: (" + pcLoc.x + "," + pcLoc.y + ")");
        System.out.println("Remaining food list:");

        for(int i = 0; i < currCandidate.getRemainingFood().size(); i++)
        {
            System.out.println("(" + currCandidate.getRemainingFood().get(i).x + "," + currCandidate.getRemainingFood().get(i).y + ")");
        }

        currCandidate.removeFood(pcLoc);

        System.out.println("After");
        for(int i = 0; i < currCandidate.getRemainingFood().size(); i++)
        {
            System.out.println("(" + currCandidate.getRemainingFood().get(i).x + "," + currCandidate.getRemainingFood().get(i).y + ")");
        }

        int minCost = Integer.MAX_VALUE;
        int minIndex = 0;

        List<Point> food = currCandidate.getRemainingFood();

        for(int i = 0; i < currCandidate.getRemainingFood().size(); i++)
        {
            if (!currCandidate.getPath().contains(food.get(i)) && costMatrix[currFoodCell + 1][pointToIndex.get(food.get(i))] < minCost && costMatrix[currFoodCell + 1][pointToIndex.get(food.get(i))] != 0)
            {
                minCost = costMatrix[currFoodCell + 1][pointToIndex.get(food.get(i))];
                minIndex = i;
            }
        }

        // check to see if branching is needed (equal cost to multiple food cells)
        // retval: [0] holds minCost, [1:] holds all points with a cost of minCost

        System.out.println("min cost = " + minCost);
        System.out.println("min index = " + minIndex);
        System.out.println("Point = (" + currCandidate.getRemainingFood().get(minIndex).x + "," + currCandidate.getRemainingFood().get(minIndex).y + ")");
        System.out.println("food array size = " + food.size());        

        ArrayList<Object> retval = new ArrayList<>();
        retval.add(minCost);
        retval.add(food.get(minIndex));

        // add any other points that have an equal cost
        for(int i = 0; i < food.size(); i++)
        {
            if(i != minIndex && costMatrix[currFoodCell + 1][pointToIndex.get(food.get(i))] == minCost)
            {
                retval.add(food.get(i));
            }
        }

        return retval;

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

            HashMap<Point, Integer> pointToIndex = new HashMap<>();

            for(int i = 0; i < foodArray.size(); i++)
            {
                System.out.println(i + " : (" + foodArray.get(i).x + "," + foodArray.get(i).y + ")");
                pointToIndex.put(foodArray.get(i), i + 1);
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
                    for(int j = 0; j < foodArray.size(); j++)
                    {
                        Candidate currCandidate = new Candidate(foodArray);
                        Point tempPoint = new Point(foodArray.get(j).x, foodArray.get(j).y);
                        currCandidate.addToPath(tempPoint);
                        currCandidate.removeFood(foodArray.get(j));
                        //currCandidate.setCost(costMatrix[0][j + 1]);
                        currCandidate.setPointCost(tempPoint, costMatrix[0][j + 1]);
                        currCandidate.setCost(currCandidate.getPointCost(tempPoint));
                        
                        candidateList.add(currCandidate);
                    }

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

                }

                // usual RNNA steps  (from a food cell to the other food cells)
                else
                {

                    // list of candidates from previous population step
                    ArrayList<Candidate> currCandidateList = populationList.get(i - 1);

                    // find costs to each other food cell and generate list with costs
                    for(int j = 0; j < currCandidateList.size(); j++)
                    {
                        Candidate currCandidate = currCandidateList.get(j);

                        ArrayList<Object> nearestNeighbors = nearestNeighbor(
                            currCandidate.getPoint(currCandidate.getPathLength() - 1), 
                            costMatrix, 
                            currCandidate, 
                            pointToIndex
                        );

                        int minCost = (int) nearestNeighbors.get(0);

                        if(nearestNeighbors.size() > 2)
                        {
                            Candidate copyCandidate = currCandidate;
                            // handle branching case (add new candidates to list)
                            for(int k = 1; k < nearestNeighbors.size(); k++)
                            {
                                copyCandidate.addToPath((Point) nearestNeighbors.get(k));
                                copyCandidate.setPointCost((Point) nearestNeighbors.get(k), minCost);
                                copyCandidate.setCost(copyCandidate.getCost() + minCost);

                                currCandidateList.add(copyCandidate);
                            }
                        }
                        else
                        {
                            // handle usual case (no branching)
                            Point nearestFood = (Point) nearestNeighbors.get(1);
                            currCandidate.addToPath(nearestFood);
                            currCandidate.setPointCost(nearestFood, minCost);
                            currCandidate.setCost(currCandidate.getCost() + minCost);

                            currCandidateList.add(currCandidate);
                        }
                        
                        populationList.add(candidateList);

                        Collections.sort(currCandidateList, new Comparator<Candidate>() {
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
                    }

                    for(int j = 0; j < currCandidateList.size(); j++)
                    {
                        Candidate currCandidate = currCandidateList.get(j);
                        System.out.print(j + " : cost=" + currCandidate.getCost() + " : ");
                        System.out.print("[(" + currCandidate.getX(j) + "," + currCandidate.getY(j) + ")," + currCandidate.getPointCost(j) + "]");
                        System.out.println("," + currCandidate.getPointCost(j) + "]");    
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
