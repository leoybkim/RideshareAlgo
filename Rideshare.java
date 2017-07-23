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
  private static int numDrivers = 10;
  private static int numPassengers = 10;
  private static Grid grid;

  public enum DriverState {
    IDLE, DRIVING_TO_PASSENGER, DRIVING_TO_DESTINATION
  }
  public enum DriverType {
    STATIONARY, RANDOM, HEATSEEKING
  }

  public static void main(String[] args) {
    grid = new Grid(dim, numDrivers, numPassengers, DriverType.HEATSEEKING);
    runQueueAlgorithm();
  }

  public static void runTabuAlgorithm(){

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

    //Main tick loop
    for (int i = 0; i < 20; i++) {
      Grid.printGrid(grid);

      if (nextPassengerSpawn == i) {
        nextPassengerSpawn = (int)calcNextPassengerSpawn() + i;
        Coordinate loc = new Coordinate(rng.nextInt(dim),rng.nextInt(dim));
        Coordinate dest = new Coordinate(rng.nextInt(dim),rng.nextInt(dim));
        queue.addFirst(new Passenger(loc, dest));
      }

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

      // Second loop - assign best driver for each passenger in the queue
      while (queue.size() > 0) {
        Passenger currPassenger = queue.peekLast();
        int minDistance = 99999999;
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
      drivers[i] = new Driver(dest, driverType);
    }
    for (int i = 0; i < passengers.length; i++) {
      Coordinate loc = new Coordinate(rand.nextInt(dim),rand.nextInt(dim));
      Coordinate dest = new Coordinate(rand.nextInt(dim),rand.nextInt(dim));
      passengers[i] = new Passenger(loc, dest);
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
    }

    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < 10; j++) {
        System.out.print(matrix[i][j]);
      }
      System.out.println();
    }


    for (Driver d : grid.drivers) {
      System.out.print(d.numTicksUntilDestination + " ");
    }

    //System.out.println();
    //System.out.println("Distance travelled: ");

    for (Driver d : grid.drivers) {
      //System.out.print(d.distanceTravelled + " ");
    }

    //System.out.println();
  }

}

class Passenger {
  Coordinate location;
  Coordinate destination;

  int waited;
  int distance;

  public Passenger(Coordinate loc, Coordinate des) {
    // Passenger's location on grid
    location = loc;
    destination = des;
    waited = 0;
    distance = Math.abs(loc.x-des.x) + Math.abs(loc.y-des.y);
  }
}

class Driver {
  Coordinate location;
  Coordinate destination;
  Solution.DriverState driverState;
  Solution.DriverType driverType;
  Passenger passenger;
  int numTicksUntilDestination;
  int distanceTravelled;
  int distanceWithPassenger; //not needed

  public Driver(Coordinate loc, Solution.DriverType type)
  {
    // driver's location on grid
    this.location = loc;
    this.driverState = Solution.DriverState.IDLE;
    this.driverType = type;
  }

  void pickUp(Passenger p) {
    this.destination = p.destination;
    return;
  }
}

class Coordinate {
  int x;
  int y;

  public Coordinate(int x, int y){
    this.x = x;
    this.y = y;
  }
}
