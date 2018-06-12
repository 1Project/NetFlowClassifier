package me.stoliarov.finalero.algo;

/**
 * Created by VladS on 5/10/2016.
 */
public interface IClassifier {
    enum Mode {
        TRAINING, CLASSIFICATION
    }
    enum TestMode {
        CROSSVALIDATION, TEST_SET, NONE
    }
    enum DataSource {
        DB, FILE
    }
}
