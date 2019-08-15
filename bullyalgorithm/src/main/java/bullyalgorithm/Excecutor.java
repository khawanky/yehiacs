package bullyalgorithm;

import com.yehia.c3s.models.Status;

import bullyalgorithm.process.ConsumerProcess;
import bullyalgorithm.process.ProducerProcess;

public class Excecutor {

	public static void main(String[] args) {
		ProducerProcess pp1 = new ProducerProcess(1, Status.UP);
		
		ConsumerProcess cp2 = new ConsumerProcess(2);
		ConsumerProcess cp3 = new ConsumerProcess(3);
		ConsumerProcess cp4 = new ConsumerProcess(4);
		ConsumerProcess cp5 = new ConsumerProcess(5);

		pp1.add(cp2);
		pp1.add(cp3);
		pp1.add(cp4);
		pp1.add(cp5);

		pp1.setStatus(Status.UP);
		
		pp1.setStatus(Status.DOWN);

		
	}

}
