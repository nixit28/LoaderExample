package com.nix.loadeer;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Nixit
 * Date: 10/4/12
 * Time: 6:16 PM
 */
public class ArgsChangListnerImpl implements ArgsChangeListener {
    ArrayList<ArgsChangeObserver> list;

    @Override
    public void addObserver(ArgsChangeObserver observer) {
        if (list == null) {
            list = new ArrayList<ArgsChangeObserver>();
        }
        list.add(observer);
    }

    @Override
    public void removeObserver(ArgsChangeObserver observer) {
        if (list != null) {
            list.remove(observer);
        }
    }

    @Override
    public void notifyObserver(String s) {
        if (list != null)
            for (ArgsChangeObserver observer : list) {
                observer.updateResultList(s);
            }
    }
}
