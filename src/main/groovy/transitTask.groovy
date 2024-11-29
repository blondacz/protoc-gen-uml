tasks.register("printTransitiveDependencies") {
    doLast {
        // Get the library identifier (group:name) from command line
        def libraryIdentifier = project.findProperty("library") ?: ""
        if (!libraryIdentifier.contains(":")) {
            println "Please provide the library in 'group:name' format using -Plibrary=<group:name>"
            return
        }

        def (group, name) = libraryIdentifier.split(":")
        def maxDepth = (project.findProperty("depth") ?: "1").toInteger()

        println "Finding transitive dependencies for $group:$name up to $maxDepth levels"

        configurations.compileClasspath.resolvedConfiguration.firstLevelModuleDependencies.each { dep ->
            if (dep.moduleGroup == group && dep.moduleName == name) {
                println "Direct dependency: ${dep.moduleGroup}:${dep.moduleName}:${dep.moduleVersion}"
                printTransitive(dep, 1, maxDepth)
                return
            }
        }
    }
}

// Recursive function to print transitive dependencies
def printTransitive(dep, currentLevel, maxDepth) {
    if (currentLevel > maxDepth) return

    dep.children.each { transitive ->
        println "  ${"  " * (currentLevel - 1)}Transitive (Level $currentLevel): ${transitive.moduleGroup}:${transitive.moduleName}:${transitive.moduleVersion}"
        printTransitive(transitive, currentLevel + 1, maxDepth)
    }
}
