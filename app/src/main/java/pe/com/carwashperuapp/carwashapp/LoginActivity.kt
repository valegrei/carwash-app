package pe.com.carwashperuapp.carwashapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pe.com.carwashperuapp.carwashapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Handle the splash screen transition.
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}