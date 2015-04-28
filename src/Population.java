//General imports
import java.util.Random;
import java.util.HashMap;

import com.sun.org.apache.bcel.internal.generic.POP;


/**
 * A class for creating a  {@link Population}. Shortly, its an array of {@link Individu}.
 * 
 * @author Jerome PIVERT
 * @author Raphael CHARLES
 */
public class Population {
	
	private int taille;
	private int nbPhens = 1;
	private int nbCadavresDansLePlacard = 0;
	private double tauxMutation;
	private Individu[] pop;
	private HashMap<String, Integer> popPhenMap = new HashMap<String, Integer>();

	//Construtor
	/**
	 * Create a new population, given size and mutation rate
	 * 
	 * @category constructor
	 * @param taille number of {@link Individu} in the population
	 * @param tauxMutation mutation rate
	 */
	public Population(int taille,double tauxMutation){
		this.taille=taille;
		this.pop=new Individu[taille];
		this.tauxMutation=tauxMutation;
	}
	/**
	 * Create a new {@link Population}, given size
	 * 
	 * @category constructor
	 * @param taille number of {@link Individu} in the population
	 */
	public Population(int taille){
		this.taille=taille;
		this.pop=new Individu[taille];
		this.tauxMutation=0;
	}
	/**
	 * Create a default {@link Population} of size 10
	 * 
	 * @category constructor
	 */
	public Population(){
		this.taille=10;
		this.pop=new Individu[10];
		this.tauxMutation=0;
	}
	/**
	 * Create a new {@link Population} from a given one, copy constructor
	 * 
	 * @category constructor
	 * @param p {@link Population} to copy
	 */
	public Population(Population p){
		this.taille=p.getTaille();
		this.pop = new Individu[p.getTaille()];
		for(int i = 0; i<this.taille; i++)
			this.pop[i] = new Individu(p.getPop()[i]);
		this.popPhenMap = (HashMap<String, Integer>) p.getPopPhenMap().clone();
		this.nbCadavresDansLePlacard = this.getNbCadavresDansLePlacard();
		this.nbPhens=p.getNbPhens();
		this.tauxMutation=p.getTauxMutation();
	}
	
	//Getters
	/**
	 * @category accessor
	 * @return size of population
	 */
	public int getTaille() {
		return taille;
	}
	/**
	 * @category accessor
	 * @return the population (array of {@link Individu})
	 */
	public Individu[] getPop() {
		return pop;
	}
	/**
	 * @category accessor
	 * @return number of {@link Phenotype} allowed in the pop
	 */
	public int getNbPhens() {
		return nbPhens;
	}
	/**
	 * @category accessor
	 * @return mutation rate
	 */
	public double getTauxMutation() {
		return tauxMutation;
	}
	/**
	 * @category accessor
	 * @return number of non-viables {@link Individu} eliminated for create this population
	 */
	public int getNbCadavresDansLePlacard() {
		return nbCadavresDansLePlacard;
	}
	/**
	 * @category accessor 
	 * @return the map of all phen in the pop
	 */
	public HashMap<String, Integer> getPopPhenMap() {
		return popPhenMap;
	}
	//Setters
	/**
	 * @category mutator
	 * @param taille size of the population
	 */
	public void setTaille(int taille) {
		this.taille = taille;
	}
	/**
	 * @category mutator
	 * @param pop manually replace the population by adding a new {@link Individu} array
	 */
	public void setPop(Individu[] pop) {
		this.pop = pop;
	}
	/**
	 * @category mutator
	 * @param nbPhens how {@link Phenotype} allowed in this population
	 */
	public void setNbPhens(int nbPhens) {
		this.nbPhens = nbPhens;
	}
	/**
	 * @category mutator
	 * @param n set mutation rate
	 */
	public void setTauxMutation(double n){
		this.tauxMutation=n;
	}
	/**
	 * @category mutator
	 * @param m HashMap to copy
	 */
	public void setHashMap(HashMap<String, Integer> m){
		this.popPhenMap = (HashMap) m.clone();
	}
	/**
	 * @category mutator
	 * @param n
	 */
	public void setNbCadavresDansLePlacard(int n){
		this.nbCadavresDansLePlacard = n;
	}
	
