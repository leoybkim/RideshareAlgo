# RideshareAlgo

There are two files, compiling Solution_BFS_Tabu.java will allow you to run the Greedy BFS and Tabu Search. Compiling Rideshare.java allows you to run the ACO.

### Greedy BFS (Queue) and Tabu Search  

```
~$ javac Solution_BFS_Tabu.java
~$ java Solution_BFS_Tabu 
```

Right now only one of the Greedy BFS and Tabu Search is running at a time. The method signatures are

```
runQueueAlgorithm(int numDrivers, int numPassengers, int dim, DriverType behaviour)
runTabuSimulation(int numDrivers, int numPassengers, int dim, DriverType behaviour)
```

Modifying this line will change the output. (Line 37)
```
Tuple t = runQueueAlgorithm(10, 10, 10, DriverType.STATIONARY);
```




### ACO

```
~$ javac Rideshare.java
~$ java Solution 
```

The grid size, number of drivers and passengers, ants and iterations can be modified under Constants.java



Reference:
https://github.com/thiagodnf/aco-framework
