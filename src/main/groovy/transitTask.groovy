tasks.register("resolveLibraryDependencies") {
    doLast {
        // Get the library identifier (group:name[:version]) from the command line
        def libraryIdentifier = project.findProperty("library") ?: ""
        if (!libraryIdentifier.contains(":")) {
            println "Please provide the library in 'group:name[:version]' format using -Plibrary=<group:name[:version]>"
            return
        }

        def maxDepth = (project.findProperty("depth") ?: "1").toInteger()
        def foundInClasspath = false

        println "Resolving dependencies for $libraryIdentifier up to $maxDepth levels"

        // Split the library identifier into group:name[:version]
        def (group, name, version) = libraryIdentifier.split(":") + [null]

        // Try to find the library in the compileClasspath configuration
        configurations.compileClasspath.resolvedConfiguration.firstLevelModuleDependencies.each { dep ->
            if (dep.moduleGroup == group && dep.moduleName == name && (version == null || dep.moduleVersion == version)) {
                println "Found library in compileClasspath configuration."
                println "${dep.moduleGroup}:${dep.moduleName}:${dep.moduleVersion}"
                printTransitive(dep, 1, maxDepth, "")
                foundInClasspath = true
                return
            }
        }

        // If not found, fall back to detached configuration
        if (!foundInClasspath) {
            println "Library not found in compileClasspath. Resolving with a detached configuration..."
            def dependencyString = version ? "$group:$name:$version" : "$group:$name"
            def configuration = configurations.detachedConfiguration(dependencies.create(dependencyString))
            configuration.resolvedConfiguration.firstLevelModuleDependencies.each { dep ->
                println "Created library dependency on the fly."
                println "${dep.moduleGroup}:${dep.moduleName}:${dep.moduleVersion}"
                printTransitive(dep, 1, maxDepth, "")
            }
        }
    }
}

// Recursive function to print transitive dependencies in ASCII-art tree format
def printTransitive(dep, currentLevel, maxDepth, prefix) {
    if (currentLevel > maxDepth) return

    def childCount = dep.children.size()
    dep.children.eachWithIndex { transitive, index ->
        def isLastChild = index == childCount - 1
        def connector = isLastChild ? "\\---" : "+---"
        println "$prefix$connector ${transitive.moduleGroup}:${transitive.moduleName}:${transitive.moduleVersion}"

        // Prepare the prefix for the next level
        def newPrefix = prefix + (isLastChild ? "    " : "|   ")
        printTransitive(transitive, currentLevel + 1, maxDepth, newPrefix)
    }
}
