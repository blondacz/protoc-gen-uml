tasks.register("printTransitiveDependencies") {
    doLast {
        // Get the library identifier (group:name) from the command line
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
                println "${dep.moduleGroup}:${dep.moduleName}:${dep.moduleVersion}"
                printTransitive(dep, 1, maxDepth, "")
                return
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
