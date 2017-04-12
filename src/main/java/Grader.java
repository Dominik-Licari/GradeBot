import org.mdkt.compiler.InMemoryJavaCompiler;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.*;


class Grader
{

	static Either<Integer, String> grade(File currentFile, File inputFile, File correctOutputFile, boolean ignoreWhiteSpace, boolean ignoreSymbolCharacters, HashMap<String, Integer> searchStrings)
	{
		try
		{
			InputStream sin = System.in;
			PrintStream sout = System.out;

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			PrintStream out = new PrintStream(stream);

			Integer score = 100;
			StringBuilder sourceCodeBuild = new StringBuilder();
			Scanner sourceReader = new Scanner(currentFile);
			while (sourceReader.hasNextLine())
			{
				sourceCodeBuild.append(sourceReader.nextLine());
				sourceCodeBuild.append("\n");
			}
			System.setOut(out);
			String sourceCode = sourceCodeBuild.toString();
			String className = currentFile.getName().replaceFirst("[.][^.]+$", "");
			Class<?> currentCodeToBeGraded;
			Method mainMethod;
			try
			{
				currentCodeToBeGraded = InMemoryJavaCompiler.compile(className, sourceCode);
				mainMethod = currentCodeToBeGraded.getMethod("main", (new String[0]).getClass());
			} catch (Error error)
			{
				return new Either<>(0, "Compilation error");
			}
			if (inputFile != null)
			{
				StringBuilder inFileBuilder = new StringBuilder();
				Scanner inFileReader = new Scanner(inputFile);
				while (inFileReader.hasNextLine())
				{
					inFileBuilder.append(inFileReader.nextLine());
					inFileBuilder.append("\n");
				}
				System.setIn(new StringsInputStream(inFileBuilder.toString()));
			}
			System.setOut(out);
			mainMethod.invoke(currentCodeToBeGraded, (Object) new String[]{});
			String actualResult = stream.toString(Charset.defaultCharset().toString());
			System.setOut(sout);
			System.setIn(sin);
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
			return new Either<>(0, "Compilation error");
		}

	}

	private static class StringsInputStream extends InputStream
	{
		Iterator<Byte> iterator;

		StringsInputStream(String s)
		{
			super();
			ArrayList<Byte> bytes = new ArrayList<>();
			ArrayList<Character> characters = new ArrayList<Character>();
			for (char c : s.toCharArray())
			{
				characters.add(c);
			}
			characters.iterator().forEachRemaining((character ->
			{
				bytes.add((byte)((character.charValue() & 0xFF00)>>8));
				bytes.add((byte)((character.charValue() & 0x00FF)));
			}));
			iterator = bytes.iterator();
		}

		@Override
		public int read() throws IOException
		{
			if (iterator.hasNext())
			return iterator.next();
			return 0;
		}
	}
}




