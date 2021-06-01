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

package dev;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import org.apache.jena.atlas.io.IO;
import org.apache.jena.atlas.lib.IRILib;
import org.apache.jena.atlas.lib.StrUtils;
import org.apache.jena.atlas.logging.LogCtl;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RIOT;
import org.apache.jena.riot.out.NodeFmtLib;
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.riot.system.PrefixMapFactory;
import org.apache.jena.shex.*;
import org.apache.jena.shex.eval.ShapeEval;
import org.apache.jena.shex.parser.ShExCompactParser;
import org.apache.jena.shex.parser.ShexParser;
import org.apache.jena.shex.sys.ValidationContext;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.sparql.sse.SSE;
import org.apache.jena.sys.JenaSystem;

public class DevShex {

    static {
        JenaSystem.init();
        LogCtl.setLog4j2();
        RIOT.getContext().set(RIOT.symTurtleDirectiveStyle, "sparql");
    }

    static String strParserTest = StrUtils.strjoinNL
            (""
             ,"<http://ex/S> {"
             ," <http://all.example/p1> LITERAL LENGTH 5 LENGTH 6"
             ,"}"
            );

    public static void main(String[] args) {
        runOne();
        System.exit(0);
//        String s = StrUtils.strjoinNL
//                ("<http://data.example/#n1> @ <http://data.example/#S2>"
//                ,"\"chat\"@en-fr@<http://...S3>"
//                ,"{FOCUS a <http://schema.example/Some/Type>}@START"
//                ,"{_ <http://...p3> FOCUS}@START"
//                 );
//        ShexShapeMap map = parseShapeMap(s);
//        map.entries().forEach(System.out::println);
//        System.exit(0);

        //partition();

        parsePrint();
        //parsePrintFile("file:///home/afs/ASF/shapes/jena-shex/files/spec/schemas/2OneInclude1.shex");
        //parsePrintFile("file:///home/afs/ASF/shapes/jena-shex/files/spec/schemas/1NOTvs.shex");
        //ShapeEvalEachOf.DEBUG = true;
        //validate();
        //validate2();
        //validate_eric_wiki();
        //validateByMap();
    }

    public static void runOne() {

//        Schema:   file:///home/afs/ASF/shapes/jena-shex/files/spec/validation/nPlus1.shex
//            Data:     file:///home/afs/ASF/shapes/jena-shex/files/spec/validation/nPlus1.ttl
//            Shape:    <http://a.example.org/S>
//            Focus:    <file:///home/afs/ASF/shapes/jena-shex/files/spec/validation/x>
//            Positive: true



        String fnShapes = "nPlus1.shex";
        String fnData = "nPlus1.ttl";

//        Node shapeRef = SSE.parseNode("<http://a.example.org/S>");
//        Node focus = SSE.parseNode("<file:///home/afs/ASF/shapes/jena-shex/files/spec/validation/x>");
//
//        String DIR = "file:///home/afs/ASF/shapes/jena-shex/files/spec/validation/";
//        Graph data = RDFDataMgr.loadGraph(DIR+fnData);
//        ShexShapes shapes = Shex.readShapes(DIR+fnShapes);

        Graph data = SSE.parseGraph("(graph (:s :a '1') (:s :a '2'))");
        data.getPrefixMapping().setNsPrefix("", "http://example/");


        Node shapeRef = SSE.parseNode(":S");
        Node focus = SSE.parseNode(":s");
        String shape = StrUtils.strjoinNL
                ("BASE <http://example/>"
                 ,"PREFIX : <http://example/>"
                 ,""
                 ,"<S> { (:a .+ | :a .); :a . }"
                 //,"<S> { :a .*; (:a .+ | :a .); :a . }"
                 //,"<S> { :p1 . |  ( :p2 . ; :p3 . ; :p1 .? ) }"
                 //,"<S> { :p1 . }"
                        );
        ShexShapes shapes = Shex.shapesFromString(shape);
        ShapeEval.debug(true);

        RDFDataMgr.write(System.out,  data,  Lang.TTL);
        System.out.println("--");
//        String s = IO.readWholeFileAsUTF8(IRILib.IRIToFilename(DIR+fnShapes));
//        System.out.print(s);
//        System.out.println("--");
        Shex.printShapes(shapes);
        System.out.println("--");


        ValidationReport report = ShexValidation.validate(data, shapes, shapeRef, focus);
        boolean b = report.conforms();
        System.out.println(b);
    }

