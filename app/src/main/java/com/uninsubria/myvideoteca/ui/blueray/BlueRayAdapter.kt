package com.uninsubria.myvideoteca.ui.blueray
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.uninsubria.myvideoteca.R


class BlueRayAdapter(
    context: Context,
    resource: Int,
    private var blueRayList: MutableList<BlueRayItem>   //BlueRayItem è una dataclass contenente i dati dei BlueRay
) : ArrayAdapter<BlueRayItem>(context, resource, blueRayList) {

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
        diskTypeAp?.setImageResource(R.drawable.ic_menu_blueray)

        val imageView = listItemView?.findViewById<ShapeableImageView>(R.id.listImage)
        // Esempio di caricamento immagine con Glide (assicurati di avere le dipendenze aggiunte)
        imageView?.let {
            Glide.with(context)
                .load(currentItem?.locandina)
                .error(R.drawable.br_disk)    //Se errata mostra quella base
                .into(it)
        }

        return listItemView!!
    }

    fun updateData(newBluRayList: MutableList<BlueRayItem>) {
        blueRayList.clear()
        blueRayList.addAll(newBluRayList)
        notifyDataSetChanged()
    }
}

