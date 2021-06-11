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

package org.apache.jena.shex.sys;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.apache.jena.atlas.lib.InternalErrorException;
import org.apache.jena.atlas.lib.Pair;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.shex.*;
import org.apache.jena.shex.expressions.TripleExpression;

public class ValidationContext {
    private final ShexSchema shapes;
    private final Graph data;
    private boolean verbose = false;
    private boolean seenValidationReportEntry = false;
    // <data node, shape>
    private Deque<Pair<Node, ShexShape>> inProgress = new ArrayDeque<>();

    private final ValidationReport.Builder validationReportBuilder = ValidationReport.create();


    public static ValidationContext create(ValidationContext vCxt) {
        // Fresh ValidationReport.Builder
        return new ValidationContext(vCxt.data, vCxt.shapes, vCxt.inProgress);
    }

    public ValidationContext(Graph data, ShexSchema shapes) {
        this(data, shapes, null);

    }

    private ValidationContext(Graph data, ShexSchema shapes ,Deque<Pair<Node, ShexShape>> progress) {
        this.data = data;
        this.shapes = shapes;
        if ( progress != null )
            this.inProgress.addAll(progress);
    }

    public TripleExpression getTripleExpression(Node label) {
        return shapes.getTripleExpression(label);
    }

    public ShexSchema getShapes() {
        return shapes;
    }

    public ShexShape getShape(Node label) {
        return shapes.get(label);
    }

    public Graph getData() {
        return data;
    }

//    private Deque<Pair<Node, ShexShape>> inProgress = new ArrayDeque<>();
//    private Deque<Node> shapesLabelsDone = new ArrayDeque<>();

    public void startValidate(ShexShape shape, Node data) {
        inProgress.push(Pair.create(data,  shape));
    }

    // Return true if done or in-progress (i.e. don't)
    public boolean cycle(ShexShape shape, Node data) {
        return inProgress.stream().anyMatch(p->
            p.getLeft().equals(data) && p.getRight().getLabel().equals(shape.getLabel())
                    );
    }

    public void finishValidate(ShexShape shape, Node data) {
        Pair<Node, ShexShape> x = inProgress.pop();
        if ( x.getLeft().equals(data) && x.getRight().equals(shape) )
            return;
        throw new InternalErrorException("Eval stack error");
    }


//    public void reportEntry(ReportItem item, Shape shape, Node focusNode, Path path, Constraint constraint) {
//        reportEntry(item.getMessage(), shape, focusNode, path, item.getValue(), constraint);
//    }
//
//    public void reportEntry(String message, Shape shape, Node focusNode, Path path, Node valueNode, Constraint constraint) {
//        if ( verbose )
//            System.out.println("Validation report entry");
//        seenValidationReportEntry = true;
//        validationReportBuilder.addReportEntry(message, shape, focusNode, path, valueNode, constraint);
//    }

    // In ShEx "satisfies" returns a boolean.
//    public boolean conforms() { return validationReportBuilder.isEmpty(); }
    public boolean hasEntries() { return ! validationReportBuilder.isEmpty(); }

    /** Current state. */
    public List<ReportItem> getReportItems() {
        return validationReportBuilder.getItems();
    }

    public void reportEntry(ReportItem item) {
        validationReportBuilder.addReportItem(item);
    }

    public ValidationReport generateReport() {
        return validationReportBuilder.build();
    }
}
