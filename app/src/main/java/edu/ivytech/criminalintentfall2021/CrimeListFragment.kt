package edu.ivytech.criminalintentfall2021

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.ivytech.criminalintentfall2021.databinding.FragmentCrimeListBinding

class CrimeListFragment : Fragment() {
    private val crimeListViewModel :CrimeListViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CrimeListViewModel::class.java)
    }
    private lateinit var binding : FragmentCrimeListBinding
    private var adapter : CrimeListFragment.CrimeAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("CrimeListFragment", "Total crimes: ${crimeListViewModel.crimes.size}")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCrimeListBinding.inflate(inflater)
        binding.crimeRecyclerView.layoutManager = LinearLayoutManager(context)
        updateUI()
        return binding.root
    }
    private fun updateUI() {
        val crimes = crimeListViewModel.crimes
        adapter = CrimeAdapter(crimes)
        binding.crimeRecyclerView.adapter = adapter
    }

    private inner class CrimeHolder(view: View) :RecyclerView.ViewHolder(view) {
        val titleTextView = itemView.findViewById<TextView>(R.id.crime_title_list)
        val dateTextView = itemView.findViewById<TextView>(R.id.crime_date_list)

    }

    private inner class CrimeAdapter(var crimes: List<Crime>) : RecyclerView.Adapter<CrimeHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val view = layoutInflater.inflate(R.layout.list_item_crime,parent,false)
            return CrimeHolder(view)
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]
            holder.dateTextView.setText(crime.date.toString())
            holder.titleTextView.setText(crime.title)
        }

        override fun getItemCount(): Int = crimes.size

    }

    companion object {
        fun newInstance():CrimeListFragment {
            return CrimeListFragment()
        }
    }
}