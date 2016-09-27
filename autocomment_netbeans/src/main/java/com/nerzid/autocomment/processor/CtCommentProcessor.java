/*
 * Copyright 2016 nerzid.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nerzid.autocomment.processor;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtComment;

/**
 *
 * @author nerzid
 *
 * Handles Comments. This processor is necessary to create/edit/delete of
 * existing comments. If CtElement(currently only for CtMethod) doesn't have
 * any comment, empty one used instead and will be added to related CtElement
 *
 */
public class CtCommentProcessor extends AbstractProcessor<CtComment> {

        @Override
        public void process(CtComment e) {

        }

        @Override
        public boolean isToBeProcessed(CtComment candidate) {
            return candidate.getCommentType() == CtComment.CommentType.JAVADOC;
        }
        
    }
