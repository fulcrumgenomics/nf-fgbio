package nextflow.fgbio

import groovy.transform.CompileStatic
import nextflow.plugin.BasePlugin
import nextflow.plugin.Scoped
import org.pf4j.PluginWrapper


@CompileStatic
class FgbioPlugin extends BasePlugin {

    FgbioPlugin(PluginWrapper wrapper) {
        super(wrapper)
    }
}
