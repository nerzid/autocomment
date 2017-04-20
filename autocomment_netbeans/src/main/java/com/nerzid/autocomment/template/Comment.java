package com.nerzid.autocomment.template;

import spoon.reflect.declaration.CtElement;

import java.util.Collection;

/**
 * Created by @author nerzid on 16.04.2017.
 */
public class Comment {
    private String text;
    private CtElement target;
    private Collection<CtElement> params;
    private String methodName;

    public Comment() {
    }

    public CtElement getTarget() {
        return target;
    }

    public void setTarget(CtElement target) {
        this.target = target;
    }

    public Collection<CtElement> getParams() {
        return params;
    }

    public void setParams(Collection<CtElement> params) {
        this.params = params;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void append(String s) {
        text += s;
    }

    public void appendAfterSpace(String s) {
        text += " " + s;
    }
}
