package maths;

import java.util.Scanner;

public class settleTest {

	 public static void main(String[] args){
		System.out.println("Calculate S from Theta and Radius. R < 0 to quit.");
		while(true){
			Scanner scanner = new Scanner(System.in);
			System.out.println("Enter T:");
			double t = scanner.nextDouble();
			System.out.println("Enter R:");
			double r = scanner.nextDouble();
			if(r <= 0)break;
			double s = (2/Math.PI)*t*r;
			System.out.println("s="+s);
			
			
			
		}
	}
}
