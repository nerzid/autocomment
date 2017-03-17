package com.nerzid.autocomment.grammar;

// Generated from CommentTitle.g4 by ANTLR 4.5.3
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link CommentTitleParser}.
 */
public interface CommentTitleListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link CommentTitleParser#rule1}.
	 * @param ctx the parse tree
	 */
	void enterRule1(CommentTitleParser.Rule1Context ctx);
	/**
	 * Exit a parse tree produced by {@link CommentTitleParser#rule1}.
	 * @param ctx the parse tree
	 */
	void exitRule1(CommentTitleParser.Rule1Context ctx);
	/**
	 * Enter a parse tree produced by {@link CommentTitleParser#boolean_one_verb_rule}.
	 * @param ctx the parse tree
	 */
	void enterBoolean_one_verb_rule(CommentTitleParser.Boolean_one_verb_ruleContext ctx);
	/**
	 * Exit a parse tree produced by {@link CommentTitleParser#boolean_one_verb_rule}.
	 * @param ctx the parse tree
	 */
	void exitBoolean_one_verb_rule(CommentTitleParser.Boolean_one_verb_ruleContext ctx);
	/**
	 * Enter a parse tree produced by {@link CommentTitleParser#boolean_two_verb_rule}.
	 * @param ctx the parse tree
	 */
	void enterBoolean_two_verb_rule(CommentTitleParser.Boolean_two_verb_ruleContext ctx);
	/**
	 * Exit a parse tree produced by {@link CommentTitleParser#boolean_two_verb_rule}.
	 * @param ctx the parse tree
	 */
	void exitBoolean_two_verb_rule(CommentTitleParser.Boolean_two_verb_ruleContext ctx);
}