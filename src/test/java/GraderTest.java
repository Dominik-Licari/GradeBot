import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;


public class GraderTest
{

	@Test
	public void testGrade() throws IOException
	{
		File testSrc = new File("/home/dominik/TestSrc.java");
		//testSrc.deleteOnExit();
		BufferedWriter testSrcOut = new BufferedWriter(new FileWriter(testSrc));
		testSrcOut.write("public class TestSrc\n" +
				"{\n" +
				"\tpublic static void main(String... args)\n" +
				"\t{\n" +
				"\t\tfor (int i = 1; i < 11; i++)\n" +
				"\t\t{\n" +
				"\t\t\tSystem.out.println(i);\n" +
				"\t\t}\n" +
				"\t}\n" +
				"}");
		testSrcOut.close();

		File testIn = File.createTempFile("TestInput", "txt");
		testSrc.deleteOnExit();

		File testOut = File.createTempFile("TestOutput", "txt");
		testSrc.deleteOnExit();
		BufferedWriter testOutOut = new BufferedWriter(new FileWriter(testOut));
		testOutOut.write("1\n" + "2\n" + "3\n" + "4\n" + "5\n" + "6\n" + "7\n" + "8\n" + "9\n" + "10\n");
		testOutOut.close();

		HashMap<String, Integer> testSearchStrings = new HashMap<>();
		testSearchStrings.put("for\\(*\\)", 1);
		testSearchStrings.put("While\\(*\\)", 2);

		Either<Integer, String> result = Grader.grade(testSrc, testIn, testOut, false, false, testSearchStrings);
		//assertEquals(98, result.getLeft().intValue());
		assertEquals(null, result.getRight());
	}
}
