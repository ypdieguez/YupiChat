/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sapp.yupi.adapters

import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputEditText
import com.sapp.yupi.ui.lettertiles.LetterTileDrawable

@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("src")
fun bindSrc(view: ImageView, name: String) {
    val drawable = LetterTileDrawable(view.resources).setLetter(name[0]).setIsCircular(true)
    view.setImageDrawable(drawable)
}

@BindingAdapter("imageFromResource")
fun imageFromResource(view: ImageView, @DrawableRes resId: Int) {
    view.setImageResource(resId)
}

@BindingAdapter("hint")
fun bindHint(view: TextInputEditText, hint: String) {
    view.hint = hint
}
