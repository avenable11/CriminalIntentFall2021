package edu.ivytech.criminalintentfall2021

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.ivytech.criminalintentfall2021.databinding.FragmentCrimeBinding
import android.text.format.DateFormat
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.ViewModelProvider
import java.util.*

private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0
private const val ARG_DATE = "date"
class CrimeDetailFragment : Fragment(),FragmentResultListener {
    private lateinit var crime: Crime
    private lateinit var binding: FragmentCrimeBinding
    private val crimeDetailViewModel :CrimeDetailViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CrimeDetailViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val crime_id : UUID = arguments?.getSerializable("crime_id") as UUID
        crimeDetailViewModel.loadCrime(crime_id)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCrimeBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            {
                crime-> crime?.let{
                    this.crime = crime
                    updateUI()
                }
            }
        )
        parentFragmentManager.setFragmentResultListener(DIALOG_DATE, viewLifecycleOwner,this)
    }

    private fun updateUI() {
        binding.crimeTitle.setText(crime.title)
        binding.crimeDate.text = DateFormat.format("EEEE, MMM dd, yyyy", crime.date)
        binding.crimeSolved.isChecked = crime.isSolved
    }

    override fun onStart() {
        super.onStart()
        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(sequence: CharSequence?, start: Int, before: Int, count: Int) {
                crime.title = sequence.toString()
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        }
        binding.crimeTitle.addTextChangedListener(titleWatcher)
        //binding.crimeDate.isEnabled = false
        binding.crimeSolved.setOnCheckedChangeListener { _, isChecked ->
            crime.isSolved = isChecked
        }
        binding.crimeDate.setOnClickListener {
            DatePickerFragment.newInstance(crime.date, DIALOG_DATE)
                .show(this@CrimeDetailFragment.parentFragmentManager, DIALOG_DATE)
        }




        //binding.crimeDate.setText(crime.date.toString());
    }

    override fun onStop() {
        super.onStop()
        crimeDetailViewModel.saveCrime(crime)
    }

    companion object {
        fun newInstance(id: UUID):CrimeDetailFragment {
            val args = Bundle().apply{
                putSerializable("crime_id", id)
            }
            return CrimeDetailFragment().apply{ arguments = args}
        }


    }



    override fun onFragmentResult(requestKey: String, result: Bundle) {

        when(requestKey) {
            DIALOG_DATE -> {
                crime.date = result.getSerializable(ARG_DATE) as Date
                updateUI()
            }
        }


    }
}