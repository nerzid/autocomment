package com.nerzid.autocomment.grammar;

// Generated from CommentTitle.g4 by ANTLR 4.5.3
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
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\7\u0089\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\5\2\62\n\2\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3E\n\3\3\4\3\4\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\5\4{\n\4\3\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\6\6\6\u0084\n\6\r\6\16\6\u0085\3\6\3\6\2\2\7\3"+
		"\3\5\4\7\5\t\6\13\7\3\2\3\5\2\13\f\17\17\"\"\u00a0\2\3\3\2\2\2\2\5\3\2"+
		"\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\3\61\3\2\2\2\5D\3\2\2\2\7z\3"+
		"\2\2\2\t|\3\2\2\2\13\u0083\3\2\2\2\r\16\7P\2\2\16\62\7P\2\2\17\20\7P\2"+
		"\2\20\21\7P\2\2\21\62\7U\2\2\22\23\7P\2\2\23\24\7P\2\2\24\62\7R\2\2\25"+
		"\26\7P\2\2\26\27\7P\2\2\27\30\7R\2\2\30\62\7U\2\2\31\32\7P\2\2\32\33\7"+
		"P\2\2\33\34\3\2\2\2\34\35\7\"\2\2\35\62\5\3\2\2\36\37\7P\2\2\37 \7P\2"+
		"\2 !\7U\2\2!\"\3\2\2\2\"#\7\"\2\2#\62\5\3\2\2$%\7P\2\2%&\7P\2\2&\'\7R"+
		"\2\2\'(\3\2\2\2()\7\"\2\2)\62\5\3\2\2*+\7P\2\2+,\7P\2\2,-\7R\2\2-.\7U"+
		"\2\2./\3\2\2\2/\60\7\"\2\2\60\62\5\3\2\2\61\r\3\2\2\2\61\17\3\2\2\2\61"+
		"\22\3\2\2\2\61\25\3\2\2\2\61\31\3\2\2\2\61\36\3\2\2\2\61$\3\2\2\2\61*"+
		"\3\2\2\2\62\4\3\2\2\2\63\64\7X\2\2\64E\7D\2\2\65\66\7X\2\2\66\67\7D\2"+
		"\2\67E\7F\2\289\7X\2\29:\7D\2\2:E\7I\2\2;<\7X\2\2<=\7D\2\2=E\7P\2\2>?"+
		"\7X\2\2?@\7D\2\2@E\7R\2\2AB\7X\2\2BC\7D\2\2CE\7\\\2\2D\63\3\2\2\2D\65"+
		"\3\2\2\2D8\3\2\2\2D;\3\2\2\2D>\3\2\2\2DA\3\2\2\2E\6\3\2\2\2FG\7X\2\2G"+
		"{\7D\2\2HI\7X\2\2IJ\7D\2\2J{\7F\2\2KL\7X\2\2LM\7D\2\2M{\7I\2\2NO\7X\2"+
		"\2OP\7D\2\2P{\7P\2\2QR\7X\2\2RS\7D\2\2S{\7R\2\2TU\7X\2\2UV\7D\2\2V{\7"+
		"\\\2\2WX\7X\2\2XY\7D\2\2YZ\3\2\2\2Z[\7\"\2\2[{\5\7\4\2\\]\7X\2\2]^\7D"+
		"\2\2^_\7F\2\2_`\3\2\2\2`a\7\"\2\2a{\5\7\4\2bc\7X\2\2cd\7D\2\2de\7I\2\2"+
		"ef\3\2\2\2fg\7\"\2\2g{\5\7\4\2hi\7X\2\2ij\7D\2\2jk\7P\2\2kl\3\2\2\2lm"+
		"\7\"\2\2m{\5\7\4\2no\7X\2\2op\7D\2\2pq\7R\2\2qr\3\2\2\2rs\7\"\2\2s{\5"+
		"\7\4\2tu\7X\2\2uv\7D\2\2vw\7\\\2\2wx\3\2\2\2xy\7\"\2\2y{\5\7\4\2zF\3\2"+
		"\2\2zH\3\2\2\2zK\3\2\2\2zN\3\2\2\2zQ\3\2\2\2zT\3\2\2\2zW\3\2\2\2z\\\3"+
		"\2\2\2zb\3\2\2\2zh\3\2\2\2zn\3\2\2\2zt\3\2\2\2{\b\3\2\2\2|}\7K\2\2}~\7"+
		"P\2\2~\177\3\2\2\2\177\u0080\7\"\2\2\u0080\u0081\5\3\2\2\u0081\n\3\2\2"+
		"\2\u0082\u0084\t\2\2\2\u0083\u0082\3\2\2\2\u0084\u0085\3\2\2\2\u0085\u0083"+
		"\3\2\2\2\u0085\u0086\3\2\2\2\u0086\u0087\3\2\2\2\u0087\u0088\b\6\2\2\u0088"+
		"\f\3\2\2\2\7\2\61Dz\u0085\3\b\2\2";
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