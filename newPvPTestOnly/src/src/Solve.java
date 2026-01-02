import org.apache.commons.math4.legacy.analysis.solvers.LaguerreSolver;
import org.apache.commons.numbers.complex.Complex;
import java.util.ArrayList;

public class Solve {
    private final ArrayList<Double> solutions = new ArrayList<>();

    //Make objects of this class in main, then use getSolution() to get the solution
    public Solve(double d, double c, double b, double a) {
        double[] coefficients = {d, c, b, a}; // d + cx + bx^2 + ax^3
        LaguerreSolver solver = new LaguerreSolver();
        Complex[] roots = solver.solveAllComplex(coefficients, 0.0);

        for (Complex root : roots) {
            if (Math.abs(root.getImaginary()) < 1e-10) { // Filter real roots
            solutions.add(root.getReal());
            }
        }
    }

    public Solve(double f, double e, double d, double c, double b, double a) {
        double[] coefficients = {f, e, d, c, b, a}; // f + ex + dx^2 + cx^3 + bx^4 + ax^5
        LaguerreSolver solver = new LaguerreSolver();
        Complex[] roots = solver.solveAllComplex(coefficients, 0.0);

        for (Complex root : roots) {
            if (Math.abs(root.getImaginary()) < 1e-10) { // Filter real roots
                solutions.add(root.getReal());
            }
        }
    }

    public int getSolution() {
        double min = solutions.getFirst();
        for (double solution : solutions) {
            if (solution < min) {
                min = solution;
            }
        }
        return (int)Math.floor(min);
    }
}
