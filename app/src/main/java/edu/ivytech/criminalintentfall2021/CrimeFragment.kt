package edu.ivytech.criminalintentfall2021

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.ivytech.criminalintentfall2021.databinding.FragmentCrimeBinding

class CrimeFragment : Fragment() {
    private lateinit var crime: Crime
    private lateinit var binding: FragmentCrimeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime = Crime()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCrimeBinding.inflate(inflater)

        return binding.root
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
        binding.crimeDate.setText(crime.date.toString());
        binding.crimeDate.isEnabled = false

        binding.crimeSolved.setOnCheckedChangeListener { _, isChecked ->
            crime.isSolved = isChecked
        }
    }
}