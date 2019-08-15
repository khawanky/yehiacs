package bullyalgorithm.process;

public interface Subject {
	public void add(Observer process);
	public void remove(Observer process);
	public void notifyAllObservers();
}
