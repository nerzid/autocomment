/**
 * Copyright (C) 2012 Red Hat, Inc. and/or its affiliates
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.jbpm.document.marshalling;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import org.jbpm.document.Document;
import org.jbpm.document.service.DocumentStorageService;
import org.jbpm.document.service.DocumentStorageServiceProvider;
import org.drools.core.common.DroolsObjectInputStream;
import java.io.IOException;
import java.util.Map;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DocumentMarshallingStrategy extends AbstractDocumentMarshallingStrategy {
    private DocumentStorageService documentStorageService;

    public DocumentMarshallingStrategy() {
        documentStorageService = DocumentStorageServiceProvider.get().getStorageService();
    }

    public DocumentMarshallingStrategy(String path) {
        documentStorageService = DocumentStorageServiceProvider.get().getStorageService();
    }

    @Override
    public Document buildDocument(String name, long size, Date lastModified, Map<String, String> params) {
        return documentStorageService.buildDocument(name, size, lastModified, params);
    }

    @Override
    public void write(ObjectOutputStream os, Object object) throws IOException {
        Document document = ((Document) (object));
        if ((document != null) && ((document.getContent()) != null)) {
            documentStorageService.saveDocument(document, document.getContent());
        } 
        os.writeUTF(document.getIdentifier());
        os.writeUTF(document.getClass().getCanonicalName());
        os.writeUTF(document.getLink());
    }

    public Object read(ObjectInputStream os) throws IOException, ClassNotFoundException {
        String objectId = os.readUTF();
        String canonicalName = os.readUTF();
        String link = os.readUTF();
        try {
            Document doc = documentStorageService.getDocument(objectId);
            Document document = ((Document) (Class.forName(canonicalName).newInstance()));
            document.setIdentifier(objectId);
            document.setLink(link);
            document.setName(doc.getName());
            document.setSize(doc.getSize());
            document.setLastModified(doc.getLastModified());
            document.setAttributes(doc.getAttributes());
            document.setContent(doc.getContent());
            return document;
        } catch (Exception e) {
            throw new RuntimeException("Cannot read document", e);
        }
    }

    @Override
    public byte[] marshal(Context context, ObjectOutputStream objectOutputStream, Object o) throws IOException {
        Document document = ((Document) (o));
        documentStorageService.saveDocument(document, document.getContent());
        ByteArrayOutputStream buff = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(buff);
        oos.writeUTF(document.getIdentifier());
        oos.writeUTF(document.getClass().getCanonicalName());
        oos.writeUTF(document.getLink());
        oos.close();
        return buff.toByteArray();
    }

    @Override
    public Object unmarshal(Context context, ObjectInputStream objectInputStream, byte[] object, ClassLoader classLoader) throws IOException, ClassNotFoundException {
        DroolsObjectInputStream is = new DroolsObjectInputStream(new ByteArrayInputStream(object), classLoader);
        // first we read out the object id and class name we stored during marshaling
        String objectId = is.readUTF();
        String canonicalName = is.readUTF();
        String link = is.readUTF();
        Document document = null;
        try {
            document = ((Document) (Class.forName(canonicalName).newInstance()));
            Document storedDoc = documentStorageService.getDocument(objectId);
            document.setIdentifier(storedDoc.getIdentifier());
            document.setName(storedDoc.getName());
            document.setLink(link);
            document.setLastModified(storedDoc.getLastModified());
            document.setSize(storedDoc.getSize());
            document.setAttributes(storedDoc.getAttributes());
            document.setContent(storedDoc.getContent());
        } catch (Exception e) {
            throw new RuntimeException("Cannot read document from storage service", e);
        }
        return document;
    }

    @Override
    public Context createContext() {
        return null;
    }
}

