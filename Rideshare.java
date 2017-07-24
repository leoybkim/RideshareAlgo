import java.io.*;
import java.util.*;
import java.lang.*;

//TODO: Add stats...
// Track time it takes for average trip
// Track time it takes for average pickup
// Track time it takes for both added together
// Track number of passengers per driver

class Solution {
  private static double lambda = 0.2;
  private static Random rng = new Random();
  private static int dim = 10;
  private static int numDrivers = 8;
  private static int numPassengers = 10;
  private static Grid grid;

  private static final int TABU_TENURE = 3;
  private static final double MATCH_FREQUENCY = dim;
  private static final int NUMBER_OF_TICKS = 50;
  //keep track of pair swap tenure using a tabu list
  private static HashMap<Pair, Integer> tabuList = new HashMap<Pair, Integer>();


  public enum DriverState {
    IDLE, DRIVING_TO_PASSENGER, DRIVING_TO_DESTINATION
  }
  public enum DriverType {
    STATIONARY, RANDOM, HEATSEEKING
  }

  public static void main(String[] args) {
    grid = new Grid(dim, numDrivers, numPassengers, DriverType.HEATSEEKING);
    generate_ACO_infile(numDrivers, numPassengers, grid);
    runQueueAlgorithm();
  }

  public static void generate_ACO_infile(int numberOfDrivers, int numberOfPassengers, Grid grid)
  {
    // The name of the file to open.
    String fileName = "test_"+numberOfDrivers+"_"+numberOfPassengers+".qap";
    try {
        // Assume default encoding.
        FileWriter fileWriter =
            new FileWriter(new File("in"+File.separator,fileName));

        // Always wrap FileWriter in BufferedWriter.
        BufferedWriter bufferedWriter =
            new BufferedWriter(fileWriter);

        // Note that write() does not automatically
        // append a newline character.
        bufferedWriter.write(Integer.toString(numberOfPassengers));
        bufferedWriter.newLine();
        bufferedWriter.write(Integer.toString(numberOfDrivers));
        bufferedWriter.newLine();

        for (int i=0; i< numberOfPassengers; i++) {
            for (int j=0; j<numberOfDrivers; j++) {
                bufferedWriter.write(Integer.toString(
                Math.abs(grid.passengers[i].location.x - grid.drivers[j].location.x)
                 + Math.abs(grid.passengers[i].location.y - grid.drivers[j].location.y)));
                bufferedWriter.write(" ");
            }
            bufferedWriter.newLine();
        }
        // Always close files.
        bufferedWriter.close();
    }
    catch(IOException ex) {
        System.out.println(
            "Error writing to file '"
            + fileName + "'");
        // Or we could just do this:
        // ex.printStackTrace();
    }
  }

