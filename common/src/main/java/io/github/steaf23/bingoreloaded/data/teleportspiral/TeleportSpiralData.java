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

    public int getStep() {
        return data.getInt("step", 0);
    }

    private TeleportationSpiral.Point computeNextSpiralPosition(int step) {
        var center = this.spiralOptions.center();
        var size = this.spiralOptions.size();

        if (step == 0) {
            return new TeleportationSpiral.Point(center.x(), center.z());
        }

        int m = (int) Math.floor(Math.sqrt(step));

        // Ring Index k
        int k = (int) Math.floor((m + 1) / 2.0);
        int offset = step - (int) Math.pow(2 * k - 1, 2);
        int side = (int) Math.floor(offset / (2 * k));
        int stepAlongSide = offset % (2 * k);

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

        return new TeleportationSpiral.Point(
            center.x() + ux * size.x(),
            center.z() + uz * size.z()
        );
    }

    public TeleportationSpiral.Point peekNextSpiralPosition() {
        int step = getStep();

        return computeNextSpiralPosition(step);
    }

    public TeleportationSpiral.Point getNextSpiralPosition() {
        int step = getStep();
        data.setInt("step", step + 1);
        data.saveChanges();

        return computeNextSpiralPosition(step);
    }
}
