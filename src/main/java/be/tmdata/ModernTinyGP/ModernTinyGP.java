package be.tmdata.ModernTinyGP;

/*
 * Program: ModernTinyGP.java
 *
 * Author:  Riccardo Poli  (email: rpoli@essex.ac.uk)
 *
 */

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.io.*;

public class ModernTinyGP {
  double[] fitness;
  char[][] population;
  static Random rd = new Random();
  static final int ADD = 110;
  static final int SUB = 111;
  static final int MUL = 112;
  static final int DIV = 113;
  static final int FSET_START = ADD;
  static final int FSET_END = DIV;
  static double[] x = new double[FSET_START];
  static double minRandom;
  static double  maxRandom;
  static char[] program;
  static int PROGRAM_COUNTER;
  static int varNumber;
  static int fitnessCases;
  static int randomNumber;
  static double fBestPopulation = 0.0;
  static double fAveragePopulation = 0.0;
  static long seed;
  static double averageLength;
  static final int MAX_LEN = 10000;
  static final int POPULATION_SIZE = 100000;
  static final int DEPTH = 5;
  static final int GENERATIONS = 100;
  static final int TSIZE = 2;

  public static final double PMUT_PER_NODE = 0.05;
  public static final double CROSSOVER_PROBABILITY = 0.9;
  static double[][] targets;

  double run() { /* Interpreter */
    char primitive = program[PROGRAM_COUNTER++];

    if (primitive < FSET_START) {
      return (x[primitive]);
    }

    switch (primitive) {
      case ADD:
        return (run() + run());
      case SUB:
        return (run() - run());
      case MUL:
        return (run() * run());
      case DIV: {
        double num = run();
        double den = run();
        if (Math.abs(den) <= 0.001) {
          return (num);
        }
        else {
          return (num / den);
        }
      }
    }
    return (0.0); // should never get here
  }

  int traverse(char[] buffer, int bufferCount) {
    if (buffer[bufferCount] < FSET_START) {
      return (++bufferCount);
    }

    switch (buffer[bufferCount]) {
      case ADD, SUB, MUL, DIV -> {
        return (traverse(buffer, traverse(buffer, ++bufferCount)));
      }
    }

    return (0); // should never get here
  }

  void setupFitness(String fileName) {
    try {
      String[] lines = Files.readAllLines(Path.of(fileName)).toArray(String[]::new);

      // Parse the parameters in the first line
      StringTokenizer tokens = new StringTokenizer(lines[0]);
      varNumber = Integer.parseInt(tokens.nextToken().trim());
      randomNumber = Integer.parseInt(tokens.nextToken().trim());
      minRandom = Double.parseDouble(tokens.nextToken().trim());
      maxRandom = Double.parseDouble(tokens.nextToken().trim());
      fitnessCases = Integer.parseInt(tokens.nextToken().trim());

      targets = new double[fitnessCases][varNumber + 1];

      if (varNumber + randomNumber >= FSET_START) {
        System.out.println("too many variables and constants");
      }

      // Parse the rest of the lines that makes up the data set
      for (int i = 1; i < fitnessCases; i++) {
        tokens = new StringTokenizer(lines[i]);
        for (int j = 0; j <= varNumber; j++) {
          targets[i][j] = Double.parseDouble(tokens.nextToken().trim());
        }
      }
    } catch (FileNotFoundException e) {
      System.out.println(e.getMessage());
      System.out.println("ERROR: Please provide a data file");
      System.exit(0);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      System.out.println("ERROR: Incorrect data format");
      System.exit(0);
    }
  }


  double calculateFitness(char[] Program) {
    int i;
    double result;
    double fit = 0.0;

    traverse(Program, 0);
    for (i = 0; i < fitnessCases; i++) {
      if (varNumber >= 0) System.arraycopy(targets[i], 0, x, 0, varNumber);
      program = Program;
      PROGRAM_COUNTER = 0;
      result = run();
      fit += Math.abs(result - targets[i][varNumber]);
    }
    return (-fit);
  }

