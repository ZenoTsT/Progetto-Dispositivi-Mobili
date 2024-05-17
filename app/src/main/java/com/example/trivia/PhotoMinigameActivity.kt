package com.example.trivia

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlin.random.Random

class PhotoMinigameActivity : AppCompatActivity() {

    private lateinit var game: Game
    private val REQUEST_CAMERA_PERMISSION = 2
    private lateinit var imgPhoto: ImageView
    private lateinit var instructionTextView: TextView
    private lateinit var resultTextView: TextView
    private lateinit var takePictureLauncher: ActivityResultLauncher<Void?>
    private val requiredPercentage = 3

    private val colorRanges = mapOf(
        "Red" to listOf(Triple(180..255, 0..80, 0..80)),
        "Yellow" to listOf(Triple(180..255, 180..255, 0..120)),
        "Green" to listOf(Triple(0..150, 100..255, 0..70)),
        "Blue" to listOf(Triple(0..100, 0..100, 130..255)),
        "White" to listOf(Triple(200..255, 200..255, 200..255))
    )

    private lateinit var selectedColor: String

    // Inizializza l'attività, imposta il listener dei pulsanti, seleziona un colore casuale all'avvio e gestisce la logica del fine gioco
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_minigame)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) { override fun handleOnBackPressed() {} })

        game = Game.getInstance()

        imgPhoto = findViewById(R.id.imgPhoto)
        instructionTextView = findViewById(R.id.instructionTextView)
        resultTextView = findViewById(R.id.resultTextView)
        val btnTakePhoto: Button = findViewById(R.id.btnTakePhoto)

        selectedColor = colorRanges.keys.random()
        instructionTextView.text = "Take a photo of something $selectedColor"

        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {

                btnTakePhoto.isEnabled = false

                imgPhoto.setImageBitmap(bitmap)
                val success = isColorPresent(bitmap, selectedColor)
                if (success) {
                    resultTextView.text = "You win!"
                    val lastPlayer = game.getLeaderboard().size - 1
                    game.getLeaderboard()[lastPlayer].addPoints(2)
                } else {
                    resultTextView.text = "You lose"
                }
                resultTextView.visibility = TextView.VISIBLE

                Handler(Looper.getMainLooper()).postDelayed({
                    returnToGameActivity()
                }, 2000)
            }
        }

        btnTakePhoto.setOnClickListener {
            checkCameraPermissionAndOpenCamera()
        }
    }

    // Controlla il permesso per la fotocamera e avvia l'intent per scattare una foto
    private fun checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
        } else {
            dispatchTakePictureIntent()
        }
    }

    // Gestisce il risultato della richiesta di permesso per la fotocamera
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(this, "Camera permission is required to use the camera", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Avvia l'intent per scattare una foto
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            takePictureLauncher.launch(null)
        } else {
            Toast.makeText(this, "No camera app found to handle the intent", Toast.LENGTH_SHORT).show()
        }
    }

    // Verifica se il colore specificato è presente in almeno il 3% dell'immagine campionando ogni 4 pixel
    private fun isColorPresent(bitmap: Bitmap, colorName: String): Boolean {
        val targetRanges = colorRanges[colorName] ?: return false
        var matchCount = 0
        val totalPixels = bitmap.width * bitmap.height

        for (x in 0 until bitmap.width step 4) {
            for (y in 0 until bitmap.height step 4) {
                val pixelColor = bitmap.getPixel(x, y)
                val red = Color.red(pixelColor)
                val green = Color.green(pixelColor)
                val blue = Color.blue(pixelColor)
                if (targetRanges.any { red in it.first && green in it.second && blue in it.third }) {
                    matchCount++
                }
            }
        }

        val sampledPixels = (bitmap.width / 4) * (bitmap.height / 4)
        val percentage = (matchCount.toDouble() / sampledPixels) * 100
        return percentage >= requiredPercentage
    }

    // Torna alla GameActivity
    private fun returnToGameActivity() {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }
}
