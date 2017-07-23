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

package problem;

import sys.InstanceReader;
import aco.ant.Ant;

/**
 * The Problem Class
 * 
 * @author Thiago Nascimento
 * @since 2014-07-13
 * @version 1.0
 */
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
	 * Get heuristic information
	 * 
	 * @param i Node i
	 * @param j Node j
	 * @return heuristic information between node i and j
	 */
	public abstract double getNij(int i, int j);
	
	/**
	 * Return number of nodes
	 * @return number of nodes
	 */
	public abstract int getNodes();
	
	/**
	 * Return the initial pheromone
	 * @return Initial pheromone
	 */
	public abstract double getT0();

	public abstract void initializeTheMandatoryNeighborhood(Ant ant);
	
	public abstract void updateTheMandatoryNeighborhood(Ant ant);

	public abstract double evaluate(Ant ant);	
	
	public abstract boolean better(Ant ant, Ant bestAnt);

	public abstract double getDeltaTau(Ant ant,int i, int j);
}