  int grow(char[] buffer, int pos, int max, int depth) {
    char prim = (char) rd.nextInt(2);
    int one_child;

    if (pos >= max) {
      return (-1);
    }

    if (pos == 0) {
      prim = 1;
    }

    if (prim == 0 || depth == 0) {
      prim = (char) rd.nextInt(varNumber + randomNumber);
      buffer[pos] = prim;
      return (pos + 1);
    } else {
      prim = (char) (rd.nextInt(FSET_END - FSET_START + 1) + FSET_START);
      switch (prim) {
        case ADD:
        case SUB:
        case MUL:
        case DIV:
          buffer[pos] = prim;
          one_child = grow(buffer, pos + 1, max, depth - 1);
          if (one_child < 0) {
            return (-1);
          }
          return (grow(buffer, one_child, max, depth - 1));
      }
    }
    return (0); // should never get here
  }

  int printIndividual(char[] buffer, int bufferCounter) {
    int a1 = 0;
    int a2;

    if (buffer[bufferCounter] < FSET_START) {
      if (buffer[bufferCounter] < varNumber)
        System.out.print("X" + (buffer[bufferCounter] + 1) + " ");
      else
        System.out.print(x[buffer[bufferCounter]]);
      return (++bufferCounter);
    }
    switch (buffer[bufferCounter]) {
      case ADD:
        System.out.print("(");
        a1 = printIndividual(buffer, ++bufferCounter);
        System.out.print(" + ");
        break;
      case SUB:
        System.out.print("(");
        a1 = printIndividual(buffer, ++bufferCounter);
        System.out.print(" - ");
        break;
      case MUL:
        System.out.print("(");
        a1 = printIndividual(buffer, ++bufferCounter);
        System.out.print(" * ");
        break;
      case DIV:
        System.out.print("(");
        a1 = printIndividual(buffer, ++bufferCounter);
        System.out.print(" / ");
        break;
    }
    a2 = printIndividual(buffer, a1);
    System.out.print(")");
    return (a2);
  }


  static char[] buffer = new char[MAX_LEN];

  char[] createRandomIndividual(int depth) {
    char[] individual;
    int len;

    len = grow(buffer, 0, MAX_LEN, depth);

    while (len < 0)
      len = grow(buffer, 0, MAX_LEN, depth);

    individual = new char[len];

    System.arraycopy(buffer, 0, individual, 0, len);
    return (individual);
  }

  char[][] createRandomPopulation(double[] fitness) {
    char[][] population = new char[POPULATION_SIZE][];

    for (int i = 0; i < POPULATION_SIZE; i++) {
      population[i] = createRandomIndividual(DEPTH);
      fitness[i] = calculateFitness(population[i]);
    }
    return (population);
  }


  void calculateAndPrintStats(double[] fitness, char[][] pop, int gen) {
    int i, best = rd.nextInt(POPULATION_SIZE);
    int nodeCount = 0;
    fBestPopulation = fitness[best];
    fAveragePopulation = 0.0;

    for (i = 0; i < POPULATION_SIZE; i++) {
      nodeCount += traverse(pop[i], 0);
      fAveragePopulation += fitness[i];
      if (fitness[i] > fBestPopulation) {
        best = i;
        fBestPopulation = fitness[i];
      }
    }

    averageLength = (double) nodeCount / POPULATION_SIZE;
    fAveragePopulation = fAveragePopulation / POPULATION_SIZE;

    System.out.print("Generation=" + gen +
        ", Avg Fitness=" + (-fAveragePopulation) +
        ", Best Fitness=" + (-fBestPopulation) +
        ", Avg Size=" + averageLength +
        "\nBest Individual: ");

    printIndividual(pop[best], 0);

    System.out.print("\n\n");
    System.out.flush();
  }

  int runTournament(double[] fitness, int tsize) {
    int best = rd.nextInt(POPULATION_SIZE), i, competitor;
    double fitnessBest = -1.0e34;

    for (i = 0; i < tsize; i++) {
      competitor = rd.nextInt(POPULATION_SIZE);
      if (fitness[competitor] > fitnessBest) {
        fitnessBest = fitness[competitor];
        best = competitor;
      }
    }
    return (best);
  }

  int runNegativeTournament(double[] fitness, int tsize) {
    int competitor;
    double fitnessWorst = 1e34;
    int worst = rd.nextInt(POPULATION_SIZE);

    for (int i = 0; i < tsize; i++) {
      competitor = rd.nextInt(POPULATION_SIZE);
      if (fitness[competitor] < fitnessWorst) {
        fitnessWorst = fitness[competitor];
        worst = competitor;
      }
    }
    return (worst);
  }

