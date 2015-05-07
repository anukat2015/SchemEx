package de.uni_koblenz.schemex.eval;

import java.util.ArrayList;

public class EvaluationResult {

	public static final int GOLD = 0;
	public static final int OTHER = 1;
	
	public long[] tcCount = {-1,-1};
	public long[] eqcCount = {-1,-1};
	public long[] pcCount = {-1,-1};
	public long[] types = {-1,-1};
	public long[] properties = {-1,-1};
	
	public ArrayList<Integer> typeGold  = new ArrayList<Integer>();
	public ArrayList<Integer> typeOther = new ArrayList<Integer>();
	public ArrayList<Integer> typeBoth  = new ArrayList<Integer>();

	public ArrayList<Integer> tcGold  = new ArrayList<Integer>();
	public ArrayList<Integer> tcOther = new ArrayList<Integer>();
	public ArrayList<Integer> tcBoth  = new ArrayList<Integer>();

	public ArrayList<Integer> tcsGold  = new ArrayList<Integer>();
	public ArrayList<Integer> tcsOther = new ArrayList<Integer>();
	public ArrayList<Integer> tcsBoth  = new ArrayList<Integer>();

	public ArrayList<Integer> pcGold  = new ArrayList<Integer>();
	public ArrayList<Integer> pcOther = new ArrayList<Integer>();
	public ArrayList<Integer> pcBoth  = new ArrayList<Integer>();
	
	public ArrayList<Integer> eqcGold  = new ArrayList<Integer>();
	public ArrayList<Integer> eqcOther = new ArrayList<Integer>();
	public ArrayList<Integer> eqcBoth  = new ArrayList<Integer>();
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Metric \tGold \tOther\n");
		buffer.append("TC     \t"+tcCount[GOLD]+"\t"+tcCount[OTHER]+"\n");
		buffer.append("EQC    \t"+eqcCount[GOLD]+"\t"+eqcCount[OTHER]+"\n");
		buffer.append("PC     \t"+pcCount[GOLD]+"\t"+pcCount[OTHER]+"\n");
		buffer.append("Types  \t"+types[GOLD]+"\t"+types[OTHER]+"\n");
		buffer.append("Props  \t"+properties[GOLD]+"\t"+properties[OTHER]+"\n");
		buffer.append("\n");
		buffer.append("---------------------------------------\n");
		buffer.append("\n");
		buffer.append("Query \tRecall \tPrecison\n");
		buffer.append("Type \t"+this.aggregateTypeRecall()+" \t"+this.aggregateTypePrecision()+"\n");
		buffer.append("TC   \t"+this.aggregateTcRecall()+" \t"+this.aggregateTcPrecision()+"\n");
		buffer.append("TCS  \t"+this.aggregateTcsRecall()+" \t"+this.aggregateTcsPrecision()+"\n");
		buffer.append("PC   \t"+this.aggregatePcRecall()+" \t"+this.aggregatePcPrecision()+"\n");
		buffer.append("EQC  \t"+this.aggregateEqcRecall()+" \t"+this.aggregateEqcPrecision()+"\n");
		return buffer.toString();
	}

	public void addTypeQuery(int goldSize, int otherSize, int bothSize) {
		this.typeGold.add(goldSize);
		this.typeOther.add(otherSize);
		this.typeBoth.add(bothSize);
	}
	
	public void addTcQuery(int goldSize, int otherSize, int bothSize) {
		this.tcGold.add(goldSize);
		this.tcOther.add(otherSize);
		this.tcBoth.add(bothSize);
	}
	
	public void addTcsQuery(int goldSize, int otherSize, int bothSize) {
		this.tcsGold.add(goldSize);
		this.tcsOther.add(otherSize);
		this.tcsBoth.add(bothSize);
	}
	
	public void addPcQuery(int goldSize, int otherSize, int bothSize) {
		this.pcGold.add(goldSize);
		this.pcOther.add(otherSize);
		this.pcBoth.add(bothSize);
	}
	
	public void addEqcQuery(int goldSize, int otherSize, int bothSize) {
		this.eqcGold.add(goldSize);
		this.eqcOther.add(otherSize);
		this.eqcBoth.add(bothSize);
	}
	
	public float aggregateTypeRecall() {
		return EvaluationResult.aggregateMetric(this.typeBoth,this.typeGold);
	}
	
	public float aggregateTypePrecision() {
		return EvaluationResult.aggregateMetric(this.typeBoth,this.typeOther);
	}
	
	public float aggregateTcRecall() {
		return EvaluationResult.aggregateMetric(this.tcBoth,this.tcGold);
	}
	
	public float aggregateTcPrecision() {
		return EvaluationResult.aggregateMetric(this.tcBoth,this.tcOther);
	}
	
	public float aggregateTcsRecall() {
		return EvaluationResult.aggregateMetric(this.tcsBoth,this.tcsGold);
	}
	
	public float aggregateTcsPrecision() {
		return EvaluationResult.aggregateMetric(this.tcsBoth,this.tcsOther);
	}
	
	public float aggregatePcRecall() {
		return EvaluationResult.aggregateMetric(this.pcBoth,this.pcGold);
	}
	
	public float aggregatePcPrecision() {
		return EvaluationResult.aggregateMetric(this.pcBoth,this.pcOther);
	}
	
	public float aggregateEqcRecall() {
		return EvaluationResult.aggregateMetric(this.eqcBoth,this.eqcGold);
	}
	
	public float aggregateEqcPrecision() {
		return EvaluationResult.aggregateMetric(this.eqcBoth,this.eqcOther);
	}
	
	public static float aggregateMetric(ArrayList<Integer> intersection, ArrayList<Integer> superset) {
		float result = 0;
		for (int i = 0; i < intersection.size(); i++) {
			int sup = superset.get(i);
			if (sup != 0) {
				float r = ( (float) intersection.get(i))/sup;
				result += r;
			}
		}
		return result/intersection.size();
	}
	
	
	
}
