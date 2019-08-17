package com.yehia.c3s.bullyalgorithm.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Utils {
	public static String getFormattedTime (long time) {
		DateFormat simple = new SimpleDateFormat("dd/MM/yyyy | HH:mm:ss:SSS"); 
        Date result = new Date(time); 
        return simple.format(result);
	}
	
	public static int[] generateRandomNumersArray(int arraySize, int numbersRange) {
		int[] randomNumbers = new int[arraySize];
		Random randomNumberGenerator = new Random();
		for (int i : randomNumbers) {
			randomNumbers[i] = randomNumberGenerator.nextInt(numbersRange);
		}
		return randomNumbers;
	}
	
	public static int getMinimumNumber(int[] numbers) {
		int minValue = numbers[0];
		  for(int i=1;i<numbers.length;i++){
		    if(numbers[i] < minValue){
			  minValue = numbers[i];
			}
		  }
		  return minValue;
	}
}
