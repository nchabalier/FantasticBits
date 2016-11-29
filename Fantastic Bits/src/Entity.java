import java.util.List;

class Entity extends Point{
	private int id;
	private int vx;
	private int vy;
	private int state;
	private double friction;
	private int radius;
	private int type; //0 for wizards, 1 for snaffles
	private double mass;
	
	
	public Entity(int id, int x, int y, int vx, int vy) {
		super(x,y);
		this.id = id;
		this.vx = vx;
		this.vy = vy;
	}
	
	public Entity(int id, int x, int y, int vx, int vy, int state) {
		this(id, x, y, vx, vy);
		this.state = state;
	}
	
	public Entity(int id, int x, int y, int vx, int vy, int state, double friction, int radius, int type, double mass) {
		this(id, x, y, vx, vy);
		this.state = state;
		this.friction = friction;
		this.radius = radius;
		this.type = type;
		this.mass = mass;
	}
	

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getVx() {
		return vx;
	}

	public void setVx(int vx) {
		this.vx = vx;
	}

	public int getVy() {
		return vy;
	}

	public void setVy(int vy) {
		this.vy = vy;
	}
	
	public Entity searchNearestSnaffle(List<Entity> snaffles) {
		int minDistance = Integer.MAX_VALUE;
		Entity nearestEntity = null;
		
		for(Entity snaffle : snaffles) {
			int newDistance = computeDistanceSquare(snaffle);
			if(newDistance < minDistance) {
				minDistance = newDistance;
				nearestEntity = snaffle;
			}
		}
		
		return nearestEntity;
	}
	
	/**
	 * Search nearest snaffle except the snaffle with input id
	 * @param snaffles
	 * @param id
	 * @return
	 */
	public Entity searchNearestSnaffleExcept(List<Entity> snaffles, int id) {
		int minDistance = Integer.MAX_VALUE;
		Entity nearestEntity = null;
		
		for(Entity snaffle : snaffles) {
			int newDistance = computeDistanceSquare(snaffle);
			if(snaffle.getId()!=id && newDistance < minDistance) {
				minDistance = newDistance;
				nearestEntity = snaffle;
			}
		}
		
		return nearestEntity;
	}
	
	public int computeDistanceSquare(Entity entity) {
		return (this.getX()-entity.getX())*(this.getX()-entity.getX()) + (this.getY()-entity.getY())*(this.getY()-entity.getY());
	}
	
	
	public boolean isUsefulToAccio(Entity entity, Entity goal) {
		if(this.isBetweenInX(entity, goal) && computeDistanceSquare(entity)>4000000) {
			return true;
		}
		return false;
	}
	
	
	
	public Point computeNextPositionInNbTour(int nbTour) {
		int tempX = this.getX();
		int tempY = this.getY();
		int tempVx = this.vx;
		int tempVy = this.vy;
		
		for(int i=0; i<nbTour; i++) {
			
			tempX += tempVx;
			tempY += tempVy;
			
			//TODO: Check collision with wall (impulsion)
			if(tempX + radius >16000) {
				if(this.type == 1 && this.isInGoal(tempY)) {
					tempX=16000;
					tempVx=0;
					tempVy=0;
				} else {
					tempX = 2*16000 -2*radius - tempX;
					tempVx = -tempVx;
				}

			} else {
				if(tempX - radius<0) {
					if(this.type == 1 && this.isInGoal(tempY)) {
						tempX=0;
						tempVx=0;
						tempVy=0;
					} else {
						tempX = 2*radius -tempX;
						tempVx = -tempVx;
					}
				}
			}
			
			//TODO: Check collision with wall (impulsion)
			if(tempY + radius >7500) {
				tempY = 2*7500 -2*radius - tempY;
				tempVy = -tempVy;
			} else {
				if(tempY - radius<0) {
					tempY = 2*radius -tempY;
					tempVy = -tempVy;
				}
			}
			
			tempVx = (int) (tempVx * friction);
			tempVy = (int) (tempVy * friction);
			
		}
		

		
		Point newPosition = new Point(tempX, tempY);
		return newPosition;
	}
	
	public boolean isInGoal(int py) {
		if(py>1900 && py<5600) {
			return true;
		}
		
		return false;
	}
	
	public void stop() {
		this.vx = 0;
		this.vy = 0;
	}
	
