/*
 * Copyright 2018 Keval Patel
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance wit
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 *  the specific language governing permissions and limitations under the License.
 */
package com.kevalpatel2106.rulerpicker

import android.content.Context
import androidx.annotation.NonNull

/**
 * Created by Kevalpatel2106 on 28-Mar-18.
 * Utils classes for this library.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
internal object RulerViewUtils {
    /**
     * Convert SP to pixel.
     *
     * @param context Context.
     * @param spValue Value in sp to convert.
     *
     * @return Value in pixels.
     */
    fun sp2px(
        @NonNull context: Context,
        spValue: Float
    ): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }
}