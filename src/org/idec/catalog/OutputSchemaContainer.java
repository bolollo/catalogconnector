package org.idec.catalog;

import java.util.ArrayList;

public class OutputSchemaContainer {
	private ArrayList<String> outputSchemas;
	private boolean finishedLoadingSchemas;
	private boolean validationFailed;

	public OutputSchemaContainer(){
		outputSchemas = new ArrayList<String>();
		finishedLoadingSchemas = false;
		validationFailed = false;
	}

	public boolean isValidationFailed() {
		return validationFailed;
	}

	public void setValidationFailed(boolean validationFailed) {
		this.validationFailed = validationFailed;
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
