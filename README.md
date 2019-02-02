# Java Binding for Rosie pattern language

This is a Java binding for the *Rosie Pattern Language* library.

**Disclaimer.** The latest Rosie native library is an alpha release. This Java binding is also in an alpha stage and therefore it is not stable and is subject to breaking changes.


## What is Rosie?

Quoting from the [official project site](https://developer.ibm.com/code/open/projects/rosie-pattern-language/):

> Rosie Pattern Language is a supercharged alternative to regular expressions (regex), matching patterns against any input text. Rosie ships with hundreds of sample patterns for timestamps, network addresses, email addresses, CSV, JSON, and many more.
>
> [...]
>
> The Rosie Pattern Language (RPL) overcomes the limitations of regular expressions by providing a language that makes it easy to specify the information you want to extract from your data. There are many similarities with regular expressions, so it’s easy to get started. RPL lets you compose simple patterns into complex ones, organize your patterns into packages, and even define transformations to be done on the matched data.
>
> Moreover, RPL is based on Parsing Expression Grammars, which can express recursive structures (like XML and JSON) that regular expressions cannot. And Parsing Expression Grammars can run in linear time in the size of the input data, making them a good choice for processing big data.
>
> The Rosie Pattern Engine is an implementation of an RPL compiler and an RPL runtime environment. Both components are written in the Lua language and use the LPEG package. The engine is a shared object file that can be linked with another application, and there is also a command line interface. It uses RPL patterns to extract information from input data and outputs structured JSON.


Useful links:

 * Github repository: https://github.com/jamiejennings/rosie-pattern-language
 * Official web site: http://rosie-lang.org/
 * Rosie introduction at IBM developerWorks: https://developer.ibm.com/code/open/projects/rosie-pattern-language/
 * Other articles at IBM developerWorks: https://developer.ibm.com/open/category/rosie-pattern-language/


# TODO

 * Keep up to date with `librosie` development.
 * Publish released packages to a Maven repository.
 * Use exceptions instead of return values to signal errors.
 * Support Windows Subsystem for Linux.


# Releasing

Modify the `settings.xml` in order to add the OSSRH server credentials:

```
<servers>
  <server>
    <id>ossrh</id>
    <username>...</username>
    <password>...</password>
  </server>
  ...
</servers>
```
and GPG configuration:
```
<profiles>
  <profile>
    <id>ossrh</id>
    <properties>
      <gpg.executable>gpg2</gpg.executable> <!-- optional -->
      <gpg.passphrase>...</gpg.passphrase>
    </properties>
  </profile>
  ...
</profiles>
```

Deploy a snapshot version to OSSRH by enabling the `ossrh` profile and running the usual

    mvn clean deploy

In order to deploy a release version, enable the `ossrh` profile and run the maven-release-plugin:

    mvn release:prepare

answering the prompts for versions and tags, followed by

    mvn release:perform

With the property `autoReleaseAfterClose` set to true an automated release to the Central Repository is performed (for release versions).
