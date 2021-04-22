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
import shex.ShexShapes;
import shex.parser.ShExCompactParser;
import shex.parser.ShexParser;

public class DevShex {

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

    static String devShapeTest = StrUtils.strjoinNL
            (""
             //, "dev:test (IRI OR BNODE) AND LITERAL"
             //, "dev:test1 IRI OR BNODE"
             //, "dev:test2 { :property1 xsd:string ; :property2 xsd:string ;  }"
             //, "dev:test { }"
             , "dev:test { :p /abc/ }"
            );

    static String strTest = StrUtils.strjoinNL
            (""
            , "dev:test0 { :p xsd:string minlength 5 maxlength 10 }"
//            , "dev:testNum { :p xsd:decimal MinExclusive -87 TotalDigits 6 FractionDigits 2 }"
//            , "dev:test1 { ^:p . {0} }"
//            , "dev:vs1   { :p [ 123 ] }"
//            , "dev:vs2   { ^:q [ 123 ] }"
//            , "dev:vs3   { :lang [ @en~ - @en-fr ]; }"
            );

    public static void main(String[] args) {
        parsePrint();
    }

    public static void parsePrint() {
        //String str = devShapeTripleExpr;
        String str = strTest;
        System.out.println(str);
        System.out.println();
        str = PREFIXES_DEV +"\n" + str;
        ShExCompactParser.DEBUG = true;
        ShExCompactParser.DEBUG_PARSE = true;
        ShexShapes shapes = shapesFromString(str);
        //ShexShapes shapes = shapesFromFile(null);

        IndentedWriter iOut = IndentedWriter.clone(IndentedWriter.stdout);

        shapes.getShapes().forEach(shape->{
            iOut.printf("Shape: %s\n",shape.getLabel());
            iOut.incIndent();
            shape.getShapeExpression().print(iOut, null);
            iOut.decIndent();
            iOut.println();
        });
        iOut.flush();
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
}
