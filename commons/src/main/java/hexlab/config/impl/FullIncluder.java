/**
 *   Copyright (C) 2011-2012 Typesafe Inc. <http://typesafe.com>
 */
package hexlab.config.impl;

import hexlab.config.ConfigIncluder;
import hexlab.config.ConfigIncluderClasspath;
import hexlab.config.ConfigIncluderFile;
import hexlab.config.ConfigIncluderURL;

interface FullIncluder extends ConfigIncluder, ConfigIncluderFile, ConfigIncluderURL,
            ConfigIncluderClasspath {

}
