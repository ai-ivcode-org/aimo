package org.ivcode.aimo.pluginmanager.loader

import java.net.URL
import java.net.URLClassLoader

/**
 * Common package/class patterns that should always be loaded from the parent classloader.
 *
 * Entries may end with an asterisk (`*`) to indicate a prefix match (e.g. `java.*` matches
 * `java.lang.String`). These entries are merged with any user-supplied allowed patterns.
 */
private val JAVA_PACKAGES = mutableSetOf(
    "java.*",
    "javax.*",
    "jdk.*",
    "sun.*",
    "com.sun.*"
)

/**
 * A child-first URLClassLoader that prefers to load classes from its own URLs except for
 * classes whose names match configured patterns. Matching patterns (including the default
 * Java/JDK package patterns) are always attempted to be loaded from the parent classloader first.
 *
 * @param urls classpath URLs available to this classloader
 * @param parent the parent ClassLoader to delegate to when a class matches the allowed patterns or as a fallback
 * @param allowedParentPatterns additional patterns indicating classes that should be loaded from the parent.
 *        Patterns ending with `*` are treated as prefixes (e.g. `org.example.*`), otherwise exact class name match.
 */
open class ParentLimitedClassloader(
    urls: Array<out URL> = emptyArray(),
    allowedParentPatterns: Set<String> = emptySet()
) : URLClassLoader(urls) {

    /**
     * The effective set of patterns that will be treated as allowed to load from the parent.
     * This is the union of the user-provided patterns and the built-in `JAVA_PACKAGES`.
     */
    private val inherited: Set<String> = allowedParentPatterns + JAVA_PACKAGES


    /**
     * Load the class with the given fully-qualified name.
     *
     * Loading strategy:
     * 1. If already loaded, return the loaded class.
     * 2. If the class name matches one of the `inherited` patterns, attempt to load from `parent` first.
     *    If the parent cannot find it, fall through to child-local loading.
     * 3. Attempt to load the class locally from this classloader (child-first).
     * 4. If not found locally, throw `ClassNotFoundException`.
     *
     * The method synchronizes on the class loading lock provided by the runtime to ensure correct
     * concurrent class loading semantics. If `resolve` is true, the loaded class will be resolved.
     *
     * @param name fully-qualified class name to load
     * @param resolve if true, resolve the class after loading
     * @return the loaded `Class<*>` instance
     * @throws ClassNotFoundException if neither this loader nor the parent can locate the class
     */
    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        synchronized(this.getClassLoadingLock(name)) {
            // Return if already loaded
            findLoadedClass(name)?.let { loaded ->
                if (resolve) resolveClass(loaded)
                return loaded
            }

            // If pattern matches, prefer parent
            if (isAllowedFromParent(name)) {
                try {
                    val fromParent = parent.loadClass(name)
                    if (resolve) resolveClass(fromParent)
                    return fromParent
                } catch (ignored: ClassNotFoundException) {
                    // fall through to local loading
                }
            }

            // Try to load locally (child-first)
            val cls = findClass(name)
            if (resolve) resolveClass(cls)
            return cls
        }
    }

    /**
     * Determine whether the given class name matches any pattern that should be loaded from the parent.
     *
     * Pattern matching rules:
     * - If a pattern ends with `*`, treat it as a prefix and return true when `name` starts with that prefix.
     * - Otherwise treat the pattern as an exact class name and require equality.
     *
     * @param name fully-qualified class name to check
     * @return true if the name should be loaded from the parent according to the configured patterns
     */
    private fun isAllowedFromParent(name: String): Boolean {
        for (pattern in inherited) {
            if (pattern.endsWith("*")) {
                val prefix = pattern.removeSuffix("*")
                if (name.startsWith(prefix)) return true
            } else {
                if (name == pattern) return true
            }
        }
        return false
    }
}