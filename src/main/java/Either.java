public class Either<X, Y>
{
	private X left;
	private Y right;

	public Either(X l, Y r)
	{
		left = l;
		right = r;
	}

	public X getLeft()
	{
		return left;
	}

	public Y getRight()
	{
		return right;
	}
}
