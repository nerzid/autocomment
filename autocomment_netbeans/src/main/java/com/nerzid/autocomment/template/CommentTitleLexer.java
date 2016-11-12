package com.nerzid.autocomment.template;

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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\69\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2"+
		"\5\2\30\n\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\5\3+\n\3\3\4\3\4\3\4\3\4\3\4\3\4\3\5\6\5\64\n\5\r\5\16\5\65"+
		"\3\5\3\5\2\2\6\3\3\5\4\7\5\t\6\3\2\3\5\2\13\f\17\17\"\"A\2\3\3\2\2\2\2"+
		"\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\3\27\3\2\2\2\5*\3\2\2\2\7,\3\2\2\2"+
		"\t\63\3\2\2\2\13\f\7P\2\2\f\30\7P\2\2\r\16\7P\2\2\16\17\7P\2\2\17\30\7"+
		"U\2\2\20\21\7P\2\2\21\22\7P\2\2\22\30\7R\2\2\23\24\7P\2\2\24\25\7P\2\2"+
		"\25\26\7R\2\2\26\30\7U\2\2\27\13\3\2\2\2\27\r\3\2\2\2\27\20\3\2\2\2\27"+
		"\23\3\2\2\2\30\4\3\2\2\2\31\32\7X\2\2\32+\7D\2\2\33\34\7X\2\2\34\35\7"+
		"D\2\2\35+\7F\2\2\36\37\7X\2\2\37 \7D\2\2 +\7I\2\2!\"\7X\2\2\"#\7D\2\2"+
		"#+\7P\2\2$%\7X\2\2%&\7D\2\2&+\7R\2\2\'(\7X\2\2()\7D\2\2)+\7\\\2\2*\31"+
		"\3\2\2\2*\33\3\2\2\2*\36\3\2\2\2*!\3\2\2\2*$\3\2\2\2*\'\3\2\2\2+\6\3\2"+
		"\2\2,-\7K\2\2-.\7P\2\2./\3\2\2\2/\60\7\"\2\2\60\61\5\3\2\2\61\b\3\2\2"+
		"\2\62\64\t\2\2\2\63\62\3\2\2\2\64\65\3\2\2\2\65\63\3\2\2\2\65\66\3\2\2"+
		"\2\66\67\3\2\2\2\678\b\5\2\28\n\3\2\2\2\6\2\27*\65\3\b\2\2";
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