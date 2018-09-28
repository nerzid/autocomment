/*
 *
 *  * Copyright 2016 nerzid.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.nerzid.autocomment.processor;

import com.nerzid.autocomment.nlp.NLPToolkit;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by @author nerzid on 9.10.2017.
 */
public class MethodNameProcessor extends AbstractProcessor<CtMethod> {
    @Override
    public void process(CtMethod element) {
        String methodName = element.getSimpleName();
        String javadoc = element.getDocComment();
        System.out.println(methodName);
        List<CtParameter> params = element.getParameters();

        BsonArray paras = new BsonArray();
        for (CtParameter p : params) {
            paras.add(new BsonDocument("param_type", new BsonString(p.getType().getSimpleName())).append("param_name", new BsonString(p.getSimpleName())));

        }
        NLPToolkit.insertMethodToMongo(methodName, javadoc, paras);
    }
}
