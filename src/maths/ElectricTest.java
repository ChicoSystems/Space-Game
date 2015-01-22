package maths;

public class ElectricTest {
	public static void main(String[] args){
		double p1 = 1;
		double l = 0;
		double p2 = 0;
		double k = 500;
		for(int i = 1; i < 2000; i=i+250){
			l = i;
			//p2 = k*p1/l;
			//p2=k*(p1/l);
			p2 = k / l;
			//p2 = k / Math.sqrt(l);
			//p2 = k / (l*l);
			//p2 = l/k;
			p2 = p1/Math.pow(2, (l/1000));
					
			System.out.println("p1:" + p1 + " l:" + l + " p2="+p2);
		}
	}
}
