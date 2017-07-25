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

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * The Instance Reader Class
 * 
 * @author Thiago Nascimento
 * @since 2014-07-27
 * @version 1.0
 */
public class InstanceReader {
	
	protected FileReader reader;
	
	protected BufferedReader buffer;
	
	protected String filename;
	
	public InstanceReader(String filename){
		this.filename = filename;		
	}
	
	/**
	 * Open file to read
	 */
	public void open() {
		try {
			this.reader = new FileReader(filename);
			this.buffer = new BufferedReader(reader);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Close file after read it
	 */
	public void close() {
		try {
			if (buffer != null) {
				buffer.close();
			}
			if (reader != null) {
				reader.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read a line
	 * @return A line read
	 */
	public String readLine(){
		try {
			return buffer.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int readInt() {
		return Integer.valueOf(readLine());
	}
	
	public double[][] readDoubleMatrix(int i, int j) {
		return readDoubleMatrix(i, j, " ");
	}

	public double[][] readDoubleMatrix(int i, int j, String separator) {
		double[][] result = new double[i][j];

		for (int k = 0; k < i; k++) {
			String[] split = readLine().split(separator);
			result[k] = Convert.toDoubleArray(split);
		}

		return result;
	}

	public int[] readIntVector(String separator) {
		String[] split = readLine().split(separator);

		int[] result = new int[split.length];

		for (int k = 0; k < split.length; k++) {
			result[k] = Integer.valueOf(split[k]);
		}

		return result;
	}

	public int[] readIntVector(int size, String separator) {
		return readIntVector(" ");
	}
	
	public int[][] readIntMatrix(int i, int j) {
		return readIntMatrix(i, j, " ");
	}

	public int[][] readIntMatrix(int i, int j, String separator) {
		int[][] result = new int[i][j];

		for (int k = 0; k < i; k++) {
			String[] split = readLine().split(separator);
			result[k] = Convert.toIntArray(split);
		}

		return result;
	}
}
