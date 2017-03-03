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
			StringBuilder sourceCodeBuild = new StringBuilder();
			Scanner sourceReader = new Scanner(currentFile);
			while (sourceReader.hasNextLine())
			{
				sourceCodeBuild.append(sourceReader.nextLine());
				sourceCodeBuild.append("\n");
			}

			String sourceCode = sourceCodeBuild.toString();
			String className = currentFile.getName().replaceFirst("[.][^.]+$", "");

			Class<?> currentCodeToBeGraded = InMemoryJavaCompiler.compile(className, sourceCode);
			Method mainMethod = currentCodeToBeGraded.getMethod("main", (new String[0]).getClass());
			InputStream overriddenIn = new FileInputStream(inputFile);
			File tmp = File.createTempFile("currentOut", null);
			PrintStream overriddenOut = new PrintStream(tmp);

			System.setIn(overriddenIn);
			System.setOut(overriddenOut);
			mainMethod.invoke(currentCodeToBeGraded, (Object) new String[]{});
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

			for (Map.Entry<String, Integer> x : searchStrings.entrySet())
			{
				String pattern = "^[\\W\\w]*" + x.getKey() + "[\\W\\w]*$";
				Integer value = x.getValue();
				if (!sourceCode.matches(pattern))
				{

					score -= value;
				}
			}

			if (!actualResult.equals(expectedResult))
			{
				return new Either<>(-score, "Incorrect output");
			} else
			{
				return new Either<>(score, null);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
			return new Either<>(-1, "Error while compiling file at " + currentFile.getAbsolutePath() + "\nStack trace:\n" + e);


		}
	}


}


