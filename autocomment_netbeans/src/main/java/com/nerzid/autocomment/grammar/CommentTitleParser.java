package com.nerzid.autocomment.grammar;

// Generated from CommentTitle.g4 by ANTLR 4.5.3
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class CommentTitleParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NPR=1, V=2, VR=3, PP=4, WS=5;
	public static final int
		RULE_rule1 = 0, RULE_boolean_one_verb_rule = 1, RULE_boolean_two_verb_rule = 2;
	public static final String[] ruleNames = {
		"rule1", "boolean_one_verb_rule", "boolean_two_verb_rule"
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

	@Override
	public String getGrammarFileName() { return "CommentTitle.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public CommentTitleParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class Rule1Context extends ParserRuleContext {
		public TerminalNode V() { return getToken(CommentTitleParser.V, 0); }
		public TerminalNode NPR() { return getToken(CommentTitleParser.NPR, 0); }
		public TerminalNode EOF() { return getToken(CommentTitleParser.EOF, 0); }
		public TerminalNode PP() { return getToken(CommentTitleParser.PP, 0); }
		public Rule1Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rule1; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CommentTitleListener ) ((CommentTitleListener)listener).enterRule1(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CommentTitleListener ) ((CommentTitleListener)listener).exitRule1(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CommentTitleVisitor ) return ((CommentTitleVisitor<? extends T>)visitor).visitRule1(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Rule1Context rule1() throws RecognitionException {
		Rule1Context _localctx = new Rule1Context(_ctx, getState());
		enterRule(_localctx, 0, RULE_rule1);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(6);
			match(V);
			setState(7);
			match(NPR);
			setState(9);
			_la = _input.LA(1);
			if (_la==PP) {
				{
				setState(8);
				match(PP);
				}
			}

			setState(11);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Boolean_one_verb_ruleContext extends ParserRuleContext {
		public TerminalNode V() { return getToken(CommentTitleParser.V, 0); }
		public TerminalNode NPR() { return getToken(CommentTitleParser.NPR, 0); }
		public TerminalNode EOF() { return getToken(CommentTitleParser.EOF, 0); }
		public Boolean_one_verb_ruleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolean_one_verb_rule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CommentTitleListener ) ((CommentTitleListener)listener).enterBoolean_one_verb_rule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CommentTitleListener ) ((CommentTitleListener)listener).exitBoolean_one_verb_rule(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CommentTitleVisitor ) return ((CommentTitleVisitor<? extends T>)visitor).visitBoolean_one_verb_rule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Boolean_one_verb_ruleContext boolean_one_verb_rule() throws RecognitionException {
		Boolean_one_verb_ruleContext _localctx = new Boolean_one_verb_ruleContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_boolean_one_verb_rule);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(13);
			match(V);
			setState(14);
			match(NPR);
			setState(15);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Boolean_two_verb_ruleContext extends ParserRuleContext {
		public List<TerminalNode> V() { return getTokens(CommentTitleParser.V); }
		public TerminalNode V(int i) {
			return getToken(CommentTitleParser.V, i);
		}
		public List<TerminalNode> NPR() { return getTokens(CommentTitleParser.NPR); }
		public TerminalNode NPR(int i) {
			return getToken(CommentTitleParser.NPR, i);
		}
		public TerminalNode EOF() { return getToken(CommentTitleParser.EOF, 0); }
		public Boolean_two_verb_ruleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_boolean_two_verb_rule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CommentTitleListener ) ((CommentTitleListener)listener).enterBoolean_two_verb_rule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CommentTitleListener ) ((CommentTitleListener)listener).exitBoolean_two_verb_rule(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CommentTitleVisitor ) return ((CommentTitleVisitor<? extends T>)visitor).visitBoolean_two_verb_rule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Boolean_two_verb_ruleContext boolean_two_verb_rule() throws RecognitionException {
		Boolean_two_verb_ruleContext _localctx = new Boolean_two_verb_ruleContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_boolean_two_verb_rule);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(17);
			match(V);
			setState(18);
			match(NPR);
			setState(19);
			match(V);
			setState(21);
			_la = _input.LA(1);
			if (_la==NPR) {
				{
				setState(20);
				match(NPR);
				}
			}

			setState(23);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\7\34\4\2\t\2\4\3"+
		"\t\3\4\4\t\4\3\2\3\2\3\2\5\2\f\n\2\3\2\3\2\3\3\3\3\3\3\3\3\3\4\3\4\3\4"+
		"\3\4\5\4\30\n\4\3\4\3\4\3\4\2\2\5\2\4\6\2\2\32\2\b\3\2\2\2\4\17\3\2\2"+
		"\2\6\23\3\2\2\2\b\t\7\4\2\2\t\13\7\3\2\2\n\f\7\6\2\2\13\n\3\2\2\2\13\f"+
		"\3\2\2\2\f\r\3\2\2\2\r\16\7\2\2\3\16\3\3\2\2\2\17\20\7\4\2\2\20\21\7\3"+
		"\2\2\21\22\7\2\2\3\22\5\3\2\2\2\23\24\7\4\2\2\24\25\7\3\2\2\25\27\7\4"+
		"\2\2\26\30\7\3\2\2\27\26\3\2\2\2\27\30\3\2\2\2\30\31\3\2\2\2\31\32\7\2"+
		"\2\3\32\7\3\2\2\2\4\13\27";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}