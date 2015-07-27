package net.ggtools.maven;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

/**
 */
@Mojo(name = "regex-copy")
public class RegexCopyMojo extends AbstractMojo {

    private static final CopyOption[] COPY_OPTIONS_WITH_OVERWRITE = new CopyOption[]{StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING};

    private static final CopyOption[] COPY_OPTIONS_WITHOUT_OVERWRITE = new CopyOption[]{StandardCopyOption.COPY_ATTRIBUTES};

    @Parameter(defaultValue = "${project.basedir}")
    private File sourceDirectory;

    @Parameter(defaultValue = "${project.build.directory}")
    private File destinationDirectory;

    @Parameter(property = "regex-copy.source", required = true)
    private String source;

    @Parameter(property = "regex-copy.destination", required = true)
    private String destination;

    @Parameter(property = "regex-copy.overwrite", defaultValue = "false")
    private boolean overwrite;

    public void execute() throws MojoExecutionException {
        getLog().info("Performing regex-copy");
        getLog().debug("Getting candidate files and directories");
        try {
            List<RegexFileScanner.Result> results = RegexFileScanner.scan(sourceDirectory.toPath(), source);
            getLog().info("Got " + results.size() + " files or directories");
            getLog().debug("Results: " + results);
            for (RegexFileScanner.Result result : results) {
                copyResult(result);
            }
        } catch (IOException e) {
            String msg = "Error while retrieving candidate files";
            getLog().error(msg, e);
            throw new MojoExecutionException(msg, e);
        }
    }

    private Path mapResultToDestination(RegexFileScanner.Result result) {
        String resultDest = destination;
        for (int i = 0; i < result.getGroups().length; i++) {
            String group = result.getGroups()[i];
            if (group != null) {
                resultDest = resultDest.replaceAll("\\{" + i + "\\}", group);
            }
        }
        return destinationDirectory.toPath().resolve(resultDest);
    }

    private void copyResult(RegexFileScanner.Result result) throws IOException {
        Path destPath = mapResultToDestination(result).toAbsolutePath();
        if (!Files.isRegularFile(result.getPath())) {
            getLog().warn("Only files are accepted at the moment skipping " + result.getPath());
            return;
        }

        if (!Files.exists(destPath.getParent())) {
            Files.createDirectories(destPath.getParent());
        }

        try {
            Files.copy(result.getPath(), destPath, overwrite ? COPY_OPTIONS_WITH_OVERWRITE : COPY_OPTIONS_WITHOUT_OVERWRITE);
        } catch (FileAlreadyExistsException e) {
            getLog().warn("File " + destPath + " already exists, skipping");
            getLog().debug(e);
        }
    }
}