  public static void runTabuSimulation(){
    //generate a random initial solution. Assign driver i to passenger i
    //a solution is a list of driver to passenger assignments
    List<Pair> solution = new ArrayList<Pair>();
    HashMap<Double, List<Pair>> permutations = new HashMap<Double, List<Pair>>();
    for (int i = 0; i < Math.max(numDrivers, numPassengers); i++){
      Driver d = new Driver(new Coordinate(-1, -1), DriverType.STATIONARY, i);
      Passenger p = new Passenger(new Coordinate(-1, -1), new Coordinate(-1, -1), i);
      if (i < numDrivers) {
        d = grid.drivers[i];
      }
      if (i < numPassengers) {
        p = grid.passengers[i];
      }

      solution.add(new Pair(d, p));
    }

    //==============SIMULATION==============================================
    Deque<Passenger> queue = new LinkedList<Passenger>();
    for (int i = 0; i < numPassengers; i++) {
      queue.addFirst(grid.passengers[i]);
    }

    int nextPassengerSpawn = 5;
    int totalNumTrips = 0;
    int totalDistanceNoPassenger = 0;
    int totalDistanceWithPassenger = 0;
    int totalCombinedDistance = 0;
    int passengerNum = 0;

    //Main tick loop
    for (int i = 0; i < 30; i++) {
      Grid.printGrid(grid);

      if (nextPassengerSpawn == i) {
        /*nextPassengerSpawn = (int)calcNextPassengerSpawn() + i;
        Coordinate loc = new Coordinate(rng.nextInt(dim),rng.nextInt(dim));
        Coordinate dest = new Coordinate(rng.nextInt(dim),rng.nextInt(dim));
        queue.addFirst(new Passenger(loc, dest, passengerNum));
        passengerNum += 1;*/
      }

      updateSimulationModel();

      // Second loop - ..tabu assignment
      int curr_driver = 0;
      List<Pair> tabu_solution = new ArrayList<Pair>();

      while (queue.size() > 0 || curr_driver < grid.drivers.length) {
        Passenger currPassenger = queue.peekLast();
        if (currPassenger == null) {
          currPassenger = new Passenger(new Coordinate(-1, -1), new Coordinate(-1, -1), passengerNum);
          passengerNum++;
        }

        Driver driver;

        if (curr_driver < grid.drivers.length) {
          driver = grid.drivers[curr_driver];
        } else {
          driver = new Driver(new Coordinate(-1, -1), DriverType.STATIONARY, curr_driver);
        }

        curr_driver++;

        if (driver.driverState != DriverState.IDLE || driver.location.x == -1) {
          continue;
        }

        if (queue.size() > 0) {
          queue.removeLast();
        }

        tabu_solution.add(new Pair(driver, currPassenger));
      }

      //get best solution by running tabu search on current solution
      if (tabu_solution.size() > 2) {
        tabu_solution = runTabuSearch(tabu_solution);
      }

      printTabuSolution(tabu_solution);


      for (Pair pair : tabu_solution) {
        if (pair.driver.location.x != -1) {
          if (pair.passenger.location.x != -1) {
            pair.driver.passenger = pair.passenger;
            pair.driver.driverState = DriverState.DRIVING_TO_PASSENGER;
            pair.driver.numTicksUntilDestination = calcDistance(pair.passenger, pair.driver);
          }
        }
      }
    }
  }

  public static void updateSimulationModel(){
    // First loop - update driver state for all drivers.
      for (int j = 0; j < numDrivers; j++) {
        Driver currDriver = grid.drivers[j];

        if (currDriver.driverState == DriverState.IDLE) {
          // Driver remains idle. Follow driver behaviour.
          // If the driver is stationary, do nothing.
          if (currDriver.driverType == DriverType.STATIONARY) {
            continue;
          }
          else if(currDriver.driverType == DriverType.RANDOM){
            Random rand = new Random();
            //rand.nextInt(max + 1 - min) + min;
            int deltaX = rand.nextInt(3) - 1, deltaY = rand.nextInt(3) - 1;
            while(deltaX == 0 || currDriver.location.x + deltaX  >= grid.dim
                  || currDriver.location.x + deltaX  < 0){
              deltaX = rand.nextInt(3) - 1;
            }
            while(deltaY == 0 || currDriver.location.y + deltaY  >= grid.dim
                  || currDriver.location.y + deltaY  < 0){
              deltaY = rand.nextInt(3) - 1;
            }
            currDriver.location.x += deltaX;
            currDriver.location.y += deltaY;

          }
          else if(currDriver.driverType == DriverType.HEATSEEKING){
            //hotspot is in the middle of the grid
            int deltaX = grid.dim/2 - currDriver.location.x;
            int deltaY = grid.dim/2 - currDriver.location.y;
            if(deltaX != 0){
              currDriver.location.x += deltaX/Math.abs(deltaX);
            }
            if(deltaY != 0){
              currDriver.location.y += deltaY/Math.abs(deltaY);
            }
          }
        }

        else if (currDriver.driverState == DriverState.DRIVING_TO_PASSENGER) {
          //still moving to passenger
          if (currDriver.numTicksUntilDestination > 0) {
            currDriver.distanceTravelled++;
            currDriver.numTicksUntilDestination--;
          }

          //found passenger
          else {
            currDriver.driverState = DriverState.DRIVING_TO_DESTINATION;
            currDriver.numTicksUntilDestination = currDriver.passenger.distance;
          }
        }

        else if (currDriver.driverState == DriverState.DRIVING_TO_DESTINATION) {
          currDriver.distanceTravelled++;
          currDriver.distanceWithPassenger++;

          if (currDriver.numTicksUntilDestination > 0) {
            currDriver.numTicksUntilDestination--;
          }
          //reached destination
          else {
            currDriver.location = currDriver.passenger.destination;
            currDriver.destination = null;
            currDriver.passenger = null;
            currDriver.driverState = DriverState.IDLE;
          }
        }
      }
  }


