package org.turnerha.sensornodes;

import java.awt.Point;
import java.util.Random;

import org.turnerha.Model;
import org.turnerha.ModelView;
import org.turnerha.geography.GeoBox;
import org.turnerha.geography.GeoLocation;
import org.turnerha.geography.Projection;
import org.turnerha.policys.collection.DataCollectionPolicy;
import org.turnerha.policys.movement.NodeMovementPolicy;

/**
 * Represents a node where the policy decisions control every aspect of the
 * node. Used for any nodes that can have any combination of policy behaviors.
 * If a node needs to ensure that certain policies are always used in
 * conjunction then a new class should be created. It can either be a complete
 * new class that implements {@link SensorNode}, or can subclass
 * {@link DefaultSensorNode}.
 * 
 * Such an example is the {@link FixedSecureNode}, which requires that a no
 * movement policy and a low sensor error rate policy are always used in
 * conjunction
 * 
 * @author hamiltont
 * 
 */
public class DefaultSensorNode implements SensorNode {

	private GeoLocation mLocation;
	private Random mRandom;

	private DataCollectionPolicy mDataCollectionPolicy;
	private NodeMovementPolicy mMovementPolicy;

	public DefaultSensorNode(GeoLocation loc, DataCollectionPolicy dataPolicy,
			NodeMovementPolicy movementPolicy, Random generator) {
		mLocation = loc;
		mDataCollectionPolicy = dataPolicy;
		mRandom = generator;
		mMovementPolicy = movementPolicy;
	}

	/**
	 * Gives the smartphone a chance to perform some actions e.g. moving, data
	 * collection, data reporting, etc
	 */
	public void update() {

		if (mMovementPolicy.shouldMove(this)) {
			double xChange = mRandom.nextDouble() / 10d;
			double yChange = mRandom.nextDouble() / 10d;
			boolean xRight = mRandom.nextBoolean();
			boolean yUp = mRandom.nextBoolean();

			if (xRight)
				mLocation.lon += xChange;
			else
				mLocation.lon -= xChange;

			if (yUp)
				mLocation.lat += yChange;
			else
				mLocation.lat -= yChange;

			// Check that we are still in the correct geospatial area and
			// Correct if we are not
			// For now we just undo the move. Eventually the movement policy
			// should tell us what to do
			if (Model.getInstance().getKml().contains(mLocation) == false) {
				if (xRight)
					mLocation.lon -= xChange;
				else
					mLocation.lon += xChange;

				if (yUp)
					mLocation.lat -= yChange;
				else
					mLocation.lat += yChange;
			}

		}

		// Move complete. Should we collect data?
		// TODO eventually we should integrate the reporting policy
		if (mDataCollectionPolicy.shouldCollectData(this)) {
			int rgb = Model.getInstance().getRealEnvironment().getValueAt(
					mLocation);
			Model.getInstance().getServer().addReading(rgb, mLocation);
		}

	}

	public GeoLocation getLocation() {
		return mLocation;
	}

	public Point getPointLocation() {
		return ModelView.getInstance().getProjection().getPointAt(mLocation);
	}

	public String toString() {
		StringBuilder builder = new StringBuilder("[");
		// TODO complete this
		return super.toString();
	}

	@Override
	public NodeMovementPolicy getMovementPolicy() {
		return mMovementPolicy;
	}

	@Override
	public boolean hasNetworkConnectivity() {
		return true;
	}

	@Override
	public Point getPointUsing(Projection p) {
		return p.getPointAt(mLocation);
	}

	@Override
	public GeoBox getLocationBoundingBox() {
		return new GeoBox(mLocation, mLocation);
	}
}
