package hexlab.config.impl;

import hexlab.config.ConfigMergeable;
import hexlab.config.ConfigValue;

interface MergeableValue extends ConfigMergeable {
    // converts a Config to its root object and a ConfigValue to itself
    ConfigValue toFallbackValue();
}