    private static void validateByMap() {
        String s = StrUtils.strjoinNL
                ("PREFIX : <http://example/>"
                 ,":s @ :S1"
//                 ,"\"chat\"@en-fr@<http://...S3>"
                 ,"{FOCUS a <http://example/Type>} @ START"
//                 ,"{_ <http://...p3> FOCUS}@START"
                        );
        String strShapes = StrUtils.strjoinNL
                ("PREFIX : <http://example/>"
                ,"START = @ :S2"
                ,":S1 {"
                ,"   :p1 LITERAL ;"
                ,"}"
                ,":S2 {"
                ,"   :p1 . ;"
                ,"}"
                );

        ShexShapes shapes = Shex.shapesFromString(strShapes);
        Shex.printShapes(shapes);
        Node focus = SSE.parseNode("<http://example/f1>");

        Graph graph = GraphFactory.createDefaultGraph();
        graph.add(SSE.parseTriple("(:s :p1 :xyz)"));
        graph.add(SSE.parseTriple("(<http://example/f0> rdf:type :Type)"));
        //graph.add(SSE.parseTriple("(<http://example/f1> rdfs:label 'def')"));
        //graph.add(SSE.parseTriple("(<http://example/f1> :link _:b)"));
        graph.add(SSE.parseTriple("(<http://example/f0> :p1 <http://example/x>)"));

        shapes.getPrefixMap().forEach((p,u)->graph.getPrefixMapping().setNsPrefix(p, u));
        System.out.println("---- Data");
        RDFDataMgr.write(System.out, graph, RDFFormat.TURTLE_FLAT);

        ShexShapeMap shapeMap = parseShapeMap(s);
        //shapeMap.entries().forEach(System.out::println);
        //ShexValidation.validate(graph, shapes, shapeMap);
        validate(graph, shapes, shapeMap);
        System.out.println("DONE");
        System.exit(0);
    }

    private static void validate(Graph graph, ShexShapes shapes, ShexShapeMap shapeMap) {
        ValidationReport report = ShexValidation.validate(graph, shapes, shapeMap);
        if ( report.conforms() )
            System.out.println("OK");
        else
            report.getEntries().forEach(System.out::println);
    }

//    private static void partition() {
//        int N = 4 ;
//        int k = 3 ;
//        Set<String> triples = new HashSet<>();
//        for ( int i = 0 ; i < N ; i++ ) {
//            triples.add(""+(char)(i+'A'));
//        }
//        List<List<Set<String>>> subSets = ShapeEvalCardinality.cardinalityPartition(triples, k, k);
//        subSets.forEach(s->System.out.println(s));
//        System.exit(0);
//    }

    public static void validate() {
        String str = PREFIXES_DEV +"\n" + strValidateTest;
        ShexShapes shapes = Shex.shapesFromString(str);
        Shex.printShapes(shapes);
        Node focus = SSE.parseNode("<http://example/f1>");

        Graph graph = GraphFactory.createDefaultGraph();
        graph.add(SSE.parseTriple("(<http://example/f0> rdfs:label 'abc')"));
        //graph.add(SSE.parseTriple("(<http://example/f1> rdfs:label 'def')"));
        //graph.add(SSE.parseTriple("(<http://example/f1> :link _:b)"));
        graph.add(SSE.parseTriple("(<http://example/f1> :iri <http://example/x>)"));

        shapes.getPrefixMap().forEach((p,u)->graph.getPrefixMapping().setNsPrefix(p, u));
        System.out.println("---- Data");
        RDFDataMgr.write(System.out, graph, RDFFormat.TURTLE_FLAT);

        Node testShape = SSE.parseNode("<http://example/s3>");
        ShexShape shape = shapes.get(testShape);
        System.out.println("---- Validation");
        ValidationReport report = ShexValidation.validate(graph, shapes, shape, focus);
        if ( report.conforms() )
            System.out.println("OK");
        else {
            report.getEntries().forEach(e->System.out.println(e));
        }
    }

    static String strValidateTest = StrUtils.strjoinNL
            (
             //"<http://example/s1> LITERAL"
             //"<http://example/s1> { rdfs:label /^z/ {3} ; }",
             //"<http://example/s2> { rdfs:label [ 'abc' ] }",

             "<http://ex/S1> {"
             ,"  (:p1 .){2} ;"
             //,"  :p3 . ;"
             ,"}"
//             "<http://example/s3> { :iri [ <http://example/x> ] }"
//             ,""
            );

    static PrefixMap pmap = PrefixMapFactory.create(SSE.getPrefixMapRead());

    public static void validate2() {
        String str = strValidateTest;

        Graph graph = GraphFactory.createDefaultGraph();
        graph.getPrefixMapping().setNsPrefix("ex", "http://example/");
        //add(graph, "(<http://example/f> :p1 'abc')");
        add(graph, "(<http://example/f> :p1 'def')");
        add(graph, "(<http://example/f> :p3 'xyz')");
        add(graph, "(<http://example/f> :p9 'xyz')");
        Node focus = SSE.parseNode("<http://example/f>");
        Node testShape = SSE.parseNode("<http://ex/S1>");
        // add(graph, "(<http://example/f1> :link _:b)");
        // add(graph, "(<http://example/f> :iri <http://example/x>)");

        validate(str, testShape, graph, focus);
        System.exit(0);
    }


