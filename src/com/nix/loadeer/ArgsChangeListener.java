package com.nix.loadeer;

/**
 * Created with IntelliJ IDEA.
 * User: Nixit
 * Date: 10/4/12
 * Time: 5:49 PM
 */
public interface ArgsChangeListener {
    public void addObserver(ArgsChangeObserver observer);

    public void removeObserver(ArgsChangeObserver observer);

    public void notifyObserver(String s);
}
