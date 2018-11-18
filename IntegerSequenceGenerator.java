/**
	IntegerSequenceGenerator
	This class implements the Linear Congruential
	pseudorandom numbers generator. It generates numbers
	based on input for all four parameters of the recurrence
	relation.
	
	@author		A07104218	Salvador Orozco Villalever
	@author		A01328937	Luis Francisco Flores Romero
	@version	1.0
	@since		21.sep.2018
*/
import java.util.*;

public class IntegerSequenceGenerator {
	private int mod;
	private int multiplier;
	private int increment;
	private int seed;
	
	private int xCurr;	//	Saves state
	
	/**
		Constructor
		Starts the generator with given parameters.
		@param	seed		The first number.
		@param	multiplier	Recurrence relation 'a' parameter.
		@param	increment	Recurrence relation 'c' parameter.
		@param	mod			Modulo for the recurrence relation.
	*/
	public IntegerSequenceGenerator(int seed, int multiplier, int increment, int mod) {
		this.xCurr = this.seed = seed;	//	First number is the seed
		this.multiplier = multiplier;
		this.increment = increment;
		this.mod = mod;
		
		if(!validate()) {
			System.out.println("Parameters are not valid");
			System.exit(0);	//	Prevent unknown behaviour from bad parameters
		}
	}
	
	/**
		Get seed
		Returns the generator's seed.
		@return		IntegerSequenceGenerator's seed.
	*/
	public int getSeed() {
		return this.seed;
	}
	
	/**
		Get multiplier
		Returns the generator's multiplier used in the
		recurrence relation.
		@return		IntegerSequenceGenerator's multiplier.
	*/
	public int getMultiplier() {
		return this.multiplier;
	}
	
	/**
		Get increment
		Returns the generator's increment, as used in
		the recurrence relation.
		@param		IntegerSequenceGenerator's increment.
	*/
	public int getIncrement() {
		return this.increment;
	}
	
	/**
		Get modulo
		Returns the generator's modulo used in the
		recurrence relation.
		@return		IntegerSequenceGenerator's modulo.
	*/
	public int getMod() {
		return this.mod;
	}
	
	/**
		Validate
		Validates parameters of the generator according to
		the recurrence relation constraints. This method
		is called in the IntegerSequenceGenerator constructor.
		@return		Whether generator's parameters are valid.
	*/
	public boolean validate() {
		if(mod <= 0) {
			System.out.println("Validate: mod should be greater than 0");
			return false;
		}
		if(multiplier <= 0 || multiplier >= mod) {
			System.out.println("Validate: multiplier should be in range (0, mod)");
			return false;
		}
		if(increment < 0 || increment >= mod) {
			System.out.println("Validate: increment should be in range [0, mod)");
			return false;
		}
		if(seed < 0 || seed >= mod) {
			System.out.println("Validate: seed should be in range [0, mod)");
			return false;
		}
		
		return true;
	}
	
	/**
		View next
		Get the next pseudorandom number without saving state.
		@return		Next pseudorandom number.
	*/
	public int viewNext() {
		return (multiplier * xCurr + increment) % mod;
	}
	
	/**
		Next
		Get the next pseudorandom number and save state.
		@return		Next pseudorandom number.
	*/
	public int next() {
		xCurr = viewNext();
		return xCurr;
	}
	
	/**
		Generate
		Generates next n pseudorandom numbers from current
		state.
		@param	n	Amount of numbers to generate.
		@return		Next n pseudorandom numbers as array.
	*/
	public int[] generate(int n) {
		int[] nums = new int[n];
		nums[0] = xCurr;
		
		for(int i = 1; i < n; i++) {
			nums[i] = this.next();
		}
		
		return nums;
	}
}