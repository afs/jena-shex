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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.jena.arq.junit.manifest.ManifestEntry;
import org.apache.jena.atlas.lib.FileOps;
import org.apache.jena.atlas.logging.Log;
import org.apache.jena.atlas.web.TypedInputStream;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.system.stream.Locator;
import org.apache.jena.riot.system.stream.LocatorFile;
import org.apache.jena.riot.system.stream.StreamManager;
import shex.expressions.Sx;

public class ShexTests {
    // Test filtering.
    static Set<String> excludes = new HashSet<>();
    static Set<String> includes = new LinkedHashSet<>();
    static boolean dumpTest = false;
    static StreamManager streamMgr = StreamManager.get().clone();


    static {
        setup();
        //Sx.TRACE : true if there are inclusions

        // --- Exclusions - development

        // ---- Exclusions - check
        // [shex] Check these again later.
        excludes.add("#1literalPattern_with_REGEXP_escapes_pass");
        excludes.add("#1literalPattern_with_REGEXP_escapes_bare_pass");
        excludes.add("#1literalPattern_with_REGEXP_escapes_pass_bare");
        excludes.add("#1literalPattern_with_REGEXP_escapes_bare_pass_escapes");

        // Unclear. Breaks when fix for unicode escapes is applied.
        // Is this because of the incompatible REGEX language choice?
        excludes.add("#1literalPattern_with_REGEXP_escapes_escaped_pass");
        excludes.add("#1literalPattern_with_REGEXP_escapes_escaped_fail_escapes");
        excludes.add("#1literalPattern_with_REGEXP_escapes_escaped_fail_escapes_bare");

        // ?? Contains \u0d00 (ill-formed surrogate pair)
        excludes.add("#1literalPattern_with_all_meta_pass");
        excludes.add("#1literalPattern_with_all_meta_pass-NA");

        // ---- Exclusions - never
        excludes.addAll(bNodeLabeltests());
        // Focus bnode - assumes same-label data and manifest.

        // Syntax exclusions - may be needed here.
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

        if ( ! includes.isEmpty() || ! excludes.isEmpty() ) {
            System.err.println("Inclusions    = "+includes.size());
            System.err.println("Exclusions    = "+(excludes.size()-bNodeLabeltests().size()));
            System.err.println("BNode labels  = "+bNodeLabeltests().size());
            System.err.println();
            dumpTest = ! includes.isEmpty();
        }

        if ( ! includes.isEmpty() )
            Sx.TRACE = true;
    }

