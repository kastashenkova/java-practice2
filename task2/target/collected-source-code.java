// FILE #1: org\example\AddHeaderMojo.java
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

// FILE #2: org\example\GenerateJavadocMojo.java
/*
 * Author: Kateryna Astashenkova
 * Date: 2026-05-26
 * File: GenerateJavadocMojo.java
 */
package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * Class {@code GenerateJavadocMojo}.
 */
@Mojo(name = "generate-comments")
public class GenerateJavadocMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * Method {@code execute}.
     */
    @Override
    public void execute() throws MojoExecutionException {
        List<String> sourceRoots = project.getCompileSourceRoots();
        for (String sourceRoot : sourceRoots) {
            Path rootPath = Paths.get(sourceRoot);
            if (!Files.exists(rootPath)) {
                continue;
            }
            try (Stream<Path> walk = Files.walk(rootPath)) {
                List<Path> javaFiles = walk.filter(p -> p.toString().endsWith(".java")).toList();
                for (Path javaFile : javaFiles) {
                    processFile(javaFile);
                }
            } catch (IOException e) {
                throw new MojoExecutionException("Error processing: " + sourceRoot, e);
            }
        }
    }

    /**
     * Method {@code processFile}.
     * @param javaFile
     */
    private void processFile(Path javaFile) throws MojoExecutionException {
        try {
            CompilationUnit cu = StaticJavaParser.parse(javaFile);
            boolean modified = false;
            for (ClassOrInterfaceDeclaration cls : cu.findAll(ClassOrInterfaceDeclaration.class)) {
                if (cls.getJavadocComment().isEmpty()) {
                    String comment = String.format("*%n" + " * Class {@code %s}.%n" + " ", cls.getNameAsString());
                    cls.setJavadocComment(comment);
                    modified = true;
                    getLog().info("  Class javadoc added: " + cls.getNameAsString());
                }
            }
            for (MethodDeclaration method : cu.findAll(MethodDeclaration.class)) {
                if (method.getJavadocComment().isEmpty()) {
                    StringBuilder comment = new StringBuilder();
                    comment.append(String.format("*%n" + " * Method {@code %s}.%n", method.getNameAsString()));
                    method.getParameters().forEach(param -> comment.append(String.format(" * @param %s%n", param.getNameAsString())));
                    if (!method.getType().asString().equals("void")) {
                        comment.append(" * @return \n");
                    }
                    comment.append(" ");
                    method.setJavadocComment(comment.toString());
                    modified = true;
                    getLog().info("  Method javadoc added: " + method.getNameAsString());
                }
            }
            if (modified) {
                Files.writeString(javaFile, cu.toString());
                getLog().info("Updated: " + javaFile.getFileName());
            } else {
                getLog().info("Skipping (already documented): " + javaFile.getFileName());
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Error processing file: " + javaFile, e);
        }
    }
}

// FILE #3: org\example\Main.java
/*
 * Author: Kateryna Astashenkova
 * Date: 2026-05-26
 * File: Main.java
 */
package org.example;

/**
 * Class {@code Main}.
 */
public class Main {

    /**
     * Method {@code main}.
     */
    static void main() {
        System.out.println("Test class for Task 2");
    }
}

