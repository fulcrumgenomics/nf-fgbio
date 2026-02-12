assemble:
	./gradlew assemble

clean:
	rm -rf .nextflow*
	rm -rf work
	rm -rf build
	./gradlew clean

test:
	./gradlew test

install:
	./gradlew installPlugin

release:
	./gradlew releasePlugin