    public static void validate_eric_wiki() {
        String str = StrUtils.strjoinNL
                (
                 "<x:S1> {"
                 ,"    ("
                 ,"      <x:p1> . ; # TC1"
                 ,"      <x:p2> .   # TC2"
                 ,"    | <x:p3> . ; # TC3"
                 ,"      <x:p2> .+  # TC4"
                 ,"    ) ;"
                 ,"    <x:p2> ['1']   # TC5"
                 ,"  }"
                        );

        Graph graph = GraphFactory.createDefaultGraph();
        graph.getPrefixMapping().setNsPrefix("ex", "http://example/");
        add(graph, "(<x:n1> <x:p2> '1')");
        add(graph, "(<x:n1> <x:p2> '2')");
        add(graph, "(<x:n1> <x:p2> '3')");
        add(graph, "(<x:n1> <x:p3> '4')");
        Node focus = SSE.parseNode("<x:n1>");
        Node testShape = SSE.parseNode("<x:S1>");

        validate(str, testShape, graph, focus);
        System.exit(0);

    }

    private static void validate(String shapesStr, Node testShape, Graph graph, Node focus) {
        System.out.println(shapesStr);
        System.out.println("--");
        ShexShapes shapes = Shex.shapesFromString(PREFIXES_DEV +"\n" +shapesStr);
        Shex.printShapes(shapes);
        System.out.println("--");
        ShexShape shape = shapes.get(testShape);
        if ( shape == null ) {
            System.err.println("No such shape: "+testShape);
            return;
        }

        RDFDataMgr.write(System.out, graph, RDFFormat.TURTLE_FLAT);
        System.out.println("--");

        ValidationContext vCxt = new ValidationContext(graph, shapes);

        System.out.println(NodeFmtLib.str(focus, pmap));
        System.out.println("--");

        boolean b = ShapeEval.matchesShapeExpr(vCxt, shape.getShapeExpression(), focus);
        System.out.println(b);
        vCxt.getReportItems().forEach(e->System.out.println(e));

    }

    private static void add(Graph graph, String str) {
        graph.add(SSE.parseTriple(str));
    }

    public static void parsePrint() {
        //parsePrintFile("/home/afs/ASF/shapes/jena-shex/files/spec/schemas/1val1IRIREF.shex", true, true);
        parsePrintString(strParserTest, true, true);
    }

    public static ShexShapes parsePrintFile(String filename) {
        return parsePrintFile(filename, true, true);
    }


    public static ShexShapes parsePrintFile(String filename, boolean debug, boolean debugParse) {
        String filename_ = ( filename.startsWith("file:") )
                ? IRILib.IRIToFilename(filename)
                : filename ;
        String str = IO.readWholeFileAsUTF8(filename_);
        System.out.println("----");
        System.out.println(str);
        System.out.println("----");
        return parsePrint(()->Shex.readShapes(filename), debug, debugParse);
    }

    public static ShexShapes parsePrintString(String str, boolean debug, boolean debugParse) {
        String str2 = PREFIXES_DEV +"\n" + str;
        System.out.println("----");
        System.out.print(str2);
        System.out.println("----");
        return parsePrint(()->Shex.shapesFromString(str2), debug, debugParse);
    }

    public static ShexShapes parsePrint(Supplier<ShexShapes> supplier, boolean debug, boolean debugParse) {
        if ( debug || debugParse )
            System.out.println();
        ShexShapes shapes = parse(supplier, debug, debugParse);
        Shex.printShapes(shapes);
        return shapes;
    }

    private static ShexShapes parse(Supplier<ShexShapes> supplier, boolean debug, boolean debugParse) {
        ShExCompactParser.DEBUG = debug;
        ShExCompactParser.DEBUG_PARSE = debugParse;
        return supplier.get();
    }

    private static ShexShapeMap parseShapeMap(String str) {
        String str2 = PREFIXES_DEV +"\n" + str;
        System.out.println("----");
        System.out.print(str);
        if ( !str.endsWith("\n") )
            System.out.println();
        System.out.println("----");

        InputStream input = new ByteArrayInputStream(str2.getBytes(StandardCharsets.UTF_8));
        ShexShapeMap shapeMap = ShexParser.parseShapesMap(input, null);
        return shapeMap;
    }

    static String PREFIXES = StrUtils.strjoinNL
            ("PREFIX foaf:   <http://xmlns.com/foaf/0.1/>"
            ,"PREFIX xsd:     <http://www.w3.org/2001/XMLSchema#>"
            ,"PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
            ,"PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#>"
            ,"PREFIX sh:      <http://www.w3.org/ns/shacl#>"
            ,"PREFIX ex:      <http://example/>"
            ,"PREFIX :        <http://example/>"
            ,"PREFIX dev:     <urn:dev:>"
            ,"PREFIX skos:    <http://www.w3.org/2004/02/skos/core#>"
            );

    static String PREFIXES_DEV = StrUtils.strjoinNL
            ("PREFIX xsd:     <http://www.w3.org/2001/XMLSchema#>"
            ,"PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
            ,"PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#>"
            ,""
            ,"PREFIX ex:      <http://example/>"
            ,"PREFIX :        <http://example/>"
            ,"PREFIX dev:     <urn:dev:>"
            );
}
