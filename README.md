# nf-fgbio

[![CI](https://github.com/fulcrumgenomics/nf-fgbio/actions/workflows/test.yml/badge.svg?branch=main)](https://github.com/fulcrumgenomics/nf-fgbio/actions/workflows/test.yml?query=branch%3Amain)
[![Nextflow](https://img.shields.io/badge/Nextflow%20DSL2-%E2%89%A522.10.2-blue.svg)](https://www.nextflow.io/)
[![Java Versions](https://img.shields.io/badge/java-8_|_11_|_17_|_21-blue)](https://github.com/fulcrumgenomics/nf-fgbio)


Use various [fgbio](https://github.com/fulcrumgenomics/fgbio/wiki/Read-Structures) functions and classes in your Nextflow scope.

<p>
<a href="https://fulcrumgenomics.com"><img src=".github/logos/fulcrumgenomics.svg" alt="Fulcrum Genomics" height="100"/></a>
</p>

[Visit us at Fulcrum Genomics](https://www.fulcrumgenomics.com) to learn more about how we can power your Bioinformatics with nf-fgbio and beyond.

<a href="mailto:contact@fulcrumgenomics.com?subject=[GitHub inquiry]"><img src="https://img.shields.io/badge/Email_us-brightgreen.svg?&style=for-the-badge&logo=gmail&logoColor=white"/></a>
<a href="https://www.fulcrumgenomics.com"><img src="https://img.shields.io/badge/Visit_Us-blue.svg?&style=for-the-badge&logo=wordpress&logoColor=white"/></a>

## Quickstart

Add the plugin to your Nextflow config:

```nextflow
plugins { id 'nf-fgbio' }
```

## readStructure

A [Read Structure](https://github.com/fulcrumgenomics/fgbio/wiki/Read-Structures) refers to a String that describes how the bases in a sequencing run should be allocated into logical reads.
The `readStructure` function converts a string into an [`fgbio` `ReadStructure` object](https://www.javadoc.io/doc/com.fulcrumgenomics/fgbio_2.13/latest/com/fulcrumgenomics/util/ReadStructure.html).
This function can be used to validate a read structure, as well as query and manipulate the read structure and its read segments.

For example:


```nextflow
include { readStructure } from 'plugin/nf-fgbio'

channel.of("12M138T", "4M3S12B100T")
  .map { it -> readStructure(it) }
  .map { it -> it.segments() }
  .view()
```


The above example creates a channel with two read structures (each as strings), converts them into a [`fgbio` `ReadStructure` object](https://www.javadoc.io/doc/com.fulcrumgenomics/fgbio_2.13/latest/com/fulcrumgenomics/util/ReadStructure.html),
and returns a vector of _read segments_ for each read structure.

```console
Vector(12M, 138T)
Vector(4M, 3S, 12B, 100T)
```

## fromSampleSheet

The `fromSampleSheet` factory method parses a [sample sheet](https://www.javadoc.io/static/com.fulcrumgenomics/fgbio_2.13/2.5.21/com/fulcrumgenomics/illumina/SampleSheet.html) from a file and returns a list of [`fgbio` `Sample` data objects](https://www.javadoc.io/doc/com.fulcrumgenomics/fgbio_2.13/latest/com/fulcrumgenomics/illumina/Sample.html).
This allows downstream processes to operate per-sample, with sample metadata stored in the `Sample` object.
For example:

```nextflow
include { fromSampleSheet } from 'plugin/nf-fgbio'

channel.fromSampleSheet("./plugins/nf-fgbio/build/resources/main/samplesheet.csv")
  .map { it -> it.sampleName }
  .view()
```

yields a channel of sample names:

```console
Sample_Name_1
Sample_Name_2
Sample_Name_3
Sample_Name_4
Sample_Name_5
Sample_Name_6
Sample_Name_7
Sample_Name_8
Sample_Name_9
Sample_Name_10
Sample_Name_11
Sample_Name_12
```

The `lane` option can be specified to restrict to samples from a specific lane.

```nextflow
channel.fromSampleSheet("/path/to/samplesheet.csv", lane: 1)
```

## Testing the Plugin Locally

Execute the following to compile and run unit tests for the plugin:

```
make compile
make test
```

To install the plugin for use in local workflows (_e.g._ not internet connected), execute the following:

```
make install-local
```

## Developing the Plugin Locally


Execute the following to build the plugin along with Nextflow source files:

```
make compile-with-nextflow
```

Test your changes to the plugin on a Nextflow script like:

```bash
NXF_PLUGINS_DEV="${PWD}/plugins" nextflow/launch.sh run <script.nf> -plugins nf-fgbio
```

## Publishing to GitHub

After bumping the version of the plugin in the file [`MANIFEST.MF`](./plugins/nf-fgbio/src/resources/META-INF/MANIFEST.MF), execute the following:

```
GITHUB_TOKEN=... GITHUB_USERNAME=... GITHUB_COMMIT_EMAIL=... make publish-to-github
```
