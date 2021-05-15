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

import java.util.HashSet;
import java.util.Set;

import org.apache.jena.arq.junit.manifest.ManifestEntry;
import org.apache.jena.atlas.logging.Log;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

public class ShexTests {
    // Test filtering.
    static Set<String> excludes = new HashSet<>();
    static Set<String> includes = new HashSet<>();
    static {

        // Development focus.
        // CLOSED

        includes.add("#1dotRef1_overReferrer,overReferent");

//        includes.add("#open3Onedotclosecard2_fail-p1");
//        includes.add("#open3Onedotclosecard2_pass-p1X2");
//
//        // ## repetitions
//
//        excludes.add("#open3Onedotclosecard2_fail-p1");
//        excludes.add("#open3Onedotclosecard2_pass-p1X2");
//        excludes.add("#open3Onedotclosecard2_pass-p1p2");
//        excludes.add("#open3Onedotclosecard2_pass-p1p3");
//        excludes.add("#open3Onedotclosecard2_pass-p2p3");
//
//        excludes.add("#open3Onedotclosecard23_fail-p1");
//        excludes.add("#open3Onedotclosecard23_pass-p1X2");
//        excludes.add("#open3Onedotclosecard23_pass-p1X3");
//        excludes.add("#open3Onedotclosecard23_pass-p1p2");
//        excludes.add("#open3Onedotclosecard23_pass-p1p3");
//        excludes.add("#open3Onedotclosecard23_pass-p2p3");
//        excludes.add("#open3Onedotclosecard23_pass-p1p2p3");
//        excludes.add("#open3Eachdotclosecard23_pass-p1p2p3X3");

        // ## closed
        excludes.add("#1dotClosed_fail_lower");

        excludes.add("#1dotClosed_fail_higher");
        excludes.add("#FocusIRI2EachBnodeNested2EachIRIRef_pass");
        excludes.add("#FocusIRI2EachBnodeNested2EachIRIRef_fail");

        // ## extra
        excludes.add("#1val1IRIREFExtra1_pass-iri2");
        excludes.add("#1val1IRIREFExtra1Closed_pass-iri2");
        excludes.add("#1val1IRIREFClosedExtra1_pass-iri2");
        excludes.add("#1val2IRIREFPlusExtra1_pass-iri2");
        excludes.add("#1val2IRIREFExtra1_pass-iri-bnode");
        excludes.add("#1val1IRIREFExtra1One_pass-iri2");
        excludes.add("#3EachdotExtra3_pass-iri2");
        excludes.add("#3Eachdot3Extra_pass-iri2");
        excludes.add("#3EachdotExtra3NLex_pass-iri2");

        // ### kind
        excludes.add("#1focusBNODE_dot_pass");
        // ###  value sets
        excludes.add("#focusvs_pass");
        // ### AND focus
        excludes.add("#1focusvsANDIRI_pass");
        // ### OR focus
        excludes.add("#1focusvsORdatatype_pass-val");

        // ### facet (bnodes)
        excludes.add("#1focusLength-dot_pass-bnode-equal");

        excludes.add("#1focusMinLength-dot_pass-bnode-equal");
        excludes.add("#1focusMinLength-dot_pass-bnode-long");
        excludes.add("#1focusMaxLength-dot_pass-bnode-short");
        excludes.add("#1focusMaxLength-dot_pass-bnode-equal");
        excludes.add("#1focusPatternB-dot_pass-bnode-match");
        excludes.add("#1focusPatternB-dot_pass-bnode-long");
        excludes.add("#1focusBNODELength_dot_pass");

        // ###  value reference
        excludes.add("#1valExprRef-IV1_fail-lit-short");
        excludes.add("#1valExprRef-IV1_pass-lit-equal");
        excludes.add("#1valExprRefbnode-IV1_fail-lit-short");
        excludes.add("#1valExprRefbnode-IV1_pass-lit-equal");

        // ## lists
        excludes.add("#1list0PlusDot-manualList_extraArc_Iv1,Iv2,Iv3_fail");

        // ----------
        // [shex] Cyclic.
        excludes.add("#3circRefPlus1_pass-recursiveData");
        excludes.add("#refBNodeORrefIRI_ReflexiveIRI");
        excludes.add("#refBNodeORrefIRI_CyclicIRI_BNode");
        excludes.add("#refBNodeORrefIRI_IntoReflexiveIRI");
        excludes.add("#refBNodeORrefIRI_IntoReflexiveBNode");
        excludes.add("#refBNodeORrefIRI_CyclicIRI_IRI");
        excludes.add("#refBNodeORrefIRI_CyclicIRI_BNode");

        // ----------
        // [shex] Convert to JUnit "ignore" tests
        // Language stem issues and startsWith
//        includes.add("#1val1languageStem_failLAtfrc");
//        includes.add("#1val1languageStemMinuslanguageStem3_passLAtfr-bel");
//        includes.add("#1val1languageStemMinuslanguageStem3_LAtfrc");

        // Come back to:
        // Regexp escape are weird.
        excludes.add("#1literalPattern_with_REGEXP_escapes_pass");
        excludes.add("#1literalPattern_with_REGEXP_escapes_bare_pass");
        excludes.add("#1literalPattern_with_REGEXP_escapes_pass_bare");
        excludes.add("#1literalPattern_with_REGEXP_escapes_bare_pass_escapes");

        // Label preserving blank node tests
        // Others in this block "accidentally" pass.
        // [shex] exclude this as well.
        excludes.add("#1bnodeLength_pass-bnode-equal");
        excludes.add("#1nonliteralLength_pass-bnode-equal");
        excludes.add("#1bnodeMinlength_pass-bnode-equal");
        excludes.add("#1bnodeMinlength_pass-bnode-long");
        excludes.add("#1nonliteralMinlength_pass-bnode-equal");
        excludes.add("#1nonliteralMinlength_pass-bnode-long");
        excludes.add("#1bnodeMaxlength_pass-bnode-short");
        excludes.add("#1bnodeMaxlength_pass-bnode-equal");
        excludes.add("#1nonliteralMaxlength_pass-bnode-short");
        excludes.add("#1nonliteralMaxlength_pass-bnode-equal");

        excludes.add("#1bnodePattern_pass-bnode-match");
        excludes.add("#1bnodePattern_fail-bnode-long");
        excludes.add("#1nonliteralPattern_pass-bnode-match");
        excludes.add("#1nonliteralPattern_pass-bnode-long");

        // [shex] Check these again later.

        // Unclear. Breaks when fix for unicode escapes is applied.
        // Is this because of the incompatible REGEX language choice?
        excludes.add("#1literalPattern_with_REGEXP_escapes_escaped_pass");
        excludes.add("#1literalPattern_with_REGEXP_escapes_escaped_fail_escapes");
        excludes.add("#1literalPattern_with_REGEXP_escapes_escaped_fail_escapes_bare");

        // ?? Contains \u0d00 (ill-formed surrogate pair)
        excludes.add("#1literalPattern_with_all_meta_pass");
        excludes.add("#1literalPattern_with_all_meta_pass-NA");

        // Assumes the _S: bnode in the manifest is the same bnode as the .shex file.
        excludes.add("#bnode1dot_fail-missing");



        // [shex]
        // Validation exclusions - may be needed here.
    //    //---- Exclusions
    //    // java does to support character classes \N (all numbers)
    //    excludes.add("1literalPattern_with_all_meta.shex");
    //
    //    // Unclear. Breaks when fix for unicode escapes is applied.
    //    // Is this because of the incompatible REGEX language choice?
    //    excludes.add("1literalPattern_with_REGEXP_escapes_escaped.shex");
    //
    //    // Regex has a null (unicode codepoint 0000) which is illegal.
    //    excludes.add("1literalPattern_with_ascii_boundaries.shex");
    //
    //    // Contains \ud800 (ill-formed surrogate pair)
    //    excludes.add("1refbnode_with_spanning_PN_CHARS_BASE1.shex");
    //
    //    // Contains \u0d00 (ill-formed surrogate pair)
    //    excludes.add("_all.shex");
    //---- Exclusions

    // -- Explicit inclusions
    // If there are inclusions, only these are executed.

        if ( ! includes.isEmpty() || ! excludes.isEmpty() ) {
            System.err.println("Inclusions = "+includes.size());
            System.err.println("Exclusions = "+excludes.size());
            System.err.println();
        }
    }

