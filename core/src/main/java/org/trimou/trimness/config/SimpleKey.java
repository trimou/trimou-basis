/*
 * Copyright 2017 Trimness team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.trimou.trimness.config;

/**
 *
 * @author Martin Kouba
 */
public class SimpleKey implements Key {

    /**
     *
     * @param key
     * @param defaultValue
     * @return a configuration key
     */
    static SimpleKey fromKey(String key, Object defaultValue) {
        return new SimpleKey(key, Key.keyToEnv(key), defaultValue);
    }

    /**
     *
     * @param envKey
     * @param defaultValue
     * @return a configuration key
     */
    static SimpleKey fromEnvKey(String envKey, Object defaultValue) {
        return new SimpleKey(Key.envToKey(envKey), envKey, defaultValue);
    }

    private final String envKey;

    private final String key;

    private final Object defaultValue;

    public SimpleKey(String key, String envKey, Object defaultValue) {
        this.envKey = envKey;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    @Override
    public String get() {
        return key;
    }

    @Override
    public String getEnvKey() {
        return envKey;
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

}
