
class Movement {
	public double vx;
	public double vy;
	
	public Movement(Entity entity, Point target, int power) {
		double dx = (target.x - entity.x);
		double dy = (target.y - entity.y);
		double norm = Math.sqrt(dx*dx+dy*dy);
		this.vx = power * dx / norm;
		this.vy = power * dy / norm;
	}
	

}
