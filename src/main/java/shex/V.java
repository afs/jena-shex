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

package shex;

import java.util.Objects;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;

public class V {

    public static ValidationReport validate(Graph graphData, ShexShapes shapes, Node shapeRef, Node focus) {
        Objects.requireNonNull(shapeRef);
        ShexShape shape = shapes.get(shapeRef);
        if ( shape == null ) {
            ValidationContext vCxt = new ValidationContext(graphData, shapes);
//            ReportItem item = new ReportItem("No such shape: "+PLib.displayStr(shapeRef), shapeRef);
//            vCxt.reportEntry(item);
            return vCxt.generateReport();
        }
        return validate(graphData, shapes, shape, focus);
    }

    public static ValidationReport validate(Graph graphData, ShexShapes shapes, ShexShape shape, Node focus) {
        Objects.requireNonNull(shape);
        ValidationContext vCxt = new ValidationContext(graphData, shapes);
        boolean b = shape.getShapeExpression().validate(vCxt, focus);
        if ( b != vCxt.conforms() ) {
            System.out.println("ValidationContext.conforms = "+vCxt.conforms());
            System.out.println("ShapeExpression().validate = "+b);
        }

        ValidationReport report = vCxt.generateReport();
        return report;
    }
}
