package com.yehia.c3s.models;

public class ProcessStatus {
	private boolean isCoordinatorExists=false;
	private boolean isTheWinningElector=false;
	
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
	
	
	
}
