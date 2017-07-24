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

/**
 * The Framework's Settings Class
 * 
 * @author Thiago Nascimento
 * @since 2014-07-27
 * @version 1.0
 */
public class Settings {
	
	/** Importance of trail */
	public static int ALPHA = 1;
	
	/** Importance of heuristic evaluate */
	public static int BETA = 1;
	
	/** Global Update Rule */
	public static double RHO = 0.1;
	
	public static double Q0 = 0.9;
	
	public static double P = 0.1;
}
