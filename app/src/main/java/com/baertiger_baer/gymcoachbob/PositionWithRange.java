/*
 * This file is subject to the terms and conditions defined in
 * file 'LICENSE', which is part of this source code package.
 */

package com.baertiger_baer.gymcoachbob;


/** A simple class that encapsulates a position on which an item was inserted and a separate range
 *  of positions that require to be updated. */
class PositionWithRange {
    static final int UNSET = -1;

    /* The first position of a range of items that has changed. */
    private int mRangeStart;
    /* The number of items that have changed from position mRangeStart. */
    private int mItemCount;
    /* The position at which the item was inserted. */
    private int mInsertPosition;

    PositionWithRange(int insertPosition, int rangeStart, int itemCount) {
        mInsertPosition = insertPosition;
        mItemCount = itemCount;
        mRangeStart = rangeStart;
    }

    int position() {
        return mInsertPosition;
    }

    int rangeStart() {
        return mRangeStart;
    }

    int count() {
        return mItemCount;
    }
}
