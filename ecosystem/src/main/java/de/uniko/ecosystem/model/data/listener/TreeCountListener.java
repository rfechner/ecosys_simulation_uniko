package de.uniko.ecosystem.model.data.listener;

import de.uniko.ecosystem.model.Model;
import de.uniko.ecosystem.model.trees.Tree;

public class TreeCountListener extends Listener {
    Class<? extends Tree> cls;

    public TreeCountListener(Class<? extends Tree> cls ){

        this.columnId = cls.getSimpleName() + " (count)";
        this.cls = cls;
    }


    @Override
    public String query() {
        return String.valueOf(Model.getInstance().getTreeCountForClass(this.cls));
    }

}
