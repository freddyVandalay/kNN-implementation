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
	int rows;
	//Path pathToFile;

	public KNN(){
		setLabel("earns");
		trainingData = importData("adult.train.5fold.csv");
		
		replaceMissingValues(1,trainingData);
		replaceMissingValues(6,trainingData);
		replaceMissingValues(13,trainingData);
		normalise(0,trainingData);
		System.out.println(trainingData[1][0]);
        
		//System.out.println(getMax(0, trainingData));
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
		String [][] data;
		String [] row;
		rows = 0;
		columns = 0;
		String [][] tempData;
		data = new String [rows][columns];
		int w = 0;
		ArrayList<Integer> columnsWithMissingValues = new ArrayList<Integer>();

		while(dataSet.hasNextLine() && w<20){
			rows++;
			//Set the num of columns for the matrix
			row = dataSet.nextLine().split(",");
			if(columns == 0){
				columns = row.length;
				//System.out.println("nr of columns: " + columns);
				//create matrix
				data = new String [rows][columns];
			}
			//Create a new matrix with space for new data
			tempData = new String [rows][columns];
			
	
			//copy data
			for (int i = 0; i < rows-1; i++){
            	for (int j = 0; j < columns; j++){
            		tempData[i][j] = data[i][j];
            		//System.out.println("Copy data: " + data[i][j]);
            	}
        	}
        
       		//Add new data
        	for(int k=0; k<columns;k++){
        		tempData[rows-1][k]=row[k].replaceAll("\\s","");//Ignores whitespace
        		//System.out.println("Added data to temp: " + tempData[rows-1][k]);
        		if(tempData[rows-1][k].equals("?")){
        			if(!columnsWithMissingValues.contains(k)){
        				columnsWithMissingValues.add(k);
        				System.out.println("Missing value on column: " + k);
        			}
        		}
        	}
        	
        	//set new data set variable
        	data = new String [rows][columns];
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
        		w++;
		}
		//System.out.println(columnsWithMissingValues);
	
		return data;
	}

	//performed on 1, 6, 13 
	private void replaceMissingValues(int column, String [][] dataSet){
			String word = findMostCommonAttr(column, dataSet);
		    System.out.println(word);
		    System.out.println(column);
		    for(int k=1; k<rows;k++){
		    	
        		if(trainingData[k][column].equals("?")){
        				System.out.println("Before: " + trainingData[k][column]);
        				trainingData[k][column] = word;
        				System.out.println("Replaced: " + trainingData[k][column] + " " + column + " " + k);
        			
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
		for (int i=1; i<rows; i++) {
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

	//performed on 0,2,4,10,10,12
	private void findMedian(int columnNum, String [][] dataSet){
		ArrayList<Double> column = new ArrayList<Double>();
		for (int j = 1; j < rows; j++){
					double number = Double.parseDouble(dataSet[j][columnNum]);
				
					System.out.println(number);
					if(!column.contains(number)){
						column.add(number);
					}
            		
		}
		Collections.sort(column);
		double median = column.get(rows/column.size());
		//System.out.println(list);
		System.out.println(median);
	}

	private double getMin(int columnNum, String [][] dataSet){

		ArrayList<Double> column = new ArrayList<Double>();
		for (int j = 1; j < rows; j++){
			double number = Double.parseDouble(dataSet[j][columnNum]);
			
			column.add(number);
		}
		Collections.sort(column);
		return column.get(0);
	}

	private double getMax(int columnNum, String [][] dataSet){
		ArrayList<Double> column = new ArrayList<Double>();
		for (int j = 1; j < rows; j++){
			double number = Double.parseDouble(dataSet[j][columnNum]);
		
			column.add(number);
		}
		Collections.sort(column);
		return column.get(column.size()-1);

	}

	private void normalise(int column, String [][] dataSet){
		double min = getMin(column, dataSet);
		double max = getMax(column, dataSet);
		double normValue;
		double originalValue;
		
		for(int i = 1; i <rows; i++){
			originalValue = Double.parseDouble(dataSet[i][column]);
			normValue = (originalValue - min)/(max - min);
			dataSet[i][column] = String.valueOf(normValue);
		}
	}

	private void euclideanDistance(){

	}

	private void fiveCV(String [][] dataSet){
		String[][] fold_1;
		String[][] fold_2;
		String[][] fold_3;
		String[][] fold_4;
		String[][] fold_5;

		String[][] temp_fold_1;
		String[][] temp_fold_2;
		String[][] temp_fold_3;
		String[][] temp_fold_4;
		String[][] temp_fold_5;

		for (int i = 0; i < rows; i++){
        		System.out.println("Data: " + i);
            	for (int j = 0; j < columns; j++){
            		System.out.println(dataSet[i][j]);
            	}
        	}
	}

	public static void main(String args[]){
		new KNN();	
	}

}

