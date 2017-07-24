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
	
	/** Ants **/
	protected Ant[] ant;

	/** Number of Ants */
	protected int numberOfAnts;

	/** Pheromone Matrix **/
	protected double[][] tau;
	
	/** Applied Problem */
	public Problem p;
	
	/** Total of Interations */
	protected int interations;
	
	/** The current interation */
	protected int it = 0;
	
	/** Total of ants that finished your tour */
	protected int finishedAnts = 0;
	
	/** Best Ant in tour */
	protected Ant bestAnt;
	
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

	public Ant solve() {
		initializeData();
		while (!terminationCondition()) {
			constructAntsSolutions();
			updatePheromones();
			daemonActions(); // optional
		}
		return bestAnt;
	}

	private void initializeData() {
		initializePheromones();
		initializeAnts();		
	}

	private void initializePheromones() {
		this.tau = new double[p.getNodesPassenger()][p.getNodesDriver()];

		for (int i = 0; i < p.getNodesPassenger(); i++) {
			for (int j = 0; j < p.getNodesDriver(); j++) {
				this.tau[i][j] = p.getT0();
			}
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

		ant.tourLength = p.evaluate(ant);

		if (p.better(ant, bestAnt)) {
			bestAnt = ant.clone();
		}

		if (++finishedAnts == numberOfAnts) {
			// Continue all execution
			finishedAnts = 0;
			notify();
		}
	}

	public double[][] getTau() {
		return tau;
	}
	
	public synchronized void setTau(int j, int i, double value) {
		tau[i][j] = value;		
	}
	
	public double getTau(int i,int j){
		return tau[i][j];
	}
	
	public abstract void daemonActions();
	
	public abstract void globalUpdateRule();
	
	public abstract void initializeAnts();	
}
