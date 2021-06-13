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
import java.util.Collections;
import java.util.List;

import org.apache.jena.graph.Graph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.shared.impl.PrefixMappingImpl;

public class ShexReport {

    private static ShexReport singletonReportConformsTrue = new ShexReport(Collections.emptySet(), (Resource)null);
    private final Collection<ReportItem> entries;
    private final Resource resultResource;

    public static Builder create() {
        return new Builder();
    }

    /** Return an immutable report that records no validation errors (violations or any other level of severity) */
    public static ShexReport reportConformsTrue() {
        return singletonReportConformsTrue;
    }

    private ShexReport(Collection<ReportItem> entries, PrefixMapping prefixes) {
        this(entries, generate(entries, prefixes));
    }

    private static Resource generate(Collection<ReportItem> entries2, PrefixMapping prefixes) {
        return null;
    }

    private ShexReport(Collection<ReportItem> entries, Resource resultResource) {
        this.entries = entries;
        this.resultResource = resultResource;
    }

    public Collection<ReportItem> getEntries() { return entries; }

    public Resource getResource() { return resultResource; }

    public Model getModel() { return resultResource.getModel(); }

    public Graph getGraph() {
        return getModel().getGraph();
    }

    public boolean conforms() { return entries.isEmpty(); }


    public static class Builder {
        private final List<ReportItem> entries = new ArrayList<>();
        private PrefixMapping prefixes = new PrefixMappingImpl();

        public Builder() { }

        public void addPrefixes(PrefixMapping pmap) {
            this.prefixes.setNsPrefixes(pmap);
        }

        public boolean isEmpty() { return entries.isEmpty(); }

        public List<ReportItem> getItems() { return new ArrayList<>(entries); }

        public void addReportItem(ReportItem e) {
            entries.add(e);
        }

        public ShexReport build() {
            return new ShexReport(entries, prefixes);
        }

    }
}
