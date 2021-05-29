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

package org.apache.jena.shex.runner;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.arq.junit.manifest.Prefix;
import org.apache.jena.arq.junit.runners.Directories;
import org.apache.jena.arq.junit.runners.Label;
import org.apache.jena.arq.junit.runners.RunnerOneTest;
import org.apache.jena.atlas.io.IndentedWriter;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

/**
 * Common super class for {@code @Runner(....)}
 * where the tests are one per file.
 */
public abstract class AbstractRunnerFiles extends ParentRunner<Runner> {
    private Description  description;
    private List<Runner> children = new ArrayList<>();

    public AbstractRunnerFiles(Class<? > klass, Function <String, Runnable> maker) throws InitializationError {
        super(klass);
        String label = getLabel(klass);
        if ( label == null )
            label = klass.getName();
        String prefix = getPrefix(klass);
        String[] directories = getDirectories(klass);
        description = Description.createSuiteDescription(label);

        for ( String directory : directories ) {
            // LEVEL per directory?
            List<String> files = getFiles(directory);
            if ( files.isEmpty() )
                //System.err.println("No files: "+label);
                throw new InitializationError("No files");

            for ( String file : files ) {
                RunnerOneTest runner = buildTest(file, maker, prefix);
                if ( runner != null ) {
                    description.addChild(runner.getDescription());
                    children.add(runner);
                }
            }
        }
    }

    protected abstract List<String> getFiles(String directory);

    // Print all manifests, top level and included.
    private static boolean PrintManifests = false;
    private static IndentedWriter out = IndentedWriter.stdout;

    public static RunnerOneTest buildTest(String filename, Function<String, Runnable> maker, String prefix) {
        Description description = Description.createSuiteDescription(filename);
        Runnable runnable = maker.apply(filename);
        String name = StringUtils.isEmpty(prefix)
                ? fixupName(filename)
                : prefix+" "+fixupName(filename);
        return new RunnerOneTest(filename, runnable);
    }

    // Keep Eclipse happy.
    public static String fixupName(String string) {
        string = string.replace('(', '[');
        string = string.replace(')', ']');
        return string;
    }

    private static String getLabel(Class<? > klass) {
        Label annotation = klass.getAnnotation(Label.class);
        return ( annotation == null ) ? null : annotation.value();
    }

    private static String getPrefix(Class<? > klass) {
        Prefix annotation = klass.getAnnotation(Prefix.class);
        return ( annotation == null ) ? null : annotation.value();
    }

    private static String[] getDirectories(Class<? > klass) throws InitializationError {
        Directories directories = klass.getAnnotation(Directories.class);
        if ( directories == null ) {
            throw new InitializationError(String.format("class '%s' must have a @Directories annotation", klass.getName()));
        }
        return directories.value();
    }

    @Override
    public Description getDescription() {
        return description;
    }

    @Override
    protected List<Runner> getChildren() {
        return children;
    }

    @Override
    protected Description describeChild(Runner child) {
        return child.getDescription();
    }

    @Override
    protected void runChild(Runner child, RunNotifier notifier) {
        child.run(notifier);
    }
}
