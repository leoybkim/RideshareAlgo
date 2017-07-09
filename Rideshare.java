import java.io.*;
import java.util.*;
import java.lang.*;


class Solution {
  public enum DriverState {
      IDLE, DRIVING_TO_PASSENGER, DRIVING_TO_DESTINATION
  }

  public static void main(String[] args) {
    /*int TICKS = Integer.parseInt(args[0]);
    int PASSENGERS = Integer.parseInt(args[1]);
    int DRIVERS = Integer.parseInt(args[2]);*/

    //Deque passengers = new LinkedList();
    int dim = 10;
    int numDrivers = 10;
    int numPassengers = 10;
    int minDistance = 99999999;
    int distance = 0;

    Driver bestDriver;
    Grid grid = new Grid(dim, numDrivers, numPassengers);

    int passenger_index = 0;
    Deque queue = new LinkedList();

    for (int i = 0; i < numPassengers/2; i++) {
      queue.addFirst(grid.passengers[i]);
      passenger_index++;
    }

    //Main tick loop
    for (int i = 0; i < 30; i++) {
      minDistance = 99999999;
      Grid.printGrid(grid);

      // loop through driver, dequeue from passenger and call calcDistance(passneger, driver)
      Passenger currPassenger = (Passenger)queue.peek();

      for (int j=0; j < numDrivers; j++) {
        Driver currDriver = grid.drivers[j];
        // Check if driver has destination/holding a passenger
        if (currDriver.driverState == DriverState.IDLE && currPassenger != null) {
          // find best driver for passenger waiting
          distance = calcDistance(currPassenger, currDriver);
          if (distance < minDistance && queue.size() > 0) {
            System.out.println("Updating min distance\n");
            minDistance = distance;
            bestDriver = currDriver;
            queue.removeLast();
            currDriver.numTicksUntilDestination = distance;
            currDriver.pickUp = currPassenger;
          }
        }
        //check if driver is going to pick up a passenger
        else if (currDriver.pickUp != null) {
          currDriver.driverState = DriverState.DRIVING_TO_PASSENGER;
          if(currDriver.numTicksUntilDestination > 0){
            currDriver.distanceTravelled++;
            currDriver.numTicksUntilDestination--;
          }
          else {
            //change driver state
            currDriver.occupant = currDriver.pickUp;
            currDriver.pickUp = null;
            currDriver.driverState = DriverState.DRIVING_TO_DESTINATION;
            currDriver.numTicksUntilDestination = currDriver.occupant.distance;
          }
        }
        //check if driver is going to drop off a passenger
        else if (currDriver.occupant != null) {
          currDriver.distanceTravelled++;
          currDriver.distanceWithPassenger++;

          if (currDriver.numTicksUntilDestination > 0) {
            currDriver.numTicksUntilDestination--;
          } else {
            currDriver.location = currDriver.occupant.destination;
            currDriver.destination = null;
            currDriver.occupant = null;
            currDriver.pickUp = null;
            currDriver.driverState = DriverState.IDLE;
          }
        }
      }
    }
  }

  public static int calcDistance(Passenger p, Driver d) {
    int distance = Math.abs(p.location.x - d.location.x) + Math.abs(p.location.y - d.location.y);
    return distance;
  }
}

class Grid {
  int dim;
  Passenger[] passengers;
  Driver[] drivers;

  public Grid(int dim, int numDrivers, int numPassengers) {
    this.dim = dim;
    this.passengers = new Passenger[numPassengers];
    this.drivers = new Driver[numDrivers];
    for (int i = 0; i < drivers.length; i++) {
      Random rand = new Random();
      Coordinate dest = new Coordinate(rand.nextInt(dim),rand.nextInt(dim));
      drivers[i] = new Driver(dest);
    }
    for (int i = 0; i < passengers.length; i++) {
      Random rand = new Random();
      Coordinate loc = new Coordinate(rand.nextInt(dim),rand.nextInt(dim));

      rand = new Random();
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
      matrix[p.location.x][p.location.y] = "P";
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

    System.out.println();
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
  Passenger occupant;
  Passenger pickUp;
  int numTicksUntilDestination;
  int distanceTravelled;
  int distanceWithPassenger;

  public Driver(Coordinate loc)
  {
    // driver's location on grid
    this.location = loc;
    this.driverState = Solution.DriverState.IDLE;
  }
  
  void pickUp(Passenger p) {
    this.destination = p.destination;
    return;
  }

  void move() {
    if (destination != null)
    {

    }
    else
    {

    }
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
