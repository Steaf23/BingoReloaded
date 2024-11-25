package io.github.steaf23.bingoreloaded.data.config;

public class IntegerOption extends ConfigurationOption<Integer>
{
    private int min = Integer.MIN_VALUE;
    private int max = Integer.MAX_VALUE;

    public IntegerOption(String configName) {
        super(configName);
    }

    /**
     * Set minimum value on option when parsing from string
     * @param min minimum value this option should have
     * @return this option
     */
    public IntegerOption withMin(int min) {
        this.min = min;
        return this;
    }

    /**
     * Set maximum value on option when parsing from string
     * @param max maximum value this option should have
     * @return this option
     */
    public IntegerOption withMax(int max) {
        this.max = max;
        return this;
    }

    @Override
    public Integer fromString(String value) {
        return Math.clamp(Integer.parseInt(value), min, max);
    }
}
