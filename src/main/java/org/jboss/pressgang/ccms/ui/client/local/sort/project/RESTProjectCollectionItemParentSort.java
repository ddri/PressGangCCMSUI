package org.jboss.pressgang.ccms.ui.client.local.sort.project;

import org.jboss.pressgang.ccms.rest.v1.collections.items.RESTProjectCollectionItemV1;
import org.jboss.pressgang.ccms.rest.v1.components.ComponentTagV1;
import org.jboss.pressgang.ccms.rest.v1.entities.RESTTagV1;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

/**
 * Sorts RESTTagCollectionItemV1 objects based on their inclusion in a parent.
 */
public final class RESTProjectCollectionItemParentSort implements Comparator<RESTProjectCollectionItemV1> {
    @NotNull private final RESTTagV1 parent;
    private final boolean ascending;

    /**
     * @param parent the entity that holds the child entities being sorted
     * @param ascending true if the items should be sorted in ascending order, false otherwise
     */
    public RESTProjectCollectionItemParentSort(@NotNull final RESTTagV1 parent, final boolean ascending) {
        this.parent = parent;
        this.ascending = ascending;
    }

    @Override
    public int compare(@Nullable final RESTProjectCollectionItemV1 arg0, @Nullable final RESTProjectCollectionItemV1 arg1) {
        final int acendingMultiplier = ascending ? 1 : -1;

        /*
            First deal with null objects
        */
        if (arg0 == null && arg1 == null) {
            return 0;
        }

        if (arg0 == arg1) {
            return 0;
        }

        if (arg0 == null) {
            return -1 * acendingMultiplier;
        }

        if (arg1 == null) {
            return 1 * acendingMultiplier ;
        }

        /*
            Fall back to comparing by id
         */
        final boolean arg0IsChild = ComponentTagV1.containedInProject(parent, arg0.getItem().getId());
        final boolean arg1IsChild = ComponentTagV1.containedInProject(parent, arg1.getItem().getId());

        if (arg0IsChild == arg1IsChild) {
            return new RESTProjectCollectionItemIDSort(ascending).compare(arg0, arg1);
        }

        if (arg0IsChild) {
            return -1 * acendingMultiplier;
        }

        return 1 * acendingMultiplier;
    }
}
