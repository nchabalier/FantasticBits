import java.util.ArrayList;
import java.util.List;

class Snaffle extends Entity{

	
	private Entity wizardCarrier = null;
	private List<Entity> spellers = new ArrayList<Entity>();
	private List<String> typesOfSpell  = new ArrayList<String>();
	private List<Integer> timeRemaining =  new ArrayList<Integer>();
	
	boolean isInGoal = false;
	
	public boolean isIntoGoal() {
		return isInGoal;
	}
	
	public void accio(Entity entity) {
		spellers.add(entity);
		typesOfSpell.add("ACCIO");
		timeRemaining.add(6);
	}
	
	public void petrificus() {
		this.vx = 0;
		this.vy = 0;
	}
	
	public void flipendo(Entity entity) {
		spellers.add(entity);
		typesOfSpell.add("FLIPENDO");
		timeRemaining.add(3);
	}
	
	public void throwToPosition(Point point, int power) {
		double distance = distance(point);
		double dirX = (point.x-this.x)/distance;
		double dirY= (point.y-this.y)/distance;
		
		this.vx+= dirX*power/this.mass;
		this.vy+= dirY*power/this.mass;

		
		wizardCarrier=null;
	}
	
	//Apply all spells launched on this entity
	public void applySpells() {
		for(int i=0; i<spellers.size(); i++) {
			Entity speller = spellers.get(i);

			double distance = distance(speller);
			
			//Spell haven't effects if distance = 0
			if(distance!=0) {
				double dirX = (this.x-speller.x)/distance;
				double dirY= (this.y-speller.y)/distance;
				
				double powerOfSpell;
				if(typesOfSpell.get(i).equals("ACCIO")) {
					//ACCIO
					powerOfSpell = -Math.min(1000, 3000/( distance*distance / 1000000 ));
					
				}else {
					//FLIPENDO
					powerOfSpell = Math.min(1000, 6000/( distance*distance / 1000000 ));
				}
				
				powerOfSpell /= this.mass;
				
				this.vx += dirX*powerOfSpell;
				this.vy += dirY*powerOfSpell;
			}
			
			int time = timeRemaining.get(i)-1;
			if(time==0) {
				//The spell stop
				spellers.remove(i);
				typesOfSpell.remove(i);
				timeRemaining.remove(i);
				i--;
			} else {
				timeRemaining.set(i, Integer.valueOf(time));
			}
			
		}
	}
	
	public Snaffle(double x, double y) {
		super(x, y);
		// TODO Auto-generated constructor stub
	}

	public Snaffle(int id, double x, double y, double vx, double vy, int state, double friction, int radius, int type,
			double mass) {
		super(id, x, y, vx, vy, state, friction, radius, type, mass);
		// TODO Auto-generated constructor stub
	}

	public Snaffle(int id, double x, double y, double vx, double vy, int state) {
		super(id, x, y, vx, vy, state);
		// TODO Auto-generated constructor stub
	}

	public Snaffle(int id, double x, double y, double vx, double vy) {
		super(id, x, y, vx, vy);
		// TODO Auto-generated constructor stub
	}
	
	//Change velocity if snaffle is not catched by a wizard
	@Override
	public void addMovement(Movement movement) {
		if(wizardCarrier==null) {
			this.vx += movement.vx/this.mass;
			this.vy += movement.vy/this.mass;
		}
	}
	
	public void updatePosition() {
		if(wizardCarrier!=null) {
			this.x = wizardCarrier.x;
			this.y = wizardCarrier.y;
			this.vx = wizardCarrier.vx;
			this.vy = wizardCarrier.vy;
		}
	}
	
	
	public void setWizard(Entity wizard) {
		this.wizardCarrier = wizard;
	}
	
	public boolean isCatched() {
		if(wizardCarrier!=null)
			return true;
		return false;
	}

}
