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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.atlas.lib.IRILib;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.out.NodeFormatter;
import org.apache.jena.riot.out.NodeFormatterTTL;
import org.apache.jena.riot.system.RiotLib;
import org.apache.jena.shex.parser.ShexParser;

public class Shex {

    public static ShexShapes shapesFromString(String inputStr) {
        InputStream input = new ByteArrayInputStream(inputStr.getBytes(StandardCharsets.UTF_8));
        ShexShapes shapes = ShexParser.parse(input, null, null);
        return shapes;
    }

    public static ShexShapes readShapes(String filenameOrURL) {
        return readShapes(filenameOrURL, null);
    }

    public static ShexShapes readShapes(String filenameOrURL, String base) {
        InputStream input = RDFDataMgr.open(filenameOrURL);
        if ( ! ( input instanceof BufferedInputStream ) )
            input = new BufferedInputStream(input, 128*1024);
        String parserBase = (base != null) ? base : IRILib.filenameToIRI(filenameOrURL);
        ShexShapes shapes = ShexParser.parse(input, IRILib.filenameToIRI(filenameOrURL), parserBase);
        return shapes;
    }

    /** Print shapes - the format details the internal structure */
    public static void printShapes(ShexShapes shapes) {
        IndentedWriter iOut = IndentedWriter.clone(IndentedWriter.stdout);
        iOut.setLinePrefix("");
        Set<String> visited = new HashSet<>();
        if ( shapes.getSource() != null )
            visited.add(shapes.getSource());
        printShapes(iOut, shapes, visited);
    }

    private static void printShapes(IndentedWriter iOut, ShexShapes shapes, Set<String> visited) {
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
                    ShexShapes imports = readShapes(iriStr);
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
}
