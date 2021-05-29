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

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

import org.apache.jena.atlas.io.IO;
import org.apache.jena.atlas.lib.FileOps;
import org.junit.runners.model.InitializationError;
import shex.ShexShapes;
import shex.parser.ShexParseException;
import shex.parser.ShexParser;

public class RunnerShexBadSyntax extends AbstractRunnerFiles {
    public RunnerShexBadSyntax(Class<? > klass) throws InitializationError {
        super(klass, RunnerShexBadSyntax::makeShexSyntaxTest);
    }

    public static Runnable makeShexSyntaxTest(String filename) {
        return ()->fileBadSyntax(filename);
    }

    @Override
    protected List<String> getFiles(String directory) {
        Path src = Paths.get(directory);
        BiPredicate<Path, BasicFileAttributes> predicate = (path,attr)->attr.isRegularFile() && path.toString().endsWith(".shex");

        Set<String> excludes = new HashSet<>();

        // Bad facets. Needs more work.
        // [shex] post parse processing rules.
//        excludes.add("1iriLength2.shex");
//        excludes.add("1literalLength2.shex");
//        excludes.add("1unknowndatatypeMaxInclusive.shex");

        //---- Exclusions

        // -- Explicit inclusions
        List<String> files = new ArrayList<>();
        Set<String> includes = new HashSet<>();

        if ( includes.isEmpty() ) {
            try {
                Files.find(src, 1, predicate)
                .filter(p-> ! excludes.contains(p.getFileName().toString()))
                .sorted()
                .map(Path::toString)
                .forEach(files::add);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

//        // -- Make parameters
//        includes.forEach(fn->files.add(fn));
        return files;
    }

    public static ShexShapes fileBadSyntax(String filename) {
        String str = IO.readWholeFileAsUTF8(filename);
        InputStream input = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        try {
            ShexShapes shapes = ShexParser.parse(input, null);
            // Should not get here.
            System.out.print("-- ");
            System.out.println(FileOps.basename(filename));
            System.out.println(str);
            fail("Parsed negative syntax test");
            return shapes;
        } catch (ShexParseException ex) {
            return null;
        }
    }
}
