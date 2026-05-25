package org.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "collect-source-code")
public class CollectSourceCodeMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(
            property = "outputFile",
            defaultValue = "${project.build.directory}/collected-source-code.java"
    )
    private File outputFile;

    @Override
    public void execute() throws MojoExecutionException {
        List<String> sourceRoots = project.getCompileSourceRoots();

        try {
            Files.createDirectories(outputFile.toPath().getParent());
        } catch (IOException e) {
            throw new MojoExecutionException("Cannot create directory: "
                    + outputFile.getAbsolutePath(), e);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {

            for (String sourceRoot : sourceRoots) {
                Path rootPath = Paths.get(sourceRoot);

                if (!Files.exists(rootPath)) {
                    continue;
                }

                try (Stream<Path> walk = Files.walk(rootPath)) {
                    List<Path> javaFiles = walk
                            .filter(p -> p.toString().endsWith(".java"))
                            .sorted()
                            .toList();

                    for (int i = 0; i < javaFiles.size(); i++) {
                        Path javaFile = javaFiles.get(i);
                        writer.write("// FILE #" + (i + 1) + ": "
                                + rootPath.relativize(javaFile));
                        writer.newLine();

                        try (Stream<String> lines = Files.lines(javaFile)) {
                            lines.forEach(line -> {
                                try {
                                    writer.write(line);
                                    writer.newLine();
                                } catch (IOException e) {
                                    throw new UncheckedIOException("Error writing line: " + line, e);
                                }
                            });
                        }
                        writer.newLine();
                    }
                } catch (IOException e) {
                    throw new MojoExecutionException(
                            "Error processing source root: " + sourceRoot, e);
                }
            }
        } catch (IOException e) {
            throw new MojoExecutionException(
                    "Error writing to output file: " + outputFile, e);
        }
    }
}
