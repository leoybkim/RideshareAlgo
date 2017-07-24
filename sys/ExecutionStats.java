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

package sys;

import java.util.Date;

import problem.Problem;
import aco.ACO;
import aco.ant.Ant;

/**
 *  The Algorithm's Executor Class
 * 
 * @author Thiago Nascimento
 * @since 2014-07-27
 * @version 1.0
 */
public class ExecutionStats {
	
	public double executionTime;
	
	public Problem p;
	
	public ACO aco;
	
	public static ExecutionStats execute(ACO aco, Problem p) {
		ExecutionStats ets = new ExecutionStats();
		double startTime = (new Date()).getTime();
		ets.p = p;
		ets.aco = aco;
		p.solve();
		ets.executionTime = (new Date()).getTime() - startTime;
		return ets;
	}
	
	public void printStats(){
		System.out.println("ACO Finished Algorithm");
		System.out.println("Execution time (ms): "+executionTime);
		System.out.println("Best Solution Found: " + p.getBestSolution);
	}
}
