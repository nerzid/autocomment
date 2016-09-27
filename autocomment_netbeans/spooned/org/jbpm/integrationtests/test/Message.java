/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 */


package org.jbpm.integrationtests.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.io.Serializable;

public class Message implements Serializable {
    private static final long serialVersionUID = 4L;

    private String message1 = "One";

    private String message2 = "Two";

    private String message3 = "Three";

    private String message4 = "Four";

    public static final int HELLO = 0;

    public static final int GOODBYE = 1;

    private String message;

    private int status;

    private List<String> list = new ArrayList<String>();

    private int number = 0;

    private Date birthday = new Date();

    private boolean fired = false;

    public Message() {
    }

    public Message(final String msg) {
        Message.this.message = msg;
    }

    public String getMessage() {
        return Message.this.message;
    }

    public void setMessage(final String message) {
        Message.this.message = message;
    }

    public int getStatus() {
        return Message.this.status;
    }

    public void setStatus(final int status) {
        Message.this.status = status;
    }

    public String getMessage1() {
        return Message.this.message1;
    }

    public void setMessage1(final String message1) {
        Message.this.message1 = message1;
    }

    public String getMessage2() {
        return Message.this.message2;
    }

    public void setMessage2(final String message2) {
        Message.this.message2 = message2;
    }

    public String getMessage3() {
        return Message.this.message3;
    }

    public void setMessage3(final String message3) {
        Message.this.message3 = message3;
    }

    public String getMessage4() {
        return Message.this.message4;
    }

    public void setMessage4(final String message4) {
        Message.this.message4 = message4;
    }

    public boolean isFired() {
        return Message.this.fired;
    }

    public void setFired(final boolean fired) {
        Message.this.fired = fired;
    }

    public Date getBirthday() {
        return Message.this.birthday;
    }

    public void setBirthday(final Date birthday) {
        Message.this.birthday = birthday;
    }

    public int getNumber() {
        return Message.this.number;
    }

    public void setNumber(final int number) {
        Message.this.number = number;
    }

    public List<String> getList() {
        return Message.this.list;
    }

    public void setList(final List<String> list) {
        Message.this.list = list;
    }

    public void addToList(final String s) {
        Message.this.list.add(s);
    }
}

