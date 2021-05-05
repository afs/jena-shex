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

import java.util.List;

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;

public class ValidationContext {
    // "Share" with SHACL?
    private final ShexShapes shapes;
    private final Graph data;
    private boolean verbose = false;
    private boolean seenValidationReportEntry = false;

    private final ValidationReport.Builder validationReportBuilder = ValidationReport.create();


    public static ValidationContext create(ValidationContext vCxt) {
        // Fresh ValidationReport.Builder
        return new ValidationContext(vCxt.data, vCxt.shapes);
    }

    public ValidationContext(Graph data, ShexShapes shapes) {
        this.data = data;
        this.shapes = shapes;
    }

    public ShexShape getShape(Node label) {
        return shapes.get(label);
    }

    public Graph getData() {
        return data;
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

    public boolean conforms() { return validationReportBuilder.isEmpty(); }

    /** Current state. */
    public List<ReportEntry> getReportEntries() {
        return validationReportBuilder.getReports();
    }


    public void reportEntry(ReportItem item) {
        validationReportBuilder.addReportEntry(new ReportEntry(item));
    }

    public ValidationReport generateReport() {
        return validationReportBuilder.build();
    }
}
