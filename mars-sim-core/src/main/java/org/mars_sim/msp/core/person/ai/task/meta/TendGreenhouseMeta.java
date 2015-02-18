/**
 * Mars Simulation Project
 * TendGreenhouseMeta.java
 * @version 3.07 2014-09-18
 * @author Scott Davis
 */
package org.mars_sim.msp.core.person.ai.task.meta;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.mars_sim.msp.core.Msg;
import org.mars_sim.msp.core.person.LocationSituation;
import org.mars_sim.msp.core.person.Person;
import org.mars_sim.msp.core.person.Robot;
import org.mars_sim.msp.core.person.ai.job.Job;
import org.mars_sim.msp.core.person.ai.job.RobotJob;
import org.mars_sim.msp.core.person.ai.task.Task;
import org.mars_sim.msp.core.person.ai.task.TendGreenhouse;
import org.mars_sim.msp.core.structure.building.Building;

/**
 * Meta task for the Tend Greenhouse task.
 */
public class TendGreenhouseMeta implements MetaTask {
    
    /** Task name */
    private static final String NAME = Msg.getString(
            "Task.description.tendGreenhouse"); //$NON-NLS-1$
    
    /** default logger. */
    private static Logger logger = Logger.getLogger(TendGreenhouseMeta.class.getName());
    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Task constructInstance(Person person) {
        return new TendGreenhouse(person);
    }

    @Override
    public double getProbability(Person person) {
        
        double result = 0D;

        if (person.getLocationSituation() == LocationSituation.IN_SETTLEMENT) {
            try {
                // See if there is an available greenhouse.
                Building farmingBuilding = TendGreenhouse.getAvailableGreenhouse(person);
                if (farmingBuilding != null) {
                    result += 100D;

                    int needyCropsNum = TendGreenhouse.getCropsNeedingTending(person.getSettlement());
                    result += needyCropsNum * 30D;

                    // Crowding modifier.
                    result *= TaskProbabilityUtil.getCrowdingProbabilityModifier(person, farmingBuilding);
                    result *= TaskProbabilityUtil.getRelationshipModifier(person, farmingBuilding);
                }
            }
            catch (Exception e) {
                logger.log(Level.SEVERE,"TendGreenhouse.getProbability(): " + e.getMessage());
            }
        }

        // Effort-driven task modifier.
        result *= person.getPerformanceRating();

        // Job modifier.
        Job job = person.getMind().getJob();
        if (job != null) {
            result *= job.getStartTaskProbabilityModifier(TendGreenhouse.class);
        }

        return result;
    }

	@Override
	public Task constructInstance(Robot robot) {
        return new TendGreenhouse(robot);
	}

	@Override
	public double getProbability(Robot robot) {
	      
        double result = 0D;

        // Job modifier.
        RobotJob robotJob = robot.getBotMind().getRobotJob();
        if (robotJob != null) 
            result = robotJob.getStartTaskProbabilityModifier(TendGreenhouse.class);
        
        //System.out.println(robot.getName() + " : TendGreenhouse  : " + result);
        
        if (result > 0 ) // if task penalty is not zero
        	
	        if (robot.getLocationSituation() == LocationSituation.IN_SETTLEMENT) {
	        	result += 100D;
	            try {
	                // See if there is an available greenhouse.
	                Building farmingBuilding = TendGreenhouse.getAvailableGreenhouse(robot);
	                if (farmingBuilding != null) {
	                    result += 100D;
	
	                    int needyCropsNum = TendGreenhouse.getCropsNeedingTending(robot.getSettlement());
	                    //System.out.println("needyCropsNum is "+needyCropsNum);
	                    result += needyCropsNum * 100D;
	
	                    // Crowding modifier.
	                    //result *= TaskProbabilityUtil.getCrowdingProbabilityModifier(robot, farmingBuilding);
	                    //result *= TaskProbabilityUtil.getRelationshipModifier(robot, farmingBuilding);
	                }
	            }
	            catch (Exception e) {
	                logger.log(Level.SEVERE,"TendGreenhouse.getProbability(): " + e.getMessage());
	            }
	            
	            // Effort-driven task modifier.
	            result *= robot.getPerformanceRating();
	        }

        
        // System.out.println("probability is " + result);
        return result;
	}
}