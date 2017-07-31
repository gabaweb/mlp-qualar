package mlp;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

    private final int INPUT_WINDOW_SIZE;

    private final int HIDDEN_LAYER_NEURONS;

    private final int PREDICT_WINDOW_SIZE;

    private final int VARIABLES;

    private final boolean INPUT;

    private final ArrayList<NormalizedField> NORMALIZATIONS;

    private final File TRANING_FILE;

    private final File VALIDATION_FILE;

    private final File TEST_FILE;

    private final int EXECUTION;

    private final Date DATE;

    public MLP(boolean INPUT, int VARIABLES, int INPUT_WINDOW_SIZE, int HIDDEN_LAYER_NEURONS, int PREDICT_WINDOW_SIZE, ArrayList<NormalizedField> NORM, String DATASET, int EXECUTION, Date DATE) {

        this.INPUT_WINDOW_SIZE = INPUT_WINDOW_SIZE;
        this.HIDDEN_LAYER_NEURONS = HIDDEN_LAYER_NEURONS;
        this.PREDICT_WINDOW_SIZE = PREDICT_WINDOW_SIZE;
        this.VARIABLES = VARIABLES;
        this.INPUT = INPUT;
        this.NORMALIZATIONS = NORM;
        this.TRANING_FILE = new File(new File(".."), DATASET + "_TREINAMENTO.csv");
        this.VALIDATION_FILE = new File(new File(".."), DATASET + "_VALIDACAO.csv");
        this.TEST_FILE = new File(new File(".."), DATASET + "_TESTE.csv");
        this.EXECUTION = EXECUTION;
        this.DATE = DATE;
    }

    public double execute() throws IOException {
        TemporalMLDataSet trainingData = createTraining(TRANING_FILE);
        TemporalMLDataSet validadingData = createTraining(VALIDATION_FILE);

        MLRegression model = trainModel(
                trainingData,
                validadingData,
                MLMethodFactory.TYPE_FEEDFORWARD,
                "?:B->SIGMOID->" + HIDDEN_LAYER_NEURONS + ":B->SIGMOID->?",
                MLTrainFactory.TYPE_RPROP,
                "");

        double erro = predict(TEST_FILE, model);

        Encog.getInstance().shutdown();

        return erro;
    }

    public TemporalMLDataSet createTraining(File rawFile) {
        TemporalMLDataSet trainingData = initDataSet();
        ReadCSV csv = new ReadCSV(rawFile.toString(), true, ';');

        for (int x = 0; csv.next(); x++) {

            TemporalPoint point = new TemporalPoint(trainingData.getDescriptions().size());
            point.setSequence(x);

            for (int y = 0; y < VARIABLES; y++) {

                point.setData(y, NORMALIZATIONS.get(y).normalize(csv.getDouble(y)));

            }

            trainingData.getPoints().add(point);

        }
        csv.close();

        trainingData.generate();
        return trainingData;
    }

    public TemporalMLDataSet initDataSet() {
        TemporalMLDataSet dataSet = new TemporalMLDataSet(INPUT_WINDOW_SIZE, PREDICT_WINDOW_SIZE);
        dataSet.addDescription(new TemporalDataDescription(TemporalDataDescription.Type.RAW, INPUT, true));
        for (int x = 0; x < VARIABLES; x++) {
            dataSet.addDescription(new TemporalDataDescription(TemporalDataDescription.Type.RAW, true, false));
        }
        return dataSet;
    }

    public MLRegression trainModel(
            MLDataSet trainingData,
            MLDataSet validadingData,
            String methodName,
            String methodArchitecture,
            String trainerName,
            String trainerArgs) {

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

    public double predict(File rawFile, MLRegression model) throws IOException {

        TemporalMLDataSet trainingData = initDataSet();
        ReadCSV csv = new ReadCSV(rawFile.toString(), true, ';');

        DateFormat dateFormat = new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss");
        new File(new File(".."), "\\results\\" + dateFormat.format(DATE)).mkdirs();
        FileWriter arq = new FileWriter(new File(new File(".."), "\\results\\" + dateFormat.format(DATE) + "\\" + dateFormat.format(DATE) + "_" + EXECUTION + ".csv"));
        PrintWriter gravarArq = new PrintWriter(arq);
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMAN);
        double soma = 0;
        int x;
        gravarArq.print("Dia;Real;Previsto;Erro Relativo Percentual\n");

        for (x = 0; csv.next(); x++) {

            if (trainingData.getPoints().size() >= trainingData.getInputWindowSize()) {

                MLData modelInput = trainingData.generateInputNeuralData(1);
                MLData modelOutput = model.compute(modelInput);
                double MP = NORMALIZATIONS.get(0).deNormalize(modelOutput.getData(0));

                gravarArq.print(x + ";" + nf.format(csv.getDouble(0)) + ";" + nf.format(MP) + ";" + nf.format(Math.abs(((MP - csv.getDouble(0)) / csv.getDouble(0)) * 100)) + "\n");
                soma = soma + Math.abs(((MP - csv.getDouble(0)) / csv.getDouble(0)) * 100);

                trainingData.getPoints().remove(0);
            }

            TemporalPoint point = new TemporalPoint(trainingData.getDescriptions().size());
            point.setSequence(x);
            for (int y = 0; y < VARIABLES; y++) {

                point.setData(y, NORMALIZATIONS.get(y).normalize(csv.getDouble(y)));

            }
            trainingData.getPoints().add(point);
        }
        arq.close();
        csv.close();

        trainingData.generate();
        return soma / x;
    }
}