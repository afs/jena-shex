/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package shex.runner;

import org.apache.jena.arq.junit.manifest.ManifestEntry;
import org.apache.jena.atlas.logging.Log;
import org.apache.jena.rdf.model.Resource;

public class ShexTests {
    /** Create a Shex test - or return null for "unrecognized" */
    public static Runnable makeShexTest(ManifestEntry entry) {
        if ( entry.getAction() == null ) {
            System.out.println("Null action: " + entry);
            return null;
        }
        Resource action = entry.getAction();

        if ( action.hasProperty(ShexT.semActs) ) {}
        if ( action.hasProperty(ShexT.shapeExterns) ) {}

        Resource testType = entry.getTestType();


        if ( testType == null ) { }

        //action.getProperty(ShexT.trait);

//        action.getProperty(ShexT.schema);
//        action.getProperty(ShexT.shape);
//        action.getProperty(ShexT.data);
//        action.getProperty(ShexT.focus);
//
//        action.getProperty(ShexT.schema); // with start
//        action.getProperty(ShexT.data);
//        action.getProperty(ShexT.focus);
//
//        action.getProperty(ShexT.schema);
//        action.getProperty(ShexT.map);
//        action.getProperty(ShexT.data);

        if ( ! testType.equals(ShexT.cValidationTest) && ! testType.equals(ShexT.cValidationFailure) ) {
            System.err.println("Skip unknown test type for: "+entry.getName());
            return null;
        }

        // -- Check
        // map or (shape+focus)
        if ( ! action.hasProperty(ShexT.schema) ) {
            System.err.println("Bad: no schema : "+entry.getName());
            return null;
        }

        if ( action.hasProperty(ShexT.map) ) {
            if ( action.hasProperty(ShexT.shape) || action.hasProperty(ShexT.focus) ) {
                System.err.println("Bad: map + (shape or focus) : "+entry.getName());
            }
            if ( !action.hasProperty(ShexT.data) || ! action.hasProperty(ShexT.schema)) {
                System.err.println("Bad: map + no (data+schema) : "+entry.getName());
            }
        } else {
            // Not map
            if ( ! action.hasProperty(ShexT.schema) || ! action.hasProperty(ShexT.data) || ! action.hasProperty(ShexT.focus) )
                System.err.println("Bad: no map, no (scheme/data/focus) : "+entry.getName());
        }
        // -- Check

        if ( testType.equals(ShexT.cValidationTest) ) {

        } else if ( testType.equals(ShexT.cValidationFailure) ) {

        } else {
            Log.warn("ShexTests", "Skip unknown test type for: "+entry.getName());
            return null;
        }

        return null;
    }


}
