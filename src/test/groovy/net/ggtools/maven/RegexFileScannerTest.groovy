package net.ggtools.maven

import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * @author Christophe Labouisse on 27/07/2015.
 */
class RegexFileScannerTest extends Specification {

    static final Path ROOT_PATH = Paths.get("src/main/java")

    def "scan with directory matching pattern"() {
        expect:
        def results = RegexFileScanner.scan(ROOT_PATH, "^(net/ggtools)/maven")
        results
        results.size() == 1
        def firstResult = results.iterator().next()
        firstResult
        firstResult.groups.size() == 2
    }

    def "scan with file matching pattern"() {
        expect:
        def results = RegexFileScanner.scan(ROOT_PATH,  "^(net/ggtools)/maven/(.*)\\.java")
        results
        results.size() == 2
        def iterator = results.iterator()
        def firstResult = iterator.next()
        firstResult
        firstResult.groups.size() == 3
        def secondResult = iterator.next()
        secondResult
        secondResult.groups.size() == 3
    }
}
