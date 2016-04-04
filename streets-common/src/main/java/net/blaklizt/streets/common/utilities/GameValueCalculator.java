package net.blaklizt.streets.common.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created with IntelliJ IDEA.
 * User: tkaviya
 * Date: 11/12/13
 * Time: 12:48 AM
 */
public class GameValueCalculator
{
	public static double getCompoundInterest(double principal, double rate,
											 double numYears, double numTimesPerYear)
	{
		return principal * Math.pow((1 + (rate/ numTimesPerYear)),(numTimesPerYear * numYears));
	}

	public static double getGameCompoundInterest(double principal, double rate,
											 double numYears, double numTimesPerYear)
	{
		double result = principal;
		while (numYears > 0)
		{
			while (numTimesPerYear > 0)
			{
				result = result + (result * rate);
				--numTimesPerYear;
			}
			--numYears;
		}
		return result;
	}

	public static void printUsage()
	{
		System.out.println("===========================================================");
		System.out.println("1. Compound Interest Calculator");
		System.out.println("   [USAGE] 1 {principal} {rate} {numYears} {numTimesPerYear}");
		System.out.println("");
		System.out.println("============================================================");
	}

	public static void main(String args[])
	{
		printUsage();
		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			while (true)
			{
				System.out.println("Please enter your choice from the menu above [numeric]:");
				String input = br.readLine();
				String[] values = input.split(" ");

				try
				{
					int choice = Integer.parseInt(values[0]);
					switch (choice)
					{
						case 1:
						{
							//Compound Interest Calculator
							if (values.length != 5) throw new NumberFormatException();
							else
							{
								for (String value : values)
								{
									//check if all values are numeric
									Double.parseDouble(value);
								}

								System.out.println("Compounded Interest = " +
										getGameCompoundInterest(
												Double.parseDouble(values[1]),
												Double.parseDouble(values[2]),
												Double.parseDouble(values[3]),
												Double.parseDouble(values[4])));
								System.out.println("");
							}
							break;
						}
					}
				}
				catch(NumberFormatException n) {}
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
