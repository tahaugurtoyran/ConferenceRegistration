package com.example.conferenceregistration

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputFilter
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.conferenceregistration.data.AppDatabase
import com.example.conferenceregistration.data.Participant
import com.example.conferenceregistration.data.ParticipantRepository
import com.example.conferenceregistration.viewmodel.ParticipantViewModel
import com.example.conferenceregistration.viewmodel.ParticipantViewModelFactory
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class MainActivity : AppCompatActivity() {

    private lateinit var btnTheme: ImageButton
    private lateinit var spNavigation: Spinner
    private lateinit var etUserId: EditText
    private lateinit var etFullName: EditText
    private lateinit var spTitle: Spinner
    private lateinit var rgType: RadioGroup
    private lateinit var imgProfile: ImageView
    private lateinit var btnRegister: Button
    private lateinit var btnWeb: Button

    private lateinit var viewModel: ParticipantViewModel
    private lateinit var repository: ParticipantRepository

    private var currentPhotoPath: String = ""
    private var isUserInteracting = false

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val bitmap = result.data?.extras?.get("data") as? Bitmap
            if (bitmap != null) {
                imgProfile.setImageBitmap(bitmap)
                currentPhotoPath = saveImageToInternalStorage(bitmap)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val dao = AppDatabase.getDatabase(this).participantDao()
        repository = ParticipantRepository(dao)

        val factory = ParticipantViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ParticipantViewModel::class.java]

        initViews()
        setupInputFilters()
        setupThemeButton()
        setupSpinners()
        setupClickListeners()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        isUserInteracting = true
    }

    private fun initViews() {
        btnTheme = findViewById(R.id.btnTheme)
        spNavigation = findViewById(R.id.spNavigation)
        etUserId = findViewById(R.id.etUserId)
        etFullName = findViewById(R.id.etFullName)
        spTitle = findViewById(R.id.spTitle)
        rgType = findViewById(R.id.rgType)
        imgProfile = findViewById(R.id.imgProfile)
        btnRegister = findViewById(R.id.btnRegister)
        btnWeb = findViewById(R.id.btnWeb)
    }


    private fun setupInputFilters() {
        val letterFilter = InputFilter { source, start, end, _, _, _ ->
            for (i in start until end) {

                if (!Character.isLetter(source[i]) && !Character.isSpaceChar(source[i])) {
                    return@InputFilter ""
                }
            }
            null
        }
        etFullName.filters = arrayOf(letterFilter)
    }

    private fun setupThemeButton() {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isNightMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES

        if (isNightMode) {
            btnTheme.setImageResource(R.drawable.ic_offmoon)
            btnTheme.setColorFilter(android.graphics.Color.WHITE)
        } else {
            btnTheme.setImageResource(R.drawable.ic_moon)
            btnTheme.setColorFilter(android.graphics.Color.BLACK)
        }

        btnTheme.setOnClickListener {
            if (isNightMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }

    private fun setupSpinners() {
        val navItems = arrayOf("Registration Page", "Verification Page")
        val navAdapter = ArrayAdapter(this, R.layout.spinner_center_item, navItems)
        navAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spNavigation.adapter = navAdapter
        spNavigation.setSelection(0)

        spNavigation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (isUserInteracting && position == 1) {
                    val intent = Intent(this@MainActivity, VerificationActivity::class.java)
                    startActivity(intent)
                    isUserInteracting = false
                    spNavigation.setSelection(0)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val titleItems = arrayOf("Prof.", "Dr.", "Student")
        val titleAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, titleItems)
        spTitle.adapter = titleAdapter
    }

    private fun setupClickListeners() {
        btnWeb.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://ankarabilim.edu.tr")) //
            startActivity(intent)
        }

        imgProfile.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) //
                cameraLauncher.launch(intent)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
            }
        }

        btnRegister.setOnClickListener {
            val idStr = etUserId.text.toString()
            val name = etFullName.text.toString()

            if (idStr.isEmpty() || name.isEmpty()) {
                showMessageDialog("Error", "Please fill all fields!", isError = true)
                return@setOnClickListener
            }

            val userId = idStr.toInt()

            lifecycleScope.launch {
                val existingUser = repository.getParticipantById(userId)

                if (existingUser != null) {
                    showMessageDialog("Registration Error", "A user with this ID is already registered!", isError = true)
                } else {
                    val title = spTitle.selectedItem.toString()
                    val selectedId = rgType.checkedRadioButtonId
                    val type = when (selectedId) {
                        R.id.rbFull -> 1
                        R.id.rbStudent -> 2
                        R.id.rbNone -> 3
                        else -> 3
                    }

                    val participant = Participant(userId, name, title, type, currentPhotoPath)
                    repository.insert(participant) //

                    showMessageDialog("Success", "Registration Successful!", isError = false)

                    etUserId.text.clear()
                    etFullName.text.clear()
                    imgProfile.setImageResource(android.R.drawable.ic_menu_camera)
                    currentPhotoPath = ""
                }
            }
        }
    }

    private fun showMessageDialog(title: String, message: String, isError: Boolean) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)

        if (isError) {
            builder.setIcon(android.R.drawable.ic_dialog_alert)
        } else {
            builder.setIcon(android.R.drawable.ic_dialog_info)
        }

        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String {
        val filename = "profile_${System.currentTimeMillis()}.jpg"
        val file = File(filesDir, filename)
        try {
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file.absolutePath
    }
}