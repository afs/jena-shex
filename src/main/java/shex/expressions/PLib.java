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

package shex.expressions;

import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.graph.Node;
import org.apache.jena.riot.out.NodeFormatter;
import org.apache.jena.riot.out.NodeFormatterTTL;
import org.apache.jena.riot.system.RiotLib;
import org.apache.jena.shacl.lib.ShLib;
import org.apache.jena.vocabulary.XSD;
import shex.Shex;
import shex.ShexShapes;

public class PLib {
    public static String displayStr(Node n) {
        return ShLib.displayStr(n);
    }

    public static String displayDT(Node n) {
        if ( n.isLiteral() && n.getLiteralDatatypeURI().startsWith(XSD.getURI()) ) {
            int x = XSD.getURI().length();
            String s = n.getLiteralDatatypeURI().substring(x);
            return "xsd:"+s;
        }
        String s = "<"+n.getLiteralDatatypeURI()+">";
        return s;
    }

    public static void printShapes(ShexShapes shapes) {
        IndentedWriter iOut = IndentedWriter.clone(IndentedWriter.stdout);
        iOut.setLinePrefix("");
        printShapes(iOut, shapes);
    }

    private static void printShapes(IndentedWriter iOut, ShexShapes shapes) {
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
                String prefix = iOut.getLinePrefix();
                iOut.incIndent(4);
                ShexShapes imports = Shex.readShapes(iriStr).asImport();
                iOut.setLinePrefix("I"+prefix);
                printShapes(iOut, imports);
                iOut.setLinePrefix(prefix);
                iOut.decIndent(4);
            });
        }
    }
}