  public static List<Pair> runTabuSearch(List<Pair> solution){

    tabuList.clear();
    List<Pair> currSolution = solution;

    for(int i = 0; i < 10; i++){
      HashMap<List<Pair>, Swap> neighbors = generateTabuNeighbors(currSolution);

      /*for(Map.Entry<List<Pair>,Swap> neighbor: neighbors.entrySet()){
        for (Pair pair: neighbor.getKey()) {
          System.out.print("("+pair.driver.id + " "+ pair.passenger.id + ") ");
        }
        System.out.println();
      }*/

      List<Pair> bestNeighbor = getBestNeighbor(neighbors);

      //update tabu list
      ArrayList<Pair> removeList = new ArrayList<Pair>();
      ArrayList<Pair> updateList = new ArrayList<Pair>();
      for (Pair k : tabuList.keySet()) {
        if(tabuList.get(k) == 1){
          removeList.add(k);
        }
        else{
          updateList.add(k);
        }
      }

      for (int j = 0; j < removeList.size(); j++) {
        tabuList.remove(removeList.get(j));
      }
      for (int j = 0; j < updateList.size(); j++) {
        Pair p = updateList.get(j);
        tabuList.put(p, tabuList.get(p) - 1);
      }

      //add our currently chosen solution to the tabu list
      tabuList.put(neighbors.get(bestNeighbor).p1, TABU_TENURE);
      tabuList.put(neighbors.get(bestNeighbor).p2, TABU_TENURE);
      currSolution = bestNeighbor;
    }
    return currSolution;

  }

  public static HashMap<List<Pair>, Swap> generateTabuNeighbors(List<Pair> solution){
    HashMap<List<Pair>, Swap> neighbors = new HashMap<List<Pair>, Swap>();
    for(int i = 0; i < 100; i++){
      List<Pair> solutionCopy = new ArrayList<Pair>(solution.size());
      for (Pair pair: solution) {
        Pair clonePair = new Pair(pair.driver, pair.passenger);
        solutionCopy.add(clonePair);
      }

      Swap swap = randomSwap(solutionCopy);
      //a neighboring solution cannot be the same as the starting solution
      if(solutionCopy.equals(solution)){
        continue;
      }
      neighbors.put(solutionCopy, swap);
    }
    return neighbors;
  }

  public static Swap randomSwap(List<Pair> solution){

    Pair  pair1, pair2, newPair1, newPair2;
    int swap1, swap2;
    Passenger passenger1, passenger2;
    Driver driver1, driver2;

    //search for a valid random swap
    while(true){
      // get random pairs to swap
      pair1 = solution.get(rng.nextInt(solution.size()));
      pair2 = solution.get(rng.nextInt(solution.size()));

      passenger1 = pair1.passenger;
      passenger2 = pair2.passenger;

      driver1 = pair1.driver;
      driver2 = pair2.driver;

      Pair testPair1 = new Pair(driver1, passenger2);
      Pair testPair2 = new Pair(driver1, passenger2);

      if(!tabuList.containsKey(testPair1) || tabuList.containsKey(testPair2)){
        break;
      }
      if(!(tabuList.get(new Pair(driver1, passenger2)) > 0) &&
         !(tabuList.get(new Pair(driver2, passenger1)) > 0)){
        break;
      }
      System.out.println("tabu list hit");
    }

    Swap swap = new Swap(pair1, pair2);

    //swap the passengers of both the pairs
    pair1.passenger = passenger2;
    pair2.passenger = passenger1;

    return swap;
  }


