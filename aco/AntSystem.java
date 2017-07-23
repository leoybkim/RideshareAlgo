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

import problem.Problem;
import aco.ant.Ant;
import aco.ant.Ant4AS;

/**
 * Ant System Class
 * 
 * @author Thiago Nascimento
 * @since 2014-07-27
 * @version 1.0
 */
public class AntSystem extends ACO {

	public AntSystem(Problem problem, int numberOfAnts, int interations) {
		super(problem, numberOfAnts, interations);
	}

	@Override
	public void daemonActions() {

	}

	@Override
	public void initializeAnts() {
		this.ant = new Ant[numberOfAnts];

		for (int k = 0; k < numberOfAnts; k++) {
			ant[k] = new Ant4AS(this);
			ant[k].addObserver(this);
		}
	}

	@Override
	public void globalUpdateRule() {
		for (int i = 0; i < p.getNodes(); i++) {
			for (int j = i; j < p.getNodes(); j++) {
				if (i != j) {
					double deltaTau = 0.0;

					for (int k = 0; k < numberOfAnts; k++) {
						if (ant[k].path[i][j] == 1) {
							deltaTau += p.getDeltaTau(ant[k], i, j);
						}
					}

					double evaporation = (1.0 - RHO) * tau[i][j];
					double deposition = deltaTau;				
					
					tau[i][j] = evaporation + deposition;
					tau[j][i] = evaporation + deposition;
				}
			}
		}
	}
}
