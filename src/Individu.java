//General imports
import java.util.Scanner;
import java.util.Random;

//Specific imports
import Jama.Matrix;
import java.util.HashMap;


/**
 * A class for creating an 'Individu'. It basically is a {@link Genotype} with a given size.
 * 
 * @author Jerome PIVERT
 * @author Raphael CHARLES	
 *
 */
public class Individu{
	
	private int size;
	private Genotype genotype;

	//File Testing
	public static void main(String[] args){
		for(boolean i=false; !i;){
			Individu ind = new Individu(5);
			i=ind.isViable();
			//System.out.println(i);
			//System.out.println(ind.genotype.getMap().toString());
			//System.out.println(ind.genotype.getPhenotype());
			System.out.println(ind.getGenotype());
		}
		int j = 0;
		double tic = System.nanoTime();
		for(int i = 0; i<30000;){
			Individu ind = new Individu(5);
			if(ind.isViable()){
				i++;
			}
			//else j++;
			//System.out.println(i);
		}
		//System.out.println(j);
		double tac = System.nanoTime();
		System.out.println((tac-tic)/1e6);
	}
	
	//Constructors
	/**
	 * Create a new Individu from a given one (copy constructor)
	 * 
	 * @category constructor
	 * @param ind object to copy
	 */
	public Individu(Individu ind){
		this.size = ind.getSize();
		this.genotype = new Genotype(ind.getGenotype());
	}
	/**
	 * Create new Individu with default values (randomly, gauss distrib genotype) with a size
	 * of 5 genes and a void phenotype
	 * 
	 * @category constructor
	 */
	public Individu(){
		this.size = 5;
		this.genotype = new Genotype(5);
	}
	/**
	 * Create new Individu with a randomly, normally distributed values
	 * with a given size and a void phenotype
	 * 
	 * @category constructor
	 * @param nbGenes
	 */
	public Individu(int nbGenes){
		this.size = nbGenes;
		this.genotype = new Genotype(nbGenes);
	}
	/**
	 * Create new Individu with a given matrix
	 * 
	 * @category constructor 
	 * @param mat matrix (future genotype)
	 */
	public Individu(Matrix mat){
		this.size = mat.getRowDimension();
		this.genotype = new Genotype(mat);
	}
	/**
	 * Create new Individu with a given matrix for genotype and phenotype
	 * 
	 * @category constructor
	 * @param gen	matrix (future genotype)
	 * @param phen matrix (future phenotype)
	 * 
	 */
	public Individu(Matrix genMat, Matrix phenMat){
		this.size =genMat.getRowDimension();
		this.genotype = new Genotype(genMat, phenMat);
	}
	/**
	 * Create new Individu with a given size and a default value for phenotype, random genotype
	 * 
	 * @category constructor
	 * @param nbGenes
	 * @param initialPhenValue
	 */
	public Individu(int nbGenes, double initialPhenValue){
		this.size = nbGenes;
		this.genotype = new Genotype(nbGenes, initialPhenValue);
	}
	
	//Getters
	/**
	 * @category accessor
	 * @return the genotype
	 */
	public Genotype getGenotype() {
		return this.genotype;
	}
	/**
	 * @category accessor
	 * @return the phenotype
	 */
	public Phenotype getPhenotype() {
		return this.genotype.getPhenotype();
	}
	/**
	 * @category accessor
	 * @return the size
	 */
	public int getSize() {
		return this.size;
	}
	
	//Setters
	/**
	 * @category mutator
	 * @param genotype the genotype to set
	 */
	public void setGenotype(Genotype genotype){
		this.genotype = genotype;
	}
	/**
	 * @category mutator
	 * @param phenotype the phenotype to set
	 */
	public void setPhenotype(Phenotype pheno) {
		this.genotype.setPhenotype(pheno);
	}
	/**
	 * @category mutator
	 * @param size the size to set
	 */
	public void setSize(int size) {
		this.size = size;
	}
	
	//Methods
	/**
	 * @category print
	 * @return object as a multiline string
	 */
	@Override
	public String toString(){
		String s = new String();
		s = "--GENOTYPE--\n";
		for(int i = 0; i != this.size; i++){
			for(int j = 0; j != this.size; j++){
				s += this.genotype.get(i, j) + "\t";
			}
			s += "\n";
		}
		s += "\n--PHENOTYPE--\n";
		for(int i = 0; i != this.size; i++){
				s += this.genotype.getPhenotype().getArray()[i][0] + "\t";
		}
		s += "\n--HASHMAP--\n";
		for(String key : genotype.getMap().keySet()){
			s += key + ":" + genotype.getMap().get(key) + "\n";
		}
		return s;
	}
	/**
	 * @category doTheMaths
	 * @return true if individu is viable
	 * an individu is viable if his phenotype(t) == phenotype(t-1)
	 */
	public boolean isViable(){
		while(true){
			Phenotype previousPhen = this.getPhenotype();
			//set new phenotype
			Phenotype newPhen = new Phenotype(this.getGenotype().times(this.getPhenotype()));
			
			//If same as previous
			if(newPhen.equals(previousPhen)){
				this.setPhenotype(newPhen);
				return true;
			}
			else if(this.genotype.getMap().containsKey(newPhen.toString()))
				return false;
			else this.setPhenotype(newPhen);
		}
	}
	
	
}
