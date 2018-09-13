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

class Candidate
{
    private int cost;
    private ArrayList<Point> path;
    private HashMap<Point, Integer> map;
    private ArrayList<Point> remainingFood;
    
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

    public Candidate(Candidate oldCandidate)
    {
        this.cost = oldCandidate.getCost();
        this.path = new ArrayList<Point>(oldCandidate.getPath());
        this.map = new HashMap<>(oldCandidate.getMap());
        this.remainingFood = new ArrayList<Point>(oldCandidate.getRemainingFood());
    }

    public void removeFood(Point point)
    {
        this.remainingFood.remove(point);
    }
    public ArrayList<Point> getRemainingFood()
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

    public int getPointCost(int i)
    {
        return map.get(path.get(i));
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

    public void setPath(List<Point> newPath)
    {
        this.path.clear();

        // might have to make this newPath.size() - 1 to avoid adding duplicate points
        for (int i = 0; i < newPath.size(); i++)
        {
            this.path.add(newPath.get(i));
        }
    }

    public void setPath(List<Point> newPath, int end)
    {
        this.path.clear();

        for (int i = 0; i < end; i++)
        {
            this.path.add(newPath.get(i));
        }
    }

    public void addToPath(Point point)
    {
        this.path.add(point);
    }

    public HashMap<Point, Integer> getMap()
    {
        return this.map;
    }

    /*
    public void setMap(HashMap<Point, Integer> newMap)
    {
        this.map = new HashMap<Point, Integer>(newMap);
    }
    */
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
        simTime = 1;
        path = new ArrayList<Point>();
    }

