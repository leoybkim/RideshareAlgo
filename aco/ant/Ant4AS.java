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

import java.util.ArrayList;

import util.RouletteWheel;
import aco.ACO;

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
		while (!nodesToVisit.isEmpty()) {
			int nextNode = doExploration(currentNode);

			//Save next node
			tour.add(new Integer(nextNode));
			path[currentNode][nextNode] = 1;
			path[nextNode][currentNode] = 1;
			
			aco.p.updateTheMandatoryNeighborhood(this);
			
			currentNode = nextNode;
		}
	}

	/**
	 * Return the next node
	 * 
	 * @param i The current node
	 * @return The next node
	 */
	protected int doExploration(int i) {
		int nextNode = -1;
		double sum = 0.0;
		
		// Update the sum
		for (Integer j : nodesToVisit) {
			if (aco.getTau(i, j) == 0.0) {
				throw new RuntimeException("tau == 0.0");
			}

			double tij = Math.pow(aco.getTau(i, j), ALPHA);
			double nij = Math.pow(aco.p.getNij(i, j), BETA);
			sum += tij * nij;
		}
		
		if (sum == 0.0) {
			throw new RuntimeException("sum == 0.0");
		}

		double[] probability = new double[aco.p.getNodes()];
		double sumProbability = 0.0;
		
		for (Integer j : nodesToVisit) {
			double tij = Math.pow(aco.getTau(i, j), ALPHA);
			double nij = Math.pow(aco.p.getNij(i, j), BETA);
			probability[j] = (tij * nij) / sum;
			sumProbability += probability[j];
		}
		
		// Select the next node by probability
		nextNode = RouletteWheel.select(probability, sumProbability);

		if (nextNode == -1) {
			throw new RuntimeException("nextNode == -1");
		}

		nodesToVisit.remove(new Integer(nextNode));
		
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