package edu.ivytech.criminalintentfall2021

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.ivytech.criminalintentfall2021.databinding.FragmentCrimeBinding
import android.text.format.DateFormat
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.ViewModelProvider
import java.io.FileOutputStream
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
    private val getSuspect = registerForActivityResult(ActivityResultContracts.PickContact()){
        uri : Uri? ->
        val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
        val cursor = uri?.let{
            requireActivity().contentResolver.query(it, queryFields, null, null, null)
        }
        cursor?.use {
            if(it.count == 0) {
                return@registerForActivityResult
            }
            it.moveToFirst()
            val suspect = it.getString(0)
            crime.suspect = suspect
            crimeDetailViewModel.saveCrime(crime)
            binding.crimeSuspect.text = suspect

        }
    }

    private val camera = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
        bitmap ->
        binding.crimePhoto.setImageBitmap(bitmap)
        val photoFile = crimeDetailViewModel.getPhotoFile(crime)
        val fileOutputStream:FileOutputStream = FileOutputStream(photoFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream)
    }
    private val permission = registerForActivityResult(ActivityResultContracts.RequestPermission())
    {
        granted->
        when {
            granted-> {
                camera.launch()
            }
            !shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                Log.e("Crime Detail", "Camera permission never ask again")
            }
            else -> {
                Log.e("Crime Detail", "Camera permission not granted")
            }
        }
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
        if(!crime.suspect.isBlank())
            binding.crimeSuspect.text = crime.suspect
        val photoFile = crimeDetailViewModel.getPhotoFile(crime)
        if(photoFile.path != null) {
            val bitmap: Bitmap? = BitmapFactory.decodeFile(photoFile.path)
            binding.crimePhoto.setImageBitmap(bitmap)
        }
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
        binding.crimeReport.setOnClickListener{
            try {
                Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject))
                }.also { intent ->
                    val chooserIntent = Intent.createChooser(intent, getString(R.string.send_report))
                    startActivity(chooserIntent)
                }
            }
            catch (e : ActivityNotFoundException)
            {
                Log.e("Crime Detail", "Unable to send report", e)
            }
        }

        binding.crimeSuspect.setOnClickListener{
            try {
                getSuspect.launch()
            } catch (e : ActivityNotFoundException)
            {
                Log.e("Crime Detail", "Could not get contact", e)
                binding.crimeSuspect.isEnabled = false
            }
        }
        binding.crimeCamera.setOnClickListener {
            permission.launch(Manifest.permission.CAMERA)
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

    private fun getCrimeReport() : String {
        val solvedString = if(crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }
        val dateString = DateFormat.format("EEEE, MMM dd, yyyy", crime.date).toString()
        var suspect = if(crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect,crime.suspect)
        }
        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspect)
    }
}