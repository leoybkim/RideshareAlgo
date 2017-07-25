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

package aco.ant;

import util.RouletteWheel;
import aco.ACO;

import java.util.ArrayList;

/**
 * The AS Ant Class
 * 
 * @author Thiago Nascimento
 * @since 2014-07-27
 * @version 1.0
 */
public class Ant4AS extends Ant {

	public Ant4AS(ACO aco) {
		super(aco);		
	}

	@Override
	public void explore() {
		currentNode = doExploration();
		path[currentNode] = 1;
//		System.out.println("Ant #"+this.id + " says that the currentNode is "+currentNode +" and the path at is " +path[currentNode]);
	}

	/**
	 * Return the next node
	 *
	 * @return The next node
	 */
	protected int doExploration() {
		int nextNode = -1;
		double sum = 0.0;
		
		// Update the sum
		for (int i = 0; i < aco.p.getNodesDriver(); i++) {
			if (aco.getTau(i) == 0.0) {
				throw new RuntimeException("tau == 0.0");
			}
			double inverseDriver = 1/(double) (aco.getDriverDistances(i));

			double tij = Math.pow(aco.getTau(i), ALPHA);
			double nij = Math.pow(inverseDriver, BETA);
			sum += tij * nij;
		}
		
		if (sum == 0.0) {
			throw new RuntimeException("sum == 0.0");
		}

		double[] probability = new double[aco.p.getNodesDriver()];
		double sumProbability = 0.0;
		
		for (int j = 0; j < aco.p.getNodesDriver(); j++) {
			double inverseDriver = 1/(double) (aco.getDriverDistances(j));
			double tij = Math.pow(aco.getTau(j), ALPHA);
			double nij = Math.pow(inverseDriver, BETA);
			probability[j] = (tij * nij) / sum;
			sumProbability += probability[j];
		}
		
		// Select the next node by probability
		nextNode = RouletteWheel.select(probability, sumProbability);

		if (nextNode == -1) {
			throw new RuntimeException("nextNode == -1");
		}
		
		return nextNode;
	}

	@Override
	public Ant clone() {
		Ant ant = new Ant4AS(aco);
		ant.id = id;
		ant.currentNode = currentNode;
		ant.tourLength = tourLength;
		ant.tour = new ArrayList<Integer>(tour);
		ant.path = path.clone();
		return ant;
	}
}