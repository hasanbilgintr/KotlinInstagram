package com.hasanbilgin.kotlininstagram.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.hasanbilgin.kotlininstagram.databinding.ActivityMainBinding

/*
başlangıçta firebase.google.com direk gideriz direk google giirş yapılı zaten olmazsa giriş yaparsınız.Goto console tıklanır.Create a project/add project tıklanır.isim verilir.continue tıklanır.enable google analytics for this project aktif olsun ve continue tıkla.google analitik için hangi hesabınızı kullanalım diye olan derseniz defaults account for Firebase tıklanır create a new account başka hesab açılması istenir. crete preject tıklanır.continue tıklanır direk oluştuurlan proje açılır.Açılan sayfada Android seçilir.package com.hasanbilgin.kotlininstagram paket ismi girilir register app tıklanır.Dowload google-services.json tklanır ve iner.inen dosya ismi değiştirilmez(yani bu olmalı google-services.json).gösterilen yere (app klasörü içine atılır).sonra next tıklanır.gösterilen kısım eklendi. // https://firebase.google.com/docs/android/setup#available-libraries eksik olan varsa burdanda eklenebilir.next tıklanır .continue  to console tıklayınca entegre edilmiş oluyor

*/
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    var email: String = ""
    var password: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        //içeri giriş yapan kullanıcı daha sonra kapatılabilir diye
        val currentUser = auth.currentUser
        if (currentUser != null) {

            Toast.makeText(this,currentUser.email.toString(),Toast.LENGTH_LONG).show()
            val intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    fun signInOnClick(view: View) {
        email = binding.emailEditText.text.toString()
        password = binding.passwordEditText.text.toString();

        if (email.isNotEmpty() && password.isNotEmpty()) {
//        if (!email.equals("") && !password.equals("")){//aynı
            //sadece başarılı olduğunda
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                //bölede email alınabilir
                //it.user?.email;
                val intent = Intent(this@MainActivity, FeedActivity::class.java)
                startActivity(intent)
                finish()
                //hata olduğunda
            }.addOnFailureListener {
                // it.localizedMessage.toString() kullanıcının anlyaabilceği dilcen hata mesajı
                Toast.makeText(this@MainActivity, it.localizedMessage.toString(), Toast.LENGTH_LONG)
                    .show()
            }

        } else {
            Toast.makeText(this, "Enter email and password!", Toast.LENGTH_LONG).show()
        }
    }

    fun signUpOnClick(view: View) {
        /*
        kayıt olmak için yapılması gerekneler var detaylı bilgiler firebase.google.com da go to doc herşey gösteriyo
        kayıt için firebase.google.comda içine girilen proje için Authentication tıklayıp get started diyoruz .Email/Password tıklayıp üstteki enable demek yetrli
         */
        //şifre en az 6hane olmak zorunda

        email = binding.emailEditText.text.toString()
        password = binding.passwordEditText.text.toString();

        if (email.isNotEmpty() && password.isNotEmpty()) {
//        if (!email.equals("") && !password.equals("")){//aynı
            //sadece başarılı olduğunda
            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {
                val intent = Intent(this@MainActivity, FeedActivity::class.java)
                startActivity(intent)
                finish()
                //hata olduğunda
            }.addOnFailureListener {
                // it.localizedMessage.toString() kullanıcının anlyaabilceği dilcen hata mesajı
                Toast.makeText(this@MainActivity, it.localizedMessage.toString(), Toast.LENGTH_LONG)
                    .show()
            }

        } else {
            Toast.makeText(this, "Enter email and password!", Toast.LENGTH_LONG).show()
        }
    }
}

