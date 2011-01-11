package org.turnerha;

import java.util.concurrent.atomic.AtomicInteger;

import org.turnerha.environment.MetricCalculator;

public class ModelController {
	private Slice[][] mFinishedSlices;
	private ModelProxy proxy;
	public AtomicInteger sleepTime = new AtomicInteger(100);
	private MetricCalculator mMetricCalc;

	public ModelController(ModelProxy proxy, int rows, int columns,
			MetricCalculator mc) {
		mFinishedSlices = new Slice[rows][columns];
		this.proxy = proxy;

		mMetricCalc = mc;
	}

	public synchronized void completeSlice(Slice slice)
			throws InterruptedException {

		mFinishedSlices[slice.getRow()][slice.getColumn()] = slice;

		if (allSlicesAreReady()) {

			// Print out some metrics
			System.out.println("Acc, Covg: " + mMetricCalc.getAccuracy() + ", "
					+ mMetricCalc.getCoverage());

			// Slow down the simulation a bit
			Thread.sleep(sleepTime.get());

			// Swap front and back model pointers
			proxy.modelLock.lock();
			mFinishedSlices = proxy.swapModel(mFinishedSlices);
			proxy.modelLock.unlock();

			// Toss out old model memory
			for (int row : Util.range(mFinishedSlices.length))
				for (int col : Util.range(mFinishedSlices[0].length))
					mFinishedSlices[row][col] = null;

			// Let all of the other slices resume working
			notifyAll();
		} else
			// Pause this slice until the others are ready
			wait();

	}

	private boolean allSlicesAreReady() {
		for (int row : Util.range(mFinishedSlices.length))
			for (int col : Util.range(mFinishedSlices[0].length))
				if (mFinishedSlices[row][col] == null)
					return false;

		return true;
	}

}
