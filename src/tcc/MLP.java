package tcc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.encog.Encog;
import org.encog.ml.MLMethod;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.temporal.TemporalDataDescription;
import org.encog.ml.data.temporal.TemporalMLDataSet;
import org.encog.ml.data.temporal.TemporalPoint;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.factory.MLTrainFactory;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.end.SimpleEarlyStoppingStrategy;
import org.encog.util.arrayutil.NormalizedField;

public class MLP {

	private int inputWindowSize;
	private int hiddenLayerNeurons;
	private int predictWindowSize;
	private int numOfVariables;
	private boolean useOutputVariableToPredict;
	private ArrayList<NormalizedField> normalizations;
	private int station;
	private String trainingTimeWindow;
	private String validatingTimeWindow;


	public MLP(boolean useOutputVariableToPredict, int numOfVariables, int inputWindowSize, int hiddenLayerNeurons, int predictWindowSize, ArrayList<NormalizedField> normalizations, int station, String trainingTimeWindow, String validatingTimeWindow) {

		this.inputWindowSize = inputWindowSize;
		this.hiddenLayerNeurons = hiddenLayerNeurons;
		this.predictWindowSize = predictWindowSize;
		this.numOfVariables = numOfVariables;
		this.useOutputVariableToPredict = useOutputVariableToPredict;
		this.normalizations = normalizations;
		this.station = station;
		this.trainingTimeWindow = trainingTimeWindow;
		this.validatingTimeWindow = validatingTimeWindow;

	}

	public TemporalMLDataSet initializeDataSet() {

		TemporalMLDataSet dataSet = new TemporalMLDataSet(inputWindowSize, predictWindowSize);

		dataSet.addDescription(new TemporalDataDescription(TemporalDataDescription.Type.RAW, useOutputVariableToPredict, true));

		for (int x = 0; x < numOfVariables; x++) {
			dataSet.addDescription(new TemporalDataDescription(TemporalDataDescription.Type.RAW, true, false));
		}

		return dataSet;
	}

	public TemporalMLDataSet createTrainingDataSet() throws SQLException, ClassNotFoundException {

		TemporalMLDataSet trainingData = initializeDataSet();
		
		Class.forName("org.sqlite.JDBC");

		Connection connection = DriverManager.getConnection("jdbc:sqlite:database.db");
		connection.setAutoCommit(false);
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("select date(horario, '" + validatingTimeWindow + "') date from ENTRADAS_TRATADAS_24 WHERE ID_ESTACAO = " + station + " order by horario desc LIMIT 1;");
		String date = rs.getString("date");
		rs.close();
		stmt.close();

		stmt = connection.createStatement();
		rs = stmt.executeQuery("SELECT VALOR FROM ENTRADAS_TRATADAS_24 where horario < '" + date + "' AND HORARIO > date('" + date + "', '" + trainingTimeWindow + "') AND ID_ESTACAO = " + station + " order by horario desc");
		
		for (int x = 0; rs.next(); x++) {

			TemporalPoint point = new TemporalPoint(trainingData.getDescriptions().size());
			point.setSequence(x);

			for (int y = 0; y < numOfVariables; y++) {

				point.setData(0, normalizations.get(0).normalize(rs.getFloat("VALOR")));

			}

			trainingData.getPoints().add(point);

		}

		trainingData.generate();
		
		rs.close();
		stmt.close();
		connection.close();

		return trainingData;
	}

