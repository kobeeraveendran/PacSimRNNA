import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
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
        numMoves = 0;
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
            Point target = PacUtils.nearestFood(pc.getLoc(), grid);
            path = BFSPath.getPath(grid, pc.getLoc(), target);

            System.out.println("Pac-Mac current at: [ " + pc.getLoc().x + ", " + pc.getLoc().y + " ]");
            System.out.println("Setting new target  : [ " + target.x + ", " + target.y + " ]");
        }

        Point next = path.remove(0);
        PacFace face = PacUtils.direction(pc.getLoc(), next);

        System.out.println("%5d : From [ %2d, %2d ] go ");

        
    }

}
