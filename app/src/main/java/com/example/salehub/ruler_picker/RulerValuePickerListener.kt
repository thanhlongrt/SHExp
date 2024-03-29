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
package com.example.salehub.ruler_picker

/**
 * Created by Kevalpatel2106 on 29-Mar-18.
 * Listener to get the callback for [RulerValuePicker] events.
 *
 * @author [kevalpatel2106](https://github.com/kevalpatel2106)
 */
interface RulerValuePickerListener {
    fun onValueChange(selectedValue: Int)
    fun onIntermediateValueChange(currentValue: Float)
}