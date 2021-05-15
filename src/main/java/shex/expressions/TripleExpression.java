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

import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import shex.ReportItem;
import shex.ValidationContext;

public abstract class TripleExpression implements ShexPrintable {

    // tripleExpr = EachOf | OneOf | TripleConstraint | tripleExprRef

    //cardinality, semActs, annotation.

    protected final int min;
    protected final int max;
    protected final Cardinality cardinality;
    // [shex] annotations
    // [shex] semanticActions

    protected TripleExpression(Cardinality cardinality) {
        this.cardinality = cardinality;
        this.min = (cardinality==null) ? 1 : cardinality.min;
        this.max = (cardinality==null) ? 1 : cardinality.max;
    }

    public String cardinalityString() {
        if ( cardinality == null )
            return "";
        return cardinality.image;
    }

    public int min() {
        return min;
    }

    public int max() {
        return max;
    }

    // Returns null on match failure.
    public abstract Set<Triple> matches(ValidationContext vCxt, Node data);

    protected Set<Triple> cardinalityCheck(ValidationContext vCxt, Node node, Set<Triple> matched, int min, int max) {
        if ( cardinality == null ) {
            return (matched==null || matched.isEmpty()) ? null : matched;
        }
        ReportItem item = cardinalityCheck(node, matched, min, max);
        if ( item != null ) {
            vCxt.reportEntry(item);
            return null;
        }
        return matched;
    }

    protected ReportItem cardinalityCheck(Node node, Set<Triple> matched, int min, int max) {
        int N = matched.size();
        if ( min >= 0 && N < min )
            return new ReportItem("Cardinality violation (min="+min+"): "+N, node);
        if ( max >= 0 && N > max )
            return new ReportItem("Cardinality violation (max="+max+"): "+N, node);
        return null;
    }

    public abstract void visit(TripleExpressionVisitor visitor);

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public String toString() {
        return this.getClass().getSimpleName()+"[]";
    }
}
