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

import static org.apache.jena.shacl.lib.ShLib.displayStr;

import org.apache.jena.graph.Node;
import shex.ReportItem;
import shex.ValidationContext;

public class NodeKindConstraint extends NodeConstraint {
    private NodeKind nodeKind;

    public NodeKindConstraint(NodeKind nodeKind) {
        this.nodeKind = nodeKind;
    }

    @Override
    public ReportItem validate(ValidationContext vCxt, Node n) {
        switch (nodeKind) {
            case BNODE :
                if ( n.isBlank() )
                    return null;
                break;
            case IRI :
                if ( n.isURI() )
                    return null;
                break;
            case LITERAL :
                if ( n.isLiteral() )
                    return null;
                break;
            case NONLITERAL :
                if ( ! n.isLiteral() )
                    return null;
                break;
            default :
//                data.isNodeTriple()
//                data.isNodeGraph()
                break;
        }
        // Bad.
        String msg = toString()+" : Expected "+nodeKind.toString()+" for "+displayStr(n);
        return new ReportItem(msg, n);
    }

    @Override
    public String toString() {
        return "NodeKind: "+nodeKind.toString();
    }
}
