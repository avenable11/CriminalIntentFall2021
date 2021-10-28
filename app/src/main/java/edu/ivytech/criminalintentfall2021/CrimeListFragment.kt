package edu.ivytech.criminalintentfall2021

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.ivytech.criminalintentfall2021.databinding.FragmentCrimeListBinding
import edu.ivytech.criminalintentfall2021.databinding.ListItemCrimeBinding
import java.text.DateFormat
import java.util.*

class CrimeListFragment : Fragment() {
    interface Callbacks {
        fun onCrimeSelected(crimeId: UUID)
    }
    private var callbacks: Callbacks? = null

    private val crimeListViewModel :CrimeListViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CrimeListViewModel::class.java)
    }
    private lateinit var binding : FragmentCrimeListBinding
    private var adapter : CrimeListFragment.CrimeAdapter? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

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

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    private inner class CrimeHolder(val itemBinding: ListItemCrimeBinding) :RecyclerView.ViewHolder(itemBinding.root), View.OnClickListener {
        private lateinit var crime :Crime

        init{
            itemBinding.root.setOnClickListener(this)
        }

        fun bind (crime:Crime) {
            this.crime = crime
            itemBinding.crimeTitleList.text = crime.title
            itemBinding.crimeDateList.text = android.text.format.DateFormat.format("EEEE, MMM dd, yyyy",crime.date).toString()
            itemBinding.solvedImage.visibility = if(crime.isSolved) View.VISIBLE else View.INVISIBLE
        }

        override fun onClick(p0: View?) {
            Toast.makeText(context,"${crime.title} pressed", Toast.LENGTH_SHORT).show()
            callbacks?.onCrimeSelected(crime.id)
        }

    }

    private inner class CrimeAdapter(var crimes: List<Crime>) : RecyclerView.Adapter<CrimeHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrimeHolder {
            val view = ListItemCrimeBinding.inflate(layoutInflater,parent,false)
            return CrimeHolder(view)
        }

        override fun onBindViewHolder(holder: CrimeHolder, position: Int) {
            val crime = crimes[position]
            holder.bind(crime)
        }

        override fun getItemCount(): Int = crimes.size

    }

    companion object {
        fun newInstance():CrimeListFragment {
            return CrimeListFragment()
        }
    }
}