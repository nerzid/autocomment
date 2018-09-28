package com.nerzid.autocomment.grammar;

// Generated from CommentTitle.g4 by ANTLR 4.6
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
	static { RuntimeMetaData.checkVersion("4.6", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NPR=1, V=2, VR=3, PP=4, WS=5;
	public static final int
		RULE_rule1 = 0, RULE_one_verb_rule = 1, RULE_two_verb_rule = 2, RULE_first_prp_rule = 3;
	public static final String[] ruleNames = {
		"rule1", "one_verb_rule", "two_verb_rule", "first_prp_rule"
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
			setState(8);
			match(V);
			setState(9);
			match(NPR);
			setState(11);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PP) {
				{
				setState(10);
				match(PP);
				}
			}

			setState(13);
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

	public static class One_verb_ruleContext extends ParserRuleContext {
		public TerminalNode V() { return getToken(CommentTitleParser.V, 0); }
		public TerminalNode EOF() { return getToken(CommentTitleParser.EOF, 0); }
		public TerminalNode NPR() { return getToken(CommentTitleParser.NPR, 0); }
		public One_verb_ruleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_one_verb_rule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CommentTitleListener ) ((CommentTitleListener)listener).enterOne_verb_rule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CommentTitleListener ) ((CommentTitleListener)listener).exitOne_verb_rule(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CommentTitleVisitor ) return ((CommentTitleVisitor<? extends T>)visitor).visitOne_verb_rule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final One_verb_ruleContext one_verb_rule() throws RecognitionException {
		One_verb_ruleContext _localctx = new One_verb_ruleContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_one_verb_rule);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(15);
			match(V);
			setState(17);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NPR) {
				{
				setState(16);
				match(NPR);
				}
			}

			setState(19);
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

	public static class Two_verb_ruleContext extends ParserRuleContext {
		public List<TerminalNode> V() { return getTokens(CommentTitleParser.V); }
		public TerminalNode V(int i) {
			return getToken(CommentTitleParser.V, i);
		}
		public List<TerminalNode> NPR() { return getTokens(CommentTitleParser.NPR); }
		public TerminalNode NPR(int i) {
			return getToken(CommentTitleParser.NPR, i);
		}
		public TerminalNode EOF() { return getToken(CommentTitleParser.EOF, 0); }
		public Two_verb_ruleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_two_verb_rule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CommentTitleListener ) ((CommentTitleListener)listener).enterTwo_verb_rule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CommentTitleListener ) ((CommentTitleListener)listener).exitTwo_verb_rule(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CommentTitleVisitor ) return ((CommentTitleVisitor<? extends T>)visitor).visitTwo_verb_rule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Two_verb_ruleContext two_verb_rule() throws RecognitionException {
		Two_verb_ruleContext _localctx = new Two_verb_ruleContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_two_verb_rule);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(21);
			match(V);
			setState(22);
			match(NPR);
			setState(23);
			match(V);
			setState(25);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NPR) {
				{
				setState(24);
				match(NPR);
				}
			}

			setState(27);
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

	public static class First_prp_ruleContext extends ParserRuleContext {
		public TerminalNode PP() { return getToken(CommentTitleParser.PP, 0); }
		public TerminalNode EOF() { return getToken(CommentTitleParser.EOF, 0); }
		public List<TerminalNode> NPR() { return getTokens(CommentTitleParser.NPR); }
		public TerminalNode NPR(int i) {
			return getToken(CommentTitleParser.NPR, i);
		}
		public TerminalNode V() { return getToken(CommentTitleParser.V, 0); }
		public First_prp_ruleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_first_prp_rule; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof CommentTitleListener ) ((CommentTitleListener)listener).enterFirst_prp_rule(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof CommentTitleListener ) ((CommentTitleListener)listener).exitFirst_prp_rule(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof CommentTitleVisitor ) return ((CommentTitleVisitor<? extends T>)visitor).visitFirst_prp_rule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final First_prp_ruleContext first_prp_rule() throws RecognitionException {
		First_prp_ruleContext _localctx = new First_prp_ruleContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_first_prp_rule);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(29);
			match(PP);
			setState(31);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
			case 1:
				{
				setState(30);
				match(NPR);
				}
				break;
			}
			setState(34);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==V) {
				{
				setState(33);
				match(V);
				}
			}

			setState(37);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==NPR) {
				{
				setState(36);
				match(NPR);
				}
			}

			setState(39);
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\7,\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\3\2\3\2\3\2\5\2\16\n\2\3\2\3\2\3\3\3\3\5\3\24\n\3\3"+
		"\3\3\3\3\4\3\4\3\4\3\4\5\4\34\n\4\3\4\3\4\3\5\3\5\5\5\"\n\5\3\5\5\5%\n"+
		"\5\3\5\5\5(\n\5\3\5\3\5\3\5\2\2\6\2\4\6\b\2\2-\2\n\3\2\2\2\4\21\3\2\2"+
		"\2\6\27\3\2\2\2\b\37\3\2\2\2\n\13\7\4\2\2\13\r\7\3\2\2\f\16\7\6\2\2\r"+
		"\f\3\2\2\2\r\16\3\2\2\2\16\17\3\2\2\2\17\20\7\2\2\3\20\3\3\2\2\2\21\23"+
		"\7\4\2\2\22\24\7\3\2\2\23\22\3\2\2\2\23\24\3\2\2\2\24\25\3\2\2\2\25\26"+
		"\7\2\2\3\26\5\3\2\2\2\27\30\7\4\2\2\30\31\7\3\2\2\31\33\7\4\2\2\32\34"+
		"\7\3\2\2\33\32\3\2\2\2\33\34\3\2\2\2\34\35\3\2\2\2\35\36\7\2\2\3\36\7"+
		"\3\2\2\2\37!\7\6\2\2 \"\7\3\2\2! \3\2\2\2!\"\3\2\2\2\"$\3\2\2\2#%\7\4"+
		"\2\2$#\3\2\2\2$%\3\2\2\2%\'\3\2\2\2&(\7\3\2\2\'&\3\2\2\2\'(\3\2\2\2()"+
		"\3\2\2\2)*\7\2\2\3*\t\3\2\2\2\b\r\23\33!$\'";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}