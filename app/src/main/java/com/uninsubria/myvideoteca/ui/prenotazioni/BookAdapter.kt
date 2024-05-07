package com.uninsubria.myvideoteca.ui.prenotazioni

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.uninsubria.myvideoteca.R

class BookAdapter(context: Context,
                  resource: Int,
                  private var bookList: MutableList<BookItem>) :
    ArrayAdapter<BookItem>(context, resource, bookList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(
                R.layout.list_item, parent, false
            )
        }
        val currentItem = getItem(position)
        val titleTextView = listItemView?.findViewById<TextView>(R.id.listTitle)
        titleTextView?.text = currentItem?.title

        val diskTypeAp = listItemView?.findViewById<ShapeableImageView>(R.id.diskType)
        val imageView = listItemView?.findViewById<ShapeableImageView>(R.id.listImage)
        // Esempio di caricamento immagine con Glide (assicurati di avere le dipendenze aggiunte)
        if (currentItem != null) {
            if (currentItem.type == "Blu-ray"){
                imageView?.let {
                    Glide.with(context)
                        .load(currentItem?.imageUrl)
                        .error(R.drawable.br_disk)    //Se errata mostra quella base
                        .into(it)
                }
                diskTypeAp?.setImageResource(R.drawable.ic_menu_blueray)
            }
            else if (currentItem.type == "DVD") {
                imageView?.let {
                    Glide.with(context)
                        .load(currentItem?.imageUrl)
                        .error(R.drawable.dvd_disk)    //Se errata mostra quella base
                        .into(it)
                }
                diskTypeAp?.setImageResource(R.drawable.ic_menu_dvd)
            }
            else if (currentItem.type == "CD") {
                imageView?.let {
                    Glide.with(context)
                        .load(currentItem?.imageUrl)
                        .error(R.drawable.cd_disk)    //Se errata mostra quella base
                        .into(it)
                }
                diskTypeAp?.setImageResource(R.drawable.ic_menu_cd)
            }
        }

        return listItemView!!
    }
    fun updateData(newBookList: MutableList<BookItem>) {
        bookList.clear()
        bookList.addAll(newBookList)
        notifyDataSetChanged()
    }
}