package com.sapp.yupi.adapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions.bitmapTransform
import com.sapp.yupi.data.Contact
import com.sapp.yupi.ui.lettertiles.LetterTileDrawable
import com.sapp.yupi.utils.DateFormatter
import com.sapp.yupi.utils.PhoneUtil.Companion.isPhoneNumber
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation.CornerType

@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("imageFromContact")
fun bindImageFromContact(view: ImageView, contact: Contact) {
    val context = view.context
    val name = contact.name

    val model = contact.thumbnailUri ?: LetterTileDrawable(context).apply {
        if (!isPhoneNumber(name)) {
            setLetter(name[0])
        }
    }

    Glide
            .with(context).load(model)
            .apply(bitmapTransform(RoundedCornersTransformation(128, 0, CornerType.ALL)))
            .into(view)
}

@BindingAdapter("dateFromLong")
fun dateFromLong(view: TextView, date: Long) {
    view.text = DateFormatter.formatTimeStampString(view.context, date).toLowerCase()
}

@BindingAdapter("fontFamily")
fun fontFamily(view: TextView, @FontRes id: Int) {
    view.typeface = ResourcesCompat.getFont(view.context, id)
}