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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import shex.parser.ShexParseException;
import shex.parser.ShexParser;

@RunWith(Parameterized.class)
public class TestShexBadSyntax {

    private static String DIR = "files/spec/negativeSyntax";

    @Parameters(name = "{index}: {0}")
    public static Iterable<Object[]> data() {
        List<Object[]> data = new ArrayList<>();
        Path src = Paths.get(DIR);
        BiPredicate<Path, BasicFileAttributes> predicate = (path,attr)->attr.isRegularFile() && path.toString().endsWith(".shex");

        Set<String> excludes = new HashSet<>();
        //---- Exclusions

        // -- Explicit inclusions
        Set<String> includes = new HashSet<>();

        if ( includes.isEmpty() ) {
            try {
                Files.find(src, 1, predicate)
                .filter(p-> ! excludes.contains(p.getFileName().toString()))
                .map(Path::toString)
                .forEach(includes::add);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // -- Make parameters
        includes.forEach(fn->{
                    Object[] testArgs = new Object[] {FileOps.basename(fn), fn};
                    data.add(testArgs);
                });
        return data;
    }

    private String name;
    private String path;

    public TestShexBadSyntax(String name, String path) {
        this.name = name ;
        this.path = path;
    }

    @Test public void test() {
        ShexShapes shapes = shapesFromFile(path.toString());
    }

    public static ShexShapes shapesFromFile(String filename) {
        String str = IO.readWholeFileAsUTF8(filename);
        InputStream input = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        try {
            ShexShapes shapes = ShexParser.parse(input, null);
            System.out.print("-- ");
            System.out.println(FileOps.basename(filename));
            System.out.println(str);
            fail("Parsed negative syntax test");
            return shapes;
        } catch (ShexParseException ex) {
            return null;
//            System.out.print("-- ");
//            System.out.println(FileOps.basename(filename));
//            System.out.println(ex.getMessage());
//            System.out.println(str);
//            throw ex;
        }
    }

}