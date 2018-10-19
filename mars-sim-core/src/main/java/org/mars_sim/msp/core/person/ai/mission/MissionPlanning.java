/**
 * Mars Simulation Project
 * MissionPlanning.java
 * @version 3.1.0 2018-10-10
 * @author Manny Kung
 */
package org.mars_sim.msp.core.person.ai.mission;

import java.io.Serializable;

import org.mars_sim.msp.core.Simulation;
import org.mars_sim.msp.core.person.RoleType;
import org.mars_sim.msp.core.time.MarsClock;

public class MissionPlanning implements Serializable {

	/** default serial id. */
	private static final long serialVersionUID = 1L;
	/** default logger. */
//	private static Logger logger = Logger.getLogger(MissionPlanning.class.getName());
//
//	private static String sourceName = logger.getName().substring(logger.getName().lastIndexOf(".") + 1,
//			logger.getName().length());
//	
	
	private int requestedSol;
	
	private String reviewedBy;
	private String requestedBy;
	private String requestTimeStamp;
	private String reviewedTimeStamp;

	private RoleType requesterRole;
	private RoleType reviewedRole;
	private PlanType status = PlanType.PENDING;
	private Mission mission;
	
	public MissionPlanning(Mission mission, String requestedBy, RoleType role) {
		this.requestedSol = Simulation.instance().getMasterClock().getMarsClock().getMissionSol();
		this.requestTimeStamp = Simulation.instance().getMasterClock().getMarsClock().getDateTimeStamp();
		this.mission = mission;
		this.requestedBy = requestedBy;
		this.requesterRole = role;
	}
	
	public void setReviewedBy(String name) {
		this.reviewedBy = name;
		// Note : resetting marsClock is needed after loading from a saved sim 
		reviewedTimeStamp = Simulation.instance().getMasterClock().getMarsClock().getDateTimeStamp();
	}
	
	public void setStatus(PlanType status) {
		this.status = status;
	}
	
	public void setReviewedRole(RoleType roleType) {
		this.reviewedRole = roleType;
	}
	
	public Mission getMission() {
		return mission;
	}
	public PlanType getStatus() {
		return status;
	}
	
	public int getMissionSol() {
		return requestedSol;
	}
}