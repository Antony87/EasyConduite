/*
 *
 *
 *  * Copyright (c) 2020.  Antony Fons
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package easyconduite.conduite;

import com.fasterxml.jackson.annotation.JsonIgnore;
import easyconduite.model.AbstractMedia;
import easyconduite.model.MediaPlayable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.ArrayList;

/**
 * This class implements the features of a trigger, like medias playing, etc.
 * <br>
 * A trigger contains a lits of actions {@link MediaAction}
 */
public class Trigger implements Comparable {

    @JsonIgnore
    private final BooleanProperty actif = new SimpleBooleanProperty(false);

    private ArrayList<MediaAction> listeActions;

    private int index;

    public Trigger() {
        listeActions = new ArrayList<>();
    }

    public Trigger(int index) {
        this();
        this.index = index;
    }

    public void runAllActions() {
        listeActions.forEach(action -> {
            action.runAction();
            setActif(true);
        });
    }

    /**
     * Cette méthode retourne {@link MediaAction} associée au {@link MediaPlayable} ou en crée une avec ce Media.
     *
     * @param media Le media qui est ou sera associé à l'action
     * @return
     */
    public MediaAction getActionFromMedia(MediaPlayable media) {
        for (MediaAction action : listeActions) {
            //si désérialization, comparer les uniqueId
            if (action.getUniqueIdMedia() == ((AbstractMedia) media).getUniqueId()) {
                action.setMedia(media);
                return action;
            }
        }
        final MediaAction action = new MediaAction(media);
        listeActions.add(action);
        return action;
    }

    @Override
    public String toString() {
        return "Trigger{" +
                "listeActions=" + listeActions +
                ", index=" + index +
                '}';
    }

    public boolean isActif() {
        return actif.get();
    }

    public void setActif(boolean actif) {
        this.actif.set(actif);
    }

    public BooleanProperty actifProperty() {
        return actif;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public ArrayList<MediaAction> getListeActions() {
        return listeActions;
    }

    public void setListeActions(ArrayList<MediaAction> listeActions) {
        this.listeActions = listeActions;
    }

    /**
     * La comapraison se fait sur la propriété index.
     *
     * @param aTrigger
     * @return
     */
    @Override
    public int compareTo(Object aTrigger) {
        return this.index - ((Trigger) aTrigger).index;
    }
}
