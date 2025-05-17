package nextflow.fgbio

import nextflow.Channel
import nextflow.plugin.Plugins
import nextflow.plugin.TestPluginDescriptorFinder
import nextflow.plugin.TestPluginManager
import nextflow.plugin.extension.PluginExtensionProvider
import org.pf4j.PluginDescriptorFinder
import spock.lang.Shared
import test.Dsl2Spec

import java.nio.file.Path

/** Unit tests for the nf-fgbio plugin that use virtual file systems and mocking to run. */
class FgbioTest extends Dsl2Spec{

    /** Share the plugin mode across all features in this specification. */
    @Shared String pluginsMode

    /** Setup the test class by loading all plugins. */
    def setup() {
        PluginExtensionProvider.reset()
        pluginsMode = System.getProperty('pf4j.mode')
        System.setProperty('pf4j.mode', 'dev')
        Path root = Path.of('.').toAbsolutePath().normalize()
        def manager = new TestPluginManager(root) {
            @Override
            protected PluginDescriptorFinder createPluginDescriptorFinder() {
                return new TestPluginDescriptorFinder() {
                    @Override
                    protected Path getManifestPath(Path pluginPath) {
                        return pluginPath.resolve('build/resources/main/META-INF/MANIFEST.MF')
                    }
                }
            }
        }
        Plugins.init(root, 'dev', manager)
    }

    /** Cleanup the test class by unloading and resetting all plugins. */
    def cleanup() {
        Plugins.stop()
        PluginExtensionProvider.reset()
        pluginsMode ? System.setProperty('pf4j.mode',pluginsMode) : System.clearProperty('pf4j.mode')
    }

    def 'should have the plugin installed but not imported and raise no exception if a fgbio is not found' () {
        when:
            String SCRIPT= '''
                channel.of('hi-mom')
            '''
        and:
            def result = new MockScriptRunner([:]).setScript(SCRIPT).execute()
        then:
            result.val == 'hi-mom'
            result.val == Channel.STOP
    }

    def 'should import the plugin and not raise an exception if a fgbio is not found but unused' () {
        when:
            String SCRIPT = '''
                include { readStructure } from 'plugin/nf-fgbio'
                channel.of('hi-mom')
            '''
        and:
            def result = new MockScriptRunner([:]).setScript(SCRIPT).execute()
        then:
            result.val == 'hi-mom'
            result.val == Channel.STOP
    }

    def 'should import the plugin and raise no exceptions when fgbio is found' () {
        when:
            String SCRIPT = '''
                include { readStructure } from 'plugin/nf-fgbio'
                channel.of('hi-mom')
            '''
        and:
            def result = new MockScriptRunner([:]).setScript(SCRIPT).execute()
        then:
            result.val == 'hi-mom'
            result.val == Channel.STOP
    }

    def 'should import the plugin and by default throw an exception for an invalid read structure' () {
        when:
            String SCRIPT = '''
                include { readStructure } from 'plugin/nf-fgbio'
                channel.of(readStructure("M1231"))
            '''
        and:
            new MockScriptRunner([:]).setScript(SCRIPT).execute()
        then:
            thrown IllegalArgumentException
    }

    def 'should import the plugin and return the correct value for a valid read structure' () {
        when:
            String SCRIPT = '''
                include { readStructure } from 'plugin/nf-fgbio'
                channel.of(readStructure('12M138T'))
            '''
        and:
            def result = new MockScriptRunner([:]).setScript(SCRIPT).execute()
        then:
            result.val.toString() == "12M138T"
            result.val == Channel.STOP
    }


    def 'should import the plugin and construct a channel of samples from a sample sheet' () {
        when:
            String SCRIPT = '''
                include { fromSampleSheet } from 'plugin/nf-fgbio'
                Channel.fromSampleSheet("src/resources/samplesheet.csv")
            '''
        and:
            def result = new MockScriptRunner([:]).setScript(SCRIPT).execute()
        then:
		result.val.sampleName == "Sample_Name_1"
		result.val.sampleName == "Sample_Name_2"
		result.val.sampleName == "Sample_Name_3"
		result.val.sampleName == "Sample_Name_4"
		result.val.sampleName == "Sample_Name_5"
		result.val.sampleName == "Sample_Name_6"
		result.val.sampleName == "Sample_Name_7"
		result.val.sampleName == "Sample_Name_8"
		result.val.sampleName == "Sample_Name_9"
		result.val.sampleName == "Sample_Name_10"
		result.val.sampleName == "Sample_Name_11"
		result.val.sampleName == "Sample_Name_12"
		result.val == Channel.STOP
    }
}
