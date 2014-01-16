package com.bah.jmockit.merge;

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

import static java.util.Arrays.asList;

import java.io.File;
import java.lang.annotation.Retention;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import mockit.coverage.CodeCoverage;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.shared.model.fileset.FileSet;
import org.apache.maven.shared.model.fileset.util.FileSetManager;
import org.apache.maven.project.MavenProject;

/**
 * Goal that merges Jmockit .ser file test results.
 */
@Mojo(name = "merge-jmockit", defaultPhase = LifecyclePhase.VERIFY)
public class MergeJMockitMojo extends AbstractMojo {

  @Parameter(defaultValue = "${project}", required=true, readonly=true)
  private MavenProject project;

  /**
   * The coverage files
   */
  @Parameter(property = "coverageFiles", required = false)
  private FileSet coverageFiles = null;

  /**
   * Where to put the report.
   */
  @Parameter(defaultValue = "${project.build.directory}/coverage-report", property = "coverage-outputDir", required = true)
  private File outputDir;

  /**
   * What format report to produce.
   */
  @Parameter(defaultValue = "html", property = "coverage-output", required = true)
  String outputFormat;

  static final String OUTPUT_DIR_PROP = "coverage-outputDir";
  static final String OUTPUT_FMT_PROP = "coverage-output";

  public void execute() throws MojoExecutionException {
    if (coverageFiles.getDirectory() == null) {
      getLog().info("No coverage directory specified, using project base directory.");
      coverageFiles.setDirectory(project.getBasedir().getAbsolutePath());
    }
    if(coverageFiles.getIncludes().size() == 0){
      getLog().info("No coverage includes specified, using the default (**/coverage.ser)");
      coverageFiles.setIncludes(asList("**/coverage.ser"));
    }

    FileSetManager fileSetManager = new FileSetManager();

    Set<String> includedFiles = new HashSet<String>(
        asList(fileSetManager.getIncludedFiles(coverageFiles)));
    Set<String> excludedFiles = new HashSet<String>(
        asList(fileSetManager.getExcludedFiles(coverageFiles)));
    includedFiles.removeAll(excludedFiles);

    
    if(includedFiles.size() == 0){
      getLog().info("No files found to merge.");
      return;
    } else {
      getLog().info("The following files will be merged: " + includedFiles);
    }
    
    String oldOutput = System.setProperty(OUTPUT_DIR_PROP,
        outputDir.getAbsolutePath());
    String oldFormat = System.setProperty(OUTPUT_FMT_PROP, outputFormat);
    
    String[] resolvedFileNames = new String[includedFiles.size()];
    int i = 0;
    for(String file : includedFiles){
      resolvedFileNames[i] = new File(coverageFiles.getDirectory(), file).getAbsolutePath();
    }

    CodeCoverage.main(includedFiles.toArray(resolvedFileNames));

    if(oldOutput!=null)
      System.setProperty(OUTPUT_DIR_PROP, oldOutput);
    if(oldFormat!=null)
      System.setProperty(OUTPUT_FMT_PROP, oldFormat);

  }
}
