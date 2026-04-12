package com.example.resapras

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RiwayatLaporanAdapter(
    private var laporanList: List<Laporan>,
    private val onItemClick: (Laporan) -> Unit
) : RecyclerView.Adapter<RiwayatLaporanAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvJudul: TextView = view.findViewById(R.id.tv_judul)
        val tvStatus: TextView = view.findViewById(R.id.tv_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.riwayat_laporan_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val laporan = laporanList[position]
        holder.tvJudul.text = laporan.judul
        holder.tvStatus.text = laporan.status

        // Set warna status
        val context = holder.itemView.context
        when (laporan.status.lowercase()) {
            "selesai" -> {
                holder.tvStatus.setBackgroundResource(R.drawable.br18_status_selesai)
                holder.tvStatus.setTextColor(context.getColor(R.color.white))
            }
            "diproses" -> {
                holder.tvStatus.setBackgroundResource(R.drawable.br18_status_diproses)
                holder.tvStatus.setTextColor(context.getColor(R.color.white))
            }
            "baru" -> {
                holder.tvStatus.setBackgroundResource(R.drawable.br18_status_baru)
                holder.tvStatus.setTextColor(context.getColor(R.color.white))
            }
            "ditolak" -> {
                holder.tvStatus.setBackgroundResource(R.drawable.br18_status_ditolak)
                holder.tvStatus.setTextColor(context.getColor(R.color.white))
            }
        }

        holder.itemView.setOnClickListener {
            onItemClick(laporan)
        }
    }

    override fun getItemCount() = laporanList.size

    fun updateData(newList: List<Laporan>) {
        laporanList = newList
        notifyDataSetChanged()
    }
}