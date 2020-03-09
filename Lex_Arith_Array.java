
// ⟨letter⟩ → a | b | ... | z | A | B | ... | Z
// ⟨digit⟩ → 0 | 1 | ... | 9
// ⟨id⟩ → ⟨letter⟩ {⟨letter⟩ | ⟨digit⟩}
// ⟨int⟩ → [+|−] {⟨digit⟩}+
// ⟨float⟩ → [+|−] ( {⟨digit⟩}+ "." {⟨digit⟩} | "." {⟨digit⟩}+ )
// ⟨floatE⟩ → (⟨int⟩ | ⟨float⟩) (e|E) [+|−] {⟨digit⟩}+
// ⟨floatF⟩ → (⟨int⟩ | ⟨float⟩ | ⟨floatE⟩) ("f" | "F")
// ⟨add⟩ → +
// ⟨sub⟩ → −
// ⟨mul⟩ → *
// ⟨div⟩ → /
// ⟨lt⟩ → <
// ⟨le⟩ → "<="
// ⟨gt⟩ → >
// ⟨ge⟩ → ">="
// ⟨eq⟩ → =
// ⟨LParen⟩ → (
// ⟨RParen⟩ → )
// ⟨LBrace⟩ → {
// ⟨RBrace⟩ → }

public abstract class Lex_Arith_Array extends IO {
	public enum State {
		// non-final states ordinal number

		Start, // 0
		Period, // 1
		E, // 2
		EPlusMinus, //

		// final states

		Id, // 4
		Int, // 5
		Float, // 6
		FloatE, // 7
		// Add, // 8
		// Sub, // 9
		Times, // 10
		Div, // 11
		LParen, // 12
		RParen, // 13
		LBrace, // 14
		RBrace, // 15
		Lt, // 16
		Gt, // 17
		Eq, // 18
		Plus, // 19
		Minus, // 20
		Le, // 21
		Ge, // 22
		Keyword_if, // 23
		Keyword_then, // 24
		Keyword_else, // 25
		Keyword_or, // 26
		Keyword_and, // 27
		Keyword_not, // 28
		Keyword_pair, // 29
		Keyword_first, // 30
		Keyword_second, // 31
		Keyword_nil, // 32

		// Keywords
		i, // 33
		f, // 34
		FloatF, UNDEF;

		private boolean isFinal() {
			return (this.compareTo(State.Id) >= 0);
		}
	}

	// By enumerating the non-final states first and then the final states,
	// test for a final state can be done by testing if the state's ordinal number
	// is greater than or equal to that of Id.

	// The following variables of "IO" class are used:


	public static String t; // holds an extracted token
	public static State state; // the current state of the FA

	private static State nextState[][] = new State[36][150];

	

	private static int driver()

	// This is the driver of the FA.
	// If a valid token is found, assigns it to "t" and returns 1.
	// If an invalid token is found, assigns it to "t" and returns 0.
	// If end-of-stream is reached without finding any non-whitespace character,
	// returns -1.

	{
		State nextSt; // the next state of the FA

		t = "";
		state = State.Start;

		if (Character.isWhitespace((char) a))
			a = getChar(); // get the next non-whitespace character
		if (a == -1) // end-of-stream is reached
			return -1;

		while (a != -1) // do the body if "a" is not end-of-stream
		{
			c = (char) a;
			nextSt = nextState[state.ordinal()][a];
			if (nextSt == State.UNDEF) // The FA will halt.
			{
				if (state.isFinal())
					return 1; // valid token extracted
				else // "c" is an unexpected character
				{
					t = t + c;
					a = getNextChar();
					return 0; // invalid token found
				}
			} else // The FA will go on.
			{
				state = nextSt;
				t = t + c;
				a = getNextChar();
			}
		}

		// end-of-stream is reached while a token is being extracted

		if (state.isFinal())
			return 1; // valid token extracted
		else
			return 0; // invalid token found
	} // end driver

	public static void getToken()

	// Extract the next token using the driver of the FA.
	// If an invalid token is found, issue an error message.

	{
		int i = driver();
		if (i == 0)
			displayln(t + " : Lexical Error, invalid token");
	}

