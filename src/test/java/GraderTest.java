import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;


public class GraderTest
{

	@Test
	public void testGrade() throws IOException
	{
		File testSrc = new File("TestSrc.java");
		//testSrc.deleteOnExit();
		PrintStream testSrcOut = new PrintStream(testSrc);
		testSrcOut.append("public class TestSrc\n" +
				"{\n" +
				"\tpublic static void main(String... args)\n" +
				"\t{\n" +
				"\t\tfor (int i = 0; i < 10; i++)\n" +
				"\t\t{\n" +
				"\t\t\tSystem.out.println(i);\n" +
				"\t\t}\n" +
				"\t}\n" +
				"}");
		testSrcOut.flush();
		File testIn = File.createTempFile("TestInput", "txt");
		testSrc.deleteOnExit();

		File testOut = File.createTempFile("TestOutput", "txt");
		testSrc.deleteOnExit();
		PrintStream testOutOut = new PrintStream(testOut);
		testOutOut.append("1\n" + "2\n" + "3\n" + "4\n" + "5\n" + "6\n" + "7\n" + "8\n" + "9\n" + "10\n");
		testOutOut.flush();

		HashMap<String, Integer> testSearchStrings = new HashMap<>();
		testSearchStrings.put("for\\(*\\)", 1);
		testSearchStrings.put("While\\(*\\)", 2);

		Either<Integer, String> result = Grader.grade(testSrc, testIn, testOut, false, false, testSearchStrings);
		//assertEquals(98, result.getLeft().intValue());
		assertEquals(null, result.getRight());
	}
}
