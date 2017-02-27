import org.mdkt.compiler.InMemoryJavaCompiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class Grader
{


	static Either<Integer, String> grade(File currentFile, File inputFile, File correctOutputFile, boolean ignoreWhiteSpace, boolean ignoreSymbolCharacters, HashMap<String, Integer> searchStrings)
	{
		try
		{
			Integer score = 100;
			InputStream in = System.in;
			PrintStream out = System.out;
			String sourceCode = currentFile.toString();
			String className = null;
			for (String line : sourceCode.split("\n"))
			{
				if (line.contains("public class"))
				{
					String[] words = line.split(" ");
					if (words[2].contains("{"))
					{
						className = words[2].substring(0, words[2].length() - 1);
					} else
					{
						className = words[2];
					}
					break;
				}
			}
			if (className == null)
			{
				return new Either<>(-1, "File at " + currentFile.getAbsolutePath() + " lacks lacks a public class");
			}


			Class<?> currentCodeToBeGraded = InMemoryJavaCompiler.compile(className, currentFile.toString());
			Method mainMethod = currentCodeToBeGraded.getMethod("main", (new String[0]).getClass());
			InputStream overriddenIn = new FileInputStream(inputFile);
			File tmp = File.createTempFile("currentOut", null);
			PrintStream overriddenOut = new PrintStream(tmp);

			System.setIn(overriddenIn);
			System.setOut(overriddenOut);
			//noinspection ConfusingArgumentToVarargsMethod
			mainMethod.invoke(currentCodeToBeGraded, new String[]{});
			System.setIn(in);
			System.setOut(out);


			String actualResult;
			{
				Scanner tmpOut = new Scanner(tmp);
				tmpOut.useDelimiter("\\z");
				actualResult = tmpOut.next();
			}
			String expectedResult;
			{
				Scanner tmpOut = new Scanner(correctOutputFile);
				tmpOut.useDelimiter("\\z");
				expectedResult = tmpOut.next();
			}

			if (ignoreWhiteSpace)
			{
				StringBuilder whitespaceStripper = new StringBuilder();
				for (char x : actualResult.toCharArray())
				{
					if (!Character.isWhitespace(x))
					{ whitespaceStripper.append(x); }
				}
				actualResult = whitespaceStripper.toString();
			}
			if (ignoreSymbolCharacters)
			{
				StringBuilder symbolStripper = new StringBuilder();
				for (char x : actualResult.toCharArray())
				{
					if (Character.isWhitespace(x) || Character.isLetterOrDigit(x))
					{ symbolStripper.append(x); }
				}
				actualResult = symbolStripper.toString();
			}


			if (actualResult.equals(expectedResult))
			{
				for (Map.Entry<String, Integer> x : searchStrings.entrySet())
				{
					String pattern = x.getKey();
					Integer value = x.getValue();
					if (!sourceCode.matches(pattern))
					{
						score -= value;
					}
				}
			} else
			{
				return new Either<>(-score, "Incorrect output");
			}


			return new Either<>(score, null);
		} catch (Exception e)
		{
			return new Either<>(-1, "Error while compiling file at " + currentFile.getAbsolutePath() + "\nStack trace:\n" + e);
		}
	}
}


