/**
 * Copyright (C) 2016 Red Hat, Inc. and/or its affiliates
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


package org.jbpm.document.service;

import org.junit.AfterClass;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import java.util.Date;
import org.jbpm.document.Document;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runner.RunWith;
import org.junit.Test;

@RunWith(value = Parameterized.class)
public class DocumentStorageServiceImplTest {
    private static final String STORAGE_PATH_TEST = "target/docs";

    @Parameters(name = "{index}: impl={0}")
    public static Iterable<Object[]> data() {
        System.setProperty("org.jbpm.document.storage", DocumentStorageServiceImplTest.STORAGE_PATH_TEST);
        return Arrays.asList(new Object[][]{ new Object[]{ new org.jbpm.document.service.impl.DocumentStorageServiceImpl() } , new Object[]{ DocumentStorageServiceProvider.get().getStorageService() } });
    }

    private DocumentStorageService documentStorageService;

    public DocumentStorageServiceImplTest(DocumentStorageService documentStorageService) {
        DocumentStorageServiceImplTest.this.documentStorageService = documentStorageService;
    }

    @AfterClass
    public static void cleanupOnce() {
        System.clearProperty("org.jbpm.document.storage");
    }

    @Before
    public void setup() {
        // clean document storage
        File storagePath = new File(DocumentStorageServiceImplTest.STORAGE_PATH_TEST);
        deleteFolder(storagePath);
    }

    protected void deleteFolder(File path) {
        File[] directories = path.listFiles();
        if (directories != null) {
            for (File file : directories) {
                if (file.isDirectory()) {
                    deleteFolder(file);
                } 
                file.delete();
            }
        } 
    }

    @Test
    public void testSaveAndGetDocument() {
        byte[] content = "document content".getBytes();
        Document document = documentStorageService.buildDocument("mydoc", content.length, new Date(), new HashMap<String, String>());
        Assert.assertNotNull(document.getIdentifier());
        documentStorageService.saveDocument(document, content);
        Document fromStorage = documentStorageService.getDocument(document.getIdentifier());
        Assert.assertNotNull(fromStorage);
        Assert.assertEquals(document.getIdentifier(), fromStorage.getIdentifier());
        Assert.assertEquals(document.getName(), fromStorage.getName());
        Assert.assertEquals(content.length, fromStorage.getContent().length);
    }

    @Test
    public void testSaveAndDeleteDocument() {
        byte[] content = "another document content".getBytes();
        Document document = documentStorageService.buildDocument("mydoc", content.length, new Date(), new HashMap<String, String>());
        Assert.assertNotNull(document.getIdentifier());
        documentStorageService.saveDocument(document, content);
        Document fromStorage = documentStorageService.getDocument(document.getIdentifier());
        Assert.assertNotNull(fromStorage);
        Assert.assertEquals(document.getIdentifier(), fromStorage.getIdentifier());
        Assert.assertEquals(document.getName(), fromStorage.getName());
        Assert.assertEquals(content.length, fromStorage.getContent().length);
        documentStorageService.deleteDocument(fromStorage);
        fromStorage = documentStorageService.getDocument(document.getIdentifier());
        Assert.assertNull(fromStorage);
    }

    @Test
    public void testSaveAndDeleteByIdDocument() {
        byte[] content = "yet another document content".getBytes();
        Document document = documentStorageService.buildDocument("mydoc", content.length, new Date(), new HashMap<String, String>());
        Assert.assertNotNull(document.getIdentifier());
        documentStorageService.saveDocument(document, content);
        Document fromStorage = documentStorageService.getDocument(document.getIdentifier());
        Assert.assertNotNull(fromStorage);
        Assert.assertEquals(document.getIdentifier(), fromStorage.getIdentifier());
        Assert.assertEquals(document.getName(), fromStorage.getName());
        Assert.assertEquals(content.length, fromStorage.getContent().length);
        documentStorageService.deleteDocument(fromStorage.getIdentifier());
        fromStorage = documentStorageService.getDocument(document.getIdentifier());
        Assert.assertNull(fromStorage);
    }

    @Test
    public void testSaveAndListDocuments() {
        long lastModified = (System.currentTimeMillis()) - 10000;
        for (int i = 0; i < 10; i++) {
            byte[] content = (i + " another document content").getBytes();
            Document document = documentStorageService.buildDocument(("mydoc" + i), content.length, new Date((lastModified + (i * 1000))), new HashMap<String, String>());
            Assert.assertNotNull(document.getIdentifier());
            documentStorageService.saveDocument(document, content);
        }
        List<Document> docs = documentStorageService.listDocuments(0, 5);
        Assert.assertNotNull(docs);
        Assert.assertEquals(5, docs.size());
        Assert.assertEquals(("mydoc" + 0), docs.get(0).getName());
        Assert.assertEquals(("mydoc" + 1), docs.get(1).getName());
        Assert.assertEquals(("mydoc" + 2), docs.get(2).getName());
        Assert.assertEquals(("mydoc" + 3), docs.get(3).getName());
        Assert.assertEquals(("mydoc" + 4), docs.get(4).getName());
        docs = documentStorageService.listDocuments(1, 5);
        Assert.assertNotNull(docs);
        Assert.assertEquals(5, docs.size());
        Assert.assertEquals(("mydoc" + 5), docs.get(0).getName());
        Assert.assertEquals(("mydoc" + 6), docs.get(1).getName());
        Assert.assertEquals(("mydoc" + 7), docs.get(2).getName());
        Assert.assertEquals(("mydoc" + 8), docs.get(3).getName());
        Assert.assertEquals(("mydoc" + 9), docs.get(4).getName());
        docs = documentStorageService.listDocuments(1, 2);
        Assert.assertNotNull(docs);
        Assert.assertEquals(2, docs.size());
        Assert.assertEquals(("mydoc" + 2), docs.get(0).getName());
        Assert.assertEquals(("mydoc" + 3), docs.get(1).getName());
    }

    @Test
    public void testListDocumentsLessThanPageSize() {
        byte[] content = "yet another document content".getBytes();
        Document document = documentStorageService.buildDocument("mydoc", content.length, new Date(), new HashMap<String, String>());
        Assert.assertNotNull(document.getIdentifier());
        documentStorageService.saveDocument(document, content);
        List<Document> docs = documentStorageService.listDocuments(0, 5);
        Assert.assertNotNull(docs);
        Assert.assertEquals(1, docs.size());
        Assert.assertEquals("mydoc", docs.get(0).getName());
    }
}

