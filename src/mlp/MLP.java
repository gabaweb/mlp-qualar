package mlp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
import org.encog.util.csv.ReadCSV;

public class MLP {

	private int inputWindowSize;
	private int hiddenLayerNeurons;
	private int predictWindowSize;
	private int numOfVariables;
	private boolean useOutputVariableToPredict;
	private ArrayList<NormalizedField> normalizations;
	private String station;

	public MLP(boolean useOutputVariableToPredict, int numOfVariables, int inputWindowSize, int hiddenLayerNeurons, int predictWindowSize, ArrayList<NormalizedField> normalizations, String station) {

		this.inputWindowSize = inputWindowSize;
		this.hiddenLayerNeurons = hiddenLayerNeurons;
		this.predictWindowSize = predictWindowSize;
		this.numOfVariables = numOfVariables;
		this.useOutputVariableToPredict = useOutputVariableToPredict;
		this.normalizations = normalizations;
		this.station = station;

	}

	public TemporalMLDataSet initializeDataSet() {

		TemporalMLDataSet dataSet = new TemporalMLDataSet(inputWindowSize, predictWindowSize);

		dataSet.addDescription(new TemporalDataDescription(TemporalDataDescription.Type.RAW, useOutputVariableToPredict, true));

		for (int x = 0; x < numOfVariables; x++) {
			dataSet.addDescription(new TemporalDataDescription(TemporalDataDescription.Type.RAW, true, false));
		}

		return dataSet;
	}

	public TemporalMLDataSet createTrainingDataSet() throws SQLException {

		TemporalMLDataSet trainingData = initializeDataSet();

		Connection connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
		connection.setAutoCommit(false);
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("select datetime(horario, '-1 year') lastdatetime from ENTRADAS_TRATADAS_DIARIO WHERE ID_ESTACAO = " + station + " order by horario desc LIMIT 1;");

		String lastdatetime = rs.getString("lastdatetime");

		stmt = connection.createStatement();
		rs = stmt.executeQuery("SELECT VALOR FROM ENTRADAS_TRATADAS_DIARIO where horario < '" + lastdatetime + "' AND ID_ESTACAO = " + station + " order by horario desc");

		for (int x = 0; rs.next(); x++) {

			TemporalPoint point = new TemporalPoint(trainingData.getDescriptions().size());
			point.setSequence(x);

			for (int y = 0; y < numOfVariables; y++) {

				point.setData(0, normalizations.get(0).normalize(rs.getFloat("VALOR")));

			}

			trainingData.getPoints().add(point);

		}

		rs.close();

		trainingData.generate();

		return trainingData;
	}

	public TemporalMLDataSet createValidadingDataSet() throws SQLException {

		TemporalMLDataSet trainingData = initializeDataSet();

		Connection connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
		connection.setAutoCommit(false);
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("select datetime(horario, '-1 year') lastdatetime from ENTRADAS_TRATADAS_DIARIO WHERE ID_ESTACAO = " + station + " order by horario desc LIMIT 1;");

		String lastdatetime = rs.getString("lastdatetime");

		stmt = connection.createStatement();
		rs = stmt.executeQuery("SELECT VALOR FROM ENTRADAS_TRATADAS_DIARIO where horario > '" + lastdatetime + "' AND ID_ESTACAO = " + station + " order by horario desc");

		for (int x = 0; rs.next(); x++) {

			TemporalPoint point = new TemporalPoint(trainingData.getDescriptions().size());
			point.setSequence(x);

			for (int y = 0; y < numOfVariables; y++) {

				point.setData(0, normalizations.get(0).normalize(rs.getFloat("VALOR")));

			}

			trainingData.getPoints().add(point);

		}

		rs.close();

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

	public double predict(MLRegression model) throws IOException, SQLException {

		TemporalMLDataSet dataSet = initializeDataSet();

		Connection connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
		connection.setAutoCommit(false);
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT * FROM ENTRADAS_TRATADAS_DIARIO WHERE ID_ESTACAO = " + station + " order by horario desc");

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

		return prediction;
	}

	public double execute() throws IOException, SQLException {

		TemporalMLDataSet trainingData = createTrainingDataSet();
		TemporalMLDataSet validadingData = createValidadingDataSet();

		MLRegression model = trainModel(trainingData, validadingData, MLMethodFactory.TYPE_FEEDFORWARD, "?:B->SIGMOID->" + hiddenLayerNeurons + ":B->SIGMOID->?", MLTrainFactory.TYPE_RPROP, "");

		double prediction = predict(model);

		Encog.getInstance().shutdown();

		return prediction;

	}
}