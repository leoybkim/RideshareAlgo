package problem;

import aco.ant.Ant;
import aco.ACO;
import java.util.*;

public class QuadraticAssignmentProblem extends Problem{

	public static final int Q = 1;

	protected int[][] mapping;

	protected int numberOfPassengers;
	protected int numberOfDrivers;

	protected int[] bestForPassenger;

	public QuadraticAssignmentProblem(String filename) {
		super(filename);

		reader.open();

		this.numberOfPassengers = reader.readInt();
		this.numberOfDrivers = reader.readInt();
		this.mapping = reader.readIntMatrix(numberOfPassengers, numberOfDrivers, " ");

		reader.close();
	}

	@Override
    public int getNodesPassenger() { return numberOfPassengers;}

    @Override
    public int getNodesDriver() { return numberOfDrivers;}

	@Override
	public double getT0() {
		return 1.0;
	}

	@Override
	public void solve(ACO aco) {
		bestForPassenger = new int[numberOfPassengers];
		int [] taken = new int[numberOfDrivers];
		for (int i = 0; i < numberOfPassengers; i++) {
            int [] passengersDrivers = mapping[i];
            Set<Ant> bestAnt = aco.solve(passengersDrivers);
            Iterator iter = bestAnt.iterator();
			while (iter.hasNext()) {
				Ant o = (Ant) iter.next();
				if (taken[o.currentNode] > 0) {
					continue;
				}else{
					bestForPassenger[i] = o.currentNode;
					taken[o.currentNode] = 1;
					break;
				}
			}
		}
	}

	@Override
    public int[] getBestSolution() {
		return bestForPassenger;
    }
}
