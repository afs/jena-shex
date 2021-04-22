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

import static java.lang.String.format;

import java.util.Objects;

import org.apache.jena.graph.Node;
import org.apache.jena.riot.out.NodeFmtLib;
import org.apache.jena.sparql.expr.NodeValue;
import shex.ReportItem;
import shex.ShexException;
import shex.ValidationContext;

public class NumRangeConstraint extends NodeConstraint {

    private final NumRangeKind rangeKind;
    private final Node value;
    private final NodeValue numericValue;

    public NumRangeConstraint(NumRangeKind rangeKind, Node value) {
        Objects.requireNonNull(rangeKind);
        this.rangeKind = rangeKind;
        this.value = value;
        NodeValue nv = NodeValue.makeNode(value);
        if ( ! nv.isNumber() )
            throw new ShexException("Not a number: "+value);
        this.numericValue = nv;
    }

    @Override
    public ReportItem validate(ValidationContext vCxt, Node n) {
        if ( ! n.isLiteral() )
            return new ReportItem("NumRange: Not a literal number", n);
        NodeValue nv = NodeValue.makeNode(n);
        int r = NodeValue.compare(numericValue, nv);

        switch(rangeKind) {
            case MAXEXCLUSIVE :
                if ( r < 0 ) return null;
                break;
            case MAXINCLUSIVE :
                if ( r <= 0 ) return null;
                break;
            case MINEXCLUSIVE :
                if ( r > 0 ) return null;
                break;
            case MININCLUSIVE :
                if ( r >= 0 ) return null;
                break;
            default :
                break;
        }
        String msg = format("Expected %s %s : got = %d", rangeKind.label(), NodeFmtLib.str(nv.getNode()), NodeFmtLib.str(n));
        return new ReportItem(msg, n);
    }

    @Override
    public String toString() {
        return "NumRange["+rangeKind.label()+" "+NodeFmtLib.displayStr(value)+"]";
    }
}
