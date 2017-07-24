package problem;

import aco.ant.Ant;

public class QuadraticAssignmentProblem extends Problem{

	public static final int Q = 1;
	
	protected int size;
	
	protected int[][] F;
	
	protected int[][] D;
	
	//Defined by Dorigo
	protected int[] potencialD;
	
	//Defined by Dorigo	
	protected int[] potencialF;

	protected int[] totalDistancePerPassenger;

	protected int[][] mapping;

	protected int numberOfPassengers;
	protected int numberOfDrivers;
	
	public QuadraticAssignmentProblem(String filename) {
		super(filename);
		
		reader.open();
		
		this.numberOfPassengers = reader.readInt();
		this.numberOfDrivers = reader.readInt();
		this.mapping = reader.readIntMatrix(numberOfPassengers, numberOfDrivers, ",");

//		this.F = reader.readIntMatrix(size, size,",");
//		this.D = reader.readIntMatrix(size, size,",");
		
		reader.close();
		
		//Calculate the potencial D e potencial F
//		this.potencialD = new int[size];
//		this.potencialF = new int[size];
		
		for (int i = 0; i < numberOfPassengers; i++) {
//			int pD = 0;
//			int pF = 0;
			int p = 0;
			for (int j = 0; j < numberOfDrivers; j++) {
//				pD += D[i][j];
//				pF += F[i][j];
				p += mapping[i][j];
			}
//			potencialD[i] = pD;
//			potencialF[i] = pF;
			totalDistancePerPassenger[i] = p;
		}
	}

	@Override
	public double getNij(int i, int j) {
		return (double) mapping[i][j];
	}

	@Override
	public int getNodes() {
		return size;
	}

	@Override
    public int getNodesPassenger() { return numberOfPassengers;}

    @Override
    public int getNodesDriver() { return numberOfDrivers;}

	@Override
	public double getT0() {
		return 1.0;
	}

	@Override
	public void initializeTheMandatoryNeighborhood(Ant ant) {
		//Add all nodes less the start node
		for (int i = 0; i < getNodes(); i++) {
			if(i != ant.currentNode){
				ant.nodesToVisit.add(new Integer(i));
			}
		}		
	}

	@Override
	public void updateTheMandatoryNeighborhood(Ant ant) {
		// Nothing to do		
	}

	@Override
	public double evaluate(Ant ant) {
		double value = 0.0;
		
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				int piI = ant.tour.get(i);
				int piJ = ant.tour.get(j);
				value += F[i][j] * D[piI][piJ];
			}
		}
				
		return value;
	}

	@Override
	public boolean better(Ant ant, Ant bestAnt) {
		return bestAnt == null || ant.tourLength < bestAnt.tourLength;
	}

	@Override
	public double getDeltaTau(Ant ant, int i, int j) {
		return Q / ant.tourLength;
	}
}
