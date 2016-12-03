import java.util.*;
import java.io.*;
import java.math.*;


/**
 * Grab Snaffles and try to throw them through the opponent's goal!
 * Move towards a Snaffle and use your team id to determine where you need to throw it.
 **/
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int myTeamId = in.nextInt(); // if 0 you need to score on the right of the map, if 1 you need to score on the left
        int magic = 0;
        Point[] goals = new Point[2];

        goals[0] = new Point(16000, 3750); //Right goal center
        goals[1] = new Point(0, 3750); //Left goal center
        
        Point[] topGoalPoints = new Point[2];
        topGoalPoints[0] = new Point(16000,1900);
        topGoalPoints[1] = new Point(0,1900);
        
        Point[] bottomGoalPoints = new Point[2];
        bottomGoalPoints[0] = new Point(16000,5600);
        bottomGoalPoints[1] = new Point(0,5600);
        
        Movement[] movements = new Movement[4];
        
        HashMap<Integer, Entity> bludgersMap = new HashMap<Integer, Entity>();
        HashMap<Integer, Entity> snafflesMap = new HashMap<Integer, Entity>();
        HashMap<Integer, Entity> wizardsMap = new HashMap<Integer, Entity>();
        
        // game loop
        while (true) {
            
            //List<Entity> wizards = new ArrayList<Entity>();
            List<Entity> opponentWizards = new ArrayList<Entity>();
            //List<Entity> snaffles = new ArrayList<Entity>();
            List<Entity> allEntities = new ArrayList<Entity>();
            
            
            int entities = in.nextInt(); // number of entities still in game
            
            for(Entity entity : snafflesMap.values()) {
            	Snaffle snaffle = (Snaffle) entity;
            	snaffle.setInGoal(true);
            }
            
            
            for (int i = 0; i < entities; i++) {
                int entityId = in.nextInt(); // entity identifier
                String entityType = in.next(); // "WIZARD", "OPPONENT_WIZARD" or "SNAFFLE" (or "BLUDGER" after first league)
                int x = in.nextInt(); // position
                int y = in.nextInt(); // position
                int vx = in.nextInt(); // velocity
                int vy = in.nextInt(); // velocity
                int state = in.nextInt(); // 1 if the wizard is holding a Snaffle, 0 otherwise
                
                Entity newEntity;
                
                switch (entityType) {
				case "WIZARD":
					
					Entity entity = wizardsMap.get(entityId);
					if(entity==null) {
						entity = new Entity(entityId,x,y,vx,vy,state, 0.75, 400, 0, 1);
						wizardsMap.put(entityId, entity);
					} else {
						entity.update(x,y,vx,vy,state);
					}
					
					break;
				case "OPPONENT_WIZARD":
	                newEntity = new Entity(entityId,x,y,vx,vy,state, 0.75, 400, 0, 1);
					opponentWizards.add(newEntity);
					break;
				case "SNAFFLE":
	                //newEntity = new Snaffle(entityId,x,y,vx,vy,state, 0.75, 150, 1, 0.5);
					//snaffles.add(newEntity);
					
					//System.err.println("ID OF SNAFFLE " + entityId);
					Snaffle snaffle = (Snaffle) snafflesMap.get(entityId);
					if(snaffle==null) {
						snaffle = new Snaffle(entityId,x,y,vx,vy,state, 0.75, 150, 1, 0.5);
						snafflesMap.put(entityId, snaffle);
						//newEntity.printAll();
					} else {
						snaffle.update(x,y,vx,vy);
						snaffle.setInGoal(false);
						//bludger.printAll();
					}
					
					break;
				case "BLUDGER":
					
					Bludger bludger = (Bludger) bludgersMap.get(entityId);
					if(bludger==null) {
						bludger = new Bludger(entityId,x,y,vx,vy,state, 0.9, 200, 2, 8);
						bludger.setLastEntityId(-1);
						bludgersMap.put(entityId, bludger);
						//newEntity.printAll();
					} else {
						bludger.update(x,y,vx,vy);
						//bludger.printAll();
					}
	                
					
	                
					break;
				default:
					break;
				}
                
            }
            

            
            List<Entity> snaffles = new ArrayList<Entity>(snafflesMap.values());
            List<Entity> wizards = new ArrayList<Entity>(wizardsMap.values());
            List<Entity> bludgers = new ArrayList<Entity>(bludgersMap.values());
            

            //Check snaffle which are in goal
            for (Iterator<Entity> iterator = snaffles.iterator(); iterator.hasNext();) {
	             Snaffle snaffle = (Snaffle) iterator.next();
	             
	          	if(snaffle.isIntoGoal()) {
	          		snafflesMap.remove(snaffle.getId());
	          		iterator.remove();

	         	}
	         }
            
            
            //Reset movements
            for(int i=0; i<4; i++) {
            	movements[i] = null;
            }
            

           
            //-------------------------------------------------Algorithm to move or launch spells with 2 wizards-------------------------------

            
            int chasestSnaffleId = -1;
            
            
            Entity[] snafflesToCatch = findNearestSnaffles(wizards, snaffles);
            
            for(Entity wizard : wizards) {
            	
            	
                if(wizard.getState() == 0) {
                    Entity nearestSnaffle = snafflesToCatch[wizard.getId()%2];
                    
                    Point wizardNextPosition = wizard.computeNextPositionInNbTour(1);
                    Point snaffleNextPosition = nearestSnaffle.computeNextPositionInNbTour(1);
                    
                    List<Entity> snafflesGoingToGoal = searchSnafflesGoingToGoal(snaffles, topGoalPoints[1-myTeamId], bottomGoalPoints[1-myTeamId]);
                    boolean spellLaunched = false;
                    
                    if(snafflesGoingToGoal.size()>0 && magic>=10) {
                    	Entity snaffleToStop = snafflesGoingToGoal.get(0);
                    	petrifcus(snaffleToStop);
                    	snaffleToStop.stop();
                    	spellLaunched = true;
                    }
                    

                    
                    if(magic >= 20 && !spellLaunched) {
                        /*if(wizard.isUsefulToAccio(nearestSnaffle, goals[myTeamId])){
                        	actio(nearestSnaffle);
                        	magic-=20;
                        	spellLaunched = true;
                        } else*/ if(lineLineIntersect(wizardNextPosition, snaffleNextPosition, topGoalPoints[myTeamId], bottomGoalPoints[myTeamId] )){
                        	
                        	boolean ennemyInTrajectory = false;
                        	
                        	for(Entity opponentWizard : opponentWizards) {
                        		Point opponentNextPosition = opponentWizard.computeNextPositionInNbTour(1);
                        		if(lineCircleIntersect(wizardNextPosition, snaffleNextPosition, opponentNextPosition , 400)) {
                        			ennemyInTrajectory = true;
                        		}
                        	}
                        	if(!ennemyInTrajectory) {
    	                    	flipendo(nearestSnaffle);
    	                    	
    	                    	// Add flipendo effect to the snaffle
    	                    	Snaffle snaffle = (Snaffle) nearestSnaffle;
    	                    	snaffle.flipendo(wizard);
    	                    	
    	                    	System.err.println("Wizard next pos:");
    	                    	wizardNextPosition.print();
    	                    	System.err.println("Snaffle next pos:");
    	                    	snaffleNextPosition.print();
    	                    	
    	                    	
    	                    	magic-=20;
    	                    	spellLaunched = true;
                        	}
                        }
                    }
                    
                    if(!spellLaunched) {
                        moveTo(nearestSnaffle, 150);
                        movements[wizard.getId()]= new Movement(wizard,(Point) nearestSnaffle, 150);
                    }
                    
                    if(snaffles.size()>1) {
                    	chasestSnaffleId = nearestSnaffle.getId();
                    }
                } else {
                	//wizard.setState(0);
                    throwTo(goals[myTeamId], 500);
                    Snaffle snaffle = (Snaffle) wizard.getSnaffleCarried();
                    if(snaffle!=null) {
                        System.err.println("SNAFFLE : " + snaffle.getId());
                        snaffle.throwToPosition(goals[myTeamId], 500);
                    }
                }
                

                
            }
            magic++;
            
            
            //----------------------------------------Predict movement of next turn----------------------------------------------
            //Move all wizards
            //FIXME we move only our wizards here
        	for(int i=0; i<2; i++) {
        		if(movements[i]!=null) {
        			wizards.get(i).addMovement(movements[i]);
        		}
        	}
            
        	long startTime = System.currentTimeMillis();
        	

            List<Entity> opponentWizardsCopy = null;
            List<Entity> wizardsCopy = null; 
            List<Entity> bludgersCopy = null;
            List<Entity> snafflesCopy = null;
			try {
				opponentWizardsCopy = cloneList(opponentWizards);
	            wizardsCopy = cloneList(wizards);
	            bludgersCopy = cloneList(bludgers);
	            snafflesCopy = cloneList(snaffles);
			} catch (CloneNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            
        	Simulation simulation = new Simulation(opponentWizardsCopy, wizardsCopy, bludgersCopy, snafflesCopy, 1, myTeamId);
        	simulation.run();



            long endTime = System.currentTimeMillis();
            System.err.println("Total execution time: " + (endTime-startTime) + "ms"); 
             
             System.err.println("------------------AFTER-----------------");
             printList(simulation.getAllEntities());
             
             //---------------------------------------------------
            
        }
    }
    

    
    public static List<Entity> cloneList(List<Entity> list) throws CloneNotSupportedException {
        List<Entity> clone = new ArrayList<Entity>(list.size());
        for (Entity item : list) clone.add((Entity) item.clone());
        return clone;
    }
    
    public static void printList(List<Entity> entities) {
    	for(Entity entity : entities) {
    		entity.print();
    	}
    }
    
    public static void moveTo(Point point, int power) {
        System.out.print("MOVE ");
        System.out.print((int)point.getX());
        System.out.print(" ");
        System.out.print((int)point.getY());
        System.out.print(" ");
        System.out.println(power);
    }
    
    public static void throwTo(Point point, int power) {
        System.out.print("THROW ");
        System.out.print((int)point.getX());
        System.out.print(" ");
        System.out.print((int)point.getY());
        System.out.print(" ");
        System.out.println(power);
    }
    
    public static void actio(Entity entity) {
    	System.out.print("ACCIO ");
        System.out.println(entity.getId());
    }
    
    public static void flipendo(Entity entity) {
    	System.out.print("FLIPENDO ");
        System.out.println(entity.getId());
    }
    
    public static void petrifcus(Entity entity) {
    	System.out.print("PETRIFICUS ");
        System.out.println(entity.getId());
    }
    

    
    /**
     * Check if segment [Point1, Point2] intersect PERPANDICULAR segment [Point3, Point4] (the goal)
     * 
     * WARNING Point3.y has to be inferior to Point4.y
     * 
     * @param point1 the wizard
     * @param point2 the snaffle
     * @param point3 the top goal
     * @param point4 the bottom goal
     * @return
     */
    public static boolean lineLineIntersect(Point point1, Point point2, Point point3, Point point4) {
    	if(point2.isBetweenInX(point1, point3)) {
	    	double a= ((double)(point2.getY()-point1.getY()))/((double)(point2.getX()- point1.getX()));
	    	double b= point1.getY() - a*point1.getX();
	    	
	    	System.err.println("a: " + a);
	    	System.err.println("b: " + b);
	    	
	    	double yTarget = a*point3.getX()+b;
	    	System.err.println("yTarget: " + yTarget);
	    	
	    	if(yTarget>point3.getY() && yTarget<point4.getY()) {
	    		return true;
	    	}
        }
    	return false;
    }

    
    public static boolean lineCircleIntersect(Point A, Point B, Point C, int radius) {
    	
    	double Ax = A.getX();
    	double Ay = A.getY();
    	double Bx = B.getX();
    	double By = B.getY();
    	double Cx = C.getX();
    	double Cy = C.getY();

    	
    	// compute the euclidean distance between A and B
    	double LAB = (double) Math.sqrt( (Bx-Ax)*(Bx-Ax)+(By-Ay)*(By-Ay) );

    	// compute the direction vector D from A to B
    	double Dx = (Bx-Ax)/LAB;
    	double Dy = (By-Ay)/LAB;

    	// Now the line equation is x = Dx*t + Ax, y = Dy*t + Ay with 0 <= t <= 1.

    	// compute the value t of the closest point to the circle center (Cx, Cy)
    	double t = Dx*(Cx-Ax) + Dy*(Cy-Ay); 

    	// This is the projection of C on the line from A to B.

    	// compute the coordinates of the point E on line and closest to C
    	double Ex = t*Dx+Ax;
    	double Ey = t*Dy+Ay;

    	// compute the euclidean distance from E to C
    	double LEC = (float) Math.sqrt( (Ex-Cx)*(Ex-Cx)+(Ey-Cy)*(Ey-Cy) );

    	// test if the line intersects the circle
    	if( LEC < radius )
    	{
    	    /*// compute distance from t to circle intersection point
    	    dt = sqrt( R² - LEC²)

    	    // compute first intersection point
    	    Fx = (t-dt)*Dx + Ax
    	    Fy = (t-dt)*Dy + Ay

    	    // compute second intersection point
    	    Gx = (t+dt)*Dx + Ax
    	    Gy = (t+dt)*Dy + Ay
    	}*/
    		return true;
    	}
    	return false;
    }
    
    public static List<Entity> searchSnafflesGoingToGoal(List<Entity> snaffles, Point topGoalPoint, Point bottomGoalPoint){
    	
    	List<Entity> snafflesGoingToGoal = new ArrayList<Entity>();
    	
    	for(Entity snaffle : snaffles) {
    		
    		if(snaffle.getVx()!=0) {
	    		Point nextPosition = snaffle.computeNextPositionInNbTour(1);
	    		if(nextPosition.isInsideGame()) {
	    			Point positionInNTour = snaffle.computeNextPositionInNbTour(3);
	    			
	    			//If snaffle is in the goal in N tour
		    		if(positionInNTour.getX()==topGoalPoint.getX()) {
		    			snafflesGoingToGoal.add(snaffle);
		    		}
	    		}
    		}
    	}
    	
    	Collections.sort(snafflesGoingToGoal, new Comparator<Entity>() {

			@Override
			public int compare(Entity o1, Entity o2) {
				double velocity1 = Math.abs(o1.getVx());
				double velocity2 = Math.abs( o2.getVx());
				
				
				return Double.compare(velocity2, velocity1);
			}
        });
    	
    	return snafflesGoingToGoal;
    }
    
    public static Entity[] findNearestSnaffles(List<Entity> wizards, List<Entity> snaffles) {
    	
    	if(snaffles.size()>1) {
	    	Entity[] snaffles1 = new Entity[2];
	    	
	    	snaffles1[0] = wizards.get(0).searchNearestEntity(snaffles);
	    	//snaffles1[1] = wizards.get(1).searchNearestSnaffleExcept(snaffles, snaffles1[0].getId());
	    	snaffles1[1] = wizards.get(1).searchNearestEntity(snaffles);
	    	
	    	double totalDistanceSquare1 = wizards.get(0).computeDistanceSquare(snaffles1[0]);
	    	totalDistanceSquare1+= wizards.get(1).computeDistanceSquare(snaffles1[1]);
	    	
	    	Entity[] snaffles2 = new Entity[2];
	    	
	    	snaffles2[1] = wizards.get(1).searchNearestEntity(snaffles);
	    	//snaffles2[0] = wizards.get(0).searchNearestSnaffleExcept(snaffles, snaffles2[1].getId());
	    	snaffles2[0] = wizards.get(0).searchNearestEntity(snaffles);
	    	
	    	double totalDistanceSquare2 = wizards.get(0).computeDistanceSquare(snaffles2[0]);
	    	totalDistanceSquare2+= wizards.get(1).computeDistanceSquare(snaffles2[1]);
	    	
	    	if(totalDistanceSquare1 > totalDistanceSquare2) {
	    		return snaffles2;
	    	} else {
	        	return snaffles1;
	    	}
    	}else {
    		return new Entity[]{snaffles.get(0), snaffles.get(0)};
    	}
    	

    }
    
    
    

}