  public static List<Pair> getBestNeighbor(HashMap<List<Pair>, Swap> neighbors) {
    // Min score is the best score
    double minimumScore = (double)Integer.MAX_VALUE;
    List<Pair> bestNeighbor = null;

    for(Map.Entry<List<Pair>,Swap> neighbor: neighbors.entrySet()){
      double score = scoreTabuSolution(neighbor.getKey());
      if(score < minimumScore){
        bestNeighbor = neighbor.getKey();
        minimumScore = score;
      }
    }
    return bestNeighbor;
  }



  public static double scoreTabuSolution(List<Pair> solution){
    //The output variables of interest are: Average # of trips completed, Average distance driven (on and off trips),
    //Average driver earnings, and total potential trips lost, wait time of passenger

    //if we assume that drivers move at constant speed, then wait time is proportional to distance travelled
    int total_passenger_wait = 0, total_driver_dist = 0, driver_to_passenger_dist = 0;
    for(Pair pair: solution){
      if(pair.driver.location.x == -1 || pair.passenger.location.x == -1) continue;
      Passenger passenger = pair.passenger;
      Driver driver  = pair.driver;
      total_passenger_wait += passenger.distance;
      driver_to_passenger_dist = Math.abs(driver.location.x - passenger.location.x) + Math.abs(driver.location.y -
                                  passenger.location.y);
      total_driver_dist += driver_to_passenger_dist + passenger.distance;
    }
    //take a weighted average of the output variables
    double score = 0.6 * (double)total_passenger_wait + 0.4 * (double)total_driver_dist;
    return score;
  }

  public static void printTabuSolution(List<Pair> solution){
    for(Pair pair: solution){
      String driver = pair.driver == null ? "NULL" : (pair.driver.id + "");
      String passenger = pair.passenger == null ? "NULL" : (pair.passenger.id + "");
      System.out.print("(" + driver + ", " + passenger+"), ");
    }
  }

  public static void runSwarmAlgorithm(){

  }

  public static void runQueueAlgorithm(){

    Deque<Passenger> queue = new LinkedList<Passenger>();
    for (int i = 0; i < numPassengers; i++) {
      queue.addFirst(grid.passengers[i]);
    }

    int nextPassengerSpawn = 5;
    int totalNumTrips = 0;
    int totalDistanceNoPassenger = 0;
    int totalDistanceWithPassenger = 0;
    int totalCombinedDistance = 0;
    int passengerNum = 0;

    //Main tick loop
    for (int i = 0; i < 20; i++) {
      Grid.printGrid(grid);

      if (nextPassengerSpawn == i) {
        nextPassengerSpawn = (int)calcNextPassengerSpawn() + i;
        Coordinate loc = new Coordinate(rng.nextInt(dim),rng.nextInt(dim));
        Coordinate dest = new Coordinate(rng.nextInt(dim),rng.nextInt(dim));
        queue.addFirst(new Passenger(loc, dest, passengerNum));
        passengerNum += 1;
      }

      // First loop - update driver state for all drivers.
      updateSimulationModel();

      // Second loop - assign best driver for each passenger in the queue
      while (queue.size() > 0) {
        Passenger currPassenger = queue.peekLast();
        int minDistance = Integer.MAX_VALUE;
        int distance = 0;
        Driver bestDriver = null;

        // Queue is empty, exit while loop.
        if (currPassenger == null) break;

        for (int j = 0; j < numDrivers; j++) {
          Driver currDriver = grid.drivers[j];

          // If driver is not idle, it cannot take a passenger
          if (currDriver.driverState != DriverState.IDLE) {
            continue;
          }

          if (currPassenger != null) {
            // find best driver for passenger waiting
            distance = calcDistance(currPassenger, currDriver);
            if (distance < minDistance && queue.size() > 0) {
              minDistance = distance;
              bestDriver = currDriver;
            }
          }
        }

        // All the drivers are taken
        if (bestDriver == null) break;

        bestDriver.numTicksUntilDestination = minDistance;
        bestDriver.passenger = currPassenger;
        bestDriver.driverState = DriverState.DRIVING_TO_PASSENGER;
        queue.removeLast();
      }
    }
  }

