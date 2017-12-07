import java.util.*; 
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.File;

public class KNN {
	File file;
	String [][] trainingData;
	String [][] testData;

	Scanner readFile;
	String label;
	int columns;
	int maxRecords = 10;
	//int rows;
	//Path pathToFile;

	Map<Double, Integer> neighbours;

	public KNN(){
		setLabel("earns");

		trainingData = importData("adult.train.5fold.csv");
		testData = importData("adult.test.csv");
		replaceMissingValues(1,trainingData);
		replaceMissingValues(6,trainingData);
		replaceMissingValues(13,trainingData);
		
		replaceMissingValues(1,testData);
		replaceMissingValues(6,testData);
		replaceMissingValues(13,testData);
		
		//0,2,4,10,10,12
		normalise(0,trainingData);
		normalise(2,trainingData);
		normalise(4,trainingData);
		normalise(10,trainingData);
		normalise(11,trainingData);
		normalise(12,trainingData);
		
		normalise(0,testData);
		normalise(2,testData);
		normalise(4,testData);
		normalise(10,testData);
		normalise(11,testData);
		normalise(12,testData);
	
		//System.out.println(trainingData[1][0]);
        //System.out.println(testData[1][0]);
        //System.out.println(trainingData[1][14]);
		fiveCV(5,trainingData);

        //kayNN(39,5, testData, trainingData);
        //double eu = euclideanDistance(1, trainingData,trainingData);
        //System.out.println(eu);
        //System.out.println(trainingData[1][4]);
		//System.out.println(getMax(0, trainingData, trainingData));
		//findMostCommonAttr(7,trainingData);
		//testData = importData("adult.test.csv");
		//replaceMissingValues(1,testData);
		//replaceMissingValues(6,testData);
		//replaceMissingValues(13,testData);
		//findMedian(2, trainingData);
		System.out.println("Done");
	}


	private String [][] importData(String fileName){
		file = new File(fileName);
		
		
		//Read file to Scanner String
		try {
			readFile = new Scanner(file);
			//scanner.useDelimiter(",");
			return buildDataMatrix(readFile);
		}
		catch(IOException exception){
			System.out.println(exception.toString());
		}
		readFile.close();
		return null;
	}

	private void setLabel(String label){
		label = "";
	}

	private String [][] buildDataMatrix(Scanner dataSet){
		String [][] data = new String[0][0];
		String [] row;
		int loadedRecords = 0;
		//rows = 0;
		columns = 0;
		String [][] tempData;
		data = new String [data.length][columns];
		ArrayList<Integer> columnsWithMissingValues = new ArrayList<Integer>();

		while(dataSet.hasNextLine() && loadedRecords<maxRecords){
			//rows++;
			//Set the num of columns for the matrix
			row = dataSet.nextLine().split(",");
			if(columns == 0){
				columns = row.length;
				//System.out.println("nr of columns: " + columns);
				//create matrix
				data = new String [data.length][columns];
			}
			//Create a new matrix with space for new data
			tempData = new String [data.length+1][columns];
			
	
			//copy data
			for (int i = 0; i < data.length; i++){
            	for (int j = 0; j < columns; j++){
            		tempData[i][j] = data[i][j];
            		//System.out.println("Copy data: " + data[i][j]);
            	}
        	}
        
       		//Add new data
        	for(int k=0; k<columns;k++){
        		tempData[data.length][k]=row[k].replaceAll("\\s","");//Ignores whitespace
        		//System.out.println("Added data to temp: " + tempData[data.length][k]);
        		if(tempData[data.length][k].equals("?")){
        			if(!columnsWithMissingValues.contains(k)){
        				columnsWithMissingValues.add(k);
        				//System.out.println("Missing value on column: " + k);
        			}
        		}
        	}
        	
        	//set new data set variable
        	data = new String [data.length][columns];
        	data=tempData;
        	/*
        	for (int i = 0; i < rows; i++){
        		//System.out.println("Data: " + i);
            	for (int j = 0; j < columns; j++){
            		//System.out.println(data[i][j]);
            	}
        	}
        	*/
        	//System.out.println(rows);
        		loadedRecords++;
		}
		//System.out.println(columnsWithMissingValues);
	
		return data;
	}

