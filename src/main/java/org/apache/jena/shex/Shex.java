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
        ShexShapes shapes = ShexParser.parse(input, null);
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
        ShexShapes shapes = ShexParser.parse(input, parserBase);
        return shapes;
    }

    /** Print shapes - the format details the internal structure */
    public static void printShapes(ShexShapes shapes) {
        IndentedWriter iOut = IndentedWriter.clone(IndentedWriter.stdout);
        iOut.setLinePrefix("");
        Set<String> visited = new HashSet<>();
        printShapes(iOut, shapes, visited);
    }

    private static void printShapes(IndentedWriter iOut, ShexShapes shapes, Set<String> visited) {
        if ( ! shapes.getPrefixMap().isEmpty() ) {
            RiotLib.writePrefixes(iOut, shapes.getPrefixMap(), true);
            iOut.println();
        }

        if ( shapes.hasImports() ) {
            shapes.getImports().forEach(iriStr->{
                String pname = shapes.getPrefixMap().abbreviate(iriStr);
                if ( pname == null )
                    iOut.printf("IMPORT <%s>\n", iriStr);
                else
                    iOut.printf("IMPORT %s\n", pname);
            });
            iOut.println();
        }
        NodeFormatter nFmt = new NodeFormatterTTL(null, shapes.getPrefixMap());
        shapes.getShapes().forEach(shape->shape.print(iOut, nFmt));
        iOut.println();
        iOut.flush();
        // Print imports.
        if ( shapes.hasImports() ) {
            shapes.getImports().forEach(iriStr->{
                if ( visited.contains(iriStr) )
                    return;
                visited.add(iriStr);
                String prefix = iOut.getLinePrefix();
                iOut.incIndent(4);
                ShexShapes imports = readShapes(iriStr);
                iOut.setLinePrefix("I"+prefix);
                printShapes(iOut, imports, visited);
                iOut.setLinePrefix(prefix);
                iOut.decIndent(4);
            });
        }
    }
}
