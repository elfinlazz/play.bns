/**
 *   Copyright (C) 2011-2012 Typesafe Inc. <http://typesafe.com>
 */
package hexlab.config;

/**
 * A set of options related to resolving substitutions. Substitutions use the
 * <code>${foo.bar}</code> syntax and are documented in the <a
 * href="https://github.com/typesafehub/config/blob/master/HOCON.md">HOCON</a>
 * spec.
 * <p>
 * Typically this class would be used with the method
 * {@link Config#resolve(ConfigResolveOptions)}.
 * <p>
 * This object is immutable, so the "setters" return a new object.
 * <p>
 * Here is an example of creating a custom {@code ConfigResolveOptions}:
 * 
 * <pre>
 *     ConfigResolveOptions options = ConfigResolveOptions.defaults()
 *         .setUseSystemEnvironment(false)
 * </pre>
 * <p>
 * In addition to {@link ConfigResolveOptions#defaults}, there's a prebuilt
 * {@link ConfigResolveOptions#noSystem} which avoids looking at any system
 * environment variables or other external system information. (Right now,
 * environment variables are the only example.)
 */
public final class ConfigResolveOptions {
    private final boolean useSystemEnvironment;
    private final boolean allowUnresolved;

    private ConfigResolveOptions(boolean useSystemEnvironment, boolean allowUnresolved) {
        this.useSystemEnvironment = useSystemEnvironment;
        this.allowUnresolved = allowUnresolved;
    }

    /**
     * Returns the default resolve options. By default the system environment
     * will be used and unresolved substitutions are not allowed.
     * 
     * @return the default resolve options
     */
    public static ConfigResolveOptions defaults() {
        return new ConfigResolveOptions(true, false);
    }

    /**
     * Returns resolve options that disable any reference to "system" data
     * (currently, this means environment variables).
     *
     * @return the resolve options with env variables disabled
     */
    public static ConfigResolveOptions noSystem() {
        return defaults().setUseSystemEnvironment(false);
    }

    /**
     * Returns options with use of environment variables set to the given value.
     *
     * @param value
     *            true to resolve substitutions falling back to environment
     *            variables.
     * @return options with requested setting for use of environment variables
     */
    public ConfigResolveOptions setUseSystemEnvironment(boolean value) {
        return new ConfigResolveOptions(value, allowUnresolved);
    }

    /**
     * Returns whether the options enable use of system environment variables.
     * This method is mostly used by the config lib internally, not by
     * applications.
     *
     * @return true if environment variables should be used
     */
    public boolean getUseSystemEnvironment() {
        return useSystemEnvironment;
    }

    /**
     * Returns options with "allow unresolved" set to the given value. By
     * default, unresolved substitutions are an error. If unresolved
     * substitutions are allowed, then a future attempt to use the unresolved
     * value may fail, but {@link Config#resolve(ConfigResolveOptions)} itself
     * will now throw.
     * 
     * @param value
     *            true to silently ignore unresolved substitutions.
     * @return options with requested setting for whether to allow substitutions
     * @since 1.2.0
     */
    public ConfigResolveOptions setAllowUnresolved(boolean value) {
        return new ConfigResolveOptions(useSystemEnvironment, value);
    }

    /**
     * Returns whether the options allow unresolved substitutions. This method
     * is mostly used by the config lib internally, not by applications.
     * 
     * @return true if unresolved substitutions are allowed
     * @since 1.2.0
     */
    public boolean getAllowUnresolved() {
        return allowUnresolved;
    }
}
