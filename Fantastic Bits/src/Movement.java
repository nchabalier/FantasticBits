
class Movement {
	public Entity entity;
	public Point pointB;
	public int power;
	
	public Movement(Entity entity, Point pointB, int power) {
		super();
		this.entity = entity;
		this.pointB = pointB;
		this.power = power;
	}
	
	public void print() {
		System.err.println("Entity");
		entity.print();
		System.err.println("Target");
		pointB.print();
		System.err.println("Power " + power);
	}
}
