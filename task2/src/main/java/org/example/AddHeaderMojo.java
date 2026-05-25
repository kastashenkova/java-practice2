/*
 * Author: Kateryna Astashenkova
 * Date: 2026-05-26
 * File: AddHeaderMojo.java
 */
package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Class {@code AddHeaderMojo}.
 */
@Mojo(name = "add-headers")
public class AddHeaderMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(property = "author", defaultValue = "unknown")
    private String author;

    /**
     * Method {@code execute}.
     */
    @Override
    public void execute() throws MojoExecutionException {
        List<String> sourceRoots = project.getCompileSourceRoots();
        String today = LocalDate.now().toString();
        for (String sourceRoot : sourceRoots) {
            Path rootPath = Paths.get(sourceRoot);
            if (!Files.exists(rootPath)) {
                continue;
            }
            try (Stream<Path> walk = Files.walk(rootPath)) {
                List<Path> javaFiles = walk.filter(p -> p.toString().endsWith(".java")).toList();
                for (Path javaFile : javaFiles) {
                    addHeaderToFile(javaFile, author, today);
                }
            } catch (IOException e) {
                throw new MojoExecutionException("Error processing: " + sourceRoot, e);
            }
        }
    }

    /**
     * Method {@code addHeaderToFile}.
     * @param javaFile
     * @param author
     * @param date
     */
    private void addHeaderToFile(Path javaFile, String author, String date) throws MojoExecutionException {
        try {
            String content = Files.readString(javaFile);
            if (content.startsWith("/*")) {
                // header exists
                return;
            }
            String header = String.format("/*%n" + " * Author: %s%n" + " * Date: %s%n" + " * File: %s%n" + " */%n",
                    author, date, javaFile.getFileName());
            Files.writeString(javaFile, header + content);
            getLog().info("Header added: " + javaFile.getFileName());
        } catch (IOException e) {
            throw new MojoExecutionException("Error writing header to: " + javaFile, e);
        }
    }
}
