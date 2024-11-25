package io.github.steaf23.bingoreloaded.data.config;

//FIXME: remove this class and use custom impl for all special cases...
public class NonSerializableOption<T> extends ConfigurationOption<T>
{
    public NonSerializableOption(String configName) {
        super(configName);
    }

    @Override
    public T fromString(String value) {
        return null;
    }
}
