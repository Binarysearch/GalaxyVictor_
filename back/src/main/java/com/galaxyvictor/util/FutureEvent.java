package com.galaxyvictor.util;


public abstract class FutureEvent{

	public abstract double getEndTime();

	public double getRemainingTime() {
		return Math.max(0, getEndTime() - System.currentTimeMillis());
	}

	public abstract void finish();

	public abstract long getId();

}