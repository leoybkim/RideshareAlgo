/*
 * Copyright 2014 Thiago Nascimento
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package aco;

import java.util.Observable;
import java.util.Observer;
import java.util.TreeSet;
import java.util.Set;
import java.util.Comparator;

import problem.Problem;
import sys.Settings;
import aco.ant.Ant;

/**
 * The ACO Class
 * 
 * @author Thiago Nascimento
 * @since 2014-07-23
 * @version 1.0
 */
public abstract class ACO implements Observer{

	/** Parameter for evaporation */
	public static final double RHO = Settings.RHO;

	public static final int Q = 1;

	protected int[] driverDistances;
	
	/** Ants **/
	protected Ant[] ant;

	/** Number of Ants */
	protected int numberOfAnts;

	/** Pheromone Matrix **/
	protected double[] tau;
	
	/** Applied Problem */
	public Problem p;
	
	/** Total of Interations */
	protected int interations;
	
	/** The current interation */
	protected int it = 0;
	
	/** Total of ants that finished your tour */
	protected int finishedAnts = 0;
	
	/** Best Ant in tour */
	protected Set<Ant> bestAnt;
	
	public ACO(Problem problem, int numberOfAnts, int interations) {
		if (problem == null) {
			throw new IllegalArgumentException("p shouldn't be null");
		}
		if (numberOfAnts <= 0) {
			throw new IllegalArgumentException("numberOfAnts shouldn't be less than 0");
		}
		if (interations <= 0) {
			throw new IllegalArgumentException("interations shouldn't be less than 0");
		}
		
		this.p = problem;
		this.numberOfAnts = numberOfAnts;		
		this.interations = interations;
	}

	public Set<Ant> solve(int[] passengerDrivers) {
		reset();
		this.driverDistances = passengerDrivers;
		initializeData();
		while (!terminationCondition()) {
			constructAntsSolutions();
			updatePheromones();
		}
		return bestAnt;
	}

	public void reset(){
		this.bestAnt = new TreeSet<Ant>(new Comparator<Ant>() {
			@Override
			public int compare(Ant o1, Ant o2) {
				// Define comparing logic here
				if (o1.currentNode == o2.currentNode) {
					return 0;
				} else {
					if (o1.tourLength < o2.tourLength)
						return -1;
					else if (o2.tourLength < o1.tourLength)
						return 1;
					else
						return -1;
				}
			}
		});
		this.it = 0;
		this.finishedAnts = 0;
	}

	public double getDeltaTau(int i) {
		return Q / driverDistances[i];
	}

	private void initializeData() {
		initializePheromones();
		initializeAnts();		
	}

	private void initializePheromones() {
		this.tau = new double[p.getNodesDriver()];

		for (int j = 0; j < p.getNodesDriver(); j++) {
			this.tau[j] = p.getT0();
		}
	}
	
	private boolean terminationCondition() {
		return ++it > interations;
	}

	private void updatePheromones() {
		globalUpdateRule();
	}

	private synchronized void constructAntsSolutions() {
		//Contruct Ant solutions
		for (int k = 0; k < numberOfAnts; k++) {
			Thread t = new Thread(ant[k],"Ant "+ant[k].id);
			t.start();
		}
		
		//Wait all ants finish your tour
		try{
			wait();
		}catch(InterruptedException ex){
			ex.printStackTrace();
		}	
	}
	
	/**
	 * Call when a ant finished your tour
	 * 
	 * @Override 
	 */
	public synchronized void update(Observable observable, Object obj) {
		Ant ant = (Ant) obj;

		ant.tourLength = driverDistances[ant.currentNode];

//		System.out.println("Ant #"+ant.id + " says that the currentNode is "+ant.currentNode +" and the tourlength at is " +ant.tourLength);

//		if (better(ant, bestAnt)) {
//			bestAnt.add(ant.clone());
//		}

		bestAnt.add(ant.clone());

		if (++finishedAnts == numberOfAnts) {
			// Continue all execution
			finishedAnts = 0;
			notify();
		}
	}

	public boolean better(Ant ant, Ant bestAnt) {
		return bestAnt == null || ant.tourLength < bestAnt.tourLength;
	}

	public double[] getTau() {
		return tau;
	}
	
	public synchronized void setTau(int i, double value) {
		tau[i] = value;
	}
	
	public double getTau(int i){
		return tau[i];
	}
	
	public int getDriverDistances(int i) {
		return driverDistances[i];
	}
	
	public abstract void globalUpdateRule();
	
	public abstract void initializeAnts();	
}
