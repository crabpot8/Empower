package org.turnerha.metrics;

import java.util.ArrayList;
import java.util.List;

/**
 * Temporary class - the {@link MetricCalculator} is currently filled with
 * complicated metric calculations, so I don't want to mess it up by modifying
 * it. Therefore, I am building this class from scratch by reading the
 * {@link MetricCalculator} code, and when this class is done I can compare the
 * results it's generating with the results generated by the
 * {@link MetricCalculator} to ensure that they are similar
 * 
 * 
 * 
 * @author hamiltont
 * 
 */
public class MetricRunner {

	private List<Metric> mMetrics = new ArrayList<Metric>();

	public MetricRunner() {
	}

	public void add(Metric m) {
		mMetrics.add(m);
	}

	public Metric get(String name) {
		for (Metric metric : mMetrics)
			if (metric.getName().equals(name))
				return metric;
		System.err.println("Metric with name '" + name
				+ "' not found. Returning null");
		return null;
	}
	
	
	

}
