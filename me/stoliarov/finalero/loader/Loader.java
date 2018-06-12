package me.stoliarov.finalero.loader;

import weka.core.Instances;

/**
 * Created by VladS on 5/10/2016.
 */
public interface Loader {
    public Instances read(String s);
}
