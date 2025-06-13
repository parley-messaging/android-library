package nu.parley.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import nu.parley.BuildConfig
import nu.parley.R
import nu.parley.util.BaseActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        openIdentifierActivity()
    }

    private fun openIdentifierActivity() {
        Handler().postDelayed({
            val intent =
                Intent(this@SplashActivity, IdentifierActivity::class.java)
            startActivity(intent)
        }, if (BuildConfig.DEBUG) 1L else 2500L)
    }
}