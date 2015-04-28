import java.util.HashMap;
import java.util.Random;


//Specific imports
import Jama.Matrix;

/**
 * A class for generating a Genotype, it extends from {@link Jama.Matrix}. 
 * An instance is basically a matrix plus a {@link Phenotype} and a {@link HashMap} which will contain differents phenotypes.
 * 
 * @author Jerome PIVERT
 * @author Raphael CHARLES
 *
 */
public class Genotype extends Matrix{
	
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1L;
	private Phenotype pheno;
	private final HashMap<String, Boolean> hashmap = new HashMap<String, Boolean>();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Genotype g = new Genotype(5);
		System.out.println(g);
		g.forceMutatation(0, 0);
		System.out.println(g);
	}
	
	//Constructor
	/**
	 * create an instance of Genotype from an other one (copy)
	 * 
	 * @category constructor
	 * @param gen
	 */
	public Genotype(Genotype gen){
		super(gen.getArrayCopy());
		this.pheno = new Phenotype(gen.getPhenotype());
		hashmap.put(this.pheno.toString(), true);
	}
	/**
	 * create a random Genotype of a given size
	 * 
	 * @category constructor
	 * @param size
	 */
	public Genotype(int size){
		super(size, size);
		Genotype.randomGen(this);
		this.pheno = new Phenotype(size);
		hashmap.put(this.pheno.toString(), true);
	}
	/**
	 * create a random Genotype and a Phenotype with an initPhenvalue
	 * 
	 * @category constructor
	 * @param size number of genes
	 * @param initPhenValue value for initializing the Phenotype 
	 */
	public Genotype(int size, double initPhenValue){
		super(size, size);
		Genotype.randomGen(this);
		this.pheno = new Phenotype(size, initPhenValue);
		hashmap.put(this.pheno.toString(), true);
	}
	/**
	 * create a default void Genotype
	 * 
	 * @category constructor
	 * @param phen Phenotype
	 * @param size size of the Genotype
	 */
	public Genotype(Phenotype phen, int size){
		super(size, size);
		this.pheno = phen;
		hashmap.put(this.pheno.toString(), true);
	}
	/**
	 * create a default Genotype with a given value
	 * 
	 * @category constructor
	 * @param phen instance of Phenotype
	 * @param size size of the Genotype
	 * @param initValue initial value in the Genotype
	 */
	public Genotype(Phenotype phen, int size, double initValue){
		super(size, size, initValue);
		this.pheno = phen;
		hashmap.put(this.pheno.toString(), true);
	}
	/**
	 * create a Genotype from a given matrix, the Phenotype will be void
	 * 
	 * @category constructor
	 * @param mat matrix (future Genotype)
	 */
	public Genotype(Matrix mat){
		super(mat.getArray());
		this.pheno = new Phenotype(mat.getRowDimension());
		hashmap.put(this.pheno.toString(), true);
	}
	/**
	 * create a Genotype from a given matrix, Phenotype value is defined by the user
	 * 
	 * @category constructor
	 * @param mat matrix (future Genotype)
	 * @param initPhenValue Phenotype value
	 */
	public Genotype(Matrix mat, double initPhenValue){
		super(mat.getArray());
		this.pheno = new Phenotype(mat.getRowDimension(), initPhenValue);
		hashmap.put(this.pheno.toString(), true);
	}
	/**
	 * create a Genotype from two given Jama.Matrix
	 * 
	 * @category constructor
	 * @param genMat matrix (future Genotype)
	 * @param phenMat matrix (future Phenotype)
	 */
	public Genotype(Matrix genMat, Matrix phenMat){
		super(genMat.getArray());
		this.pheno = new Phenotype(phenMat);
		hashmap.put(this.pheno.toString(), true);
	}

	//Getters and setters
	/**
	 * set a new Phenotype
	 * 
	 * @category mutator
	 * @param p Phenotype
	 */
	public void setPhenotype(Phenotype p){
		//hashmap.remove(this.pheno.toString());
		this.pheno = p;
		hashmap.put(p.toString(), true);
	}
	/**
	 * get the current Phenotype
	 * 
	 * @category accessor
	 * @return Phenotype
	 */
	public Phenotype getPhenotype(){
		return this.pheno;
	}
	/**
	 * get all the encountered Phenotype for this instance
	 * 
	 * @category accessor
	 * @return HashMap of all Phenotype
	 */
	public HashMap<String, Boolean> getMap(){
		return this.hashmap;
	}
	
	
	//Methods
	/**
	 * return a mutli-line String for representing the Genotype
	 * 
	 * @category print
	 */
	@Override
	public String toString(){
		String str = new String();
		for(int i = 0; i < this.getRowDimension(); i++){
			str += "[\t";
			for(int j = 0; j < this.getColumnDimension(); j++){
				str += this.get(i, j) + "\t";
			}
			str += "]\n";
		}
		return str;
	}
	/**
	 * @category genotypeGenerator
	 * @param row 
	 * @param col
	 * @return random filled Jama.Matrix - normally distributed
	 */
	private static void randomGen(Matrix tmp){
		//Matrix tmp = new Matrix(row, col);
		Random rand = new Random();
		for(int i = 0; i < tmp.getRowDimension(); i++){
			for(int j = 0; j < tmp.getColumnDimension(); j++){
				tmp.set(i, j, rand.nextGaussian());
			}
		}
	}
	/**
	 * put all values for the indexed gene to 0
	 * 
	 * @category doTheMaths
	 * @param geneIndex index to knockout da Matrix ! put 0 everywheeeeeereee
	 */
	public void knockout(int geneIndex){
		for(int i = 0; i<this.getRowDimension(); i++){
			for(int j = 0; j<this.getColumnDimension(); j++){
				if(i == geneIndex || j == geneIndex){
					this.set(i, j, 0.0);
				}
			}
		}
	}
	/**
	 * force the mutation at i,j
	 * 
	 * @category doTheMaths
	 * @param i row
	 * @param j col
	 */
	public void forceMutatation(int i, int j){
		Random rand = new Random();
		this.set(i, j, rand.nextGaussian());
	}
	
}
