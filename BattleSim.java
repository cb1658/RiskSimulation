// This simulates battles for the board game of Risk.

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class BattleSim {

	static Random rand = new Random();
	static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) {
		
		// STANDARD BATTLE SIMULATION USAGE
		
		BattleSim bs = new BattleSim();
		int numAttackers = 0, numDefenders = 0;
				
		while(true) {
			System.out.println("Enter # Attackers: ");
			try {
				numAttackers = Integer.parseInt(sc.nextLine());
			}catch(NumberFormatException e) {
				System.out.println("Invalid input! \n");
				continue;
			}
			break;
		}
		
		while(true) {
			System.out.println("Enter # Defenders: ");
			try {
				numDefenders = Integer.parseInt(sc.nextLine());
			}catch(NumberFormatException e) {
				System.out.println("Invalid input! \n");
				continue;
			}
			break;
		}
		
		System.out.println("\n--------------------------------");
		
		int[] result = bs.simulateBattle(numAttackers, numDefenders, true);
		
		System.out.println("\n--------------------------------\n");
		
		if(result[0] > result[1]) {
			System.out.println("Attackers win, Attackers Remaining: " + result[0]);
		}else {
			System.out.println("Attack failed, Defenders Remaining: " + result[1]);
		}
		
		
		// AVERAGE RESULT USAGE
		
		System.out.println("\n--------------------------------\n");
		double sum = 0;
		int iterations = 10000;
		for(int i = 0; i < iterations; i++) {
			
			int[] outcome = bs.simulateBattle(2, 2, false);
			if(outcome[0] > outcome[1]) {
				sum += outcome[0];
			}else {
				sum -= outcome[1];
			}
		}
		System.out.println(sum/iterations);
		
		// Average result of a 3v2:
		
		System.out.println("\n--------------------------------\n");
		
		int iter = 100000;
		double sum2 = 0;
		for(int i = 0; i < iter; i++) {
			sum2 += (bs.defendersLossPerThreeAttackers());
		}
		System.out.println(sum2/iter);
		
		// For every 3 attackers lost, ~2.43 defenders are lost.
		// Therefore, on average, you need greater than 3/2.43 = ~1.25x odds in order to win a battle in Risk.
		
		// -------------------
		// % Chance of Attacker winning:
		
		System.out.println("\n--------------------------------\n");
		
		double count = 0;
		int times = 10000;
		for(int i = 0; i < iterations; i++) {
			
			int[] outcome = bs.simulateBattle(1, 3, false);
			if(outcome[0] > outcome[1]) {
				count++;
			}
		}
		
		System.out.println(count/times*100 + "% chance of attacker winning");
	}

	public int[] simulateBattle(int numAttackers, int numDefenders, boolean userDelay) {
		
		while(numAttackers > 0 && numDefenders > 0) {
			
			if(numAttackers >= 3 && numDefenders >= 2) { // Primary Case
				if(userDelay) sc.nextLine();
				int[] update = threeAttackers(numDefenders);
				numAttackers -= update[0];
				numDefenders -= update[1];
				
				if(userDelay) System.out.println("Attackers Remaining: " + numAttackers + ", Defenders Remaining: " + numDefenders);
				continue;
			}
			
			if(numAttackers >= 3 && numDefenders == 1) {
				if(userDelay) sc.nextLine();
				int[] update = threeAttackers(1);
				numAttackers -= update[0];
				numDefenders -= update[1];
				
				if(userDelay) System.out.println("Attackers Remaining: " + numAttackers + ", Defenders Remaining: " + numDefenders);
				continue;
			}
			
			
			if(numAttackers == 2 && numDefenders >= 2) {
				if(userDelay) sc.nextLine();
				int[] update = twoAttackers(numDefenders);
				numAttackers -= update[0];
				numDefenders -= update[1];
				
				if(userDelay) System.out.println("Attackers Remaining: " + numAttackers + ", Defenders Remaining: " + numDefenders);
				continue;
			}
			if(numAttackers == 2 && numDefenders == 1) {
				if(userDelay) sc.nextLine();
				int[] update = twoAttackers(1);
				numAttackers -= update[0];
				numDefenders -= update[1];
				
				if(userDelay) System.out.println("Attackers Remaining: " + numAttackers + ", Defenders Remaining: " + numDefenders);
				continue;
			}
			if(numAttackers == 1 && numDefenders >= 2) {
				if(userDelay) sc.nextLine();
				int[] update = oneAttacker(numDefenders);
				numAttackers -= update[0];
				numDefenders -= update[1];
				
				if(userDelay) System.out.println("Attackers Remaining: " + numAttackers + ", Defenders Remaining: " + numDefenders);
				continue;
			}
			if(numAttackers == 1 && numDefenders == 1) {
				if(userDelay) sc.nextLine();
				int[] update = oneAttacker(1);
				numAttackers -= update[0];
				numDefenders -= update[1];
				
				if(userDelay) System.out.println("Attackers Remaining: " + numAttackers + ", Defenders Remaining: " + numDefenders);
				continue;
			}
			
		}
		
		
		
		return new int[] {numAttackers, numDefenders};
	}
	
	public int defendersLossPerThreeAttackers() {
		return threeAttackers(Integer.MAX_VALUE)[1];
	}
	
	private int[] threeAttackers(int numDefenders) {
		
		int aLoss = 0; int dLoss = 0;
		
		while(aLoss < 3 && numDefenders - dLoss > 0) {
			
			int aD1, aD2, aD3, dD1, dD2;
			aD1 = aD2 = aD3 = dD1 = dD2 = 0;
			
			if(aLoss == 0) {
				aD1 = diceRoll(); aD2 = diceRoll(); aD3 = diceRoll();
			}
			if(aLoss == 1) {
				aD1 = diceRoll(); aD2 = diceRoll();
			}
			if(aLoss == 2) {
				aD1 = diceRoll();
			}
			
			if(numDefenders - dLoss > 1) {
				dD1 = diceRoll(); dD2 = diceRoll();
			}
			if(numDefenders - dLoss == 1) {
				dD1 = diceRoll();
			}
			
			int aMax1 = findMax(aD1, aD2, aD3); int aMax2 = findSecondMax(aD1, aD2, aD3);
			int dMax1 = findMax(dD1, dD2, 0); int dMax2 = findSecondMax(dD1, dD2, 0);
			
			if(aMax1 > dMax1)
				dLoss++;
			else
				aLoss++;
			
			if(numDefenders - dLoss == 0 || aLoss == 3) {
				return new int[]{aLoss, dLoss};
			}
			
			
			if(aMax2 > dMax2)
				dLoss++;
			else
				aLoss++;
			
		}
		
		return new int[]{aLoss, dLoss};
	}
	
	private int[] twoAttackers(int numDefenders) {
		
		int aLoss = 0; int dLoss = 0;
		
		while(aLoss < 2 && numDefenders - dLoss > 0) {
			
			int aD1, aD2, dD1, dD2;
			aD1 = aD2 = dD1 = dD2 = 0;
			
			if(aLoss == 0) {
				aD1 = diceRoll(); aD2 = diceRoll(); 
			}
			if(aLoss == 1) {
				aD1 = diceRoll(); 
			}
			
			if(numDefenders - dLoss > 1) {
				dD1 = diceRoll(); dD2 = diceRoll();
			}
			if(numDefenders - dLoss == 1) {
				dD1 = diceRoll();
			}
			
			int aMax1 = findMax(aD1, aD2, 0); int aMax2 = findSecondMax(aD1, aD2, 0);
			int dMax1 = findMax(dD1, dD2, 0); int dMax2 = findSecondMax(dD1, dD2, 0);
			
			if(aMax1 > dMax1)
				dLoss++;
			else
				aLoss++;
			
			if(numDefenders - dLoss == 0 || aLoss == 2) {
				return new int[]{aLoss, dLoss};
			}
			
			if(aMax2 > dMax2)
				dLoss++;
			else
				aLoss++;
			
		}
		
		return new int[]{aLoss, dLoss};
		
	}
	
	private int[] oneAttacker(int numDefenders) {
		
		int aLoss = 0; int dLoss = 0;
		
		while(aLoss < 1 && numDefenders - dLoss > 0) {
			
			int aD1, dD1, dD2;
			dD1 = dD2 = 0;
			
			aD1 = diceRoll();  
			
			if(numDefenders - dLoss > 1) {
				dD1 = diceRoll(); dD2 = diceRoll();
			}
			if(numDefenders - dLoss == 1) {
				dD1 = diceRoll();
			}

			int dMax1 = findMax(dD1, dD2, 0); 
			
			if(aD1 > dMax1)
				dLoss++;
			else
				aLoss++;
			
		}
		
		return new int[]{aLoss, dLoss};
		
	}
	
	private int findMax(int a, int b, int c) {
		int max = Integer.max(a,b);
		max = Integer.max(max, c);
		return max; 
	}
	private int findSecondMax(int a, int b, int c) {
		int[] val = {a, b, c};
		Arrays.sort(val);
		return val[1];
	}
	
	public static int diceRoll() {
		return 1+rand.nextInt(6);
	}
	
}

// v0.2: Beta
// v1.0: Bug Fixes
// v1.0.1: Bug Fixes + User Delay + Documentation
// v1.0.2: defendersLossPerThreeAttackers + Documentation
