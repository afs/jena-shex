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

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.shex.sys.ShexLib;
import org.apache.jena.shex.sys.SysShex;

public class ShexShapeAssociation {
//  node: an RDF node, or a triple pattern which is used to select RDF nodes.
//  shape: ShEx shapeExprLabel or the string "START" for the start shape expression.
//  status: [default="conformant"] "nonconformant" or "conformant".
//  reason: [optional] a string stating a reason for failure or success.
//  appInfo: [optional] an application-specific JSON-LD structure

    public final Node node;
    public final Triple pattern;
    public final Node shapeExprLabel;
    public final Status status;
    public final String reason;
    public final Node focus;
//  public final String appInfo;

    public ShexShapeAssociation(Node node, Triple pattern, Node shapeExprLabel) {
        this(node, pattern, shapeExprLabel, null, null, null);
    }

    public ShexShapeAssociation(ShexShapeAssociation assoc, Node focusNode, Status status, String reason) {
        // Reporting form.
        this(assoc.node, assoc.pattern, assoc.shapeExprLabel, focusNode, status, reason);
    }

    private ShexShapeAssociation(Node node, Triple pattern, Node shapeExprLabel, Node focusNode, Status status, String reason) {
        super();
        this.node = node;
        this.pattern = pattern;
        this.shapeExprLabel = shapeExprLabel;
        this.status = status;
        this.focus = focusNode;
        this.reason = reason;
    }


    public boolean isSubjectFocus() {
        return pattern != null && SysShex.focusNode.equals(pattern.getSubject());
    }

    public boolean isObjectFocus() {
        return pattern != null && SysShex.focusNode.equals(pattern.getObject());
    }

    public Triple asMatcher() {
        if ( pattern == null )
            return null;
        return Triple.create(n(pattern.getSubject()),
                             n(pattern.getPredicate()),
                             n(pattern.getObject()));
    }

    private Node n(Node node) {
        return ( node == null || node.isExt() ) ? Node.ANY : node ;
    }

    @Override
    public String toString() {
        StringBuilder sBuff = new StringBuilder();
        String str = toStringBasic();
        if ( focus != null )
            str = str+" "+ShexLib.displayStr(focus);
        if ( status != null )
            str = str+" "+status;
        if ( reason != null )
            str = str+" :: "+reason;
        return str;
    }

    private String toStringBasic() {
        if ( pattern != null ) {
            return String.format("{ %s %s %s } @ %s",
                                 str(pattern.getSubject()), str(pattern.getPredicate()), str(pattern.getObject()),
                                 str(shapeExprLabel));
        }
        if ( node != null ) {
            return String.format("%s @ %s", str(node), str(shapeExprLabel));
        }
        return "ShexShapeAssociation/null";
    }

    private static String str(Node x) {
        if ( x == null || x.equals(Node.ANY) )
            return "_";
        if ( x == SysShex.focusNode )
            return "FOCUS";
        if ( x == SysShex.startNode )
            return "START";
        return ShexLib.displayStr(x);
    }
}
