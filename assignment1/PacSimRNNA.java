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


    @Override
    public void init()
    {
        simTime = 0;
        path = new ArrayList();
    }

}