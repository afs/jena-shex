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

import java.util.Objects;
import java.util.Set;

import org.apache.jena.atlas.io.IndentedWriter;
import org.apache.jena.atlas.lib.NotImplemented;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.out.NodeFormatter;
import shex.ValidationContext;

public class TripleExpressionRef extends TripleExpression {

    private Node ref;

    public TripleExpressionRef(Node node) {
        super(null);
        this.ref = node;
    }

    @Override
    public Set<Triple> matches(ValidationContext vCxt, Node data) {
        throw new NotImplemented();
        //vCxt.getShape(ref);
    }


    @Override
    public void visit(TripleExpressionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ref);
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        TripleExpressionRef other = (TripleExpressionRef)obj;
        return Objects.equals(ref, other.ref);
    }

    @Override
    public void print(IndentedWriter iOut, NodeFormatter nFmt) {
        iOut.print("tripleExprRef: ");
        nFmt.format(iOut, ref);
        iOut.println();
    }

    @Override
    public String toString() {
        return "TripleExpressionRef["+PLib.displayStr(ref)+"]";
    }
}
