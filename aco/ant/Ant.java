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
import java.util.List;
import java.util.Observable;

import sys.Settings;
import util.PseudoRandom;
import aco.ACO;

/**
 * The Ant Class
 * 
 * @author Thiago Nascimento
 * @since 2014-07-27
 * @version 1.0
 */
public abstract class Ant extends Observable implements Runnable {
	
	public static int ANT_ID = 1;
	
	/** Importance of trail */
	public static final int ALPHA = Settings.ALPHA;
	
	/** Importance of heuristic evaluate */
	public static final int BETA = Settings.BETA;

	/** Identifier */
	public int id = ANT_ID++;
	
	public ACO aco;
	
	public List<Integer> tour;
	
	/** The Current Node */
	public int currentNode;
	
	public int[][] path;
	
	public List<Integer> nodesToVisit;

	public double tourLength;
	
	public Ant(ACO aco) {
		this.aco = aco;
		reset();		
	}
	
	public void reset(){
		this.currentNode = -1;
		this.tourLength = 0;
		this.nodesToVisit = new ArrayList<Integer>();
		this.tour = new ArrayList<Integer>();
		this.path = new int[aco.p.getNodesDriver()];
	}

	@Override
	public void run() {
		init();
		explore();		
		setChanged();
		notifyObservers(this);
	}
	
	public void init(){
		reset();
		this.currentNode = PseudoRandom.randInt(0, aco.p.getNodesDriver() - 1);
		this.tour.add(new Integer(currentNode));
		this.aco.p.initializeTheMandatoryNeighborhood(this);
	}
	
	@Override
	public String toString() {
		return "Ant " + id + " " + tour+" "+tourLength;
	}

	/**
	 * Construct the solutions
	 */
	public abstract void explore();

	/**
	 * Clone the ant
	 */
	public abstract Ant clone();
}
