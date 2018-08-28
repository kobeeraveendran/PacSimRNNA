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

    public PacFace action(Object state)
    {
        PacCell[][] grid = (PacCell[][]) state;
        PacmanCell pc = PacUtils.findPackman(grid);

        if (pc == null)
        {
            return null;
        }

        if (path.isEmpty())
        {
            Point target = PacUtils.nearestFood(pc.getLoc(), grid);
            path = BFSPath.getPath(grid, pc.getLoc(), target);

            System.out.println("Pac-Mac current at: [ " + pc.getLoc().x + ", " + pc.getLoc().y + " ]");
            System.out.println("Setting new target  : [ " + target.x + ", " + target.y + " ]");
            
            
        }
    }

}
