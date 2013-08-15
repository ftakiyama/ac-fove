package br.usp.poli.takiyama.acfove;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import br.usp.poli.takiyama.common.Marginal;
import br.usp.poli.takiyama.common.Parfactor;

public class LoggedACFOVE extends ACFOVE {

	private final VelocityEngine engine;
	private final VelocityContext context;
	private final Template logTemplate;
	
	// stores step results
	private final Writer stepWriter;
	
	// writes everything to a file
	private final Writer logWriter;
	
	public LoggedACFOVE(Marginal parfactors) {
		super(parfactors);
		this.engine = new VelocityEngine();
		this.engine.init();
		this.context = new VelocityContext();
		this.stepWriter = new StringWriter();
		this.logTemplate = engine.getTemplate("lib/templates/template.vm");
		
		Writer tempWriter = null;
		try {
			tempWriter = new BufferedWriter(new FileWriter("log/acfove.html"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		this.logWriter = tempWriter;
		printQuery();
		printMarginal();
	}
	
	private void printQuery() {
		Template mTemplate = engine.getTemplate("lib/templates/query_template.vm");
		VelocityContext mContext = new VelocityContext();
		
		mContext.put("query", super.result().preservable());
		
		mTemplate.merge(mContext, stepWriter);
	}
	
	private void printMarginal() {

		Template mTemplate = engine.getTemplate("lib/templates/marginal_template.vm");
		VelocityContext mContext = new VelocityContext();
		
		mContext.put("operation", super.currentOperation());
		mContext.put("marginal", super.result());
		
		mTemplate.merge(mContext, stepWriter);
		
		
	}
	
	@Override
	Marginal runStep() {
		Marginal result = super.runStep();
		printMarginal();
		return result;
	}
	
		
	@Override
	public Parfactor run() {
		Parfactor result = null;
		try {
			result = super.run();
		} catch (IllegalArgumentException e) {
			System.out.print("Finished with errors.");
		} catch (IllegalStateException e) {
			System.out.print("Finished with more than 1 parfactor.");
		}
				
		context.put("allSteps", stepWriter.toString());
		logTemplate.merge(context, logWriter);
		
		try {
			logWriter.flush();
		} catch (IOException e) {
			System.err.println("Could not write to log file.");
		}
		
		return result;
	}
	
}
