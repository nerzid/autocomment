// Generated from CommentTitle.g4 by ANTLR 4.6
package com.nerzid.autocomment.grammar;

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
	 * Enter a parse tree produced by {@link CommentTitleParser#one_verb_rule}.
	 * @param ctx the parse tree
	 */
	void enterOne_verb_rule(CommentTitleParser.One_verb_ruleContext ctx);
	/**
	 * Exit a parse tree produced by {@link CommentTitleParser#one_verb_rule}.
	 * @param ctx the parse tree
	 */
	void exitOne_verb_rule(CommentTitleParser.One_verb_ruleContext ctx);
	/**
	 * Enter a parse tree produced by {@link CommentTitleParser#two_verb_rule}.
	 * @param ctx the parse tree
	 */
	void enterTwo_verb_rule(CommentTitleParser.Two_verb_ruleContext ctx);
	/**
	 * Exit a parse tree produced by {@link CommentTitleParser#two_verb_rule}.
	 * @param ctx the parse tree
	 */
	void exitTwo_verb_rule(CommentTitleParser.Two_verb_ruleContext ctx);
}