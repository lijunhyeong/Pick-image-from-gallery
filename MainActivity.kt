import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val pickPictureButton: Button by lazy {
        findViewById(R.id.pickPictureButton)
    }
    private val imageView : ImageView by lazy {
        findViewById(R.id.imageView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAddPicture()
    }

    private fun initAddPicture() {
        pickPictureButton.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Execute the desired function when well authorized
                    navigatePhotos()

                }
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    // Authorization consent guidance. Ability to pop permission pop-up after pop-up check
                    showPermissionPopup()
                }
                else -> {
                    // Request permission
                    requestPermissions(
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        1000
                    )
                }
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> {
                // an authorized thing
                if (grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Functions that get when authorization is granted
                    navigatePhotos()
                } else {
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                //
            }
        }
    }

    // 사진첩 이동
    private fun navigatePhotos() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"     // Import as image type.
        startActivityForResult(intent, 2000)        // Code 2000 granted because results must be obtained through callback

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Return if nothing has been sent (exceptional)
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        when (requestCode) {
            2000 -> {
                val selectedImageUri: Uri? = data?.data
                if (selectedImageUri != null) {
                    imageView.setImageURI(selectedImageUri)
                } else {
                    Toast.makeText(this, "Failed to get pictures.", Toast.LENGTH_LONG).show()
                }
            }
            else -> {
                Toast.makeText(this, "Failed to get pictures.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showPermissionPopup() {
        AlertDialog.Builder(this)
            .setTitle("Permission is required.")
            .setMessage("You need permission to retrieve pictures from the app.")
            .setPositiveButton("agree") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            }
            .setNegativeButton("cancel") { _, _ -> }
            .create()
            .show()
    }

}
