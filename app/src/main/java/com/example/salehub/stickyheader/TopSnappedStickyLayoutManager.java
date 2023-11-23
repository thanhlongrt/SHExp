package com.example.salehub.stickyheader;

import android.content.Context;


public final class TopSnappedStickyLayoutManager extends StickyLayoutManager {

    public TopSnappedStickyLayoutManager(Context context, StickyHeaderHandler headerHandler) {
        super(context, headerHandler);
    }

    @Override public void scrollToPosition(int position) {
        super.scrollToPositionWithOffset(position, 0);
    }
}
