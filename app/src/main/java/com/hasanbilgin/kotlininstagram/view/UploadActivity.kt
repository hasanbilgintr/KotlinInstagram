package com.hasanbilgin.kotlininstagram.view


import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore

import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage


import com.hasanbilgin.kotlininstagram.databinding.ActivityUploadBinding
import java.util.UUID

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture: Uri? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registerLaoncher()

        auth = Firebase.auth
//        firestore= FirebaseFirestore.getInstance()
        firestore = Firebase.firestore
//         storage= FirebaseStorage.getInstance()
        storage = Firebase.storage


    }


    fun uploadButtonOnClick(view: View) {
        //firebase sitesine gidip projeyi seçip firebase Database seçilip create database tıklıyoruz çıkan ekranda start in test mode seçtik.burda oluşturulcak veri tabanı nerde (lokasyon)(verilerimiz nerde tutulcak) olucağına karar veriyoruz eğer yasal bir uygulama ise nam5(United States) seç  yada markete vs koymucaksan istedğini seçebilirsin biz eur3(europe) seçtik. ve seçtikten sonra değiştirilemez ve enable diyoruz.dökümantasyonlarıda vardır incelenebilir .rules sekmesine gelerek allow read, write: if request.auth!=null; yapıldı üye olmayan giriş yapamaz anlamında.Storage aynı şekilde grt started yapıldı.ruleside aynı yaparsın

        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"
        //storage files alanını veriyo (Ana klasör)
        val referance = storage.reference
        //ana klasörün altına (içine) images klasörü açılcak ve image.jpg dosyası atılcak
        //de olabilir
//        val imageReferance=referance.child("images").child("image.jpg")
//        val imageReferance = referance.child("images/image.jpg")
        val imageReferance = referance.child("images").child(imageName)

        if (selectedPicture != null) {
            imageReferance.putFile(selectedPicture!!).addOnSuccessListener {
                //dowloadurl->firestore kaydedtirmek amacımız
                //burda
                val uploadPicturesReferance = storage.reference.child("images").child(imageName)
                //burada indirildikten sonra ki metot
                uploadPicturesReferance.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()

                    if (auth.currentUser != null) {
                        //Any burda int string boolen vs herşey olabilir  anlamında
                        val postMap = hashMapOf<String, Any>()
                        postMap.put("dowsnloadUrl", downloadUrl)
                        postMap.put("userEmail", auth.currentUser!!.email!!)
                        postMap.put("commnet", binding.commentEditText.text.toString());
                        //Timestamp firebase kütüphanesi alındı
                        postMap.put("date", Timestamp.now())

                        firestore.collection("Posts").add(postMap).addOnSuccessListener {
                            //activity kapaması
                            finish()
                        }.addOnFailureListener {
                            Toast.makeText(this@UploadActivity, it.localizedMessage, Toast.LENGTH_LONG).show()
                        }

                    }


                }
            }.addOnFailureListener {
                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_LONG).show()
            }
        } else {

        }


    }


    fun selectImageOnClick(view: View) { //Manifest android kütüphanesini kullanıyor
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { //android 33+ READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) { //izin alma mantığını kullanııya göstereyimmi ? android kendi belirler
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_MEDIA_IMAGES)) { //rationale
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", View.OnClickListener { //request permission
                        permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                    }).show()
                } else { //request permission
                    permissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery) ///intent
            }
        } else {
            //yani izin yoksa
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) { //androidde tekrar izne tabi kılınması gerekiyorsa onun kontrlü
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE).setAction("Give Permission") { //request permisson
                        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    }.show()
                } else { //request permisson
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI) //start activity for result
                activityResultLauncher.launch(intentToGallery)
            }
        }

    }

    private fun registerLaoncher() {
        //galeriye gidilmesi için yolu gösterilmesi
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intenFromResult = result.data
                if (intenFromResult != null) {
                    selectedPicture = intenFromResult.data
                    selectedPicture.let {
                        binding.imageView.setImageURI(it)
                        //diğer bi bitmapa gerek kalmıyo çünkü firebas url yetiyo
                    }
                }
            }
        } //androide sorulan izin yeri
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) { //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            } else { //permission denied
                Toast.makeText(this@UploadActivity, "Permisson needed!", Toast.LENGTH_LONG).show()

            }
        }
    }
}