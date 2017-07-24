package problem;

import sys.InstanceReader;
import aco.ant.Ant;
import aco.ACO;


public abstract class Problem {

	protected InstanceReader reader;

	private String filename;

	public Problem(String filename){
		this.filename = filename;
		this.reader = new InstanceReader(filename);
	}

	public String getFilename() {
		return filename;
	}

	/**
	 * Return number of nodes
	 * @return number of nodes
	 */
	public abstract int getNodesPassenger();

	public abstract int getNodesDriver();

	/**
	 * Return the initial pheromone
	 * @return Initial pheromone
	 */
	public abstract double getT0();

	public abstract void solve(ACO aco);

	public abstract String getBestSolution();
}
