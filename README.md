# regex-copy-plugin

[![Build Status](https://travis-ci.org/ggtools/regex-copy-plugin.svg?branch=master)](https://travis-ci.org/ggtools/regex-copy-plugin)

## Description

The maven resource plugins lacks the possibility of renaming the resources during the copy process. This plugin aims at fixing this by providing a versatile renaming system. In a nutshell a regular expression will be used to match the source resources and the groups from this regular expression will be used to build the destination.

## Configuration

```xml
<plugin>
    <artifactId>regex-copy-maven-plugin</artifactId>
    <configuration>
        <sourceDirectory>src/main/java</sourceDirectory>
        <destinationDirectory>target/test-harness/regex-copy</destinationDirectory>
        <source>(net/ggtools/maven)/(.+)\.java</source>
        <destination>{2}/Test.java</destination>
    </configuration>
</plugin>
```
