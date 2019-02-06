# Java Binding for Rosie pattern language

This is a Java binding for the *Rosie Pattern Language* library (`librosie`).


## What is Rosie?

Quoting from the [original project site](https://developer.ibm.com/code/open/projects/rosie-pattern-language/):

> Rosie Pattern Language is a supercharged alternative to regular expressions (regex), matching patterns against any input text. Rosie ships with hundreds of sample patterns for timestamps, network addresses, email addresses, CSV, JSON, and many more.


Useful links:

 * Official web site: http://rosie-lang.org/
 * Code repository: https://gitlab.com/rosie-pattern-language/rosie


# TODO

 - [ ] Write JavaDoc (maybe adapt from original librosie).
 - [ ] Write usage and add some examples here.


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
