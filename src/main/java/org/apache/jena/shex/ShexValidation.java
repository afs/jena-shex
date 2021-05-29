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

import java.util.List;
import java.util.Objects;

import org.apache.jena.atlas.lib.InternalErrorException;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.shex.expressions.PLib;
import org.apache.jena.shex.sys.ValidationContext;

public class ShexValidation {

    /** Validate data using a collection of shapes and a shape map */
    public static ValidationReport validate(Graph graph, ShexShapes shapes, ShexShapeMap shapeMap) {
        ValidationContext vCxt = new ValidationContext(graph, shapes.withImports());
        shapeMap.entries().forEach(e->{
            List<Node> focusNodes;
            if ( e.node != null ) {
                focusNodes = List.of(e.node);
            } else if ( e.pattern != null ) {
                Triple t = e.asMatcher();
                focusNodes = graph.find(t).mapWith(triple-> (e.isSubjectFocus()?triple.getSubject():triple.getObject()) ).toList();
            } else
                throw new InternalErrorException("Shex shape mapping has no node and no pattern");
            if ( focusNodes.isEmpty() ) {
                System.out.println("Nothing to do");
                return;
            }
            for ( Node focus : focusNodes ) {
                validationStep(vCxt, e.shapeExprLabel, focus);
            }
        });
        ValidationReport report = vCxt.generateReport();
        return vCxt.generateReport();
    }

    /** Validate a specific node (the focus), with a specific shape in a set of shapes. */
    public static ValidationReport validate(Graph graphData, ShexShapes shapes, Node shapeRef, Node focus) {
        Objects.requireNonNull(shapeRef);
        shapes = shapes.withImports();
        ValidationContext vCxt = new ValidationContext(graphData, shapes);
        boolean rtn = validationStep(vCxt, shapeRef, focus);
        if ( rtn )
            return ValidationReport.reportConformsTrue();
        atLeastOneReport(vCxt, focus);
        return vCxt.generateReport();
    }

    /** Validate a specific node (the focus), against a shape. */
    public static ValidationReport validate(Graph graphData, ShexShapes shapes, ShexShape shape, Node focus) {
        Objects.requireNonNull(shape);
        shapes = shapes.withImports();
        ValidationContext vCxt = new ValidationContext(graphData, shapes);
        vCxt.startValidate(shape, focus);
        boolean b = shape.satisfies(vCxt, focus);
        // [shex] Reports accumulate for validation paths not taken.
        vCxt.finishValidate(shape, focus);
        if ( b )
            return ValidationReport.reportConformsTrue();
        // Ensure at least one entry.
        atLeastOneReport(vCxt, focus);
        return vCxt.generateReport();
    }

    private static boolean validationStep(ValidationContext vCxt1, Node shapeRef, Node focus) {
        ValidationContext vCxt = ValidationContext.create(vCxt1);
        ShexShape shape = vCxt.getShape(shapeRef);
        if ( shape == null ) {
            ReportItem item = new ReportItem("No such shape: "+PLib.displayStr(shapeRef), shapeRef);
            vCxt.reportEntry(item);
            return false;
        }
        vCxt.startValidate(shape, focus);
        boolean b = shape.satisfies(vCxt, focus);
        if ( b )
            return b;
        vCxt.finishValidate(shape, focus);
        vCxt.getReportItems().forEach(vCxt1::reportEntry);
        atLeastOneReport(vCxt1, focus);
        return false;
    }

    private static void atLeastOneReport(ValidationContext vCxt, Node focus) {
        // Ensure at least one entry.
        if ( vCxt.getReportItems().isEmpty() ) {
            ReportItem reportItem = new ReportItem("Failed", focus);
            vCxt.reportEntry(reportItem);
        }
    }
}
