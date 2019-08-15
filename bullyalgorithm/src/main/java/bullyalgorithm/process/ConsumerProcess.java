package bullyalgorithm.process;

import com.yehia.c3s.models.Status;

public class ConsumerProcess implements Observer{
	int id;
	
	public ConsumerProcess(int id) {
		this.id=id;
	}

	public void update(Status status) {
		System.out.println("Get notification with status: "+status.name());
	}
}
