import java.io.File;
import java.util.HashMap;


public class GradeBot
{

	private static GradeBot instance = new GradeBot();

	private File[] sourceCode;
	private File dataFile;
	private boolean ignoreWhiteSpace;
	private boolean ignoreSymbolCharacters;
	private HashMap<String, Integer> searchStrings;

	private GradeBot()
	{
		sourceCode = null;
		dataFile = null;
		ignoreSymbolCharacters = false;
		ignoreSymbolCharacters = false;
		searchStrings = null;

	}

	public GradeBot getInstance()
	{
		return instance;
	}

	public void setSourceCode(File[] sourceCode)
	{
		this.sourceCode = sourceCode;
	}

	public void setDataFile(File dataFile)
	{
		this.dataFile = dataFile;
	}

	public void setIgnoreWhiteSpace(boolean ignoreWhiteSpace)
	{
		this.ignoreWhiteSpace = ignoreWhiteSpace;
	}

	public void setIgnoreSymbolCharacters(boolean ignoreSymbolCharacters)
	{
		this.ignoreSymbolCharacters = ignoreSymbolCharacters;
	}

	public void setSearchStrings(HashMap<String, Integer> searchStrings)
	{
		this.searchStrings = searchStrings;
	}

	public HashMap<String, String> grade()
	{
		HashMap<String, String> grades = new HashMap<String, String>();
		for (File currentFile : sourceCode)
		{

			Either<Integer, String> result = Grader.grade(currentFile, dataFile, ignoreWhiteSpace, ignoreSymbolCharacters, searchStrings);
			if (result.getLeft() == -1)
			{
				grades.put(currentFile.getAbsolutePath(), result.getRight());
			} else
			{
				grades.put(currentFile.getAbsolutePath(), result.getLeft().toString());
			}
		}
		return grades;
	}
}
