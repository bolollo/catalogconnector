package org.idec.catalog;

import java.util.ArrayList;

public class OutputSchemaContainer {
	private ArrayList<String> outputSchemas;
	private boolean finishedLoadingSchemas;

	public OutputSchemaContainer(){
		outputSchemas = new ArrayList<String>();
		finishedLoadingSchemas = false;
	}
	
	public ArrayList<String> getOutputSchemas() {
		return outputSchemas;
	}

	public void setOutputSchemas(ArrayList<String> outputSchemas) {
		this.outputSchemas = outputSchemas;
	}
	
	public boolean isFinishedLoadingSchemas() {
		return finishedLoadingSchemas;
	}

	public void setFinishedLoadingSchemas(boolean finishedLoadingSchemas) {
		this.finishedLoadingSchemas = finishedLoadingSchemas;
	}
}
