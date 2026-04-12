package com.example.resapras

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DaftarLaporanAdapter(
    private var list: List<Laporan>,
    private val onDetailClick: (Laporan) -> Unit
) : RecyclerView.Adapter<DaftarLaporanAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Sesuaikan dengan ID yang ada di daftar_laporan_list.xml
        val tvKode: TextView = view.findViewById(R.id.tv_kode_laporan)
        val tvDibuat: TextView = view.findViewById(R.id.tv_dibuat_pada)
        val tvJudul: TextView = view.findViewById(R.id.tv_judul)
        val tvPrioritas: TextView = view.findViewById(R.id.tv_prioritas)
        val tvStatus: TextView = view.findViewById(R.id.tv_status)
        val btnDetail: TextView = view.findViewById(R.id.btn_Detail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // INI YANG BERBEDA - menggunakan daftar_laporan_list.xml
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.daftar_laporan_list, parent, false)  // ← beda di sini
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvKode.text = item.kodeLaporan
        holder.tvDibuat.text = item.dibuatPada.take(19).replace("T", " ")
        holder.tvJudul.text = item.judul
        holder.tvPrioritas.text = item.prioritas
        holder.tvStatus.text = item.status

        // Optional: set background berbeda untuk status
        when (item.status.lowercase()) {
            "selesai" -> {
                holder.tvStatus.background =
                    holder.itemView.context.getDrawable(R.drawable.br18_status_selesai)
            }
            "diproses" -> {
                holder.tvStatus.background =
                    holder.itemView.context.getDrawable(R.drawable.br18_status_diproses)
            }
            "baru" -> {
                holder.tvStatus.background =
                    holder.itemView.context.getDrawable(R.drawable.br18_status_baru)
            }
            "ditolak" -> {
                holder.tvStatus.background =
                    holder.itemView.context.getDrawable(R.drawable.br18_status_ditolak)
            }
        }

        holder.btnDetail.setOnClickListener { onDetailClick(item) }
    }

    override fun getItemCount() = list.size

    fun updateData(newData: List<Laporan>) {
        Log.d("DaftarLaporanAdapter", "Update data: ${newData.size} items")
        list = newData
        notifyDataSetChanged()
    }
}