package com.nerzid.autocomment.grammar;

// Generated from CommentTitle.g4 by ANTLR 4.6
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CommentTitleLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.6", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NPR=1, V=2, VR=3, PP=4, WS=5;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"NPR", "V", "VR", "PP", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, null, null, null, "'IN'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "NPR", "V", "VR", "PP", "WS"
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\7\u008d\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2"+
		"9\n\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\5\3L\n\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\4\3\4\3\4\5\4\u0082\n\4\3\5\3\5\3\5\3\6\6\6\u0088\n\6\r\6\16\6\u0089"+
		"\3\6\3\6\2\2\7\3\3\5\4\7\5\t\6\13\7\3\2\3\5\2\13\f\17\17\"\"\u00a6\2\3"+
		"\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\38\3\2\2\2\5"+
		"K\3\2\2\2\7\u0081\3\2\2\2\t\u0083\3\2\2\2\13\u0087\3\2\2\2\r\16\7P\2\2"+
		"\169\7P\2\2\17\20\7P\2\2\20\21\7P\2\2\219\7U\2\2\22\23\7P\2\2\23\24\7"+
		"P\2\2\249\7R\2\2\25\26\7P\2\2\26\27\7P\2\2\27\30\7R\2\2\309\7U\2\2\31"+
		"\32\7L\2\2\329\7L\2\2\33\34\7P\2\2\34\35\7P\2\2\35\36\3\2\2\2\36\37\7"+
		"\"\2\2\379\5\3\2\2 !\7P\2\2!\"\7P\2\2\"#\7U\2\2#$\3\2\2\2$%\7\"\2\2%9"+
		"\5\3\2\2&\'\7P\2\2\'(\7P\2\2()\7R\2\2)*\3\2\2\2*+\7\"\2\2+9\5\3\2\2,-"+
		"\7P\2\2-.\7P\2\2./\7R\2\2/\60\7U\2\2\60\61\3\2\2\2\61\62\7\"\2\2\629\5"+
		"\3\2\2\63\64\7L\2\2\64\65\7L\2\2\65\66\3\2\2\2\66\67\7\"\2\2\679\5\3\2"+
		"\28\r\3\2\2\28\17\3\2\2\28\22\3\2\2\28\25\3\2\2\28\31\3\2\2\28\33\3\2"+
		"\2\28 \3\2\2\28&\3\2\2\28,\3\2\2\28\63\3\2\2\29\4\3\2\2\2:;\7X\2\2;L\7"+
		"D\2\2<=\7X\2\2=>\7D\2\2>L\7F\2\2?@\7X\2\2@A\7D\2\2AL\7I\2\2BC\7X\2\2C"+
		"D\7D\2\2DL\7P\2\2EF\7X\2\2FG\7D\2\2GL\7R\2\2HI\7X\2\2IJ\7D\2\2JL\7\\\2"+
		"\2K:\3\2\2\2K<\3\2\2\2K?\3\2\2\2KB\3\2\2\2KE\3\2\2\2KH\3\2\2\2L\6\3\2"+
		"\2\2MN\7X\2\2N\u0082\7D\2\2OP\7X\2\2PQ\7D\2\2Q\u0082\7F\2\2RS\7X\2\2S"+
		"T\7D\2\2T\u0082\7I\2\2UV\7X\2\2VW\7D\2\2W\u0082\7P\2\2XY\7X\2\2YZ\7D\2"+
		"\2Z\u0082\7R\2\2[\\\7X\2\2\\]\7D\2\2]\u0082\7\\\2\2^_\7X\2\2_`\7D\2\2"+
		"`a\3\2\2\2ab\7\"\2\2b\u0082\5\7\4\2cd\7X\2\2de\7D\2\2ef\7F\2\2fg\3\2\2"+
		"\2gh\7\"\2\2h\u0082\5\7\4\2ij\7X\2\2jk\7D\2\2kl\7I\2\2lm\3\2\2\2mn\7\""+
		"\2\2n\u0082\5\7\4\2op\7X\2\2pq\7D\2\2qr\7P\2\2rs\3\2\2\2st\7\"\2\2t\u0082"+
		"\5\7\4\2uv\7X\2\2vw\7D\2\2wx\7R\2\2xy\3\2\2\2yz\7\"\2\2z\u0082\5\7\4\2"+
		"{|\7X\2\2|}\7D\2\2}~\7\\\2\2~\177\3\2\2\2\177\u0080\7\"\2\2\u0080\u0082"+
		"\5\7\4\2\u0081M\3\2\2\2\u0081O\3\2\2\2\u0081R\3\2\2\2\u0081U\3\2\2\2\u0081"+
		"X\3\2\2\2\u0081[\3\2\2\2\u0081^\3\2\2\2\u0081c\3\2\2\2\u0081i\3\2\2\2"+
		"\u0081o\3\2\2\2\u0081u\3\2\2\2\u0081{\3\2\2\2\u0082\b\3\2\2\2\u0083\u0084"+
		"\7K\2\2\u0084\u0085\7P\2\2\u0085\n\3\2\2\2\u0086\u0088\t\2\2\2\u0087\u0086"+
		"\3\2\2\2\u0088\u0089\3\2\2\2\u0089\u0087\3\2\2\2\u0089\u008a\3\2\2\2\u008a"+
		"\u008b\3\2\2\2\u008b\u008c\b\6\2\2\u008c\f\3\2\2\2\7\28K\u0081\u0089\3"+
		"\b\2\2";
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