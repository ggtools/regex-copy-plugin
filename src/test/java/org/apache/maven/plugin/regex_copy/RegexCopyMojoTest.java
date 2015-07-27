package org.apache.maven.plugin.regex_copy;

import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.project.MavenProject;
import org.assertj.core.data.MapEntry;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

public class RegexCopyMojoTest {

    @Rule
    public MojoRule rule = new MojoRule();

    private MavenProject mavenProject;

    private File pom;

    private final static File TARGET_DIRECTORY  = new File("target/test-harness/regex-copy");

    @Before
    public void setUp() throws Exception {
        mavenProject = rule.readMavenProject(new File("src/test/resources/unit/regex-copy"));
        assertThat(mavenProject).isNotNull();
        pom = mavenProject.getFile();
        assertThat(pom).isNotNull().exists();
        if (!TARGET_DIRECTORY.exists()) {
            TARGET_DIRECTORY.mkdirs();
        }
    }

    @Test
    public void testParameters() throws Exception {
        Mojo mojo = rule.lookupMojo("regex-copy", pom);
        assertThat(mojo).isNotNull();
        assertThat(rule.getVariablesAndValuesFromObject(mojo)).contains(
                MapEntry.entry("sourceDirectory", new File("src/main/java")),
                MapEntry.entry("destinationDirectory", TARGET_DIRECTORY),
                MapEntry.entry("source", "(net/ggtools/maven)/(.+)\\.java"),
                MapEntry.entry("destination", "{2}/Test.java")
        );
    }

    @Test
    public void testRegexCopyGoal() throws Exception {
        Path tempDirectory = Files.createTempDirectory(TARGET_DIRECTORY.toPath(), "regex-copy-goal");
        File tempDirFile = tempDirectory.toFile();
        tempDirFile.deleteOnExit();

        Mojo mojo = rule.lookupMojo("regex-copy", pom);
        rule.setVariableValueToObject(mojo, "destinationDirectory", tempDirFile);
        assertThat(rule.getVariablesAndValuesFromObject(mojo)).contains(MapEntry.entry("destinationDirectory", tempDirFile));
        mojo.execute();
        assertThat(tempDirectory.resolve("RegexCopyMojo/Test.java").toFile()).exists().isFile();
        assertThat(tempDirectory.resolve("RegexFileScanner/Test.java").toFile()).exists().isFile();
    }
}
