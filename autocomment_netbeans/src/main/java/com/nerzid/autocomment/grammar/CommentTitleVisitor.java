package com.nerzid.autocomment.grammar;

// Generated from CommentTitle.g4 by ANTLR 4.5.3
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
	 * Visit a parse tree produced by {@link CommentTitleParser#boolean_one_verb_rule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolean_one_verb_rule(CommentTitleParser.Boolean_one_verb_ruleContext ctx);
	/**
	 * Visit a parse tree produced by {@link CommentTitleParser#boolean_two_verb_rule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBoolean_two_verb_rule(CommentTitleParser.Boolean_two_verb_ruleContext ctx);
}