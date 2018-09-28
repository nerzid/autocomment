package com.nerzid.autocomment.grammar;

// Generated from CommentTitle.g4 by ANTLR 4.6
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link CommentTitleParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface CommentTitleVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link CommentTitleParser#rule1}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRule1(CommentTitleParser.Rule1Context ctx);
	/**
	 * Visit a parse tree produced by {@link CommentTitleParser#one_verb_rule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOne_verb_rule(CommentTitleParser.One_verb_ruleContext ctx);
	/**
	 * Visit a parse tree produced by {@link CommentTitleParser#two_verb_rule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTwo_verb_rule(CommentTitleParser.Two_verb_ruleContext ctx);
	/**
	 * Visit a parse tree produced by {@link CommentTitleParser#first_prp_rule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFirst_prp_rule(CommentTitleParser.First_prp_ruleContext ctx);
}