package com.dungeonrpg.game.audio;

/** Sound effects the game can play. Each one is loaded from assets/sounds/<fileName>.ogg when the file exists. */
public enum SoundId {
    SWING("swing"),
    CAST("cast"),
    BLINK("blink"),
    HIT("hit"),
    DEATH("death"),
    LEVEL_UP("level_up"),
    PICKUP("pickup"),
    EQUIP("equip"),
    UI_CLICK("ui_click");

    private final String fileName;

    SoundId(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