  public static int scoreQueueSolution(){
    return 0;
  }

  public static int calcDistance(Passenger p, Driver d) {
    int distance = Math.abs(p.location.x - d.location.x) + Math.abs(p.location.y - d.location.y);
    return distance;
  }
  private static long calcNextPassengerSpawn() {
        double u = rng.nextDouble(); //generate random number between 0...1
        double x = ((-1/lambda)*Math.log(1-u));
        return Math.round(x);
  }
}

class Grid {

  public int dim;
  Passenger[] passengers;
  Driver[] drivers;

  public Grid(int dim, int numDrivers, int numPassengers, Solution.DriverType driverType) {
    this.dim = dim;
    this.passengers = new Passenger[numPassengers];
    this.drivers = new Driver[numDrivers];
    Random rand = new Random();
    for (int i = 0; i < drivers.length; i++) {
      Coordinate dest = new Coordinate(rand.nextInt(dim),rand.nextInt(dim));
      drivers[i] = new Driver(dest, driverType, i);
    }
    for (int i = 0; i < passengers.length; i++) {
      Coordinate loc = new Coordinate(rand.nextInt(dim),rand.nextInt(dim));
      Coordinate dest = new Coordinate(rand.nextInt(dim),rand.nextInt(dim));
      passengers[i] = new Passenger(loc, dest, i);
    }
  }

  static void printGrid(Grid grid) {
    String[][] matrix = new String[10][10];

    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 10; j++) {
        matrix[i][j] = "-";
      }
    }

    for (Passenger p : grid.passengers) {
      //matrix[p.location.x][p.location.y] = "P";
    }
    for (Driver d : grid.drivers) {
      matrix[d.location.x][d.location.y] = "D";
      System.out.print(d.numTicksUntilDestination + " ");
    }

    System.out.println();

    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 10; j++) {
        System.out.print(matrix[i][j] + " ");
      }
      System.out.println();
    }


  }

}

class Passenger {
  Coordinate location;
  Coordinate destination;

  int waited, distance, id;

  public Passenger(Coordinate loc, Coordinate des, int id) {
    // Passenger's location on grid
    location = loc;
    destination = des;
    waited = 0;
    distance = Math.abs(loc.x-des.x) + Math.abs(loc.y-des.y);
    this.id = id;
  }
}

class Driver {
  Coordinate location, destination;
  Solution.DriverState driverState;
  Solution.DriverType driverType;
  Passenger passenger;
  int numTicksUntilDestination, distanceTravelled, distanceWithPassenger, id;

  public Driver(Coordinate loc, Solution.DriverType type, int id)
  {
    // driver's location on grid
    this.location = loc;
    this.driverState = Solution.DriverState.IDLE;
    this.driverType = type;
    this.id = id;
  }

  void pickUp(Passenger p) {
    this.destination = p.destination;
    return;
  }
}

class Coordinate {
  int x, y;
  public Coordinate(int x, int y){
    this.x = x;
    this.y = y;
  }
}


//https://stackoverflow.com/questions/521171/a-java-collection-of-value-pairs-tuples
class Pair {

  public Driver driver;
  public Passenger passenger;

  public Pair(Driver driver, Passenger passenger) {
    this.driver = driver;
    this.passenger = passenger;
  }

  //public Driver getDriver() { return driver; }
  //public Passenger getPassenger() { return passenger; }

  @Override
  public int hashCode() {
    int hashCodeD = driver == null ? 0 : driver.hashCode();
    int hashCodeP = passenger == null ? 0 : passenger.hashCode();
    return hashCodeD ^ hashCodeP;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Pair)) return false;
    Pair pairo = (Pair) o;

    return this.driver.equals(pairo.driver) &&
           this.passenger.equals(pairo.passenger);
  }
}

class Swap {
  //assume d1 -> p1, d2 - > p1 at the beginning
  public Pair p1, p2;
  public Swap(Pair p1, Pair p2){
    this.p1 = p1;
    this.p2 = p2;
  }
}
