package com.nerzid.autocomment.template;

import com.nerzid.autocomment.nlp.Tokenizer;
import com.nerzid.autocomment.sunit.SUnit;
import com.nerzid.autocomment.sunit.VoidReturnSUnit;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;

import java.util.Collection;
import java.util.List;

/**
 * Created by @author nerzid on 16.04.2017.
 */
public class VoidReturnSUnitCT extends SUnitCommentTemplate {

    public VoidReturnSUnitCT(String comment) {
        super(comment);
    }

    public VoidReturnSUnitCT(SUnit sunit) {
        super(sunit);
    }

    // e.g. VBZ NN - appendString()
    public String getCommentWithOneVerb(VoidReturnSUnit voidReturnSUnit) {
        String commentStr = "";
        CtElement element = voidReturnSUnit.getElement();
        if (element instanceof CtInvocation) {
            CtInvocation invoc = (CtInvocation) element;
            String verb = invoc.getExecutable().getSimpleName();
            String target = invoc.getTarget().getType().getSimpleName();
            List<CtExpression> params = invoc.getArguments();

            List<String> verbs = Tokenizer.split(verb);
            verb = verbs.get(0);
            if (verbs.size() > 1) {
                verb += " " + verbs.get(1);
            }

            commentStr = verb + " " + params.get(0).getType().getSimpleName() + "{" +
                    params.get(0).toString() + "}"
                    + " to " + target + "{" + invoc.getTarget().toString() + "}";
        }

        return commentStr;
    }
}
