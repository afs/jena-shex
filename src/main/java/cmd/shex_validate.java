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

package cmd;

import org.apache.jena.atlas.logging.LogCtl;
import org.apache.jena.cmd.ArgDecl;
import org.apache.jena.cmd.CmdException;
import org.apache.jena.cmd.CmdGeneral;
import org.apache.jena.graph.Graph;
import org.apache.jena.graph.Node;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RiotException;
import org.apache.jena.shex.*;
import org.apache.jena.sys.JenaSystem;

/** ShEx validation.
 * <p>
 * Usage: <code>shex validate [--text] --shapes SHAPES --data DATA</code>
 */
public class shex_validate extends CmdGeneral {

    static {
        LogCtl.setLogging();
        JenaSystem.init();
    }

    private ArgDecl argOutputText  = new ArgDecl(false, "--text");
    //private ArgDecl argOutputRDF   = new ArgDecl(false, "--rdf");
    private ArgDecl argData        = new ArgDecl(true, "--data", "--datafile", "-d");
    private ArgDecl argShapes      = new ArgDecl(true, "--shapes", "--shapesfile", "--shapefile", "-s");
    private ArgDecl argShapeMap    = new ArgDecl(true, "--map");
    private ArgDecl argTargetNode  = new ArgDecl(true, "--target", "--node", "-n");

    private String  datafile = null;
    private String  shapesfile = null;
    private String  mapfile = null;
    private String  targetNode = null;  // Parse later.
    private boolean textOutput = false;

    public static void main (String... argv) {
        new shex_validate(argv).mainRun() ;
    }

    public shex_validate(String[] argv) {
        super(argv) ;
        super.add(argShapes,        "--shapes", "Shapes file");
        super.add(argData,          "--data",   "Data file");
        super.add(argShapeMap,      "--map",    "ShEx shapes map");
        super.add(argTargetNode,    "--target", "Validate specific node [may use prefixes from the data]");
        super.add(argOutputText,    "--text",   "Output in concise text format");
        //super.add(argOutputRDF,  "--rdf", "Output in RDF (Turtle) format");
    }

    @Override
    protected String getSummary() {
        return getCommandName()+" [--target URI|--map mapsFile] --shapes shapesFile --data dataFile";
    }

    @Override
    protected void processModulesAndArgs() {
         super.processModulesAndArgs();

         datafile = super.getValue(argData);
         shapesfile = super.getValue(argShapes);

         // No -- arguments, use act on single file of shapes and data.
         if ( datafile == null && shapesfile == null ) {
             if ( positionals.size() == 1 ) {
                 datafile = positionals.get(0);
                 shapesfile = positionals.get(0);
             }
         }

         if ( datafile == null )
             throw new CmdException("Usage: "+getSummary());
         if ( shapesfile == null )
             shapesfile = datafile;

         textOutput = super.hasArg(argOutputText);

         if ( contains(argTargetNode) ) {
             targetNode = getValue(argTargetNode);
         }
         if ( contains(argShapeMap) )
             mapfile = getValue(argShapeMap);

         if ( targetNode != null && mapfile != null )
             throw new CmdException("Only one of target node and map file");
    }

    @Override
    protected void exec() {

        ShexSchema shapes = Shex.readShapes(shapesfile);

        Graph dataGraph = load(datafile, "data file");

//        if ( targetNode != null ) {
//            String x = dataGraph.getPrefixMapping().expandPrefix(targetNode);
//            Node node = NodeFactory.createURI(x);
//            ShexValidation.validate(graphData, shapes, shapeRef, focus)
//        }

        if ( mapfile != null ) {
            ShexShapeMap map = Shex.readShapesMap(mapfile);
            ValidationReport report = ShexValidation.validate(dataGraph, shapes, map);
            // XXX Print report function
            // ShexLib.
            if ( report.conforms() ) {
                System.out.println("OK");
            } else {
                report.getEntries().forEach(e->System.out.println(e));
            }
            System.exit(0);
        }


//        if ( isVerbose() )
//            ValidationContext.VERBOSE = true;

//        ValidationReport report = ( node != null )
//            ? ShaclValidator.get().validate(shapesGraph, dataGraph, node)
//            : ShaclValidator.get().validate(shapesGraph, dataGraph);

        Node shapeRef = null;
        Node focus = null;
        ValidationReport report = ShexValidation.validate(dataGraph, shapes, shapeRef, focus);

        if ( report.conforms() )
            System.out.println("OK");
        else {
            report.getEntries().forEach(e->System.out.println(e));
        }
//
//
//        if ( textOutput )
//            ShLib.printReport(report);
//        else
//            RDFDataMgr.write(System.out, report.getGraph(), Lang.TTL);
    }

    private Graph load(String filename, String scope) {
        try {
            Graph graph = RDFDataMgr.loadGraph(filename);
            return graph;
        } catch (RiotException ex) {
            System.err.println("Loading "+scope);
            throw ex;
        }
    }

    @Override
    protected String getCommandName() {
        return "shex_validate";
    }
}
