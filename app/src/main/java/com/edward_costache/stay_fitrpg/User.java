package com.edward_costache.stay_fitrpg;

public class User {
    private String username;
    private double level;
    private double strength;
    private double agility;
    private double stamina;
    private double health;

    public User()
    {

    }

    public User(String username) {
        setUsername(username);
        setLevel(1);
        setStrength(1);
        setAgility(1);
        setStamina(1);
        setHealth(50);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getLevel() {
        return level;
    }

    public void setLevel(double level) {
        this.level = level;
    }

    public double getStrength() {
        return strength;
    }

    public void setStrength(double strength) {
        this.strength = strength;
    }

    public double getAgility() {
        return agility;
    }

    public void setAgility(double agility) {
        this.agility = agility;
    }

    public double getStamina() {
        return stamina;
    }

    public void setStamina(double stamina) {
        this.stamina = stamina;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }
}
