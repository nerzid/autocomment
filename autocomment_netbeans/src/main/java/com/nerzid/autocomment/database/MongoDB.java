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

package com.nerzid.autocomment.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Created by @author nerzid on 15.10.2017.
 */
public class MongoDB {

    public static MongoClient mongoClient = new MongoClient("localhost", 27017);
    public static MongoDatabase db = mongoClient.getDatabase("local");
    public static MongoCollection collection = db.getCollection("sunits_with_comments2");


    public static void insertDocument(Document doc){
        try {
            collection.insertOne(doc);
        } catch (MongoWriteException e) {
            System.out.println("duplicate");
        }
    }

}
