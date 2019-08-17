package com.yehia.c3s.bullyalgorithm.models;

public class ProcessStatus {
	private boolean isCoordinatorExists=false;
	private boolean isTheWinningElector=false;
	private boolean hasSentElection=false;
	
	public ProcessStatus(boolean isCoordinatorExists, boolean isTheWinningElector) {
		super();
		this.isCoordinatorExists = isCoordinatorExists;
		this.isTheWinningElector = isTheWinningElector;
	}
	
	public boolean isCoordinatorExists() {
		return isCoordinatorExists;
	}
	public void setCoordinatorExists(boolean isCoordinatorExists) {
		this.isCoordinatorExists = isCoordinatorExists;
	}
	public boolean isTheWinningElector() {
		return isTheWinningElector;
	}
	public void setTheWinningElector(boolean isTheWinningElector) {
		this.isTheWinningElector = isTheWinningElector;
	}

	public boolean isHasSentElection() {
		return hasSentElection;
	}

	public void setHasSentElection(boolean hasSentElection) {
		this.hasSentElection = hasSentElection;
	}
}
