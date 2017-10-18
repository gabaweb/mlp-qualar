package test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import org.encog.util.arrayutil.NormalizationAction;
import org.encog.util.arrayutil.NormalizedField;

import tcc.MLP;

public class TestMLP {

	public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        
        boolean useOutputVariableToPredict = true;
        int numOfVariables = 1;
        int inputWindowSize = 4;
        int hiddenLayerNeurons = 22;
        int predictWindowSize = 1;
        int station = 113;
    	String validatingTimeWindow = "-6 months";
    	String trainingTimeWindow = "-6 months";
        
        ArrayList<NormalizedField> normalizations = new ArrayList<>();
        
        normalizations.add(new NormalizedField(NormalizationAction.Normalize, "MP", 100, 0, 1, 0));
        //normalizations.add(new NormalizedField(NormalizationAction.Normalize, "TEMP", 50, 0, 1, 0));
        //normalizations.add(new NormalizedField(NormalizationAction.Normalize, "UR", 100, 0, 1, 0));
        //normalizations.add(new NormalizedField(NormalizationAction.Normalize, "VV", 10, 0, 1, 0));

        double prediction = new MLP(useOutputVariableToPredict, numOfVariables, inputWindowSize, hiddenLayerNeurons, predictWindowSize, normalizations, station, validatingTimeWindow, trainingTimeWindow).execute();
        
        System.out.println(prediction);
	}

}
