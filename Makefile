version ?= $(shell grep 'Plugin-Version' plugins/nf-fgbio/src/resources/META-INF/MANIFEST.MF | awk '{ print $$2 }')

.PHONY: all
all: compile build

.PHONY: clean
clean:
	./gradlew clean

.PHONY: compile
compile:
	./gradlew compileGroovy

.PHONY: compile-with-nextflow
compile-with-nextflow:
	grep -qxF 'includeBuild("nextflow")' settings.gradle || echo 'includeBuild("nextflow")' >> settings.gradle
	./gradlew :nextflow:exportClasspath assemble
	sed -i.backup '/includeBuild("nextflow")/d' settings.gradle
	rm -f settings.gradle.backup

.PHONY: test
test:
	./gradlew :plugins:nf-fgbio:test --warning-mode all

.PHONY: build
build:
	./gradlew copyPluginZip

.PHONY: install-local
install-local: build
	mkdir -p ${HOME}/.nextflow/plugins/
	rm -rf ${HOME}/.nextflow/plugins/nf-fgbio-${version}
	cp -r build/plugins/nf-fgbio-${version} ${HOME}/.nextflow/plugins/nf-fgbio-${version}

.POHNY: publish-to-github
publish-to-github:
	./gradlew plugins:upload
