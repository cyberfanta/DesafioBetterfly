package com.cyberfanta.desafiobetterfly.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cyberfanta.desafiobetterfly.R
import com.google.firebase.analytics.FirebaseAnalytics


class MainActivity : AppCompatActivity() {
    private var mFirebaseAnalytics: FirebaseAnalytics? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
    }
}