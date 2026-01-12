package com.example.conferenceregistration

import android.content.Intent
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.example.conferenceregistration.data.AppDatabase
import com.example.conferenceregistration.data.ParticipantRepository
import com.example.conferenceregistration.viewmodel.ParticipantViewModel
import com.example.conferenceregistration.viewmodel.ParticipantViewModelFactory

class VerificationActivity : AppCompatActivity() {

    private lateinit var btnTheme: ImageButton
    private lateinit var spNavigation: Spinner
    private lateinit var etSearchId: EditText
    private lateinit var btnVerify: Button
    private lateinit var layoutResult: LinearLayout
    private lateinit var imgResultPhoto: ImageView
    private lateinit var tvResultName: TextView
    private lateinit var tvResultTitle: TextView

    private lateinit var viewModel: ParticipantViewModel
    private var isUserInteracting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        val dao = AppDatabase.getDatabase(this).participantDao()
        val repository = ParticipantRepository(dao)
        val factory = ParticipantViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ParticipantViewModel::class.java]

        initViews()
        setupThemeButton() // <--- Tema ayarları
        setupNavigationSpinner()

        btnVerify.setOnClickListener {
            val idStr = etSearchId.text.toString()
            if (idStr.isNotEmpty()) {
                viewModel.verifyParticipant(idStr.toInt())
            } else {
                Toast.makeText(this, "Please enter User ID", Toast.LENGTH_SHORT).show()
            }
        }

        observeViewModel()
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        isUserInteracting = true
    }

    private fun initViews() {
        btnTheme = findViewById(R.id.btnTheme)
        spNavigation = findViewById(R.id.spNavigation)
        etSearchId = findViewById(R.id.etSearchId)
        btnVerify = findViewById(R.id.btnVerify)
        layoutResult = findViewById(R.id.layoutResult)
        imgResultPhoto = findViewById(R.id.imgResultPhoto)
        tvResultName = findViewById(R.id.tvResultName)
        tvResultTitle = findViewById(R.id.tvResultTitle)
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
    // ---------------------------------------------

    private fun setupNavigationSpinner() {
        val navItems = arrayOf("Registration Page", "Verification Page")
        val navAdapter = ArrayAdapter(this, R.layout.spinner_center_item, navItems)
        navAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spNavigation.adapter = navAdapter


        spNavigation.setSelection(1)

        spNavigation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                if (isUserInteracting && position == 0) {
                    finish()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun observeViewModel() {
        viewModel.searchResult.observe(this) { user ->
            if (user != null) {
                tvResultName.text = user.fullName
                tvResultTitle.text = user.title

                if (user.photoPath.isNotEmpty()) {
                    val bitmap = BitmapFactory.decodeFile(user.photoPath)
                    imgResultPhoto.setImageBitmap(bitmap)
                } else {
                    imgResultPhoto.setImageResource(android.R.drawable.ic_menu_gallery)
                }

                val color = when (user.registrationType) {
                    1 -> Color.GREEN
                    2 -> Color.BLUE
                    3 -> Color.parseColor("#FFA500") // Orange
                    else -> Color.WHITE
                }
                layoutResult.setBackgroundColor(color)

            } else {
                tvResultName.text = "User Not Found!"
                tvResultTitle.text = ""
                imgResultPhoto.setImageResource(0)
                layoutResult.setBackgroundColor(Color.RED)
            }
        }
    }
}