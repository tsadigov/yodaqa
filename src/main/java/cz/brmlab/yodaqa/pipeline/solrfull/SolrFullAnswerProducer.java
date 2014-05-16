package cz.brmlab.yodaqa.pipeline.solrfull;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.FlowControllerFactory;
import org.apache.uima.flow.impl.FixedFlowController;
import org.apache.uima.resource.ResourceInitializationException;

import cz.brmlab.yodaqa.analysis.passage.PassageAnalysisAE;
import cz.brmlab.yodaqa.analysis.passextract.PassageExtractorAE;
import cz.brmlab.yodaqa.pipeline.AnswerGenerator;
import cz.brmlab.yodaqa.pipeline.ResultGenerator;

/**
 * From the QuestionCAS, generate a bunch of CandidateAnswerCAS instances.
 *
 * This is an aggregate AE that will run a particular flow based on primary
 * search, result analysis, passage extraction and generating candidate
 * answers from chosen document passages.
 *
 * In this case, the flow is based on processing full results of
 * a Solr fulltext search. */

public class SolrFullAnswerProducer /* XXX: extends AggregateBuilder ? */ {
	public static AnalysisEngineDescription createEngineDescription() throws ResourceInitializationException {
		AggregateBuilder builder = new AggregateBuilder();

		AnalysisEngineDescription primarySearch = AnalysisEngineFactory.createEngineDescription(
				SolrFullPrimarySearch.class,
				SolrFullPrimarySearch.PARAM_RESULT_INFO_ORIGIN, "cz.brmlab.yodaqa.pipeline.solrfull.fulltext");
		builder.add(primarySearch);
		AnalysisEngineDescription resultGenerator = AnalysisEngineFactory.createEngineDescription(
				ResultGenerator.class,
				ResultGenerator.PARAM_RESULT_INFO_ORIGIN, "cz.brmlab.yodaqa.pipeline.solrfull.fulltext");
		builder.add(resultGenerator);

		AnalysisEngineDescription passageExtractor = PassageExtractorAE.createEngineDescription();
		builder.add(passageExtractor);
		AnalysisEngineDescription passageAnalysis = PassageAnalysisAE.createEngineDescription();
		builder.add(passageAnalysis);

		AnalysisEngineDescription answerGenerator = AnalysisEngineFactory.createEngineDescription(
				AnswerGenerator.class);
		builder.add(answerGenerator);

		builder.setFlowControllerDescription(
				FlowControllerFactory.createFlowControllerDescription(
					FixedFlowController.class,
					FixedFlowController.PARAM_ACTION_AFTER_CAS_MULTIPLIER, "drop"));

		AnalysisEngineDescription aed = builder.createAggregateDescription();
		aed.getAnalysisEngineMetaData().getOperationalProperties().setOutputsNewCASes(true);
		return aed;
	}
}
