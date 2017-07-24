package problem;

import aco.ant.Ant;
import aco.ACO;
import java.util.*;

public class QuadraticAssignmentProblem extends Problem{

	public static final int Q = 1;
	public ACO aco;

	protected int[][] mapping;

	protected int numberOfPassengers;
	protected int numberOfDrivers;

	public QuadraticAssignmentProblem(String filename) {
		super(filename);

		reader.open();

		this.numberOfPassengers = reader.readInt();
		this.numberOfDrivers = reader.readInt();
		this.mapping = reader.readIntMatrix(numberOfPassengers, numberOfDrivers, ",");

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
        List<Integer> pairings = new ArrayList<Integer>();
		for (int i = 0; i < numberOfPassengers; i++) {
			// TODO pass in the single row array with the passenger's distance to each driver
            // returns an array ordered by the best driver for this passenger
            int [] passengersDrivers = mapping[i];
            System.out.println("Passenger " +i);
            Set<Ant> bestAnt = aco.solve(passengersDrivers);
            Iterator iter = bestAnt.iterator();
            while (iter.hasNext()) {
                System.out.println(iter.next());
            }
		}
		// TODO deal with any tiebreakers
	}

	@Override
    public String getBestSolution() {
	    return "";
    }
}
