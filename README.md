# Java Binding for Rosie pattern language

This is a Java binding for the *Rosie Pattern Language* library (`librosie`).

It requires a Rosie installation, which must be done separately. See the [Rosie repository](https://gitlab.com/rosie-pattern-language/rosie).


## What is Rosie?

Quoting from the [original project site](https://rosie-lang.org/about/):

> Rosie is a supercharged alternative to Regular Expressions (regex), matching patterns against any input text. Rosie ships with a standard library of patterns for matching timestamps, network addresses, email addresses, CSV files, JSON, and many more common syntactic forms.


Useful links:

 * Official web site: http://rosie-lang.org/
 * Code repository: https://gitlab.com/rosie-pattern-language/rosie


# TODO

 - Replace jackson-databind with something else?
 - Add some examples here.


# Usage

To include in your Maven project, add the following dependency to the pom:

```
<dependency>
    <groupId>com.github.antoniomacri</groupId>
    <artifactId>rosie-pattern-language</artifactId>
    <version>${rosie-pattern-language.version}</version>
</dependency>
```
specifying the version number.


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