	public Collision collision(Entity entity) {
		
		// Square of the distance
	    double dist = this.distance2(entity);

	    // Sum of the radii squared
	    double sr = (this.radius + entity.radius)*(this.radius + entity.radius);

	    // We take everything squared to avoid calling sqrt uselessly. It is better for performances

	    if (dist < sr) {
	        // Objects are already touching each other. We have an immediate collision.
	        return new Collision(this, entity, 0.0f);
	    }

	    // Optimisation. Objects with the same speed will never collide
	    if (this.vx == entity.vx && this.vy == entity.vy) {
	        return null;
	    }

	    // We place ourselves in the reference frame of u. u is therefore stationary and is at (0,0)
	    int x = this.x - entity.x;
	    int y = this.y - entity.y;
	    Point myp = new Point(x, y);
	    int vx = this.vx - entity.vx;
	    int vy = this.vy - entity.vy;
	    Point up = new Point(0, 0);

	    // We look for the closest point to u (which is in (0,0)) on the line described by our speed vector
	    Point p = up.closest(myp, new Point(x + vx, y + vy));

	    // Square of the distance between u and the closest point to u on the line described by our speed vector
	    double pdist = up.distance2(p);

	    // Square of the distance between us and that point
	    double mypdist = myp.distance2(p);

	    // If the distance between u and this line is less than the sum of the radii, there might be a collision
	    if (pdist < sr) {
	     // Our speed on the line
	        double length = Math.sqrt(vx*vx + vy*vy);

	        // We move along the line to find the point of impact
	        double backdist = Math.sqrt(sr - pdist);
	        p.x = (int) (p.x - backdist * (vx / length));
	        p.y = (int) (p.y - backdist * (vy / length));

	        // If the point is now further away it means we are not going the right way, therefore the collision won't happen
	        if (myp.distance2(p) > mypdist) {
	            return null;
	        }

	        pdist = p.distance(myp);

	        // The point of impact is further than what we can travel in one turn
	        if (pdist > length) {
	            return null;
	        }

	        // Time needed to reach the impact point
	        double t = pdist / length;

	        return new Collision(this, entity, t);
	    }

	    return null;
	}
	
	
	void bounce(Entity entity) {

        // If a pod has its shield active its mass is 10 otherwise it's 1
        //float m1 = this.shield ? 10 : 1;
        //float m2 = entity.shield ? 10 : 1;
        //float mcoeff = (m1 + m2) / (m1 * m2);

		double nx = this.x - entity.x;
		double ny = this.y - entity.y;

        // Square of the distance between the 2 pods. This value could be hardcoded because it is always 800²
		double nxnysquare = nx*nx + ny*ny;

		double dvx = this.vx - entity.vx;
		double dvy = this.vy - entity.vy;

        // fx and fy are the components of the impact vector. product is just there for optimisation purposes
		double product = nx*dvx + ny*dvy;
		double fx = (nx * product) / (nxnysquare);
		double fy = (ny * product) / (nxnysquare);

        // We apply the impact vector once
        this.vx -= fx;
        this.vy -= fy;
        entity.vx += fx;
        entity.vy += fy;

        // If the norm of the impact vector is less than 120, we normalize it to 120
        double impulse = Math.sqrt(fx*fx + fy*fy);
        if (impulse < 120.0) {
            fx = fx * 120.0 / impulse;
            fy = fy * 120.0 / impulse;
        }

        // We apply the impact vector a second time
        this.vx -= fx;
        this.vy -= fy;
        entity.vx += fx;
        entity.vy += fy;

        // This is one of the rare places where a Vector class would have made the code more readable.
        // But this place is called so often that I can't pay a performance price to make it more readable.
    }
	
	public void move(double t) {
	    this.x += this.vx * t;
	    this.y += this.vy * t;
	}
	

	
	public void printAll() {
		System.err.println("Id: " + id);
		System.err.println("Vx: " +vx);
		System.err.println("Vy: " +vy);
		System.err.println("State " +state);
		System.err.println("Radius " + radius);
		System.err.println("Type " +type);
	}

	public void addMovement(Movement movement) {
		double dirX = movement.pointB.x - movement.entity.x;
		double dirY = movement.pointB.y - movement.entity.y;
		double norm = Math.sqrt(dirX*dirX + dirY*dirY);
		dirX/=norm;
		dirY/=norm;
		
		double coef = (double)movement.power / (double)this.mass;
		
		this.vx += dirX*coef;
		this.vy += dirY*coef;
		
		System.err.println("********** " + vx);
		System.err.println("********** " + vy);
		
		
	}
}
