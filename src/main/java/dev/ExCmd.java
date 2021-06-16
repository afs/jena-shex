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

package dev;

import cmd.shex_validate;
import org.apache.jena.atlas.logging.LogCtl;
import org.apache.jena.graph.Graph;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RIOT;
import org.apache.jena.shex.*;
import org.apache.jena.shex.sys.ShexLib;
import org.apache.jena.sys.JenaSystem;

public class ExCmd {
    static {
        JenaSystem.init();
        LogCtl.setLog4j2();
        RIOT.getContext().set(RIOT.symTurtleDirectiveStyle, "sparql");
    }

    public static void main(String[] args) {
        // Usage: shex_validate [--target URI] --shapes shapesFile --data dataFile
        shex_validate.main("--shapes=examples/schema.shex",
                           "--data=examples/data.ttl",
                           "--map=examples/shape-map.shexmap"
                        );
        System.exit(0);
        String SHAPES = "examples/schema.shex";
        String SHAPES_MAP = "examples/shape-map.shexmap";
        String DATA = "examples/data.ttl";

        // -- Data
        System.out.println("Read data");
        Graph dataGraph = RDFDataMgr.loadGraph(DATA);
        System.out.println("Read shapes");
        ShexSchema shapes = Shex.readShapes(SHAPES);

        // -- Map
        System.out.println("Read shapes map");
        ShexShapeMap shapeMap = Shex.readShapeMap(SHAPES_MAP);

        // -- Validate
        System.out.println("Validate");
        ShexReport report = ShexValidation.validate(dataGraph, shapes, shapeMap);

        // -- Print report.
        ShexLib.printReport(report);
    }
}
