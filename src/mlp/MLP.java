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

	public MLP(boolean useOutputVariableToPredict, int numOfVariables, int inputWindowSize, int hiddenLayerNeurons, int predictWindowSize, ArrayList<NormalizedField> normalizations) {

		this.inputWindowSize = inputWindowSize;
		this.hiddenLayerNeurons = hiddenLayerNeurons;
		this.predictWindowSize = predictWindowSize;
		this.numOfVariables = numOfVariables;
		this.useOutputVariableToPredict = useOutputVariableToPredict;
		this.normalizations = normalizations;
		
	}
	
	public TemporalMLDataSet initializeDataSet() {
		
		TemporalMLDataSet dataSet = new TemporalMLDataSet(inputWindowSize, predictWindowSize);
		
		dataSet.addDescription(new TemporalDataDescription(TemporalDataDescription.Type.RAW, useOutputVariableToPredict, true));
		
		for (int x = 0; x < numOfVariables; x++) {
			dataSet.addDescription(new TemporalDataDescription(TemporalDataDescription.Type.RAW, true, false));
		}
		
		return dataSet;
	}

	
	public TemporalMLDataSet createDataSet() throws SQLException {
		
		TemporalMLDataSet trainingData = initializeDataSet();

		Connection connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
		connection.setAutoCommit(false);		
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT VALOR FROM ENTRADAS_TRATADAS");
		
		for (int x = 0; rs.next(); x++) {

            TemporalPoint point = new TemporalPoint(trainingData.getDescriptions().size());
            point.setSequence(x);

            //for (int y = 0; y < numOfVariables; y++) {

                point.setData(0, normalizations.get(0).normalize(rs.getFloat("VALOR")));

            //}
            
            trainingData.getPoints().add(point);
            
		}
		
		rs.close();
		
        trainingData.generate();

		return trainingData;
	}

	public TemporalMLDataSet createDataSetFromCSV(File rawFile) {
		
		TemporalMLDataSet trainingData = initializeDataSet();
        ReadCSV csv = new ReadCSV(rawFile.toString(), true, ';');

        for (int x = 0; csv.next(); x++) {

            TemporalPoint point = new TemporalPoint(trainingData.getDescriptions().size());
            point.setSequence(x);

            for (int y = 0; y < numOfVariables; y++) {

                point.setData(y, normalizations.get(y).normalize(csv.getDouble(y)));

            }

            trainingData.getPoints().add(point);

        }
        csv.close();

        trainingData.generate();
        return trainingData;
    }

	public MLRegression trainModel(MLDataSet trainingData, MLDataSet validadingData, String methodName,
			String methodArchitecture, String trainerName, String trainerArgs) {

		MLMethodFactory methodFactory = new MLMethodFactory();
		MLMethod method = methodFactory.create(methodName, methodArchitecture, trainingData.getInputSize(),	trainingData.getIdealSize());

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
		ResultSet rs = stmt.executeQuery("SELECT VALOR FROM ENTRADAS_TRATADAS");
						
		NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.GERMAN);
		double soma = 0;
		int sequenceNumber;
		
		for (sequenceNumber = 0; rs.next(); sequenceNumber++) {
			
			// do we have enough data for a prediction yet?
			if (dataSet.getPoints().size() >= dataSet.getInputWindowSize()) {
				
				// Make sure to use index 1, because the temporal data set is always one ahead
				// of the time slice its encoding.  So for RAW data we are really encoding 0.
				MLData modelInput = dataSet.generateInputNeuralData(1);
				MLData modelOutput = model.compute(modelInput);
				double prediction = normalizations.get(0).deNormalize(modelOutput.getData(0));
				
				// GRAVAR RESULTADO BANCO DE DADOS

				//sequenceNumber
				//csv.getDouble(0)
				//prediction
				//Math.abs(((prediction - rs.getFloat("VALOR")) / rs.getFloat("VALOR")) * 100) //error
				
				soma = soma + Math.abs(((prediction - rs.getFloat("VALOR")) / rs.getFloat("VALOR")) * 100);
				
				// Remove the earliest training element.  Unlike when we produced training data,
				// we do not want to build up a large data set.  We just add enough data points to produce
				// input to the model.
				
				dataSet.getPoints().remove(0);
			}
			
			// we need a sequence number to sort the data
			TemporalPoint point = new TemporalPoint(dataSet.getDescriptions().size());
			point.setSequence(sequenceNumber);
			//for (int y = 0; y < numOfVariables; y++) {

				point.setData(0, normalizations.get(0).normalize(rs.getFloat("VALOR")));

			//}
			dataSet.getPoints().add(point);
		}
		
		// generate the time-boxed data
		//dataSet.generate();
		//return dataSet;
		
		return soma / sequenceNumber;
	}
	
	public double execute() throws IOException, SQLException {
		
    	TemporalMLDataSet trainingData = createDataSet();
    	TemporalMLDataSet validadingData = createDataSet();

        MLRegression model = trainModel(
                trainingData,
                validadingData,
                MLMethodFactory.TYPE_FEEDFORWARD,
                "?:B->SIGMOID->" + hiddenLayerNeurons + ":B->SIGMOID->?",
                MLTrainFactory.TYPE_RPROP,
                "");

        double erro = predict(model);

        Encog.getInstance().shutdown();
        
        return erro;
        
    }
}