package de.uniko.ecosystem.model.data.listener;

import de.uniko.ecosystem.model.Model;
import de.uniko.ecosystem.model.trees.Tree;

public class TreeVolumeListener extends Listener {
    Class<? extends Tree> cls;

    public TreeVolumeListener(Class<? extends Tree> cls ){

        this.columnId = cls.getSimpleName() + " volume (m^3)";
        this.cls = cls;
    }


    @Override
    public String query() {
        return String.valueOf(Model.getInstance().getVolumeForClass(this.cls));
    }

}
