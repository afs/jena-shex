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
        this.min = (cardinality==null) ? -1 : cardinality.min;
        this.max = (cardinality==null) ? -1 : cardinality.max;
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

//    @Override
//    public void validate(ValidationContext vCxt, Node data) {
//        throw new NotImplemented(this.getClass().getSimpleName()+".validate");
//    }

    public abstract Set<Triple> matches(ValidationContext vCxt, Node data);

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
