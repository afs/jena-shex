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

import java.util.function.Supplier;

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
import shex.*;
import shex.parser.ShExCompactParser;

public class DevShex {

    static String strValidateTest = StrUtils.strjoinNL
            (
             //"<http://example/s1> LITERAL"
             "<http://example/s1> { rdfs:label /^z/ {3} ;"
             ,"                   }"
            );

    static String strParserTest = StrUtils.strjoinNL
            (""
            , "dev:testNum EXTRA :p {"
            , "   ( :p xsd:decimal | :s xsd:string {3,4} ) {1,2} ;"
            //, "   :p xsd:decimal+ | :s xsd:string {2};"
//            , "   :d xsd:decimal MinExclusive -87 TotalDigits 6 FractionDigits 2 ;"
            // Where is v?
            //, "   :v [. - @en-US - 'abc' - <x>]"
            //,"    :iri IRI"
            ,"}"
            );

    public static void main(String[] args) {
        parsePrint();
        //validate();
    }

    public static void validate() {
        String str = PREFIXES_DEV +"\n" + strValidateTest;
        ShexShapes shapes = Shex.shapesFromString(str);
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
        parsePrintFile("/home/afs/ASF/shapes/jena-shex/files/spec/schemas/1val1IRIREF.shex", true, true);
        //parsePrintString(strParserTest, true, true);
    }


    public static ShexShapes parsePrintFile(String filename, boolean debug, boolean debugParse) {
        String str = IO.readWholeFileAsUTF8(filename);
        System.out.println("----");
        System.out.println(str);
        System.out.println("----");
        return parsePrint(()->Shex.shapesFromFile(filename), debug, debugParse);
    }

    public static ShexShapes parsePrintString(String str, boolean debug, boolean debugParse) {
        System.out.println("----");
        System.out.println(str);
        System.out.println("----");
        String str2 = PREFIXES_DEV +"\n" + str;
        return parsePrint(()->Shex.shapesFromString(str2), debug, debugParse);
    }

    public static ShexShapes parsePrint(Supplier<ShexShapes> supplier, boolean debug, boolean debugParse) {
        if ( debug || debugParse )
            System.out.println();
        ShexShapes shapes = parse(supplier, debug, debugParse);
        printShapes(shapes);
        return shapes;
    }

    private static ShexShapes parse(Supplier<ShexShapes> supplier, boolean debug, boolean debugParse) {
        ShExCompactParser.DEBUG = debug;
        ShExCompactParser.DEBUG_PARSE = debugParse;
        return supplier.get();
    }

    public static void printShapes(ShexShapes shapes) {
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
