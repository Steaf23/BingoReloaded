package io.github.steaf23.bingoreloaded.gui.textured;

public enum ItemModel {
    INVISIBLE(1010),
    INFO(1011),
    NUMBER_0(1012),
    NUMBER_1(1013),
    NUMBER_2(1014),
    NUMBER_3(1015),
    NUMBER_4(1016),
    NUMBER_5(1017),
    ;

    private final int data;

    ItemModel(int data) {
        this.data = data;
    }

    public int getData() {
        return data;
    }
}
