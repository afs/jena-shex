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

package org.apache.jena.shex.expressions;

import static java.lang.String.format;

import java.util.Objects;

import org.apache.jena.graph.Node;
import org.apache.jena.shex.ReportItem;
import org.apache.jena.shex.sys.ValidationContext;
import org.apache.jena.sparql.expr.nodevalue.NodeFunctions;

public class StrLengthConstraint extends NodeConstraint {

    private final StrLengthKind lengthType;
    private final int length;

    public StrLengthConstraint(StrLengthKind lengthType, int len) {
        Objects.requireNonNull(lengthType);
        this.lengthType = lengthType;
        this.length = len;
    }

    @Override
    public ReportItem nodeSatisfies(ValidationContext vCxt, Node n) {
        if ( ! n.isLiteral() && ! n.isURI() ) {
            String msg = format("%s: Not a literal or URI: %s", lengthType.label(), PLib.displayStr(n));
            return new ReportItem(msg, n);
        }
        String str = NodeFunctions.str(n);
        switch (lengthType) {
            case LENGTH :
                if ( str.length() == length )
                    return null;
                break;
            case MAXLENGTH :
                if ( str.length() <= length )
                    return null;
                break;
            case MINLENGTH :
                if ( str.length() >= length )
                    return null;
                break;
            default :
                break;

        }

        String msg = format("Expected %s %d : got = %d", lengthType.label(), length, str.length());
        return new ReportItem(msg, n);
    }

    @Override
    public int hashCode() {
        return Objects.hash(length, lengthType);
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        StrLengthConstraint other = (StrLengthConstraint)obj;
        return length == other.length && lengthType == other.lengthType;
    }

    @Override
    public String toString() {
        return "StrLength["+lengthType.label()+" "+length+"]";
    }
}