package bearmaps.hw4;

import bearmaps.proj2ab.DoubleMapPQ;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.Collections;

public class AStarSolver<Vertex> implements ShortestPathsSolver<Vertex> {

    private HashMap<Vertex, Double> distTo;
    private HashMap<Vertex, WeightedEdge> edgeTo;
    private List<Vertex> solution;
    private DoubleMapPQ<Vertex> spQ;
    private AStarGraph<Vertex> G;
    private Vertex source;
    private Vertex goal;
    private double conTime;
    private int numStates;
    private double sum;
    private boolean solved;
    private boolean unsolved;
    private boolean overtime;

    public AStarSolver(AStarGraph<Vertex> input, Vertex start, Vertex end, double timeout) {
        Stopwatch sw2 = new Stopwatch();
        source = start;
        goal = end;
        G = input;
        solution = new ArrayList<>();
        distTo = new HashMap<>();
        edgeTo = new HashMap<>();
        spQ = new DoubleMapPQ<>();

        distTo.put(source, 0.0);
        spQ.add(source, distTo.get(source));


        Stopwatch sw = new Stopwatch();
        while (spQ.size() > 0 && !spQ.getSmallest().equals(goal) && sw.elapsedTime() < timeout) {
            //System.out.println("Elapsed time so far: "+sw.elapsedTime());

            Vertex temp = spQ.removeSmallest();
            //System.out.println("Smallest "+temp);
            //solution.add(temp);
            numStates += 1;
            for (WeightedEdge e : G.neighbors(temp)) {
                //System.out.println("meow");
                if (e.to() != temp) {
                    relax(e);
                }
            }

        }
        //solution.add(goal);
        //System.out.println("Solution :");
        printSol();
        //System.out.println("Goal was reached : "+spQ.getSmallest().equals(goal));
        if (spQ.size() == 0) {
            unsolved = true;
        } else if (spQ.getSmallest().equals(goal)) {
            solved = true;
        } else {
            overtime = true;
        }

        conTime = sw2.elapsedTime();

    }

    public SolverOutcome outcome() {
        SolverOutcome solOut = null;

        if (solved) {
            solOut = SolverOutcome.SOLVED;
        }
        if (unsolved) {
            solOut = SolverOutcome.UNSOLVABLE;
        }
        if (overtime) {
            solOut = SolverOutcome.TIMEOUT;
        }
        return solOut;
    }

    public List<Vertex> solution() {
        return solution;
    }

    public double solutionWeight() {
        if (G == null) {
            return 0;
        }

        return sum;
    }

    public int numStatesExplored() {
        if (G == null) {
            return 0;
        }
        return numStates;
    }

    public double explorationTime() {
        if (G == null) {
            return 0;
        }
        return conTime;
    }

    private void relax(WeightedEdge e) {

        /*
        System.out.println();
        System.out.print("("+e.from()+")");
        System.out.print(" ----> (" +e.to()+") ");
        System.out.println();
        */
        //System.out.println("----------");


        if (G == null) {
            return;
        }

        Vertex p = (Vertex) e.from();
        Vertex q = (Vertex) e.to();
        double w = e.weight();

        //System.out.println(distTo.get(q));
        //System.out.println(distTo.get(p));


        if (!distTo.containsKey(q)) {
            distTo.put(q, distTo.get(p) + w);

            edgeTo.put(q, e);

            if (spQ.contains(q)) {
                spQ.changePriority(q, distTo.get(q) + G.estimatedDistanceToGoal(q, goal));
            } else {
                spQ.add(q, distTo.get(q) + G.estimatedDistanceToGoal(q, goal));
            }
        } else if (distTo.get(q) > distTo.get(p) + w) {
            distTo.put(q, distTo.get(p) + w);

            edgeTo.put(q, e);

            if (spQ.contains(q)) {
                spQ.changePriority(q, distTo.get(q) + G.estimatedDistanceToGoal(q, goal));
            } else {
                spQ.add(q, distTo.get(q) + G.estimatedDistanceToGoal(q, goal));
            }
        }
        //System.out.println("Smallest: "+spQ.getSmallest());

    }

    public Iterable<WeightedEdge> pathTo(Vertex v) {
        Stack<WeightedEdge> path = new Stack<WeightedEdge>();
        //List<WeightedEdge> path = new ArrayList<WeightedEdge>();
        for (WeightedEdge e = edgeTo.get(v); e != null; e = edgeTo.get(e.from())) {
            path.push(e);
        }
        return path;
    }

    private void printSol() {
        for (WeightedEdge e : pathTo(goal)) {
            //System.out.println(e.from()+" ----> "+e.to());
            sum = sum + e.weight();
            solution.add((Vertex) e.to());
        }
        solution.add(source);
        Collections.reverse(solution);

    }
}
