package me.stoliarov.finalero.loader;

import me.stoliarov.eldiploma.domain.FlowClass;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

import java.io.File;

/**
 * Created by VladS on 5/10/2016.
 */
public class DatabaseLoader implements Loader{
    private static final String csvFileName = "E:\\api\\tmp.csv";
    private static final String arffFileName = "E:\\api\\tmp.arff";

    private static final SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
    private static final Session session = sessionFactory.openSession();

    public static Session getSession() {
        return session;
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    @Override
    public Instances read(String s) {
        try {
            String query = "COPY (select * from flowclass limit 100) TO \'" + csvFileName + "\' WITH CSV HEADER";
            session.createSQLQuery(query).addEntity(FlowClass.class)
                    .executeUpdate();

            // load CSV
            CSVLoader loader = new CSVLoader();
            loader.setSource(new File(csvFileName));
            Instances data = loader.getDataSet();

            // save ARFF
            File arffFile = new File(arffFileName);
            ArffSaver saver = new ArffSaver();
            saver.setInstances(data);
            saver.setFile(arffFile);
            saver.writeBatch();

            return new FileLoader().read(arffFileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Instances read(String sql, String fileName) {
        try {
            String query = "COPY (" + sql + ") TO \'" + fileName + "\' WITH CSV HEADER";
            session.createSQLQuery(query).addEntity(FlowClass.class)
                    .executeUpdate();

            // load CSV
            CSVLoader loader = new CSVLoader();
            loader.setSource(new File(fileName));
            Instances data = loader.getDataSet();

            String newFileName = fileName.replace(".csv", "") + ".arff";
            // save ARFF
            File arffFile = new File(newFileName);
            ArffSaver saver = new ArffSaver();
            saver.setInstances(data);
            saver.setFile(arffFile);
            saver.writeBatch();

            return new FileLoader().read(newFileName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
