package net.ggtools.maven

import spock.lang.Specification

import java.nio.file.Paths

import static net.ggtools.maven.RegexFileScannerTest.ROOT_PATH

/**
 * @author Christophe Labouisse on 26/07/2015.
 */
class RegexFileVisitorTest extends Specification {


    def "addResultIfMatch with net/ggtools/maven directory"() {
        setup:
        def visitor = new RegexFileScanner.RegexFileVisitor(ROOT_PATH, "^(net/ggtools)/maven\$")
        expect:
        visitor.addResultIfMatch(Paths.get("src/main/java/net/ggtools/maven"))
        def results = visitor.results
        results.size() == 1
        def firstResult = results.iterator().next()
        firstResult
        firstResult.groups.size() == 2
    }

    def "addResultIfMatch with net/ggtools/maven/RegexCopyMojo.java file"() {
        setup:
        def visitor = new RegexFileScanner.RegexFileVisitor(ROOT_PATH, "^(net/ggtools)/maven\$")
        expect:
        !visitor.addResultIfMatch(Paths.get("src/main/java/net/ggtools/maven/RegexCopyMojo.java"))
        visitor.results.empty
    }

    def "addResultIfMatch with partial expression"() {
        setup:
        def visitor = new RegexFileScanner.RegexFileVisitor(ROOT_PATH, "^(net/ggtools)/maven")
        expect:
        visitor.addResultIfMatch(Paths.get("src/main/java/net/ggtools/maven/RegexCopyMojo.java"))
        visitor.addResultIfMatch(Paths.get("src/main/java/net/ggtools/maven"))
        !visitor.addResultIfMatch(Paths.get("src/main/java/net/ggtools"))
        def results = visitor.results
        results.size() == 2
        def iterator = results.iterator()
        def firstResult = iterator.next()
        firstResult
        firstResult.groups.size() == 2
        def secondResult = iterator.next()
        secondResult
        secondResult.groups.size() == 2
    }
}
