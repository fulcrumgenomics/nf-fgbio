package nextflow.fgbio

import groovy.transform.CompileStatic
import groovyx.gpars.dataflow.DataflowWriteChannel

import java.nio.file.Path
import java.nio.file.Paths

import scala.None
import scala.Option

import nextflow.Session
import nextflow.Channel
import nextflow.plugin.extension.Factory
import nextflow.plugin.extension.Function
import nextflow.plugin.extension.PluginExtensionPoint
import nextflow.util.CheckHelper
import nextflow.extension.CH

import com.fulcrumgenomics.illumina.Sample
import com.fulcrumgenomics.illumina.SampleSheet
import com.fulcrumgenomics.illumina.SampleSheet$
import com.fulcrumgenomics.util.ReadStructure
import com.fulcrumgenomics.util.ReadStructure$

/** The nf-fgbio Nextflow plugin entrypoint. */
@CompileStatic
class FgbioExtension extends PluginExtensionPoint {

	private static final Map SAMPLE_SHEET_PARAMS = [
		lane: Integer
    ]

	private Session session


	@Override
    void init(Session session) {
		this.session = session
	}

	@Function
	ReadStructure readStructure(String readStructure) {
		ReadStructure$.MODULE$.apply(readStructure)
	}

	@Factory
    DataflowWriteChannel fromSampleSheet(String path) {
        fromSampleSheet(Collections.emptyMap(), path)
    }


	@Factory
    DataflowWriteChannel fromSampleSheet(Map opts, String path) {
		CheckHelper.checkParams('fromSampleSheet', opts, SAMPLE_SHEET_PARAMS)
		return pathToChannel(path, opts)
	}

	protected DataflowWriteChannel pathToChannel(String path, Map opts) {
        final channel = CH.create()

		session.addIgniter {-> 
			def sampleSheetPath = Paths.get(path)
			Option<java.lang.Object> lane = Option.apply(null)
			if( opts.lane ) {
				lane = Option.apply(opts.lane)
			}
			def sampleSheet = SampleSheet$.MODULE$.apply(sampleSheetPath, lane)
			for( int i=0; i < sampleSheet.size(); i++) {
				channel.bind(sampleSheet.get(i))
			}
			channel.bind(Channel.STOP)
		}
        return channel
    }
}