    /** Create a Shex test - or return null for "unrecognized" */
    public static Runnable makeShexTest(ManifestEntry entry) {
        if ( ! runTestExclusionsInclusions(entry) )
            return null;

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
                // Expected: ShexT.schema, ShexT.shape, ShexT.data, ShexT.focus
                try {
                    Resource schema = action.getProperty(ShexT.schema).getResource();
                    Resource shape = action.getProperty(ShexT.shape).getResource();
                    Resource data = action.getProperty(ShexT.data).getResource();
                    // URI or literal.
                    RDFNode focus = action.getProperty(ShexT.focus).getObject();
                } catch (Exception ex) {
                    System.err.println("--");
                    System.err.println(entry.getEntry());
                    System.err.println(ex.getClass().getName());
                    System.err.println(ex.getMessage());
                    ex.printStackTrace();
                    return null;
                }

                try {
                    return new ShexValidationTest(entry);
                } catch (Exception ex) {
                    //System.err.println(entry.getURI());
                    System.err.println(entry.getAction().getProperty(ShexT.schema).getObject());
                    ex.printStackTrace();
                    return null;
                }
            }

            // Roll into the above?
            if ( action.hasProperty(ShexT.focus) ) {
                // Expected: ShexT.schema (with start), ShexT.data, ShexT.focus
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
                // Expected: ShexT.schema (with start), ShexT.map, ShexT.data
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

    private static boolean runTestExclusionsInclusions(ManifestEntry entry) {
        String fragment = fragment(entry);
        if ( fragment != null ) {
            if ( !includes.isEmpty() && !includes.contains(fragment) )
                return false;

            if ( excludes.contains(fragment) ) {
                // [shex] Convert to "ignored"
                //System.err.println("Skipping:  "+fragment);
                return false;
            }
        }
        return true;
    }

    // [shex] Migrate
    public static String fragment(ManifestEntry entry) {
        if ( entry.getEntry().isURIResource() )
            return fragment(entry.getEntry().getURI());
        return null;
    }


    // [shex] Migrate
    public static String fragment(String uri) {
        int j = uri.lastIndexOf('#') ;
        String fn = (j >= 0) ? uri.substring(j) : uri ;
        return fn ;
    }

}
