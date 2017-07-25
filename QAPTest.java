
import problem.Problem;
import problem.QuadraticAssignmentProblem;
import sys.ExecutionStats;
import aco.ACO;
import aco.AntSystem;

public class QAPTest {

	private static int ants = 10;

	private static int interations = 10;

	public static void main(String[] args) {
		Problem p = new QuadraticAssignmentProblem("in/test_8_10.qap");
		ACO aco = new AntSystem(p, ants, interations);

		ExecutionStats es = ExecutionStats.execute(aco, p);
		es.printStats();
	}

	public void run(String filename) {
		Problem p = new QuadraticAssignmentProblem(filename);

		ACO aco = new AntSystem(p, ants, interations);

		ExecutionStats es = ExecutionStats.execute(aco, p);
		es.printStats();
	}
}
