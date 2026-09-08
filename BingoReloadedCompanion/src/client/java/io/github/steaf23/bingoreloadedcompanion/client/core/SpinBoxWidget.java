package io.github.steaf23.bingoreloadedcompanion.client.core;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SpinBoxWidget extends LinearLayout {

	private static final Identifier NUMBER_DECREMENT = Identifier.parse("bingoreloadedcompanion:number_input_left");
	private static final Identifier NUMBER_INCREMENT = Identifier.parse("bingoreloadedcompanion:number_input_right");
	private static final Identifier NUMBER_BACKGROUND = Identifier.parse("bingoreloadedcompanion:number_background");

	int maxCharacters;

	double min = 0.0;
	double max = 0.0;
	double step = 1.0;

	final boolean useWholeNumbers;
	final EditBox editBox;
	final SpriteIconButton decrementButton;
	final SpriteIconButton incrementButton;
	final Consumer<Double> callback;

	// min == 0, max == 10000, step == 1
	public static SpinBoxWidget defaultIntegerBox(Font font, Consumer<Integer> valueCallback) {
		SpinBoxWidget spinbox = new SpinBoxWidget(font, 0, 0, Component.empty(), true, value -> valueCallback.accept(value.intValue()));
		spinbox.min = 0;
		spinbox.max = 10000;
		spinbox.maxCharacters(6);
		spinbox.setDoubleValue(0.0);
		spinbox.step = 1;
		return spinbox;
	}

	// min = 0.0, max = 1.0, step == 0.1
	public static SpinBoxWidget defaultDoubleBox(Font font, Consumer<Double> valueCallback) {
		SpinBoxWidget spinbox = new SpinBoxWidget(font, 0, 0, Component.empty(), false, valueCallback);
		spinbox.min = 0.0;
		spinbox.max = 1.0;
		spinbox.setDoubleValue(0.0);
		spinbox.step = 0.1;
		return spinbox;
	}

	private SpinBoxWidget(Font font, int x, int y, Component name, boolean useWholeNumbers, @Nullable Consumer<Double> callback) {
		super(x, y, Orientation.HORIZONTAL);
		this.useWholeNumbers = useWholeNumbers;
		this.callback = callback;

		String stepString = this.useWholeNumbers ? String.valueOf((int) step) : String.valueOf(step);

		decrementButton = SpriteIconButton.builder(Component.literal("-" + stepString), (btn) -> setDoubleValue(valueAsDouble() - step), true)
				.size(8, 12)
				.sprite(new WidgetSprites(NUMBER_DECREMENT), 8, 12)
				.withTootip()
				.build();
		addChild(decrementButton);

		editBox = new EditBox(font, 0, 0, Component.empty()) {
			@Override
			public void setValue(String string) {
				try {
					String val = validateValue(string);
					super.setValue(val);
				} catch (NumberFormatException exception) {
					//ignore exception;
				}
			}

			@Override
			public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
				if (scrollY != 0) {
					setDoubleValue(valueAsDouble() + step * scrollY);
				}
				else if (scrollX != 0) {
					setDoubleValue(valueAsDouble() + step * scrollX);
				}

				return true;
			}

			private String validateValue(String value) {
				double val = 0.0;
				if (!value.isBlank()) {
					val = Double.parseDouble(value);
				}

				val = Math.clamp(val, min, max);

				if (useWholeNumbers) {
					return "%d".formatted((int)val);
				}
				else {
					return "%.3f".formatted(val);
				}
			}
		};
		editBox.setSize((maxCharacters + (min < 0 ? 2 : 1)) * 6, 12);
		editBox.setResponder(this::valueChanged);
		addChild(editBox);

		incrementButton = SpriteIconButton.builder(Component.literal("+" + stepString), (btn) -> setDoubleValue(valueAsDouble() + step), true)
				.size(8, 12)
				.sprite(new WidgetSprites(NUMBER_INCREMENT), 8, 12)
				.withTootip()
				.build();
		addChild(incrementButton);
	}

	public SpinBoxWidget minValue(double min) {
		if (min > max) {
			System.out.printf("Cannot set spin box min value to be higher than max: %f > %f%n", min, max);
			return this;
		}
		this.min = min;
		editBox.setValue(String.valueOf(Math.max(this.min, valueAsDouble())));
		return this;
	}

	public SpinBoxWidget maxValue(double max) {
		if (max < min) {
			System.out.printf("Cannot set spin box max value to be lower than min: %f < %f%n", max, min);
			return this;
		}

		this.max = max;
		editBox.setValue(String.valueOf(Math.min(this.max, valueAsDouble())));
		return maxCharacters(String.valueOf(max).length());
	}

	public SpinBoxWidget startValue(double value) {
		setDoubleValue(value);
		return this;
	}

	public SpinBoxWidget maxCharacters(int max) {
		this.maxCharacters = max;
		editBox.setSize((maxCharacters + (min < 0 ? 2 : 1)) * 6, 12);
		return this;
	}

	public void setDoubleValue(double val) {
		editBox.setValue("" + val);
	}

	public double valueAsDouble() {
		try {
			return Double.parseDouble(editBox.getValue());
		} catch (NumberFormatException formatException) {
			return 0.0;
		}
	}

	private boolean validateValue(String value) {
		double val = 0.0;
		if (!value.isBlank()) {
			try {
				val = Double.parseDouble(value);
			} catch (NumberFormatException formatException) {
				return false;
			}
		}

		return val == Math.clamp(val, min, max);
	}

	private void valueChanged(String newValue) {
		if (!validateValue(newValue)) {
			return;
		}

		Double val = valueAsDouble();
		if (callback != null) {
			callback.accept(val);
		}
	}
}
