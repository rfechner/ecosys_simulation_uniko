package de.uniko.ecosystem.model.data.listener;

public abstract class Listener {
    String columnId;


    public abstract String query();
    public String getColumnId(){
        return this.columnId;
    }
}
