package com.nerzid.autocomment.template;

// Generated from CommentTitle.g4 by ANTLR 4.5.3
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CommentTitleLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NP=1, V=2, PP=3, WS=4;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"NP", "V", "PP", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "NP", "V", "PP", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public CommentTitleLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "CommentTitle.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\6Q\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2\60\n\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3C\n\3\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\5\6\5L\n\5\r\5\16\5M\3\5\3\5\2\2\6\3\3\5\4\7\5\t\6\3\2\3\5\2\13\f\17"+
		"\17\"\"]\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\3/\3\2\2\2\5"+
		"B\3\2\2\2\7D\3\2\2\2\tK\3\2\2\2\13\f\7P\2\2\f\r\7P\2\2\r\16\3\2\2\2\16"+
		"\17\7\"\2\2\17\60\5\3\2\2\20\21\7P\2\2\21\22\7P\2\2\22\23\7U\2\2\23\24"+
		"\3\2\2\2\24\25\7\"\2\2\25\60\5\3\2\2\26\27\7P\2\2\27\30\7P\2\2\30\31\7"+
		"R\2\2\31\32\3\2\2\2\32\33\7\"\2\2\33\60\5\3\2\2\34\35\7P\2\2\35\36\7P"+
		"\2\2\36\37\7R\2\2\37 \7U\2\2 !\3\2\2\2!\"\7\"\2\2\"\60\5\3\2\2#$\7P\2"+
		"\2$\60\7P\2\2%&\7P\2\2&\'\7P\2\2\'\60\7U\2\2()\7P\2\2)*\7P\2\2*\60\7R"+
		"\2\2+,\7P\2\2,-\7P\2\2-.\7R\2\2.\60\7U\2\2/\13\3\2\2\2/\20\3\2\2\2/\26"+
		"\3\2\2\2/\34\3\2\2\2/#\3\2\2\2/%\3\2\2\2/(\3\2\2\2/+\3\2\2\2\60\4\3\2"+
		"\2\2\61\62\7X\2\2\62C\7D\2\2\63\64\7X\2\2\64\65\7D\2\2\65C\7F\2\2\66\67"+
		"\7X\2\2\678\7D\2\28C\7I\2\29:\7X\2\2:;\7D\2\2;C\7P\2\2<=\7X\2\2=>\7D\2"+
		"\2>C\7R\2\2?@\7X\2\2@A\7D\2\2AC\7\\\2\2B\61\3\2\2\2B\63\3\2\2\2B\66\3"+
		"\2\2\2B9\3\2\2\2B<\3\2\2\2B?\3\2\2\2C\6\3\2\2\2DE\7K\2\2EF\7P\2\2FG\3"+
		"\2\2\2GH\7\"\2\2HI\5\3\2\2I\b\3\2\2\2JL\t\2\2\2KJ\3\2\2\2LM\3\2\2\2MK"+
		"\3\2\2\2MN\3\2\2\2NO\3\2\2\2OP\b\5\2\2P\n\3\2\2\2\6\2/BM\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}

    @Override
    public void recover(LexerNoViableAltException e) {
        throw new RuntimeException();
    }

    @Override
    public void recover(RecognitionException re) {
        throw new RuntimeException();
    }
}