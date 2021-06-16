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

import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.shared.impl.PrefixMappingImpl;

/** ShEx validation report.
 * <p>
 * This has a ShEx <a href ="https://shexspec.github.io/shape-map/#shapemap-structure">defined structure</a> (one item per shape)
 * and also all the validation item reports more in the style of SHACL/
 */
public class ShexReport {
//
//    private static ShexReport singletonReportConformsTrue = new ShexReport(Collections.emptySet(),
//                                                                           Collections.emptyList(),
//                                                                           (Resource)null);
    private final Collection<ReportItem> entries;
    private final Resource resultResource;
    private final List<ShexShapeAssociation> reports;

    public static Builder create() {
        return new Builder();
    }

//    /** Return an immutable report that records no validation errors (violations or any other level of severity) */
//    public static ShexReport reportConformsTrue() {
//        return singletonReportConformsTrue;
//    }

    private ShexReport(Collection<ReportItem> entries, List<ShexShapeAssociation> reports, PrefixMapping prefixes) {
        this(entries, reports, generate(entries, prefixes));
    }

    private static Resource generate(Collection<ReportItem> entries, PrefixMapping prefixes) {
        // [shex] No graph for now.
        return null;
    }

    private ShexReport(Collection<ReportItem> entries, List<ShexShapeAssociation> reports, Resource resultResource) {
        this.entries = new ArrayList<>(entries);
        this.reports = new ArrayList<>(reports);
        this.resultResource = resultResource;
    }

    public Collection<ReportItem> getEntries() { return entries; }

    public List<ShexShapeAssociation> getReports() { return reports; }

    public Resource getResource() { return resultResource; }

    public Model getModel() { return resultResource.getModel(); }

    public Graph getGraph() {
        return getModel().getGraph();
    }

    public boolean conforms() {
        boolean b1 = entries.isEmpty();
        boolean b2 = reports.stream().allMatch(a -> a.status == Status.conformant);
        if ( b1 != b2 ) {
            long x = reports.stream().filter(a -> a.status == Status.conformant).count();
            System.err.printf("conforms() inconsistent:  e:%s/r:%s %d/%d[%d]\n", b1, b2, entries.size(), reports.size(),x);
            System.err.println(entries);
            System.err.println(reports);
            System.err.println();
        }
        return b2;

    }
    //public boolean conforms() { return entries.isEmpty(); }


    public static class Builder {

        private final List<ReportItem> entries = new ArrayList<>();
        private final List<ShexShapeAssociation> reports = new ArrayList<>();
        private PrefixMapping prefixes = new PrefixMappingImpl();

        public Builder() { }

        public void addPrefixes(PrefixMapping pmap) {
            this.prefixes.setNsPrefixes(pmap);
        }

        public boolean hasEntries() { return ! entries.isEmpty(); }
        public boolean hasReports() { return ! reports.isEmpty(); }

        public List<ReportItem> getItems() { return entries; }
        public List<ShexShapeAssociation> getReports() { return reports; }

        public void addReportItem(ReportItem e) {
            entries.add(e);
        }

        /** Create a new report line item from an exists (shex map) entry and add it to the reports */
        public void shexReport(ShexShapeAssociation entry, Node focusNode, Status result, String reason) {
            // [shex] focus node.
            ShexShapeAssociation ssa = new ShexShapeAssociation(entry, focusNode, result, reason);
            shexReport(ssa);
        }

        public void shexReport(ShexShapeAssociation entry) {
            reports.add(entry);
        }

        public ShexReport build() {
            return new ShexReport(entries, reports, prefixes);
        }

    }
}
