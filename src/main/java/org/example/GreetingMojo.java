package org.example;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "sayhi")
public class GreetingMojo extends AbstractMojo
{
    @Override
    public void execute() {
        getLog().info("Hello World!");
    }
}
