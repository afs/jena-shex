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
import org.apache.jena.sparql.expr.nodevalue.NodeFunctions;
import shex.ReportItem;
import shex.ValidationContext;

public class NumLengthConstraint extends NodeConstraint {

    private final NumLengthKind lengthType;
    private final int length;

    public NumLengthConstraint(NumLengthKind lengthType, int len) {
        Objects.requireNonNull(lengthType);
        this.lengthType = lengthType;
        this.length = len;
    }

    @Override
    public ReportItem validate(ValidationContext vCxt, Node n) {
        String str = NodeFunctions.str(n);
        int idx = str.indexOf('.');
        int digits = (idx<0) ? length : length-1 ;

        switch (lengthType) {
            case FRACTIONDIGITS :
                if ( idx < 0 )
                    return null;
                if ( idx <= length )
                    return null;
                break;
            case TOTALDIGITS :
                if ( digits <= length )
                    return null;
                break;
            default :
                break;
        }

        String msg = format("Expected %s %d : got = %d", lengthType.label(), length, str.length());
        return new ReportItem(msg, n);
    }

    @Override
    public String toString() {
        return "NumLength["+lengthType.label()+" "+length+"]";
    }
}
