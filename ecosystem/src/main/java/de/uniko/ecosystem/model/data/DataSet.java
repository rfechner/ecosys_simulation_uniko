package de.uniko.ecosystem.model.data;

import de.uniko.ecosystem.model.Model;
import de.uniko.ecosystem.model.data.listener.Listener;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/** The goal is to have an object which can be written as csv easily on command.
 *
 *  DataSet follows a strict syntax to keep things simple:
 *  Listener Objects are added on construction of a DataSet Object.
 *  once update() is called, every Listener object queries the Models instance for
 *  the corresponding information and filling a StringBuilder with this info.
 *
 *  The resulting row of information is then stored inside a list for further processing.
 *
 */
public class DataSet {
    private static final String DELIMITER = ",";
    private static final String ENCODING = "UTF-8";
    List<String> entries = new ArrayList<>();
    List<Listener> listeners;
    StringBuilder sb;

    public DataSet(List<Listener> listeners){
        this.listeners = listeners;
        this.sb = new StringBuilder();
    }



    public void writeToCSV(String path){

        try{
            PrintWriter pw = new PrintWriter(path, ENCODING);


            // Add meta data to csv.
            for(String metadata : Model.getInstance().getMetaData()){
                pw.println(metadata);
            }

            this.sb.setLength(0);

            for(Listener l : this.listeners){
                this.sb.append(l.getColumnId()).append(DELIMITER);
            }
            this.sb.deleteCharAt(this.sb.length() - 1);

            pw.println(this.sb);

            for(String line : this.entries){
                pw.println(line);
            }

            pw.close();
        } catch(FileNotFoundException | UnsupportedEncodingException exception){
            exception.printStackTrace();
        }

    }

    public void update(){
        //clear Buffer
        this.sb.setLength(0);

        for(Listener l : this.listeners){
            this.sb.append(l.query()).append(DELIMITER);
        }

        // remove last semicolon
        this.sb.deleteCharAt(this.sb.length() - 1);

        this.entries.add(this.sb.toString());
    }

    public void reset(){
        this.entries.clear();
    }
}
