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
import java.util.function.Consumer;

import org.apache.jena.atlas.lib.InternalErrorException;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.shared.impl.PrefixMappingImpl;

/** ShEx validation report.
 * <p>
 * This has a ShEx <a href ="https://shexspec.github.io/shape-map/#shapemap-structure">defined structure</a> (one item per shape)
 * and also all the validation item reports, more in the style of SHACL.
 */
public class ShexReport {
    private final Collection<ReportItem> entries;
    private final Resource resultResource;
    private final List<ShexShapeAssociation> reports;
    private final boolean conforms;

    public static Builder create() {
        return new Builder();
    }

    private ShexReport(Collection<ReportItem> entries, List<ShexShapeAssociation> reports, PrefixMapping prefixes) {
        this(entries, reports, generate(entries, prefixes));
    }

    private static Resource generate(Collection<ReportItem> entries, PrefixMapping prefixes) {
        // No graph for the report for now.
        return null;
    }

    private ShexReport(Collection<ReportItem> entries, List<ShexShapeAssociation> reports, Resource resultResource) {
        this.entries = new ArrayList<>(entries);
        this.reports = new ArrayList<>(reports);
        this.resultResource = resultResource;
        // Conforms if all shape validations are conformant
        this.conforms = reports.stream().allMatch(a -> a.status == Status.conformant);
        // Consistency check.
        if ( conforms != entries.isEmpty() ) {
            long x = reports.stream().filter(a -> a.status == Status.conformant).count();
            String msg = String.format("conforms() inconsistent:  e:%s/r:%s %d/%d[%d]\n", conforms, entries.isEmpty(), entries.size(), reports.size(),x);
            throw new InternalErrorException(msg);
        }
    }

    //public boolean hasEntries() { return ! entries.isEmpty(); }
    public boolean hasReports() { return ! reports.isEmpty(); }

    public void forEachReport(Consumer<ShexShapeAssociation> action) {
        reports.forEach(action);
    }

//    public Collection<ReportItem> getEntries()     { return entries; }
//    public List<ShexShapeAssociation> getReports() { return reports; }

    public Resource getResource() { return resultResource; }

    public Model getModel() { return resultResource.getModel(); }

    public Graph getGraph() {
        return getModel().getGraph();
    }

    public boolean conforms() {
        return conforms;
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