	private static void setNextState() {
		for (int s = 0; s <= 35; s++)
			for (int c = 0; c <= 149; c++)
				nextState[s][c] = State.UNDEF;

		for (char c = 'A'; c <= 'Z'; c++) {
			nextState[State.Start.ordinal()][c] = State.Id;
			nextState[State.Id.ordinal()][c] = State.Id;

		}

		for (char c = 'a'; c <= 'z'; c++) {
			nextState[State.Start.ordinal()][c] = State.Id;
			nextState[State.Id.ordinal()][c] = State.Id;


		}

		// In this case we are adding Next step if we get '='after '<' or '>'
		for (char c = '<'; c <= '>'; c++) {

			if (c == '=') {
				nextState[State.Lt.ordinal()][c] = State.Le;
				nextState[State.Gt.ordinal()][c] = State.Ge;

			}

		}

		for (char d = '0'; d <= '9'; d++) {
			// adding steps is (-||+||'.') comes before int
			nextState[State.Plus.ordinal()][d] = State.Int;
			nextState[State.Minus.ordinal()][d] = State.Int;

			nextState[State.Start.ordinal()][d] = State.Int; // Start ===> Int

			nextState[State.Id.ordinal()][d] = State.Id; // Id === Digit==> Id

			nextState[State.Int.ordinal()][d] = State.Int; // int =====>loop====> Int

			nextState[State.Period.ordinal()][d] = State.Float; // '.' ======> float

			nextState[State.Float.ordinal()][d] = State.Float; // float ====>loop===> float

			nextState[State.E.ordinal()][d] = State.FloatE;
			nextState[State.EPlusMinus.ordinal()][d] = State.FloatE;
			nextState[State.FloatE.ordinal()][d] = State.FloatE;
		}

		nextState[State.Start.ordinal()]['+'] = State.Plus;
		nextState[State.Start.ordinal()]['-'] = State.Minus;

		nextState[State.Start.ordinal()]['*'] = State.Times;
		nextState[State.Start.ordinal()]['/'] = State.Div;
		nextState[State.Start.ordinal()]['('] = State.LParen;
		nextState[State.Start.ordinal()][')'] = State.RParen;
		nextState[State.Start.ordinal()]['{'] = State.LBrace;
		nextState[State.Start.ordinal()]['}'] = State.RBrace;
		nextState[State.Start.ordinal()]['<'] = State.Lt;
		nextState[State.Start.ordinal()]['>'] = State.Gt;
		nextState[State.Start.ordinal()]['='] = State.Eq;

		// Adding transition for key world if
		nextState[State.Start.ordinal()]['I'] = State.Start;
		nextState[State.Start.ordinal()]['i'] = State.Id;
		nextState[State.Id.ordinal()]['f'] = State.Keyword_if;
		for (char c = 'a'; c <= 'z'; c++) {
			nextState[State.Keyword_if.ordinal()][c] = State.Id;

		}


		nextState[State.Start.ordinal()]['t'] = State.Id;
		nextState[State.Id.ordinal()]['h'] = State.Keyword_then;
		nextState[State.Keyword_then.ordinal()]['e'] = State.Keyword_then;
		nextState[State.Keyword_then.ordinal()]['n'] = State.Keyword_then;

		for (char c = 'a'; c <= 'z'; c++) {
			nextState[State.Keyword_then.ordinal()][c] = State.Id;

		}




		// Adding trasition to none final state
		nextState[State.Minus.ordinal()]['.'] = State.Period;

		// Adding trasition from Peroid to FloatF
		nextState[State.Period.ordinal()]['F'] = State.FloatF;
		nextState[State.Period.ordinal()]['f'] = State.FloatF;

		// Adding transition from int to Period
		nextState[State.Int.ordinal()]['E'] = State.E;
		nextState[State.Int.ordinal()]['e'] = State.E;

		// Adding trasition from Peroid to E
		nextState[State.Period.ordinal()]['E'] = State.E;
		nextState[State.Period.ordinal()]['e'] = State.E;

		// adding transition from start to peroid on '.'
		nextState[State.Start.ordinal()]['.'] = State.Period;
		nextState[State.Int.ordinal()]['.'] = State.Period;

		nextState[State.Float.ordinal()]['E'] = State.E;
		nextState[State.Float.ordinal()]['e'] = State.E;

		nextState[State.E.ordinal()]['+'] = State.EPlusMinus;
		nextState[State.E.ordinal()]['-'] = State.EPlusMinus;

		// Adding FloatF after FloatE
		nextState[State.FloatE.ordinal()]['F'] = State.FloatF;
		nextState[State.FloatE.ordinal()]['f'] = State.FloatF;

		// Adding FloatF after Float
		nextState[State.Float.ordinal()]['F'] = State.FloatF;
		nextState[State.Float.ordinal()]['f'] = State.FloatF;

		// Adding Float after Int
		nextState[State.Int.ordinal()]['F'] = State.FloatF;
		nextState[State.Int.ordinal()]['f'] = State.FloatF;

	} // end setNextState

	public static void setLex()

	// Sets the nextState array.

	{
		setNextState();
	}

	public static void main(String argv[])

	{
		// argv[0]: input file containing source code using tokens defined above
		// argv[1]: output file displaying a list of the tokens

		setIO(argv[0], argv[1]);
		setLex();

		int i;

		while (a != -1) // while "a" is not end-of-stream
		{
			i = driver(); // extract the next token
			if (i == 1)
				displayln(t + "   : " + state.toString());
			else if (i == 0)
				displayln(t + " : Lexical Error, invalid token");
		}

		closeIO();
	}
}