    public ArrayList<Object> nearestNeighbor(Point pcLoc, int[][] costMatrix, Candidate currCandidate, HashMap<Point, Integer> pointToIndex)
    {
        int currFoodCell = pointToIndex.get(pcLoc);

        // maybe consider moving this
        currCandidate.removeFood(pcLoc);

        int minCost = Integer.MAX_VALUE;
        int minIndex = 0;

        List<Point> food = currCandidate.getRemainingFood();

        for (int i = 0; i < food.size(); i++)
        {
            if (!currCandidate.getPath().contains(food.get(i)) && currFoodCell != pointToIndex.get(food.get(i)) && costMatrix[currFoodCell][pointToIndex.get(food.get(i))] != 0 && costMatrix[currFoodCell][pointToIndex.get(food.get(i))] < minCost)
            {
                //System.out.println("at costMatrix[" + (currFoodCell) + "][" + pointToIndex.get(food.get(i)) + "]");
                minCost = costMatrix[currFoodCell][pointToIndex.get(food.get(i))];
                //System.out.println("minCost = " + minCost);
                minIndex = i;
                //System.out.println("food point: (" + food.get(i).x + "," + food.get(i).y + ")");
            }
            /*
            if (!currCandidate.getPath().contains(food.get(i)) && costMatrix[currFoodCell + 1][pointToIndex.get(food.get(i))] < minCost)
            {
                minCost = costMatrix[currFoodCell + 1][pointToIndex.get(food.get(i))];
                // index in the food array
                minIndex = i;
            }
            */
        }

        ArrayList<Object> retval = new ArrayList<>();
        retval.add(minCost);
        retval.add(food.get(minIndex));

        for (int i = 0; i < food.size(); i++)
        {
            if (i != minIndex && currFoodCell != pointToIndex.get(food.get(i)) && costMatrix[currFoodCell][pointToIndex.get(food.get(i))] == minCost)
            {
                retval.add(food.get(i));
            }
        }

        /*
        for (int i = 0; i < retval.size(); i++)
        {
            System.out.println(retval.get(i));
        }
        */

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

        if (path.isEmpty())
        {
            List<Point> food = PacUtils.findFood(grid);
            int foodCount = food.size();

            int[][] costMatrix = new int[foodCount + 1][foodCount + 1];

            for (int i = 0; i < foodCount; i++)
            {
                for (int j = 0; j < i; j++)
                {
                    int cost = BFSPath.getPath(grid, food.get(i), food.get(j)).size();

                    costMatrix[i + 1][j + 1] = cost;
                    costMatrix[j + 1][i + 1] = cost;
                }

                int costFromPac = BFSPath.getPath(grid, pc.getLoc(), food.get(i)).size();
                costMatrix[0][i + 1] = costFromPac;
                costMatrix[i + 1][0] = costFromPac;
            }

            System.out.println("Cost table:\n");

            for (int i = 0; i < costMatrix.length; i++)
            {
                for (int j = 0; j < costMatrix.length; j++)
                {
                    System.out.print("\t" + costMatrix[i][j]);
                }

                System.out.println();
            }

            List<Point> foodArray = PacUtils.findFood((PacCell[][]) state);

            System.out.println("\nFood Array:\n");

            HashMap<Point, Integer> pointToIndex = new HashMap<>();

            for (int i = 0; i < foodArray.size(); i++)
            {
                System.out.println(i + " : (" + foodArray.get(i).x + "," + foodArray.get(i).y + ")");
                pointToIndex.put(foodArray.get(i), i + 1);
            }

            // plan generation timer
            long startTime = System.currentTimeMillis();

            ArrayList<Candidate> prevPopulation = new ArrayList<>();

            // population steps
            for (int i = 0; i < foodArray.size(); i++)
            {
                System.out.println("\nPopulation at step " + (i + 1) + " :");

                ArrayList<Candidate> candidateList = new ArrayList<>();

                int numEntries = Math.max(foodArray.size(), prevPopulation.size());

                // pop step 1: from pacman to initial food dot
                if (i == 0)
                {
                    for (int j = 0; j < foodArray.size(); j++)
                    {
                        ArrayList<Point> tempFood = new ArrayList<>();

                        for (int k = 0; k < foodArray.size(); k++)
                        {
                            tempFood.add(foodArray.get(k));
                        }

                        Candidate currCandidate = new Candidate(tempFood);
                        Point tempPoint = new Point(foodArray.get(j).x, foodArray.get(j).y);
                        currCandidate.addToPath(tempPoint);
                        currCandidate.removeFood(tempPoint);
                        currCandidate.setPointCost(tempPoint, costMatrix[0][j + 1]);
                        currCandidate.setCost(currCandidate.getPointCost(tempPoint));

                        candidateList.add(currCandidate);
                    }

                    Collections.sort(candidateList, new Comparator<Candidate>() {
                        @Override
                        public int compare(Candidate cand1, Candidate cand2)
                        {
                            if (cand1.getCost() > cand2.getCost())
                            {
                                return 1;
                            }
                            else if (cand1.getCost() < cand2.getCost())
                            {
                                return -1;
                            }
                            else
                            {
                                return 0;
                            }
                        }
                    });

                    prevPopulation = new ArrayList<>(candidateList);

                    int index = 0;

                    for (Candidate cand : candidateList)
                    {
                        System.out.print(index + " : cost=" + cand.getCost());
                        System.out.print(" : [(" + cand.getX(0) + "," + cand.getY(0) + ")");
                        System.out.println("," + cand.getPointCost(0) + "]");
                        index++;
                    }
                }

                else
                {

                    ArrayList<Candidate> oldCandidateList = new ArrayList<>(prevPopulation);

                    candidateList.clear();

                    int numCandidates = oldCandidateList.size();

                    for (int j = 0; j < numCandidates; j++)
                    {
                        //Candidate currCandidate = candidateList.get(j);
                        //Candidate currCandidate = new Candidate(candidateList.get(j).getRemainingFood());
                        Candidate currCandidate = new Candidate(oldCandidateList.get(j));

                        /*
                        System.out.println("Candidate " + j + ":");
                        System.out.println(candidateList.get(j).getCost());
                        System.out.println(candidateList.get(j).getPath());
                        */

                        //currCandidate.setPath(candidateList.get(j).getPath());
                        //currCandidate.setCost(candidateList.get(j).getCost());
                        //currCandidate.setMap(candidateList.get(j).getMap());

                        //System.out.println("Current candidate: " + currCandidate.getPath());

                        //System.out.println("This point: (" + currCandidate.getPoint(i - 1).x + "," + currCandidate.getPoint(i - 1).y + ")");

                        ArrayList<Object> nearestNeighbors = nearestNeighbor(
                            currCandidate.getPoint(i - 1), costMatrix, currCandidate, pointToIndex
                        );

                        int minCost = (int) nearestNeighbors.get(0);

                        if (nearestNeighbors.size() > 2)
                        {
                            

                            Point temp = (Point) nearestNeighbors.get(1);
                            Point point = new Point(temp.x, temp.y);
                            
                            currCandidate.addToPath(point);
                            currCandidate.setPointCost(point, minCost);
                            currCandidate.setCost(currCandidate.getCost() + minCost);
                            currCandidate.removeFood(point);

                            //System.out.println(">2 zone candidate: " + currCandidate.getPath());

                            System.out.println("Added branch candidate with point: (" + point.x + "," + point.y + ")");

                            candidateList.add(currCandidate);
                            
                            //System.out.println("Point: " + point);

                            //System.out.println("Current Candidate List: ");

                            /*
                            for (int k = 0; k < candidateList.size(); k++)
                            {
                                System.out.println(candidateList.get(k).getPath());
                            }
                            */

                            
                            for (int k = 2; k < nearestNeighbors.size(); k++)
                            {
                                temp = (Point) nearestNeighbors.get(k);

                                Point tempPoint = new Point(temp.x, temp.y);

                                List<Point> tempFood = new ArrayList<>(currCandidate.getRemainingFood());                                
                                
                                Candidate tempCandidate = new Candidate(tempFood);

                                tempCandidate.setPath(currCandidate.getPath(), currCandidate.getPathLength() - 1);
                                tempCandidate.setCost(currCandidate.getCost());
                                tempCandidate.removeFood(point);

                                //System.out.println("Temp candidate path: ");

                                tempCandidate.addToPath(tempPoint);
                                tempCandidate.setPointCost(tempPoint, minCost);

                                
                                for (int l = 0; l < tempCandidate.getPathLength(); l++)
                                {
                                    System.out.println(tempCandidate.getPath().get(l));
                                }

                                //System.out.println("Added branch candidate with point: (" + tempCandidate.getPoint(tempCandidate.getPathLength() - 1).x + "," + tempCandidate.getPoint(tempCandidate.getPathLength() - 1).y + ")");
                                

                                candidateList.add(tempCandidate);
                            }
                                                      
                            
                        }

                        else
                        {
                            
                            Point temp2 = (Point) nearestNeighbors.get(1);
                            Point point2 = new Point(temp2.x, temp2.y);

                            currCandidate.addToPath(point2);
                            currCandidate.setPointCost(point2, minCost);
                            currCandidate.setCost(currCandidate.getCost() + minCost);

                            candidateList.add(currCandidate);

                            //System.out.println("Added normal candidate with point: (" + point2.x + "," + point2.y + ")");
                            
                            
                        }



                    }

                    Collections.sort(candidateList, new Comparator<Candidate>() {
                        @Override
                        public int compare(Candidate cand1, Candidate cand2)
                        {
                            if (cand1.getCost() > cand2.getCost())
                            {
                                return 1;
                            }
                            else if (cand1.getCost() < cand2.getCost())
                            {
                                return -1;
                            }
                            else
                            {
                                return 0;
                            }
                        }
                    });

                    //prevPopulation = new ArrayList<>(candidateList);
                    
                    for (int j = 0; j < candidateList.size(); j++)
                    {
                        Candidate currCandidate = candidateList.get(j);

                        System.out.print(j + " : cost=" + currCandidate.getCost() + " : ");

                        for (int k = 0; k < currCandidate.getPathLength(); k++)
                        {
                            //System.out.println(currCandidate.getPath().get(0));
                            Point tempPoint = currCandidate.getPath().get(currCandidate.getPathLength() - 1);
                            System.out.print("[(" + currCandidate.getX(k) + "," + currCandidate.getY(k) + "),");
                            System.out.print(currCandidate.getPointCost(tempPoint) + "]");
                        }

                        System.out.println();
                        
                    }

                    if (i == foodArray.size() - 1)
                    {
                        path.add(candidateList.get(0).getPath().get(0));
                        List<Point> innerSteps;

                        for (int j = 0; j < candidateList.get(0).getPathLength() - 1; j++)
                        {
                            //path.add(candidateList.get(0).getPoint(j));
                            // add the intermediate cells in the path from one food dot to another
                            innerSteps = BFSPath.getPath(grid, candidateList.get(0).getPath().get(j), candidateList.get(0).getPath().get(j + 1));
                            
                            for (int k = 0; k < innerSteps.size(); k++)
                            {
                                path.add(innerSteps.get(k));
                            }
                        }
                    }

                    System.out.println();
                }
                
            }

            long timeElapsed = System.currentTimeMillis() - startTime;

            System.out.println("Time to generate plan: " + (int) timeElapsed + " msec");

            System.out.println("\nSolution moves:");
        }
        

        Point curr = path.get(0);
        Point next = path.remove(0);
        PacFace face = PacUtils.direction(pc.getLoc(), next);

        System.out.println(simTime + " : From [ " + curr.x + ", " + curr.y + " ] go " + face);
        simTime++;

        return face;
    }
}