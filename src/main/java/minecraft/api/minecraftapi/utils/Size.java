package minecraft.api.minecraftapi.utils;

public enum Size {

    FIFTY_FOUR(54),
    TWENTY_SEVEN(27),
    EIGHT_TEEN(18),
    FORTY_FIVE(45),
    NINE(9),
    THIRTY_SIX(36),


   // 45 54 27 18

    ;
    private final int guiSize;

    Size(int guiSize){

        this.guiSize = guiSize;
    }

    public int getGuiSize() {
        return guiSize;
    }
}