	//performed on 1, 6, 13 
	private void replaceMissingValues(int column, String [][] dataSet){
			String word = findMostCommonAttr(column, dataSet);
		    //System.out.println(word);
		    //System.out.println(column);
		    //System.out.println("Length" + dataSet.length);
		    for(int k=1; k<dataSet.length;k++){
		    	
        		if(dataSet[k][column].equals("?")){
        				//System.out.println("Before: " + dataSet[k][column]);
        				trainingData[k][column] = word;
        				//System.out.println("Replaced: " + dataSet[k][column] + " " + column + " " + k);
        			
        		}
        	}
	}

	private void formatData(){

	}

	//performed on 1,3,5,6,7,8,9 and 13
	private String findMostCommonAttr(int column, String [][] dataSet){
		Map<String, Integer> attributeSet = new HashMap<String, Integer>();
		ArrayList<String> attributeNames = new ArrayList<String>();
		String mostCommonAttr = "";
		String temp = "";
		int biggestKey=0;
		for (int i=1; i<dataSet.length; i++) {
			if(attributeSet.containsKey(dataSet[i][column])){
				attributeSet.put(dataSet[i][column], attributeSet.get(dataSet[i][column]) + 1);
			}
			else{
				attributeSet.put(dataSet[i][column], 1);
				attributeNames.add(dataSet[i][column]);
			}
		}
		
		for(int j = 0; j<attributeNames.size(); j++){
			if (attributeSet.get(attributeNames.get(j))>biggestKey){
				biggestKey = attributeSet.get(attributeNames.get(j));
				mostCommonAttr = attributeNames.get(j);
			}	
		}

		//System.out.println(mostCommonAttr);
		return mostCommonAttr;

	}
	/*
	//performed on 0,2,4,10,10,12
	private void findMedian(int columnNum, String [][] dataSet){
		ArrayList<Double> column = new ArrayList<Double>();
		for (int j = 1; j < dataSet.length; j++){
					double number = Double.parseDouble(dataSet[j][columnNum]);
				
					System.out.println(number);
					if(!column.contains(number)){
						column.add(number);
					}
            		
		}
		Collections.sort(column);
		double median = column.get(dataSet.length/column.size());
		//System.out.println(list);
		System.out.println(median);
	}
	*/
	private double getMin(int columnNum, String [][] dataSet){
		ArrayList<Double> column = new ArrayList<Double>();
		//System.out.println(dataSet.length);
		for (int j = 1; j < dataSet.length; j++){
			double number = Double.parseDouble(dataSet[j][columnNum]);
			
			column.add(number);
		}
		Collections.sort(column);
		//System.out.println("Min: " + column.get(0));

		return column.get(0);
	}

	private double getMax(int columnNum, String [][] dataSet){
		ArrayList<Double> column = new ArrayList<Double>();
		for (int j = 1; j < dataSet.length; j++){
			double number = Double.parseDouble(dataSet[j][columnNum]);
		
			column.add(number);
		}
		Collections.sort(column);
		//System.out.println("Max: " + column.get(column.size()-1) + " at columnNum: " + columnNum);
		return column.get(column.size()-1);

	}

	private void normalise(int column, String [][] dataSet){
		double min = getMin(column, dataSet);
		double max = getMax(column, dataSet);
		double normValue;
		double originalValue;
		double constant = 1;
		if(min != max){
		for(int i = 1; i <dataSet.length; i++){
			originalValue = Double.parseDouble(dataSet[i][column]);

			normValue = Math.abs((originalValue - min)/(max - min));
			
			dataSet[i][column] = String.valueOf(normValue);
			//System.out.println("Normalised " + originalValue + " too " + dataSet[i][column]);
		}
		}
	}

	private String kayNN(int k, int row, String[][] record1, String[][] record2){
		//System.out.println("kayNN method:-----------------------");
		//Map<Double, Integer> neighbours = new HashMap<Double, Integer>();
		neighbours = new HashMap<Double, Integer>();
		
		ArrayList<Double> neighbourDistances = new ArrayList<Double>();
		//ArrayList<String> neighbourLabels = new ArrayList<String>();
		
		int nOfNeighbours = neighbourDistances.size();
		for (int i = 1; i < record2.length; i++){
			double distance = euclideanDistance(row, i, record1, record2);
			if(neighbourDistances.size()<k){
				neighbourDistances.add(distance);
				neighbours.put(distance, i);
				Collections.sort(neighbourDistances);
			}	
			else{
				//System.out.println("neighbourDistances is full: " + neighbourDistances.size());
				//System.out.println(distance + " < " + neighbourDistances.get(neighbourDistances.size()-1) );
				if(distance < neighbourDistances.get(neighbourDistances.size()-1)){
					
					neighbours.remove(neighbourDistances.get(neighbourDistances.size()-1));
					neighbours.put(distance, i);
					neighbourDistances.remove(neighbourDistances.size()-1);
					neighbourDistances.add(distance);
					Collections.sort(neighbourDistances);
				}
			}
		}

		//System.out.println("Distances list" + neighbourDistances);
		//System.out.println("neighbours list" + neighbours);
		//System.out.println("neighbours index" + neighbours.get(neighbourDistances.get(neighbourDistances.size()-1)));
		//System.out.println(neighbours.values());
		
		return labelMaker(row, neighbours.values());

	}

