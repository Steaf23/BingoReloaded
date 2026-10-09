package io.github.steaf23.bingoreloaded.data.teleportspiral;

import io.github.steaf23.bingoreloaded.BingoReloaded;
import io.github.steaf23.bingoreloaded.lib.data.core.DataAccessor;

public class TeleportSpiralData {

    private final DataAccessor data = BingoReloaded.getDataAccessor(
        "data/spiral"
    );

    private final TeleportationSpiral spiralOptions;

    public TeleportSpiralData(TeleportationSpiral spiralOptions) {
        this.spiralOptions = spiralOptions;
    }

    public TeleportationSpiral getSpiralOptions() {
        return spiralOptions;
    }

    // Retrieve the current spiral step.
    public int getStep() {
        return data.getInt("step", 0);
    }

    // Deterministically compute the spiral position for a given step.
    private TeleportationSpiral.Point computeSpiralPosition(int step) {
        var center = this.spiralOptions.center();
        var size = this.spiralOptions.size();

        // Step 1: If step==0, return the center position.
        if (step == 0) {
            return new TeleportationSpiral.Point(center.x(), center.z());
        }

        // Step 2: Identify the ring number.
        // Ring 1 is the 8 that surround the center.
        // Ring 2 is the 16 that surround ring 1.
        int m = (int) Math.floor(Math.sqrt(step));
        int k = (int) Math.floor((m + 1) / 2.0);

        // Step 3: Position within ring
        int offset = step - (int) Math.pow(2 * k - 1, 2); // How many steps into the current ring are we.
        int side = (int) Math.floor(offset / (2 * k)); // 0=right, 1=top, 2=left, 3=bottom
        int stepAlongSide = offset % (2 * k); // How many steps along the side are we.

        int ux = 0;
        int uz = 0;

        switch (side) {
            case 0:
                ux = k;
                uz = -(k - 1) + stepAlongSide;
                break;
            case 1:
                ux = k - 1 - stepAlongSide;
                uz = k;
                break;
            case 2:
                ux = -k;
                uz = k - 1 - stepAlongSide;
                break;
            case 3:
                ux = -(k - 1) + stepAlongSide;
                uz = -k;
                break;
        }

        // Step 4: Scale result by the config values.
        return new TeleportationSpiral.Point(
            center.x() + ux * size.x(),
            center.z() + uz * size.z()
        );
    }

    // Returns the next spiral position, **without incrementing the step counter**.
    public TeleportationSpiral.Point peekNextSpiralPosition() {
        int step = getStep();

        return computeSpiralPosition(step);
    }

    // Returns the spiral position for a given step value.
    public TeleportationSpiral.Point peekSpiralPosition(int step) {
        return computeSpiralPosition(step);
    }

    // Returns the next spiral position, **and increments the step counter**.
    public TeleportationSpiral.Point getNextSpiralPosition() {
        int step = getStep();
        data.setInt("step", step + 1);
        data.saveChanges();

        return computeSpiralPosition(step);
    }

    // Resets the step counter to zero.
    public void reset() {
		data.setInt("step", 0);
		data.saveChanges();
	}
}