	//Methods
	/**
	 * Add a dead-body to the current pop	
	 */
	public void unMortDePlus(){
		this.nbCadavresDansLePlacard+=1;
	}
	/**
	 * Cree une {@link Population} initiale de n {@link Individus} viable (n : taille population)
	 * On autorise qu'un seul {@link Phenotype} pour la population initiale
	 * 
	 * @category initialize
	 * @param indSize number of genes for an {@link Individu} 
	 */
	public void initPop(int indSize){
		int i=0;
		while(i<this.taille){
			Individu indiv=new Individu(indSize);
			if(indiv.isViable()){
				if(i>=1){
					if(this.pop[0].getPhenotype().equals(indiv.getPhenotype())){
						this.pop[i]=indiv;
						i+=1;
					}
				}
				else{
					this.pop[i]=indiv;
					this.popPhenMap.put(indiv.getPhenotype().toString(), 1);
					i+=1;
				}
			}
			else{
				this.nbCadavresDansLePlacard+=1;
			}
		}
	}
	/**
	 * Create a new {@link Individu} from two others. We randomly choose each element in the parents for creating the child.
	 * 
	 * @category generate
	 * @param p population for picking parents
	 * @return the new viable child (possibly altered by mutations)
	 */
	public static Individu reproduction(Population p){
		//Choose Individu index
		Random rand=new Random();
		int index1=rand.nextInt(p.getTaille());
		int index2=index1;
		while(index1==index2){
			index2=rand.nextInt(p.getTaille());
		}

		//Select parents
		Individu ind1=p.getPop()[index1];
		Individu ind2=p.getPop()[index2];
		Individu enfant= new Individu(ind1.getSize(),1);
		
		//Create child genotype
		for(int i=0;i<ind1.getSize();i++){
			for(int j=0;j<ind1.getSize();j++){
				if(rand.nextBoolean()){
					enfant.getGenotype().set(i, j, ind1.getGenotype().get(i, j));
				}
				else{
					enfant.getGenotype().set(i, j, ind2.getGenotype().get(i, j));
				}
			}
		}
		return enfant;
		
	}
	/**
	 * Force mutation in one {@link Individu} with a given rate
	 * 
	 * @category generate
	 * @param indiv {@link Individu}
	 * @param tauxMutation mutation rate
	 * @return altered {@link Individu}
	 */
	public static Individu mutation(Individu indiv, double tauxMutation){
		Random rand=new Random();
		for(int i=0;i<indiv.getSize();i++){
			for(int j=0;j<indiv.getSize();j++){
				double prob=rand.nextDouble();
				if(prob<=tauxMutation){
					indiv.getGenotype().set(i, j, rand.nextGaussian());
				}
			}
		}
		return indiv;
	}
	/**
	 * Use a {@link Population} for generating the next. This is the default function, it uses the default number of allowed {@link Phenotype} (4)
	 * 
	 * @category generate
	 * @param p population parent
	 * @return new population
	 */
	public static Population nextGen(Population p){
		if(p.getTaille() <= 1) return new Population(0);
		
		//Init intermediate pop
		Population newPop=new Population(p.getTaille(),p.getTauxMutation());
		newPop.setNbPhens(p.getNbPhens());
		
		//Init counters
		int i=0;
		int nbPhens=0;
		
		while(i<p.getTaille()){
			//Create Individu
			Individu enfant=Population.reproduction(p);
			enfant=Population.mutation(enfant, newPop.getTauxMutation());
			while(enfant.isViable()==false){
				enfant=Population.reproduction(p);
				enfant=Population.mutation(enfant, p.getTauxMutation());
				newPop.unMortDePlus();
			}
			//Check Phenotype
			if(nbPhens<newPop.getNbPhens()){
				newPop.getPop()[i]=enfant;
				if(!newPop.getPopPhenMap().containsKey(enfant.getPhenotype().toString())){
					newPop.getPopPhenMap().put(enfant.getPhenotype().toString(), 1);
					nbPhens+=1;
				}
				i+=1;
			}
			else if(newPop.getPopPhenMap().containsKey(enfant.getPhenotype().toString())){
				int valeur=newPop.getPopPhenMap().get(enfant.getPhenotype().toString());
				newPop.getPopPhenMap().put(enfant.getPhenotype().toString(),valeur+1);
				newPop.getPop()[i]=enfant;
				i+=1;
			}
			else{
				newPop.unMortDePlus();
			}
		}
		return newPop;
	}
	/**
	 * Use a {@link Population} for generating the next. You can specify how many {@link Phenotype} will be allowed in the next population
	 * 
	 * @category generate
	 * @param p population parent
	 * @param nbPhens how many {@link Phenotype} allowed
	 * @return new population
	 */
	public static Population nextGen(Population p, int nbPhens){
		p.setNbPhens(nbPhens);
		return nextGen(p);
	}
	/**
	 * Use a {@link Population} for generating the next. This function will limit the number of reproductions. It will return a new population
	 * with a different size.
	 * 
	 * @category generate
	 * @param p population parent
	 * @param percentMaxReprod percent of Individu who can reproduce
	 * @param maxPopSize size max of the population
	 * @return new population size may be different from the previous
	 */
	public static Population nextGenLimited(Population p, double percentMaxReprod, int maxPopSize){
		if(p.getTaille() <= 1) return new Population(0);
		
		//Init intermediate pop
		int maxReprod = (int)(percentMaxReprod*p.getTaille());
		Population newPop=new Population(maxPopSize,p.getTauxMutation());
		newPop.setNbPhens(p.getNbPhens());
		
		//Init counters
		int i=0;
		int nbPhens=0;
		int nbReprod = 0; //reprod counter
		int nbInd = 0; //viable Individu counter
		
		while(i<newPop.getTaille()){
			if(nbReprod >= maxReprod) break;
			//Create child
			Individu enfant=Population.reproduction(p);
			enfant=Population.mutation(enfant, newPop.getTauxMutation());
			nbReprod++;
			while(enfant.isViable()==false){
				if(nbReprod >= maxReprod) break;
				enfant=Population.reproduction(p);
				enfant=Population.mutation(enfant, p.getTauxMutation());
				nbReprod++;
				newPop.unMortDePlus();
			}
			if(nbReprod >= maxReprod) break;
			
			//Check Phenotype
			if(nbPhens<newPop.getNbPhens()){
				newPop.getPop()[i]=enfant;
				if(!newPop.getPopPhenMap().containsKey(enfant.getPhenotype().toString())){
					newPop.getPopPhenMap().put(enfant.getPhenotype().toString(), 1);
					nbPhens+=1;
				}
				i+=1;
				nbInd++;
			}
			else if(newPop.getPopPhenMap().containsKey(enfant.getPhenotype().toString())){
				int valeur=newPop.getPopPhenMap().get(enfant.getPhenotype().toString());
				newPop.getPopPhenMap().put(enfant.getPhenotype().toString(),valeur+1);
				newPop.getPop()[i]=enfant;
				i+=1;
				nbInd++;
			}
			else{
				newPop.unMortDePlus();
			}
		}
		//Create the final pop to be returned
		Population finalPop = new Population(nbInd, newPop.getTauxMutation());
		Individu[] tmp = new Individu[nbInd];
		finalPop.setNbPhens(newPop.getNbPhens());
		finalPop.setHashMap(newPop.getPopPhenMap());
		finalPop.setNbCadavresDansLePlacard(newPop.getNbCadavresDansLePlacard());
		for(int j = 0; j<nbInd; j++){
			tmp[j] = newPop.getPop()[j];
		}
		finalPop.setPop(tmp);
		return finalPop;
	}
	/**
	 * Use a {@link Population} for generating the next. Limit the number of reproduction and specifiy how many {@link Phenotype} are allowed in the next population
	 * 
	 * @category generate
	 * @param p population parent
	 * @param percentMaxReprod the percent of allowed reprod
	 * @param maxPopSize 
	 * @param nbPhens how many {@link Phenotype} allowed
	 * @return new population, size may differ from previous
	 */
	public static Population nextGenLimited(Population p,double percentMaxReprod, int maxPopSize, int nbPhens){
		p.setNbPhens(nbPhens);
		return nextGenLimited(p, percentMaxReprod, maxPopSize);
	}
	/**
	 * Return a multi-line string for representing a generation.
	 * Will also call toString() from {@link Individu}
	 * 
	 * @category print
	 */
	@Override
	public String toString(){
		String str=new String();
		for(int i=0;i<this.getTaille();i++){
			str+="******INDIVIDU "+(i+1)+" ******\n";
			str+=this.getPop()[i]+"\n";
		}
		return str;
	}
	
	public static void main(String[] args) {
		Population test=new Population(2);
		test.initPop(5);
		//System.out.println(test);
		//Individu enf=Population.reproduction(test);
		//System.out.println(enf);
		Population newPop=Population.nextGen(test,5);
		/*System.out.println(newPop.getPopPhenMap().keySet());
		int i=0;
		for(String str :newPop.getPopPhenMap().keySet()){
			i+=newPop.getPopPhenMap().get(str);
			System.out.println(newPop.getPopPhenMap().get(str));
		}
		System.out.println(test.getPop().length);
		System.out.println(i);*/
	}

}
