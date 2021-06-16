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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.apache.jena.atlas.lib.InternalErrorException;
import org.apache.jena.atlas.lib.ListUtils;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.shex.sys.ShexLib;
import org.apache.jena.shex.sys.ValidationContext;

public class ShexValidation {

    /** Validate data using a collection of shapes and a shape map */
    public static ShexReport validate(Graph graph, ShexSchema shapes, ShexShapeMap shapeMap) {
        shapes = shapes.importsClosure();
        ValidationContext vCxt = new ValidationContext(graph, shapes);

        List<ShexShapeAssociation> reports = new ArrayList<>();

        shapeMap.entries().forEach(mapEntry->{
            Collection<Node> focusNodes;
            if ( mapEntry.node != null ) {
                focusNodes = List.of(mapEntry.node);
            } else if ( mapEntry.pattern != null ) {
                Triple t = mapEntry.asMatcher();
                focusNodes = graph.find(t).mapWith(triple-> (mapEntry.isSubjectFocus()?triple.getSubject():triple.getObject()) ).toSet();
                if ( focusNodes.isEmpty() ) {
                    // [shex] report error.
                    //System.err.println("Nothing to do: "+ShexLib.displayStr(mapEntry.pattern));
                    return;
                }
            } else
                throw new InternalErrorException("Shex shape mapping has no node and no pattern");
            // The validation work for this map entry.
            for ( Node focus : focusNodes ) {
                track(mapEntry.shapeExprLabel, focus);
                validationStep(vCxt, mapEntry, mapEntry.shapeExprLabel, focus);
            }
        });

        ShexReport report = vCxt.generateReport();
        return report;
    }

    /** Validate a specific node (the focus), with a specific shape in a set of shapes. */
    public static ShexReport validate(Graph graphData, ShexSchema shapes, Node shapeRef, Node focus) {
        Objects.requireNonNull(shapeRef);
        ShexShapeAssociation entry = new ShexShapeAssociation(focus, null, shapeRef);
        shapes = shapes.importsClosure();
        ValidationContext vCxt = new ValidationContext(graphData, shapes);
        boolean isValid = validationStep(vCxt, entry, shapeRef, focus);
        return vCxt.generateReport();
    }

    /** Validate a specific node (the focus), against a shape. */
    public static ShexReport validate(Graph graphData, ShexSchema shapes, ShexShape shape, Node focus) {
        // [shex] report
        Objects.requireNonNull(shape);
        ShexShapeAssociation entry = new ShexShapeAssociation(focus, null, shape.getLabel());

        ShexShape s1 = shapes.getStart();

        shapes = shapes.importsClosure();

        ShexShape s2 = shapes.getStart();

        ValidationContext vCxt = new ValidationContext(graphData, shapes);
        boolean isValid = validationStep(vCxt, entry, shape.getLabel(), focus);
        return vCxt.generateReport();
    }

    private static boolean validationStep(ValidationContext vCxt, ShexShapeAssociation mapEntry, Node shapeRef, Node focus) {
        // Isolate.
        ShexShape shape = vCxt.getShape(shapeRef);
        if ( shape == null ) {
            vCxt.getShape(shapeRef);
            String msg = "No such shape: "+ShexLib.displayStr(shapeRef);
            ReportItem item = new ReportItem(msg, shapeRef);
            vCxt.reportEntry(item);
            report(vCxt, mapEntry, shapeRef, Status.nonconformant, msg);
            return false;
        }
        return validationStep(vCxt, mapEntry, shape, shapeRef, focus);
    }

    private static boolean validationStep(ValidationContext vCxt, ShexShapeAssociation mapEntry, ShexShape shape, Node shapeRef, Node focus) {
        // Isolate.
        ValidationContext vCxtInner = ValidationContext.create(vCxt);
        vCxtInner.startValidate(shape, focus);
        boolean isValid = shape.satisfies(vCxtInner, focus);
        vCxtInner.finishValidate(shape, focus);
        if ( ! isValid ) {
            atLeastOneReportItem(vCxtInner, focus);
            vCxtInner.copyInto(vCxt); // Report items.
        }
        createShexReportLine(vCxt, mapEntry, isValid, shapeRef, focus);
        return isValid;
    }

    private static void createShexReportLine(ValidationContext vCxt, ShexShapeAssociation mapEntry, boolean conforms, Node shapeRef, Node focus) {
        // Shex shapes report.
        if ( conforms ) {
            report(vCxt, mapEntry, focus, Status.conformant, null);
            return;
        }

        if ( mapEntry == null )
            return;

        ReportItem item = ListUtils.last(vCxt.getReportItems());
        String reason = (item!=null) ? item.getMessage() : null;
        report(vCxt, mapEntry, focus, Status.nonconformant, reason);
    }

    private static void atLeastOneReportItem(ValidationContext vCxt, Node focus) {
        // Ensure at least one entry.
        if ( vCxt.getReportItems().isEmpty() ) {
            ReportItem reportItem = new ReportItem("Failed", focus);
            vCxt.reportEntry(reportItem);
        }
    }

    private static void report(ValidationContext vCxt, ShexShapeAssociation entry, Node focusNode, Status result, String reason) {
        vCxt.shexReport(entry, focusNode, result, reason);
    }

    private static void track(Node shapeExprLabel, Node focus) {}
}
