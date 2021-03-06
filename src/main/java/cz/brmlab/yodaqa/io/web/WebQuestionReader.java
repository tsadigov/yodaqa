package cz.brmlab.yodaqa.io.web;

import de.tudarmstadt.ukp.dkpro.core.api.parameter.ComponentParameters;

import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.CasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import cz.brmlab.yodaqa.flow.dashboard.Question;
import cz.brmlab.yodaqa.flow.dashboard.QuestionDashboard;
import cz.brmlab.yodaqa.model.Question.QuestionInfo;


/**
 * A collection that reads the question from WebInterface. */

public class WebQuestionReader extends CasCollectionReader_ImplBase {
	/**
	 * Name of optional configuration parameter that contains the language
	 * of questions. This is mandatory as x-unspecified will break e.g. OpenNLP.
	 */
	public static final String PARAM_LANGUAGE = ComponentParameters.PARAM_LANGUAGE;
	@ConfigurationParameter(name = PARAM_LANGUAGE, mandatory = true)
	protected String language;

	@Override
	public void initialize(UimaContext aContext) throws ResourceInitializationException {
		super.initialize(aContext);
	}

	@Override
	public boolean hasNext() throws CollectionException {
		return true;
	}

	protected void initCas(JCas jcas, Question q) {
		jcas.setDocumentLanguage(language);

		QuestionInfo qInfo = new QuestionInfo(jcas);
		qInfo.setSource("web");
		qInfo.setQuestionId(q.getId());
		qInfo.addToIndexes(jcas);
	}

	@Override
	public void getNext(CAS aCAS) throws CollectionException {
		Question q = QuestionDashboard.getInstance().getQuestionToAnswer();
		try {
			JCas jcas = aCAS.getJCas();
			initCas(jcas, q);
			jcas.setDocumentText(q.getText());
		} catch (CASException e) {
			throw new CollectionException(e);
		}
	}

	@Override
	public Progress[] getProgress() {
		return new Progress[]{new ProgressImpl(0, -1, Progress.ENTITIES)};
	}

	@Override
	public void close() {
	}
}
