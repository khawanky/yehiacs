package bullyalgorithm.process;

import java.util.ArrayList;
import java.util.List;

import com.yehia.c3s.models.Status;

public class ProducerProcess implements Subject {
	int id;
	Status status;
	List <Observer> observerList;
	
	public ProducerProcess(int id, Status status) {
		this.id = id;
		this.status = status;
		observerList = new ArrayList<Observer>();
	}

	public void add(Observer process) {
		observerList.add(process);
	}

	public void remove(Observer process) {
		observerList.remove(process);
	}

	public void notifyAllObservers() {
		for (Observer observer : observerList) {
			observer.update(status);
		}
		
	}
	
	public void setStatus(Status status) {
		this.status = status;
		this.notifyAllObservers();
	}
	
}