  char[] crossover(char[] parent1, char[] parent2) {
    int xo1start;
    int xo1end;
    int xo2start;
    int xo2end;
    char[] offspring;
    int len1 = traverse(parent1, 0);
    int len2 = traverse(parent2, 0);
    int lenOffspring;

    xo1start = rd.nextInt(len1);
    xo1end = traverse(parent1, xo1start);

    xo2start = rd.nextInt(len2);
    xo2end = traverse(parent2, xo2start);

    lenOffspring = xo1start + (xo2end - xo2start) + (len1 - xo1end);

    offspring = new char[lenOffspring];

    System.arraycopy(parent1, 0, offspring, 0, xo1start);
    System.arraycopy(parent2, xo2start, offspring,
        xo1start, (xo2end - xo2start));
    System.arraycopy(parent1, xo1end, offspring,
        xo1start + (xo2end - xo2start), (len1 - xo1end));

    return (offspring);
  }

  char[] mutation(char[] parent, double pmut) {
    int len = traverse(parent, 0);
    int mutationSite;
    char[] parentCopy = new char[len];

    System.arraycopy(parent, 0, parentCopy, 0, len);
    for (int i = 0; i < len; i++) {
      if (rd.nextDouble() < pmut) {
        mutationSite = i;
        if (parentCopy[mutationSite] < FSET_START)
          parentCopy[mutationSite] = (char) rd.nextInt(varNumber);
        else
          switch (parentCopy[mutationSite]) {
            case ADD:
            case SUB:
            case MUL:
            case DIV:
              parentCopy[mutationSite] =
                  (char) (rd.nextInt(FSET_END - FSET_START + 1)
                      + FSET_START);
          }
      }
    }
    return (parentCopy);
  }

  void printParameters() {
    System.out.print("-- TINY GP (Java version) --\n");
    System.out.print("SEED=" + seed + "\nMAX_LEN=" + MAX_LEN +
        "\nPOPULATION_SIZE=" + POPULATION_SIZE + "\nDEPTH=" + DEPTH +
        "\nCROSSOVER_PROBABILITY=" + CROSSOVER_PROBABILITY +
        "\nPMUT_PER_NODE=" + PMUT_PER_NODE +
        "\nMIN_RANDOM=" + minRandom +
        "\nMAX_RANDOM=" + maxRandom +
        "\nGENERATIONS=" + GENERATIONS +
        "\nTSIZE=" + TSIZE +
        "\n----------------------------------\n");
  }

  public ModernTinyGP(String fileName, long randomizationSeed) {
    fitness = new double[POPULATION_SIZE];
    seed = randomizationSeed;

    if (seed >= 0) {
      rd.setSeed(seed);
    }

    setupFitness(fileName);

    for (int i = 0; i < FSET_START; i++) {
      x[i] = (maxRandom - minRandom) * rd.nextDouble() + minRandom;
    }

    population = createRandomPopulation(fitness);
  }

  void evolve() {
    int generation;
    int individuals;
    int offspring;
    int parent1;
    int parent2;
    int parent;
    double newFitness;
    char[] newIndividual;

    printParameters();

    calculateAndPrintStats(fitness, population, 0);

    for (generation = 1; generation < GENERATIONS; generation++) {
      if (fBestPopulation > -1e-5) {
        System.out.print("PROBLEM SOLVED\n");
        System.exit(0);
      }

      for (individuals = 0; individuals < POPULATION_SIZE; individuals++) {
        if (rd.nextDouble() < CROSSOVER_PROBABILITY) {
          parent1 = runTournament(fitness, TSIZE);
          parent2 = runTournament(fitness, TSIZE);
          newIndividual = crossover(population[parent1], population[parent2]);
        } else {
          parent = runTournament(fitness, TSIZE);
          newIndividual = mutation(population[parent], PMUT_PER_NODE);
        }
        newFitness = calculateFitness(newIndividual);
        offspring = runNegativeTournament(fitness, TSIZE);
        population[offspring] = newIndividual;
        fitness[offspring] = newFitness;
      }

      calculateAndPrintStats(fitness, population, generation);
    }

    System.out.print("PROBLEM ∗NOT∗ SOLVED\n");
    System.exit(1);
  }

  public static void main(String[] args) {
    String fileName = "problem.dat";
    long randomizationSeed = -1;

    if (args.length == 2) {
      randomizationSeed = Integer.parseInt(args[0]);
      fileName = args[1];
    }

    if (args.length == 1) {
      fileName = args[0];
    }

    var gp = new ModernTinyGP(fileName, randomizationSeed);
    gp.evolve();
  }
}
