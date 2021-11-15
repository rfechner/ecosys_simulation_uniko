package de.uniko.ecosystem.model.data;

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
    StringBuilder sb = new StringBuilder();

    public DataSet(List<Listener> listeners){
        this.listeners = listeners;
    }

    public void writeToCSV(String path){

        try{
            PrintWriter pw = new PrintWriter(path, ENCODING);

            this.sb.setLength(0);

            for(Listener l : this.listeners){
                sb.append(l.getColumnId()).append(DELIMITER);
            }
            sb.deleteCharAt(sb.length() - 1);

            pw.println(sb);

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
        sb.setLength(0);

        for(Listener l : this.listeners){
            sb.append(l.query()).append(DELIMITER);
        }

        // remove last semicolon
        sb.deleteCharAt(sb.length() - 1);

        this.entries.add(sb.toString());
    }
}
