package com.gadarts.game;

import java.util.HashMap;

public class Weapons {
    private static HashMap<Weapon, Boolean> weapons = new HashMap<Weapon, Boolean>();
    private static Weapon selected;

    public static void setWeaponState(Weapon weapon, boolean b) {
        weapons.put(weapon, b);
    }

    public static void setSelected(Weapon weapon) {
        selected = weapon;
    }

    public static Weapon getSelected() {
        return selected;
    }

    public enum Weapon {HAND_GUN}
}
