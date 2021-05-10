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

package shex.runner;

import static org.junit.Assert.assertEquals;

import dev.DevShex;
import org.apache.jena.arq.junit.manifest.ManifestEntry;
import org.apache.jena.atlas.io.IO;
import org.apache.jena.atlas.lib.IRILib;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.riot.RDFDataMgr;
import shex.*;
import shex.expressions.PLib;

public class RunShexValidation implements Runnable {

    private final ManifestEntry entry;
    private final Resource schema;
    private final Node shape;
    private final Resource data;
    private final Node focus;
    private final ShexShapes shapes;
    private final boolean positiveTest;

    public RunShexValidation(ManifestEntry entry) {
        this.entry = entry;
        Resource action = entry.getAction();
        this.schema = action.getProperty(ShexT.schema).getResource();
        this.shape  = action.getProperty(ShexT.shape).getResource().asNode();
        this.data   = action.getProperty(ShexT.data).getResource();
        // URI or literal.
        this.focus = action.getProperty(ShexT.focus).getObject().asNode();
        this.shapes = Shex.shapesFromFile(schema.getURI());
        this.positiveTest = entry.getTestType().equals(ShexT.cValidationTest);
    }

    @Override
    public void run() {
        Graph graph = RDFDataMgr.loadGraph(data.getURI());
        try {
            ValidationReport report = V.validate(graph, shapes, shape, focus);
            boolean b = (positiveTest == report.conforms());
            if ( !b ) {
                System.out.println("-- "+entry.getName());
                System.out.println("Schema:   "+schema.getURI());
                System.out.println("Shape:    "+PLib.displayStr(shape));
                System.out.println("Focus:    "+PLib.displayStr(focus));
                System.out.println("Positive: "+positiveTest);
                String fn = IRILib.IRIToFilename(schema.getURI());
                String s = IO.readWholeFileAsUTF8(fn);
                System.out.print(s);
                if ( ! s.endsWith("\n") )
                    System.out.println();
                report.getEntries().forEach(System.out::println);
                System.out.println();
            }

            assertEquals(entry.getName(), positiveTest, report.conforms());
        } catch (Exception ex) {
            ex.printStackTrace();
            DevShex.printShapes(shapes);
            System.err.println(entry.getEntry().getLocalName());
            throw ex;
        }
    }


}
