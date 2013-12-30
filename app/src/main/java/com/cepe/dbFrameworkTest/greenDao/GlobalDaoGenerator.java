package com.cepe.dbFrameworkTest.greenDao;/*
 * Copyright (C) 2011 Markus Junginger, greenrobot (http://greenrobot.de)
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

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

/**
 * Generates entities and DAOs for the example project DaoExample.
 *
 * Run it as a Java application (not Android).
 *
 * @author Markus
 */
class GlobalDaoGenerator {

    public static void main(String[] args) throws Exception {
        Schema schema = new Schema(1, "com.cepe.dbFrameworkTest.greenDao.gen");

        addPerson(schema);

        new DaoGenerator().generateAll(schema, "../DBFrameworktests/app/src/main/java");
    }

    private static void addPerson(Schema schema) {
        Entity note = schema.addEntity("Person");
        note.addIdProperty();
        note.addStringProperty("firstName").notNull();
        note.addStringProperty("lastName");
    }

}