tasks.register("printTransitiveDependencies") {
    doLast {
        // Get the library identifier (group:name) from command line
        def libraryIdentifier = project.findProperty("library") ?: ""
        if (!libraryIdentifier.contains(":")) {
            println "Please provide the library in 'group:name' format using -Plibrary=<group:name>"
            return
        }

        def (group, name) = libraryIdentifier.split(":")

        println "Finding transitive dependencies for $group:$name"
        configurations.compileClasspath.resolvedConfiguration.firstLevelModuleDependencies.each { dep ->
            if (dep.moduleGroup == group && dep.moduleName == name) {
                println "Direct dependency: ${dep.moduleGroup}:${dep.moduleName}:${dep.moduleVersion}"
                dep.children.each { transitive ->
                    println "  Transitive: ${transitive.moduleGroup}:${transitive.moduleName}:${transitive.moduleVersion}"
                }
                return
            }
        }
    }
}