	private String labelMaker(int row ,Collection indices){
		HashMap<String, Integer> predictedEarnings = new HashMap<String, Integer>();
		ArrayList<String> label = new ArrayList<String>();
		Iterator <Integer> iterator = neighbours.values().iterator();
		String prediction = "";
		//System.out.println(trainingData[1][14]);
		while(iterator.hasNext()){
			//System.out.println(iterator.next());
			int index = iterator.next();
		
			prediction = trainingData[index][14];
			
			if(!predictedEarnings.containsKey(prediction)){
				predictedEarnings.put(trainingData[index][14], 1);
			}
			else{
				predictedEarnings.put(trainingData[index][14], predictedEarnings.get(trainingData[index][14]) + 1 );
				label.add(trainingData[index][14]);
			}
			
			
		}

		int maxValue = Collections.max(predictedEarnings.values());

		for(int j = 0; j<label.size(); j++){
			if (predictedEarnings.get(label.get(j)) == maxValue ){
				prediction = label.get(j);
			}	
		}
		//System.out.println(predictedEarnings);
		//System.out.println(maxValue);
		//System.out.println("Record is: " + trainingData[row][14] + " AGE: " + trainingData[row][8] + " Row: " + row + " " +  "predictied: " + prediction);

		return prediction;
	}
	private double euclideanDistance(int row, int row2, String [][] testFold, String[][] trainingData){
		//System.out.println("Euclidean method: ---------------------- ");
		double distance = 0;
		for (int i = 0; i < 14; i++){
			//System.out.println("Euclidean i " + i);
			//System.out.println("Euclidean p1: " + testFold[row][i]);
			//System.out.println("Euclidean p2: " + trainingData[row2][i]);
			
			try{
				double dp1 = Double.parseDouble(testFold[row][i]);
				double dp2 = Double.parseDouble(trainingData[row2][i]);
				distance = distance + Math.pow(dp1 - dp2, 2.0);
				//System.out.println("numerical distance: " + Math.pow(dp1 - dp2, 2.0));
			}
			catch(Exception e){

				if(testFold[row][i].equals(trainingData[row2][i])){
					distance++;
					//System.out.println("String distance: " );
				}
			}
		}
		
		return distance;
	}

	private void fiveCV(int folds, String [][] dataSet){
		ArrayList<Integer> current_fold;
		ArrayList<Double> k_accuracy;
		ArrayList<Integer> valuesOf_k = new ArrayList<Integer>();
		//int predictedRight = 0;
		for(int i = 1; i<10; i= i+2){
			valuesOf_k.add(i);
		}
		
		Iterator <Integer> k_iterator = valuesOf_k.iterator();

		while(k_iterator.hasNext()){
			int k = k_iterator.next();
			for(int fold=1; fold <= folds; fold++){
				current_fold = new ArrayList<Integer>();
				for (int i = 1; i < dataSet.length; i++){
					if(dataSet[i][15].equals(Integer.toString(fold))){
						current_fold.add(i);
					}
				}
				//System.out.println("Fold length: " + current_fold.size() + " FOLD COUNT: " + fold);
				Iterator <Integer> iterator = current_fold.iterator();

			
				while(iterator.hasNext() && current_fold.size() != 0){
					int x = iterator.next();
					//System.out.println("Iterator input" + x);
					//kayNN(k ,x, dataSet, dataSet);
					if(dataSet[x][14].equals(kayNN(k ,x, dataSet, dataSet))){
					System.out.println("YOure the best");
					}
				}
			}
		}

	}


	public static void main(String args[]){
		new KNN();	
	}

}

