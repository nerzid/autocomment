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


package org.jbpm.compiler.xml;

import org.drools.core.xml.ExtensibleXmlParser;
import java.io.IOException;
import org.xml.sax.InputSource;
import java.io.InputStream;
import java.util.List;
import java.io.Reader;
import org.xml.sax.SAXException;
import javax.xml.parsers.SAXParser;
import org.drools.core.xml.SemanticModules;

public class XmlProcessReader {
    private ExtensibleXmlParser parser;

    private List<Process> processes;

    public XmlProcessReader(final SemanticModules modules, ClassLoader classLoader) {
        this(modules, classLoader, null);
    }

    public XmlProcessReader(final SemanticModules modules, ClassLoader classLoader, final SAXParser parser) {
        if (parser == null) {
            XmlProcessReader.this.parser = new ExtensibleXmlParser();
        } else {
            XmlProcessReader.this.parser = new ExtensibleXmlParser(parser);
        }
        XmlProcessReader.this.parser.setSemanticModules(modules);
        XmlProcessReader.this.parser.setData(new ProcessBuildData());
        XmlProcessReader.this.parser.setClassLoader(classLoader);
    }

    /**
     * Read a <code>Process</code> from a <code>Reader</code>.
     * 
     * @param reader
     *            The reader containing the rule-set.
     * 
     * @return The rule-set.
     */
    public List<Process> read(final Reader reader) throws IOException, SAXException {
        XmlProcessReader.this.processes = ((ProcessBuildData) (XmlProcessReader.this.parser.read(reader))).getProcesses();
        return XmlProcessReader.this.processes;
    }

    /**
     * Read a <code>Process</code> from an <code>InputStream</code>.
     * 
     * @param inputStream
     *            The input-stream containing the rule-set.
     * 
     * @return The rule-set.
     */
    public List<Process> read(final InputStream inputStream) throws IOException, SAXException {
        XmlProcessReader.this.processes = ((ProcessBuildData) (XmlProcessReader.this.parser.read(inputStream))).getProcesses();
        return XmlProcessReader.this.processes;
    }

    /**
     * Read a <code>Process</code> from an <code>InputSource</code>.
     * 
     * @param in
     *            The rule-set input-source.
     * 
     * @return The rule-set.
     */
    public List<Process> read(final InputSource in) throws IOException, SAXException {
        XmlProcessReader.this.processes = ((ProcessBuildData) (XmlProcessReader.this.parser.read(in))).getProcesses();
        return XmlProcessReader.this.processes;
    }

    void setProcesses(final List<Process> processes) {
        XmlProcessReader.this.processes = processes;
    }

    public List<Process> getProcess() {
        return XmlProcessReader.this.processes;
    }

    public ProcessBuildData getProcessBuildData() {
        return ((ProcessBuildData) (XmlProcessReader.this.parser.getData()));
    }
}

