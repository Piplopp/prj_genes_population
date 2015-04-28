import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import com.sun.org.apache.xalan.internal.xsltc.util.IntegerArray;
import com.sun.xml.internal.ws.api.pipe.NextAction;

import Jama.Matrix;
//POPULATION TRANSFORMER LA LISTE EN ARRAY (PLUS PERFORMANT)

/**
 * The main class of the programm, it contain all static methods for analysis.
 * 
 * @author Jerome PIVERT
 * @author Raphael CHARLES
 */
public class Analysis {

	/**
	 * Let the show begin !
	 * 
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {	
		double tic = System.nanoTime();
		//File writing
		FileWriter file = new FileWriter("popSize.txt", true);
		BufferedWriter bw = new BufferedWriter(file);
					int j = 0;

		//for(int j = 0; j < 10; j++){		
			
			//Initialize your population
			Population pop = new Population(300, 0.05);
			System.out.println("Initialization...");
			pop.initPop(5);
			
	
			//Initialize your Generations
			Population[] generations = new Population[2000];
			generations[0] = pop;
			System.out.println("Processing generation "+j+".0...");
			runGenePersistence(generations[0]);
			//runDeadGeneration(generations[0]);
			//runGlobalKOAnalysis(generations[0]);
			//runGlobalMutationAnalysis(generations[0]);
			bw.write(generations[0].getTaille()+"\n");
			bw.flush();
			
			//Do the maths !
			for(int i = 1; i<generations.length; i++){
				System.out.println("Processing generation "+j+"."+i+"...");
				generations[i] = Population.nextGen(generations[i-1], 4);
				bw.write(generations[i].getTaille()+"\n");
				bw.flush();
				if(generations[i].getTaille() <= 1) break;
				runGenePersistence(generations[i]);
				//runDeadGeneration(generations[i]);
				//runGlobalKOAnalysis(generations[i]);
				//runGlobalMutationAnalysis(generations[i]);
			}
			System.out.println("Process done.");
		//}
		bw.flush();
		bw.close();	
		double tac = System.nanoTime();
		System.out.println((tac-tic)/1e9);//in seconds

	}
	
	//Runnables
		//Knock-out
	/**
	 * Run the KO analysis for a given generation. It will create one file per gene.
	 * output filename: ko_resistance_at_[genePosition+1].txt
	 *
	 * @category runAnalysis
	 * @param pop specific generation
	 * @throws IOException 
	 */
	public static void runSeparateKOAnalysis(Population pop) throws IOException{
		int stillViables = 0;
		for(int i = 0; i<pop.getPop()[0].getSize(); i++){
			stillViables = knockoutResistance(pop, i);
			
			//File writing
			FileWriter file = new FileWriter("ko_resistance_at_"+(i+1)+".txt", true);
			BufferedWriter bw = new BufferedWriter(file);
			
			bw.write(stillViables+"\n");
			bw.flush();
			bw.close();
		}
	}
	/**
	 * Run the global KO analysis for a given generation. It creates one file.
	 * output filename: global_ko_resistance.txt
	 * 
	 * @category runAnalysis
	 * @param pop specific generation
	 * @throws IOException
	 */
	public static void runGlobalKOAnalysis(Population pop) throws IOException{
		FileWriter file = new FileWriter("global_ko_resistance_.txt", true);
		BufferedWriter bw = new BufferedWriter(file);
		
		bw.write(knockoutResistance(pop)+"\n");
		bw.flush();
		bw.close();
	}
		//Mutations
	/**
	 * Run the mutation analysis for a given generation and rate. 1 file/mutation rate
	 * output filename: mutation_resistance_taux_[mutationRate].txt
	 * 
	 * @category runAnalysis
	 * @param pop specific generation
	 * @param tauxMutation mutation rate
	 * @throws IOException
	 */
	public static void runMutationAnalysis(Population pop, double tauxMutation) throws IOException{
		int stillViables = 0;
		stillViables = mutationResistance(pop, tauxMutation);
		
		//File writing
		FileWriter file = new FileWriter("mutation_resistance_taux_"+tauxMutation+".txt", true);
		BufferedWriter bw = new BufferedWriter(file);
		
		bw.write(stillViables+"\n");
		bw.flush();
		bw.close();
	}
	/**
	 * Run the global mutation analysis for a given generation. It creates one file with 3 columns, in order: max	median	mean
	 * output filename: global_mutation_resistance.txt
	 * 
	 * @category runAnalysis
	 * @param pop
	 * @throws IOException
	 */
	public static void runGlobalMutationAnalysis(Population pop) throws IOException{
		int[] stillViables = mutationResistance(pop);
		double mean = 0;
		double median = 0;
		int max = 0;
		int matrixSize = pop.getPop()[0].getSize();
		
		//Maths
		for(int i = 0; i<stillViables.length; i++){
			mean += stillViables[i];
			if(stillViables[i] > max)
				max = stillViables[i];
		}
		mean = mean/(stillViables.length*matrixSize*matrixSize);
/*
		Arrays.sort(stillViables);
		if(stillViables.length % 2 == 0)
			median = ((double)stillViables[stillViables.length/2] + (double)stillViables[stillViables.length/2 - 1])/2;
		else
			median = (double)stillViables[stillViables.length/2];
*/
		//File writing
		FileWriter file = new FileWriter("global_mutation_resistance.txt", true);
		BufferedWriter bw = new BufferedWriter(file);
		
		bw.write(max+"\t"+median+"\t"+mean+"\n");
		bw.flush();
		bw.close();
		
	}
		//Misc
	/**
	 * export the death analysis for a given generation. 
	 * output filename: death_count.txt
	 * 
	 * @category runAnalysis
	 * @param pop
	 * @throws IOException
	 */
	public static void runDeadGeneration(Population pop) throws IOException{
		//File writing
		FileWriter file = new FileWriter("death_count.txt", true);
		BufferedWriter bw = new BufferedWriter(file);
				
		bw.write(pop.getNbCadavresDansLePlacard()+"\n");
		bw.flush();
		bw.close();
	}
	/**
	 * export the number of different 'gene' for a specific generation. Create one file: maximumResistance \t nbPhen \t meanRes
	 * 
	 * @category runAnalysis
	 * @param pop
	 * @throws IOException
	 */
	public static void runGenePersistence(Population pop) throws IOException{
		Integer[] occur = genePersistence(pop);
		double mean = 0;
		int nbPhen = occur.length;
		double maxRes = 0;
		
		//Maths
		for(int i = 0; i<occur.length; i++){
			mean += (double)occur[i];
			if((double)occur[i] > maxRes)
				maxRes = (double)occur[i];
		}
		mean = mean/(occur.length*pop.getPop()[0].getSize());
		
		//File writing
		FileWriter file = new FileWriter("global_gene_persistance.txt", true);
		BufferedWriter bw = new BufferedWriter(file);
			
		bw.write(maxRes+"\t"+nbPhen+"\t"+mean+"\n");
		bw.flush();
		bw.close();	
	}
	
