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

package org.apache.jena.shex;

import java.io.InputStream;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.apache.jena.atlas.io.IO;
import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.atlas.lib.IRILib;
import org.apache.jena.atlas.web.TypedInputStream;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.out.NodeFormatter;
import org.apache.jena.riot.out.NodeFormatterTTL;
import org.apache.jena.riot.system.RiotLib;
import org.apache.jena.shex.parser.ShExJ;
import org.apache.jena.shex.parser.ShexParser;

public class Shex {
    /**
     * Parse the string in ShExC syntax to produce a ShEx schema.
     * @param inputStr
     * @return ShexSchema
     */
    public static ShexSchema shapesFromString(String inputStr) {
        return shapesFromString(inputStr, null);
    }

    /**
     * Parse the string in ShExC syntax to produce a ShEx schema.
     * @param inputStr
     * @param baseURI
     * @return ShexSchema
     */
    public static ShexSchema shapesFromString(String inputStr, String baseURI) {
        ShexSchema shapes = ShexParser.parse(new StringReader(inputStr), baseURI);
        return shapes;
    }

    /**
     * Read the file to produce a ShEx schema.
     * @param filenameOrURL
     * @return ShexSchema
     */
    public static ShexSchema readShapes(String filenameOrURL) {
        return readShapes(filenameOrURL, null);
    }

    /**
     * Read the file to produce a ShEx schema.
     * @param filenameOrURL
     * @param base
     * @return ShexSchema
     */
    public static ShexSchema readShapes(String filenameOrURL, String base) {
        InputStream input = RDFDataMgr.open(filenameOrURL);
        // Buffering done in ShexParser
//        if ( ! ( input instanceof BufferedInputStream ) )
//            input = new BufferedInputStream(input, 128*1024);
        String parserBase = (base != null) ? base : IRILib.filenameToIRI(filenameOrURL);
        ShexSchema shapes = ShexParser.parse(input, IRILib.filenameToIRI(filenameOrURL), parserBase);
        return shapes;
    }

    /** Print shapes - the format details the internal structure */
    public static void printShapes(ShexSchema shapes) {
        IndentedWriter iOut = IndentedWriter.clone(IndentedWriter.stdout);
        iOut.setLinePrefix("");
        Set<String> visited = new HashSet<>();
        if ( shapes.getSource() != null )
            visited.add(shapes.getSource());
        printShapes(iOut, shapes, visited);
    }

    private static void printShapes(IndentedWriter iOut, ShexSchema shapes, Set<String> visited) {
        boolean havePrinted = false;

        if ( ! shapes.getPrefixMap().isEmpty() ) {
            RiotLib.writePrefixes(iOut, shapes.getPrefixMap(), true);
            havePrinted = true;
        }

        if ( shapes.hasImports() ) {
            if ( havePrinted )
                iOut.println();
            shapes.getImports().forEach(iriStr->{
                String pname = shapes.getPrefixMap().abbreviate(iriStr);
                if ( pname == null )
                    iOut.printf("IMPORT <%s>\n", iriStr);
                else
                    iOut.printf("IMPORT %s\n", pname);
            });
            havePrinted = true;
        }

        if ( ! shapes.getShapes().isEmpty() ) {
            boolean shapePrinted = false;
            NodeFormatter nFmt = new NodeFormatterTTL(null, shapes.getPrefixMap());
            for ( ShexShape shape : shapes.getShapes() ) {
                if ( havePrinted )
                    iOut.println();
                shape.print(iOut, nFmt);
                havePrinted = true;
            }
        }

        // Print imports.
        if ( shapes.hasImports() ) {
            if ( havePrinted )
                iOut.println();
            shapes.getImports().forEach(iriStr->{
                if ( visited.contains(iriStr) )
                    return;
                visited.add(iriStr);
                String prefix = iOut.getLinePrefix();
                iOut.println("Import = <"+iriStr+">");
                iOut.incIndent(4);
                try {
                    ShexSchema imports = readShapes(iriStr);
                    iOut.setLinePrefix(prefix+"I");
                    printShapes(iOut, imports, visited);
                } catch (Exception ex) {
                    iOut.println("Failed to read shapes: "+ex.getMessage());
                } finally {
                    iOut.setLinePrefix(prefix);
                    iOut.decIndent(4);
                }
            });
            havePrinted = true;
        }
        iOut.flush();
    }

    /**
     * Parse the file to get a ShEx shape map.
     * @param filename
     * @return ShexShapeMap
     */
    public static ShexShapeMap readShapesMap(String filename) {
        return readShapesMap(filename, IRILib.filenameToIRI(filename));
    }

    /**
     * Parse the file to get a ShEx shape map.
     * @param filename
     * @param baseURI
     * @return ShexShapeMap
     */
    public static ShexShapeMap readShapesMap(String filename, String baseURI) {
        InputStream input = IO.openFile(filename);
        return readShapesMap(input, baseURI);
    }

    /**
     * Parse the {@code InputStream} to get a ShEx shape map.
     * @param input
     * @param baseURI
     * @return ShexShapeMap
     */
    public static ShexShapeMap readShapesMap(InputStream input, String baseURI) {
        return ShexParser.parseShapesMap(input, baseURI);
    }

    /**
     * Parse a shape map from a {@code StringReader}.
     * @param inputStr
     * @param baseURI
     * @return ShexShapeMap
     */
    public static ShexShapeMap shapesMapFromString(String inputStr, String baseURI) {
        return ShexParser.parseShapesMap(new StringReader(inputStr), baseURI);
    }

    /** Read a {@link ShexShapeMap} from a file or URL. */
    public static ShexShapeMap readShapesMapJson(String filenameOrURL) {
        TypedInputStream in = RDFDataMgr.open(filenameOrURL);
        return readShapesMapJson(in.getInputStream());
    }

    /**
     * Parse the {@code InputStream} to get a ShEx shape map from JSON syntax.
     * @param input
     * @return ShexShapeMap
     */
    public static ShexShapeMap readShapesMapJson(InputStream input) {
        return ShExJ.readShapesMapJson(input);
    }
}
