import Jama.Matrix;

/**
 * A class for generating a phenotype. It extends from {@link Jama.Matrix}.
 * We override the methods toString(), equals(Object o) and setMatrix(...) from the superclass because we need to threshold before modifications.
 * 
 * @author Jerome PIVERT
 * @author Raphael CHARLES
 *
 */
public class Phenotype extends Matrix{
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*Phenotype p = new Phenotype(4);
		System.out.println(p);
		Phenotype p2 = new Phenotype(8,-1);
		System.out.println(p2);
		System.out.println(p.equals(p2));*/
		
		Matrix m = new Matrix(2, 3);
		System.out.println(m.getRowDimension());
		for(int i = 0; i<2; i++){
			for(int j = 0; j<3; j++){
				System.out.println(m.get(i, j));
			}	
		}
	}
	
	//Constructor
	/**
	 * copy constructor for a phenotype
	 * @category constructor
	 * @param p phenotype to copy
	 */
	public Phenotype(Phenotype p){
		super(p.getArrayCopy());
	}
	/**
	 * create a default void phenotype
	 * 
	 * @category constructor
	 * @param size number of genes
	 */
	public Phenotype(int size){
		super(size, 1);
	}
	/**
	 * create a new phenotype from a given one, it will be tresholded
	 * 
	 * @category constructor
	 * @param initPhen initial phenotype as a matrix
	 */
	public Phenotype(Matrix initPhen){
		super(initPhen.getArray());
		Phenotype.threshold(this);
	}
	/**
	 * create a new phenotype with a given value
	 * 
	 * @category constructor
	 * @param size number of genes
	 * @param initValue phenotype initial value 
	 */
	public Phenotype(int size, double initValue){
		super(size, 1, (int) initValue);
		Phenotype.threshold(this);
	}
	
	//Getters & setters -from superclass-
	/**
	 * override the default method for thresholding --see jama.Matrix--
	 * 
	 * @category mututator
	 */
	@Override
	public void setMatrix(int arg0, int arg1, int[] arg2, Matrix arg3){
		Phenotype.threshold(arg3);
		super.setMatrix(arg0, arg1, arg2, arg3);
	}
	
	//Methodes
	/**
	 * run the thresholding of a matrix, if > 0 set 1, if < 0 set -1
	 * 
	 * @category doTheMaths
	 * @param toThresh matrix to apply tresholding
	 */
	private static void threshold(Matrix toThresh){
		for(int i = 0; i != toThresh.getRowDimension(); i++){
			if(toThresh.getArray()[i][0] >= 0){
				toThresh.set(i, 0, 1);
			}
			else toThresh.set(i, 0, -1);
		}
	}
	/**
	 * run the thresholding of a matrix, if > 0 set 1, if < 0 set -1, else set 0
	 * 
	 * @category doTheMaths
	 * @param toThresh matrix to apply tresholding
	 */
	private static void threshold_zeroAllowed(Matrix toThresh){
		for(int i = 0; i != toThresh.getRowDimension(); i++){
			if(toThresh.getArray()[i][0] > 0){
				toThresh.set(i, 0, 1);
			}
			else if(toThresh.getArray()[i][0] < 0){
				toThresh.set(i, 0, -1);
			}
			else toThresh.set(i, 0, 0); //this specific case (due to gauss distribution) has a null-near probability
		}
	}
	/**
	 * return a single line string for representing the phenotype
	 * 
	 * @category print
	 */
	@Override
	public String toString(){
		String str = new String();
		str = "[ ";
		for(int i = 0; i < this.getRowDimension(); i++)
			str += this.get(i, 0) + " ";
		return str+"]";
	}
	/**
	 * override the existing equals method
	 * 
	 * @category compare
	 * @param lambda other object to compare with
	 * @return true if this and lambda have same values
	 */
	@Override
	public boolean equals(Object lambda){
		if(!(lambda instanceof Phenotype)) //if lambda is not a phenotype
			return false;
		Phenotype p = (Phenotype) lambda; //be sure its the good type

		//Same dimension ?
		if(p.getRowDimension() != this.getRowDimension() || p.getColumnDimension() != this.getColumnDimension())
			return false;
		//Same values ?
		for(int i = 0; i<p.getRowDimension(); i++){
			int pValue = (int) p.get(i, 0);
			int thisValue = (int) this.get(i, 0);
			if(pValue != thisValue)	
				return false;
		}
		return true;
	}
	
}