	public TemporalMLDataSet createValidadingDataSet() throws SQLException, ClassNotFoundException {

		TemporalMLDataSet trainingData = initializeDataSet();
		
		Class.forName("org.sqlite.JDBC");

		Connection connection = DriverManager.getConnection("jdbc:sqlite:database.db");
		connection.setAutoCommit(false);
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("select date(horario, '" + validatingTimeWindow + "') date from ENTRADAS_TRATADAS_24 WHERE ID_ESTACAO = " + station + " order by horario desc LIMIT 1;");
		String date = rs.getString("date");
		rs.close();
		stmt.close();

		stmt = connection.createStatement();
		rs = stmt.executeQuery("SELECT VALOR FROM ENTRADAS_TRATADAS_24 where horario > '" + date + "' AND ID_ESTACAO = " + station + " order by horario desc");

		for (int x = 0; rs.next(); x++) {

			TemporalPoint point = new TemporalPoint(trainingData.getDescriptions().size());
			point.setSequence(x);

			for (int y = 0; y < numOfVariables; y++) {

				point.setData(0, normalizations.get(0).normalize(rs.getFloat("VALOR")));

			}

			trainingData.getPoints().add(point);

		}

		rs.close();
		stmt.close();
		connection.close();

		trainingData.generate();

		return trainingData;
	}

	public MLRegression trainModel(MLDataSet trainingData, MLDataSet validadingData, String methodName, String methodArchitecture, String trainerName, String trainerArgs) {

		MLMethodFactory methodFactory = new MLMethodFactory();
		MLMethod method = methodFactory.create(methodName, methodArchitecture, trainingData.getInputSize(), trainingData.getIdealSize());

		MLTrainFactory trainFactory = new MLTrainFactory();
		MLTrain train = trainFactory.create(method, trainingData, trainerName, trainerArgs);

		SimpleEarlyStoppingStrategy stop = new SimpleEarlyStoppingStrategy(validadingData, 100);

		train.addStrategy(stop);

		int epoch = 1;

		while (!stop.shouldStop()) {
			train.iteration();
			System.out.println("Epoch #" + epoch + " Validation Error: " + stop.getValidationError());
			epoch++;
		}

		train.finishTraining();

		return (MLRegression) train.getMethod();
	}

	public double predict(MLRegression model) throws IOException, SQLException, ClassNotFoundException {

		TemporalMLDataSet dataSet = initializeDataSet();
		
		Class.forName("org.sqlite.JDBC");

		Connection connection = DriverManager.getConnection("jdbc:sqlite:database.db");
		connection.setAutoCommit(false);
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM ENTRADAS_TRATADAS_24 WHERE ID_ESTACAO = " + station + " order by horario desc");
		
		int sequenceNumber;
		double prediction = -1;

		for (sequenceNumber = 0; rs.next(); sequenceNumber++) {

			// we need a sequence number to sort the data
			TemporalPoint point = new TemporalPoint(dataSet.getDescriptions().size());
			point.setSequence(sequenceNumber);
			for (int y = 0; y < numOfVariables; y++) {

				point.setData(0, normalizations.get(0).normalize(rs.getFloat("VALOR")));

			}
			dataSet.getPoints().add(point);

			// do we have enough data for a prediction yet?
			if (dataSet.getPoints().size() >= dataSet.getInputWindowSize()) {

				// Make sure to use index 1, because the temporal data set is always one ahead
				// of the time slice its encoding. So for RAW data we are really encoding 0.
				MLData modelInput = dataSet.generateInputNeuralData(1);
				MLData modelOutput = model.compute(modelInput);
				prediction = normalizations.get(0).deNormalize(modelOutput.getData(0));

			}

		}
		
		rs.close();
		stmt.close();
		connection.close();

		return prediction;
	}

	public double execute() throws IOException, SQLException, ClassNotFoundException {

		TemporalMLDataSet trainingData = createTrainingDataSet();
		TemporalMLDataSet validadingData = createValidadingDataSet();

		MLRegression model = trainModel(trainingData, validadingData, MLMethodFactory.TYPE_FEEDFORWARD, "?:B->SIGMOID->" + hiddenLayerNeurons + ":B->SIGMOID->?" , MLTrainFactory.TYPE_RPROP, "");

		double prediction = predict(model);

		Encog.getInstance().shutdown();

		return prediction;

	}
}