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
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

public class ShexTests {
    /** Create a Shex test - or return null for "unrecognized" */
    public static Runnable makeShexTest(ManifestEntry entry) {
        Resource testType = entry.getTestType();
        if ( testType == null ) {
            //RepresentationTest
            System.out.println("No test type: " + entry.getName());
            return null;
        }

        if ( testType.equals(ShexT.cRepresentationTest) ) {
            return ()->{};
        }

        Resource action = entry.getAction();
        if ( action == null ) {
            System.out.println("Action expected: " + entry.getName());
            return null;
        }

        if ( action.hasProperty(ShexT.semActs) ) {}
        if ( action.hasProperty(ShexT.shapeExterns) ) {}


        if ( testType == null ) { }

        //action.getProperty(ShexT.trait);

        // One of:

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

        //sht:RepresentationTest

        if ( ! testType.equals(ShexT.cValidationTest) && ! testType.equals(ShexT.cValidationFailure) ) {
            System.err.println("Skip unknown test type for: "+entry.getName());
            return ()->{};
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

        if ( testType.equals(ShexT.cValidationTest) || testType.equals(ShexT.cValidationFailure) ) {
            boolean faiureTest = testType.equals(ShexT.cValidationFailure);

            if ( action.hasProperty(ShexT.shape) ) {
//              action.getProperty(ShexT.schema);
//              action.getProperty(ShexT.shape);
//              action.getProperty(ShexT.data);
//              action.getProperty(ShexT.focus);
                try {
                    Resource schema = action.getProperty(ShexT.schema).getResource();
                    Resource shape = action.getProperty(ShexT.shape).getResource();
                    Resource data = action.getProperty(ShexT.data).getResource();
                    // URI or literal.
                    RDFNode focus = action.getProperty(ShexT.focus).getObject();

                    String fn = schema.getURI();
                    if ( fn.endsWith("schemas/1literalPattern_with_REGEXP_escapes_escaped.shex")
                      ||fn.endsWith("schemas/1literalPattern_with_all_meta.shex") ) {
                        System.err.println("Skipping:  "+entry.getEntry().getLocalName());
                        System.err.println("Shex file: "+fn);
                        return null;
                    }

                } catch (Exception ex) {
                    System.err.println("--");
                    System.err.println(entry.getEntry());
                    System.err.println(ex.getClass().getName());
                    System.err.println(ex.getMessage());
                    ex.printStackTrace();
                    return null;
                }

                try {
                    Runnable r = new RunShexValidation(entry);
                    return r;
                } catch (Exception ex) {
                    //System.err.println(entry.getURI());
                    System.err.println(entry.getAction().getProperty(ShexT.schema).getObject());
                    ex.printStackTrace();
                    return null;
                }
            }

            // Roll into the above?
            if ( action.hasProperty(ShexT.focus) ) {
//              action.getProperty(ShexT.schema); // with start
//              action.getProperty(ShexT.data);
//              action.getProperty(ShexT.focus);
                try {
                    Resource schema = action.getProperty(ShexT.schema).getResource();
                    // No shape.
                    Resource data = action.getProperty(ShexT.data).getResource();
                    // URI or literal.
                    RDFNode focus = action.getProperty(ShexT.focus).getObject();

                    Runnable r = ()->{};
                    return r;
                } catch (Exception ex) {
                    System.err.println(ex.getClass().getName());
                    System.err.println(ex.getMessage());
                    System.err.println(entry.getEntry().getLocalName());
                    return null;
                }
            }

            if ( action.hasProperty(ShexT.map) ) {
//              action.getProperty(ShexT.schema);
//              action.getProperty(ShexT.map);
//              action.getProperty(ShexT.data);
                try {
                    Resource schema = action.getProperty(ShexT.schema).getResource();
                    Resource map = action.getProperty(ShexT.map).getResource();
                    Resource data = action.getProperty(ShexT.data).getResource();
                    Runnable r = ()->{};
                    return r;
                } catch (Exception ex) {
                    System.err.println(ex.getClass().getName());
                    System.err.println(ex.getMessage());
                    System.err.println(entry.getEntry().getLocalName());
                    return null;
                }
            }

            // Unknown.
            System.err.println("Unknown: "+entry.getName());
            return null;

        } else {
            Log.warn("ShexTests", "Skip unknown test type for: "+entry.getName());
            return null;
        }
    }


}