	//Analysis
		//Knock-out
	/**
	 * get the percentage of knock-out resistance.
	 * example: if they resist at 3position on 6 --> resistance = 0.5
	 * 
	 * @category analysis
	 * @param pop population to test
	 * @return percentage of resistance
	 */
	public static double knockoutResistance(Population pop){
		Population tmpKnockedPop = new Population(pop); //copy
		int[] stillViable = new int[tmpKnockedPop.getTaille()];
		int mean = 0;
		for(int indexInd = 0; indexInd < pop.getTaille(); indexInd++){ //each Individu
			for(int i = 0; i < pop.getPop()[indexInd].getSize(); i++){ //each gene
				tmpKnockedPop.getPop()[indexInd].getGenotype().knockout(i);
				if(tmpKnockedPop.getPop()[indexInd].isViable()){
					stillViable[indexInd]++;
				}
			}
		}
		for(int i = 0; i<stillViable.length; i++){
			mean += stillViable[i];
		}
		return (double) mean/(stillViable.length*pop.getPop()[0].getSize());
	}
	/**
	 * get the number of Genotype still viable after knock-out in a specific position
	 * 
	 * @category analysis
	 * @param pop specific population
	 * @param indexGeneToKnock gene to be knocked
	 * @return the number of still viable {@link Individu}
	 */
	public static int knockoutResistance(Population pop, int indexGeneToKnock){
		Population tmpKnockedPop = new Population(pop); //copy
		int stillViable = 0;
		for(int i = 0; i < tmpKnockedPop.getTaille(); i++){
			tmpKnockedPop.getPop()[i].getGenotype().knockout(indexGeneToKnock);
			if(tmpKnockedPop.getPop()[i].isViable()){
				stillViable++;
			}
		}
		return stillViable;
	}
		//Mutations
	/**
	 * count the number of stillViables Individu after mutation at each position one by one.
	 * 
	 * @category analysis
	 * @param pop
	 * @return int[] --> how many times this {@link Individu} survived a mutation
	 */
	public static int[] mutationResistance(Population pop){
		int[] nbResist = new int[pop.getTaille()];
		Population tmpPop = new Population(pop);
		double saveValue = 0.0;
		
		for(int i = 0; i < nbResist.length; i++){//for each Individu
			for(int row = 0; row < pop.getPop()[i].getSize(); row ++){
				for(int col = 0; col < pop.getPop()[i].getSize(); col ++){
					
					saveValue = pop.getPop()[i].getGenotype().get(row, col);
					tmpPop.getPop()[i].getGenotype().forceMutatation(row, col);
					
					if(tmpPop.getPop()[i].isViable())
						nbResist[i]++;
					
					tmpPop.getPop()[i].getGenotype().set(row, col, saveValue);
				}
			}
		}
		return nbResist;
	}
	/**
	 * get the number of Genotype still viable after mutation at all position in a specific population
	 * 
	 * @category analysis
	 * @param pop specific population
	 * @param tauxMutation mutation rate
	 * @return the number of still viable {@link Individu}
	 */
	public static int mutationResistance(Population pop, double tauxMutation){
		Population tmpMutated =  new Population(pop); //copy
		int stillViable = 0;
		for(int i = 0; i < tmpMutated.getTaille(); i++){
			tmpMutated.getPop()[i] = Population.mutation(tmpMutated.getPop()[i], tauxMutation);
			if(tmpMutated.getPop()[i].isViable()){
				stillViable++;
			}
		}
		return stillViable;
	}
		//Gene persistence
	/**
	 * count the number of repeated genes in a specific population. We are working with 'interaction matrix' this is not a gene.
	 * 
	 * @category analysis
	 * @param pop
	 * @return count of all encountered genes
	 */
	public static Integer[] genePersistence(Population pop){
		Population tmpPop = new Population(pop);
		int nbGenes = tmpPop.getPop()[0].getSize();
		
		HashMap<String, Integer> repeatedGenes = new HashMap<String, Integer>();
		for(Individu currentInd : tmpPop.getPop()){
			for(int row = 0; row < nbGenes; row++){
				String str = "[\t";
				for(int col = 0; col < nbGenes; col++){
					str += currentInd.getGenotype().get(row, col)+"\t";
				}
				str += "]";
				
				if(repeatedGenes.containsKey(str))
					repeatedGenes.put(str, repeatedGenes.get(str)+1);
				else repeatedGenes.put(str, 1);
			}
		}
		
		return repeatedGenes.values().toArray(new Integer[repeatedGenes.values().size()]); //magic
	}
/*
	/**
	 * count the number of repeated genes in a specific population. We are working with 'interaction matrix' this is not a gene.
	 * We count the number of repeated 'lines' in a {@link Jama.Matrix}.
	 * 
	 * @category analysis
	 * @param pop population to count the genes
	 * @throws IOException 
	 */
/*	public static int[] genePersistence(Population pop) throws IOException{
		HashMap<String, Integer>[] repeatedGenes = new HashMap[pop.getPop()[0].getSize()]; //one map per gene
		Population tmpPop = new Population(pop);
		
		int nbGenes = tmpPop.getPop()[0].getSize();
		int[] occurDifferentGene = new int[nbGenes];
		
		for(int gene = 0; gene < nbGenes; gene++){ //for each gene (row in matrix) (default 0-4)
			repeatedGenes[gene] = new HashMap<String, Integer>();
			for(Individu currentInd : tmpPop.getPop()){//for each individu in the pop
				String strCurrentGene = "[\t";
				for(int i = 0; i < nbGenes; i++){//build the string (create the key for HashMap)
					strCurrentGene+=currentInd.getGenotype().get(gene, i)+"\t";
				}
				strCurrentGene += "]";
				//check/update the gene map for the current 'Individu'
				if(repeatedGenes[gene].containsKey(strCurrentGene)){
					repeatedGenes[gene].put(strCurrentGene, repeatedGenes[gene].get(strCurrentGene)+1);}
				else {repeatedGenes[gene].put(strCurrentGene, 1);}
			}
		}
		//count the number of differents genes for each row
		for(int i = 0; i<nbGenes; i++){
			occurDifferentGene[i] = repeatedGenes[i].size();
		}
		
		return occurDifferentGene;
	}
*/	
	
	//Log
	/**
	 * Write in Logfile
	 * 
	 * @category fileWriting
	 * @param f	file writer
	 * @param s name of the method
	 * @param time execution time of the method
	 * @throws IOException
	 */
	public static void writeInLog(FileWriter f, String s, double time) throws IOException{
		//File writing
		BufferedWriter bw = new BufferedWriter(f);
		bw.write(s+"\t"+time);
		bw.flush();
		bw.close();
	}
}
