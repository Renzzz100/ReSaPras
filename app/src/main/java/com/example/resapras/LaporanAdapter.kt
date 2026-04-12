package com.example.resapras

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LaporanAdapter(
    private var list: List<Laporan>,
    private val onDetailClick: (Laporan) -> Unit
) : RecyclerView.Adapter<LaporanAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvKode: TextView = view.findViewById(R.id.tv_kode_laporan)
        val tvDibuat: TextView = view.findViewById(R.id.tv_dibuat_pada)
        val tvJudul: TextView = view.findViewById(R.id.tv_judul)
        val tvPrioritas: TextView = view.findViewById(R.id.tv_prioritas)
        val tvStatus: TextView = view.findViewById(R.id.tv_status)
        val btn_Detail: TextView = view.findViewById(R.id.btn_Detail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.laporan_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        holder.tvKode.text = item.kodeLaporan
        holder.tvDibuat.text = item.dibuatPada.take(19).replace("T", " ")
        holder.tvJudul.text = item.judul
        holder.tvPrioritas.text = item.prioritas
        holder.tvStatus.text = item.status
        holder.btn_Detail.setOnClickListener { onDetailClick(item) }
    }

    override fun getItemCount() = list.size

    fun updateData(newData: List<Laporan>) {
        Log.d("LaporanAdapter", "=== UPDATE DATA ===")
        Log.d("LaporanAdapter", "Old size: ${list.size}")
        Log.d("LaporanAdapter", "New size: ${newData.size}")

        list = newData
        notifyDataSetChanged()

        Log.d("LaporanAdapter", "Data updated, getItemCount: ${itemCount}")
    }
}