    private static Collection<String> bNodeLabeltests() {
        // jena-shex operates on post-parsing files.
        //
        // Tests that assume that
        // + blank node labels are preserved after parsing.
        // + the same label in different files is the same blank node.
        // which is not true. In Jena, blank nodes are global.
        // and different between parsing two files. (this is required by RDF).

        Set<String> bNodeLabelTests = new HashSet<>();

        // ### facet (bnodes)
        bNodeLabelTests.add("#1focusLength-dot_fail-bnode-short");
        bNodeLabelTests.add("#1focusLength-dot_pass-bnode-equal");
        bNodeLabelTests.add("#1focusLength-dot_fail-bnode-long");

        bNodeLabelTests.add("#1focusMinLength-dot_pass-bnode-equal");
        bNodeLabelTests.add("#1focusMinLength-dot_pass-bnode-long");
        bNodeLabelTests.add("#1focusMaxLength-dot_pass-bnode-short");
        bNodeLabelTests.add("#1focusMaxLength-dot_pass-bnode-equal");
        bNodeLabelTests.add("#1focusPatternB-dot_pass-bnode-match");
        bNodeLabelTests.add("#1focusPatternB-dot_pass-bnode-long");
        bNodeLabelTests.add("#1focusBNODELength_dot_pass");

        bNodeLabelTests.add("#1bnodeLength_pass-bnode-equal");
        bNodeLabelTests.add("#1bnodeLength_fail-bnode-short");
        bNodeLabelTests.add("#1bnodeLength_pass-bnode-equal");
        bNodeLabelTests.add("#1bnodeLength_fail-bnode-long");
        bNodeLabelTests.add("#1bnodeLength_fail-lit-equal");
        bNodeLabelTests.add("#1bnodeLength_fail-iri-equal");

        bNodeLabelTests.add("#1nonliteralLength_fail-bnode-short");
        bNodeLabelTests.add("#1nonliteralLength_pass-bnode-equal");
        bNodeLabelTests.add("#1nonliteralLength_fail-bnode-long");

        bNodeLabelTests.add("#1bnodeMinlength_pass-bnode-equal");
        bNodeLabelTests.add("#1bnodeMinlength_pass-bnode-long");
        bNodeLabelTests.add("#1nonliteralMinlength_pass-bnode-equal");
        bNodeLabelTests.add("#1nonliteralMinlength_pass-bnode-long");
        bNodeLabelTests.add("#1bnodeMaxlength_pass-bnode-short");
        bNodeLabelTests.add("#1bnodeMaxlength_pass-bnode-equal");
        bNodeLabelTests.add("#1nonliteralMaxlength_pass-bnode-short");
        bNodeLabelTests.add("#1nonliteralMaxlength_pass-bnode-equal");

        bNodeLabelTests.add("#1bnodePattern_pass-bnode-match");
        bNodeLabelTests.add("#1bnodePattern_fail-bnode-short");
        bNodeLabelTests.add("#1bnodePattern_fail-bnode-long");

        bNodeLabelTests.add("#1bnodeMinlength_fail-bnode-short");
        bNodeLabelTests.add("#1bnodeMinlength_pass-bnode-equal");
        bNodeLabelTests.add("#1bnodeMinlength_pass-bnode-long");

        bNodeLabelTests.add("#1bnodePattern_pass-bnode-match");
        bNodeLabelTests.add("#1bnodePattern_fail-bnode-long");

        bNodeLabelTests.add("#1nonliteralPattern_pass-bnode-match");
        bNodeLabelTests.add("#1nonliteralPattern_pass-bnode-long");

        bNodeLabelTests.add("#1nonliteralMinlength_fail-bnode-short");
        bNodeLabelTests.add("#1nonliteralMinlength_pass-bnode-equal");
        bNodeLabelTests.add("#1nonliteralMinlength_pass-bnode-long");

        bNodeLabelTests.add("#1bnodeMaxlength_pass-bnode-short");
        bNodeLabelTests.add("#1bnodeMaxlength_pass-bnode-equal");
        bNodeLabelTests.add("#1bnodeMaxlength_fail-bnode-long");

        bNodeLabelTests.add("#1nonliteralMaxlength_pass-bnode-short");
        bNodeLabelTests.add("#1nonliteralMaxlength_pass-bnode-equal");
        bNodeLabelTests.add("#1nonliteralMaxlength_fail-bnode-long");

        bNodeLabelTests.add("#1valExprRefbnode-IV1_pass-lit-equal");

        bNodeLabelTests.add("#1focusBNODE_dot_fail-iriFocusLabel-equal");
        bNodeLabelTests.add("#1focusBNODE_dot_pass");
        bNodeLabelTests.add("#bnode1dot_pass-others_lexicallyEarlier");


        return bNodeLabelTests;
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
                    return new ShexValidationTest(entry);
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
                    System.err.println("Shex test with map");
                    return null;
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
            // Includes, if present.
            if ( includes.contains(fragment) )
                return true;
            if ( ! includes.isEmpty() )
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

    private static void setup() {
        // Setup StreamManager.
        String places[] = { "files/spec/schemas/", "files/spec/validation/" };


        for ( String dir : places ) {
            Locator alt = new LocatorShexTest(dir);
            streamMgr.addLocator(alt);
        }
        StreamManager.setGlobal(streamMgr);
    }

    // Hunt files.
    static class LocatorShexTest extends LocatorFile {
        public LocatorShexTest(String dir) {
            super(dir);
        }

        @Override
        public TypedInputStream open(String filenameOrURL) {
            String fn = FileOps.basename(filenameOrURL);
            String url = fn.endsWith(".shex")
                    ? fn
                    : fn+".shex";
            return super.open(url);
        }

        @Override
        public String getName() {
            return "LocatorShexTest";
        }
    }
}
