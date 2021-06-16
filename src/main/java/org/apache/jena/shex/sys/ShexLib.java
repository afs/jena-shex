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

package org.apache.jena.shex.sys;

import org.apache.jena.atlas.io.IndentedLineBuffer;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.out.NodeFormatter;
import org.apache.jena.riot.out.NodeFormatterTTL;
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.riot.system.PrefixMapFactory;
import org.apache.jena.shex.ShexReport;
import org.apache.jena.shex.ShexShapeAssociation;
import org.apache.jena.shex.Status;
import org.apache.jena.shex.expressions.ShapeExprVisitor;
import org.apache.jena.shex.expressions.ShapeExprWalker;
import org.apache.jena.shex.expressions.ShapeExpression;
import org.apache.jena.shex.expressions.TripleExprVisitor;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;

public class ShexLib {
    /** Extract the fragment from a URI. Return "". */
    public static String fragment(String uri) {
        int idx = uri.indexOf('#');
        if ( idx < 0 )
            return "";
        return uri.substring(idx);
    }

    public static void walk(ShapeExpression shExpr, ShapeExprVisitor beforeVisitor, ShapeExprVisitor afterVisitor) {
        ShapeExprWalker walker = new ShapeExprWalker(beforeVisitor, afterVisitor, null, null);
        shExpr.visit(walker);
    }

    public static void walk(ShapeExpression shExpr,
                            ShapeExprVisitor beforeVisitor, ShapeExprVisitor afterVisitor,
                            TripleExprVisitor beforeTripleExpressionVisitor, TripleExprVisitor afterTripleExpressionVisitor
                            ) {
        ShapeExprWalker walker = new ShapeExprWalker(beforeVisitor, afterVisitor,
                                                     beforeTripleExpressionVisitor, afterTripleExpressionVisitor);
        shExpr.visit(walker);
    }

    private static PrefixMap displayPrefixMap = PrefixMapFactory.createForOutput();
    private static NodeFormatter nodeFmtAbbrev = new NodeFormatterTTL(null, displayPrefixMap);

    static {
        displayPrefixMap.add("owl",  OWL.getURI());
        displayPrefixMap.add("rdf",  RDF.getURI());
        displayPrefixMap.add("rdfs", RDFS.getURI());
        displayPrefixMap.add("xsd",  XSD.getURI());
    }

    /** Display string for human-readable output. */
    public static String strDatatype(Node n) {
        if ( n.isLiteral() && n.getLiteralDatatypeURI().startsWith(XSD.getURI()) ) {
            int x = XSD.getURI().length();
            String s = n.getLiteralDatatypeURI().substring(x);
            return "xsd:"+s;
        }
        String s = "<"+n.getLiteralDatatypeURI()+">";
        return s;
    }

    /** Display string for human-readable output. */
    public static String displayStr(Node n) {
        if ( n == null )
            return "<null>";
        if ( n == SysShex.focusNode )
            return "FOCUS";
        if ( n == SysShex.startNode )
            return "START";
        return displayStr(n, nodeFmtAbbrev);
    }

    private static String displayStr(Node n, NodeFormatter nodeFmt) {
        IndentedLineBuffer sw = new IndentedLineBuffer() ;
        nodeFmt.format(sw, n);
        return sw.toString() ;
    }

    public static String displayStr(Triple triple) {
        return displayStr(triple.getSubject())+" "+displayStr(triple.getPredicate())+" "+displayStr(triple.getObject());
    }

    public static void printReport(ShexReport report) {
//        if ( report.conforms() ) {
//                System.out.println("OK");
//        } else {
//            report.getEntries().forEach(e->System.out.println(e));
//        }
        if ( report.conforms() ) {
            System.out.println("OK");
        } else {
            report.getReports().forEach(reportEntry->printReport(reportEntry));
        }
    }

    private static void printReport(ShexShapeAssociation reportEntry) {
        Node targetNode = reportEntry.node;
        Triple triplePattern = reportEntry.pattern;
        Node shapeLabel = reportEntry.shapeExprLabel;
        Status status = reportEntry.status;
        String reason = reportEntry.reason;
        Node focusNode = reportEntry.focus;

        switch (status) {
            case conformant :
                System.out.printf("Node = %s, Shape = %s, Focus = %s, Status = %s\n",
                                  displayStr(targetNode), displayStr(shapeLabel),
                                  displayStr(focusNode), status.toString());
                break;
            case nonconformant :
                System.out.printf("Node = %s, Shape = %s, Focus = %s, Status = %s, Reason = %s\n",
                                  displayStr(targetNode), displayStr(shapeLabel),
                                  displayStr(focusNode), status.toString(),
                                  (reason==null)?"--": reason);
                break;
        }
    }
}
