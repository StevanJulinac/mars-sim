/**
 * Mars Simulation Project
 * RecordActivityMeta.java
 * @version 3.1.0 2018-06-09
 * @author Manny Kung
 */
package org.mars_sim.msp.core.person.ai.task.meta;

import java.io.Serializable;

import org.mars_sim.msp.core.Msg;
import org.mars_sim.msp.core.person.FavoriteType;
import org.mars_sim.msp.core.person.Person;
import org.mars_sim.msp.core.person.PhysicalCondition;
import org.mars_sim.msp.core.person.RoleType;
import org.mars_sim.msp.core.person.ai.job.JobType;
import org.mars_sim.msp.core.person.ai.task.RecordActivity;
import org.mars_sim.msp.core.person.ai.task.Task;
import org.mars_sim.msp.core.robot.Robot;

/**
 * Meta task for the RecordActivity task.
 */
public class RecordActivityMeta implements MetaTask, Serializable {

    /** default serial id. */
    private static final long serialVersionUID = 1L;

    /** Task name */
    private static final String NAME = Msg.getString(
            "Task.description.recordActivity"); //$NON-NLS-1$

    private static final String REPORTER = "Reporter";
    
    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Task constructInstance(Person person) {
        return new RecordActivity(person);
    }

    @Override
    public double getProbability(Person person) {

    	double result = 0D;
      
        // Probability affected by the person's stress and fatigue.
        PhysicalCondition condition = person.getPhysicalCondition();
        double fatigue = condition.getFatigue();
        double stress = condition.getStress();
        double hunger = condition.getHunger();
        
        if (fatigue > 1500 || stress > 75 || hunger > 750)
        	return 0;
        
        if (JobType.getJobType(person.getMind().getJob().getName(person.getGender())) == JobType.getJobType(REPORTER)) {      
        	result += 300D;
        }
        
        double pref = person.getPreference().getPreferenceScore(this);
         
      	result += pref * 5D;
        
        if (person.isInside()) {
                    
            if (fatigue < 1200D || stress < 75D || hunger < 750D) {
            	
            	result -= (fatigue/150D + stress/15D + hunger/150);
            }
            
            // TODO: what drives a person go to a particular building ? 
          
    	}
        
        else {
            if (fatigue < 600D && stress< 25D|| hunger < 500D) {
            	result -= (fatigue/100D + stress/10D + hunger/100);
            }
            else
            	result = 0;
        }
	            	
        // Effort-driven task modifier.
        result *= person.getPerformanceRating();

        // Modify if operation is the person's favorite activity.
        if (person.getFavorite().getFavoriteActivity() == FavoriteType.OPERATION) {
            result *= 1.25D;
        }

        if (result > 0) {
            RoleType roleType = person.getRole().getType();

            if (roleType != null && roleType == RoleType.PRESIDENT)
            	result -= 400D;
            
        	else if (roleType == RoleType.MAYOR)
            	result -= 200D;
        			
        	else if (roleType == RoleType.COMMANDER)
                result -= 100D;
        	
        	else if (roleType == RoleType.SUB_COMMANDER)
        		result -= 50D;
            
            else if (roleType == RoleType.CHIEF_OF_AGRICULTURE
            	|| roleType == RoleType.CHIEF_OF_ENGINEERING
            	|| roleType == RoleType.CHIEF_OF_LOGISTICS_N_OPERATIONS
            	|| roleType == RoleType.CHIEF_OF_MISSION_PLANNING
            	|| roleType == RoleType.CHIEF_OF_SAFETY_N_HEALTH
            	|| roleType == RoleType.CHIEF_OF_SCIENCE
            	|| roleType == RoleType.CHIEF_OF_SUPPLY_N_RESOURCES) {
            
            	result -= 10D;
            }
        }
        
        result *= person.getAssociatedSettlement().getGoodsManager().getTourismFactor();
        
        if (result < 0) result = 0;
		        
        //System.out.println("result : " + result);
        return result;
    }

	@Override
	public Task constructInstance(Robot robot) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getProbability(Robot robot) {
		// TODO Auto-generated method stub
		return 0;
	}
}