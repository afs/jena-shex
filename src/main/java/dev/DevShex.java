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

import org.apache.jena.atlas.io.IO;
import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.atlas.lib.StrUtils;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.out.NodeFormatter;
import org.apache.jena.riot.out.NodeFormatterTTL;
import org.apache.jena.sparql.graph.GraphFactory;
import org.apache.jena.sparql.sse.SSE;
import shex.ShexShape;
import shex.ShexShapes;
import shex.V;
import shex.ValidationReport;
import shex.parser.ShExCompactParser;
import shex.parser.ShexParser;

public class DevShex {

    static String strValidateTest = StrUtils.strjoinNL
            (
             //"<http://example/s1> LITERAL"
             "<http://example/s1> { rdfs:label /^z/ {3} ;"
             ,"                   }"
            );

    static String strParserTest = StrUtils.strjoinNL
            (""
            , "dev:testNum IRI AND {"
            , "   :p xsd:decimal ;"
            , "   :s xsd:string  ;"
            , "|"
//            , "   :d xsd:decimal MinExclusive -87 TotalDigits 6 FractionDigits 2 ;"
            // Where is v?
            //, "   :v [. - @en-US - 'abc' - <x>]"
            ,"    :iri IRI"
            ,"}"
            );

    public static void main(String[] args) {
        //parsePrint();
        validate();
    }

    public static void validate() {
        ShexShapes shapes = parse(strValidateTest, false, false);
        printShapes(shapes);
        Node target = SSE.parseNode("<http://example/s1>");
        Node focus = SSE.parseNode("<http://example/f1>");

        Graph graph = GraphFactory.createDefaultGraph();
        graph.add(SSE.parseTriple("(<http://example/s1> rdfs:label 'abc')"));
        graph.add(SSE.parseTriple("(<http://example/f1> rdfs:label 'def')"));
        graph.add(SSE.parseTriple("(<http://example/f1> :link _:b)"));

        shapes.getPrefixMap().forEach((p,u)->graph.getPrefixMapping().setNsPrefix(p, u));
        System.out.println("---- Data");
        RDFDataMgr.write(System.out, graph, RDFFormat.TURTLE_FLAT);

        ShexShape shape = shapes.get(target);
        System.out.println("---- Validation");
        ValidationReport report = V.validate(graph, shapes, shape, focus);
        if ( report.conforms() )
            System.out.println("OK");
        else {
            report.getEntries().forEach(e->System.out.println(e));
        }
    }

    public static void parsePrint() {
        parsePrint(strParserTest, true, true);
    }

    public static ShexShapes parsePrint(String str, boolean debug, boolean debugParse) {
        ShexShapes shapes = parse(str, debug, debugParse);
        if ( debug || debugParse )
            System.out.println();
        printShapes(shapes);
        return shapes;
    }

    private static ShexShapes parse(String strTest, boolean debug, boolean debugParse) {
        //String str = devShapeTripleExpr;
        String str = strTest;
        System.out.println("----");
        System.out.println(str);
        System.out.println("----");
        str = PREFIXES_DEV +"\n" + str;

        ShExCompactParser.DEBUG = debug;
        ShExCompactParser.DEBUG_PARSE = debugParse;

        ShexShapes shapes = shapesFromString(str);
        return shapes;

    }

    public static ShexShapes shapesFromFile(String filename) {
        InputStream input = IO.openFileBuffered(filename);
        ShexShapes shapes = ShexParser.parse(input, null);
        return shapes;
    }

    public static ShexShapes shapesFromString(String inputStr) {
        InputStream input = new ByteArrayInputStream(inputStr.getBytes(StandardCharsets.UTF_8));
        //ShexShapes shapes = shapesFromFile(null);
        ShexShapes shapes = ShexParser.parse(input, null);
        return shapes;
    }

    private static void printShapes(ShexShapes shapes) {
        IndentedWriter iOut = IndentedWriter.clone(IndentedWriter.stdout);
        NodeFormatter nFmt = new NodeFormatterTTL(null, shapes.getPrefixMap());
        shapes.getShapes().forEach(shape->shape.print(iOut, nFmt));
        iOut.flush();
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
            ,""
            );

    static String PREFIXES_DEV = StrUtils.strjoinNL
            ("PREFIX xsd:     <http://www.w3.org/2001/XMLSchema#>"
            ,"PREFIX rdf:     <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"
            ,"PREFIX rdfs:    <http://www.w3.org/2000/01/rdf-schema#>"
            ,""
            ,"PREFIX ex:      <http://example/>"
            ,"PREFIX :        <http://example/>"
            ,"PREFIX dev:     <urn:dev:>"
            ,""
            );
}
