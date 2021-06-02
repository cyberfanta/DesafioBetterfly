package com.cyberfanta.desafiobetterfly.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Insets
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.*
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.cyberfanta.desafiobetterfly.R
import com.google.firebase.analytics.FirebaseAnalytics


class MainActivity : AppCompatActivity() {
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    private var authorOpened: Boolean = false
    private var deviceWidth: Int = 0
    private var deviceHeight:Int = 0


    /**
     * The initial point of this app
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)

        //Calculate Current Device Size
        calculateDeviceDimensions()

        //Binding all onClick functions
        bindingAllOnClickFunctions()
    }

    /**
     * Load all onClick functions for all views on MainActivity
     */
    private fun bindingAllOnClickFunctions (){
        //Binding onClick function for about menu option
        val author : ConstraintLayout = findViewById(R.id.author)
        author.setOnClickListener {
            authorSelected(author)
            authorOpened = false
        }

        //Binding for characterButton
        val button1 : Button = findViewById(R.id.characterButton)
        button1.setOnClickListener {
            footerAction(button1)
        }

        //Binding for locationButton
        val button2 : Button = findViewById(R.id.locationButton)
        button2.setOnClickListener {
            footerAction(button2)
        }

        //Binding for episodeButton
        val button3 : Button = findViewById(R.id.episodeButton)
        button3.setOnClickListener {
            footerAction(button3)
        }
    }

    /**
     * Process the behavior of the app when user press back button
     */
    override fun onBackPressed() {
        val constraintLayout : ConstraintLayout = findViewById(R.id.author)
        if (authorOpened) {
            authorSelected(constraintLayout)
            authorOpened = false
            return
        }

        super.onBackPressed()
    }

    //Main Menu
    /**
     * Create the setting menu of the application
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    /**
     * Handle the setting menu of the application
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_about) {
            val constraintLayout = findViewById<ConstraintLayout>(R.id.author)
            constraintLayout.visibility = View.VISIBLE
            setAnimation(constraintLayout, "translationX", 300, false, deviceWidth.toFloat(), 0f)
            authorOpened = true
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    // Author Menu
    /**
     * Show the developer info
     */
    private fun authorSelected(view: View) {
        setAnimation(view, "translationX", 300, false, 0f, deviceWidth.toFloat())
    }

    /**
     * Make a simple animation on a view
     */
    private fun setAnimation(view: View, propertyName: String, duration: Long, repeat: Boolean, value1: Float, value2: Float) {
        val objectAnimator = ObjectAnimator.ofFloat(view, propertyName, value1, value2)
        val animator = AnimatorSet()
        animator.play(objectAnimator)
        animator.duration = duration
        if (repeat) {
            animator.addListener(
                object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        animator.start()
                    }
                }
            )
        }
        animator.start()
    }

    /**
     * Calculate the device dimension
     */
    private fun calculateDeviceDimensions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics = windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            deviceWidth = windowMetrics.bounds.width() - insets.left - insets.right
            deviceHeight = windowMetrics.bounds.height() - insets.top - insets.bottom
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            deviceWidth = displayMetrics.widthPixels
            deviceHeight = displayMetrics.heightPixels
        }
    }

    /**
     * Footer function to deal with the behavior
     */
    private fun footerAction(view: View) {
        val characterIVColor : ImageView = findViewById(R.id.characterButtonColor)
        val locationIVColor : ImageView = findViewById(R.id.locationButtonColor)
        val episodeIVColor : ImageView = findViewById(R.id.episodeButtonColor)
        val characterIVBN : ImageView = findViewById(R.id.characterButtonBN)
        val locationIVBN : ImageView = findViewById(R.id.locationButtonBN)
        val episodeIVBN : ImageView = findViewById(R.id.episodeButtonBN)

        when(view.id) {
            R.id.characterButton -> {
                locationIVColor.visibility = View.INVISIBLE
                episodeIVColor.visibility = View.INVISIBLE
                locationIVBN.visibility = View.VISIBLE
                episodeIVBN.visibility = View.VISIBLE

                characterIVColor.visibility = View.VISIBLE
                characterIVBN.visibility = View.INVISIBLE
            }
            R.id.locationButton -> {
                characterIVColor.visibility = View.INVISIBLE
                episodeIVColor.visibility = View.INVISIBLE
                characterIVBN.visibility = View.VISIBLE
                episodeIVBN.visibility = View.VISIBLE

                locationIVColor.visibility = View.VISIBLE
                locationIVBN.visibility = View.INVISIBLE
            }
            R.id.episodeButton -> {
                characterIVColor.visibility = View.INVISIBLE
                locationIVColor.visibility = View.INVISIBLE
                characterIVBN.visibility = View.VISIBLE
                locationIVBN.visibility = View.VISIBLE

                episodeIVColor.visibility = View.VISIBLE
                episodeIVBN.visibility = View.INVISIBLE
            }
        }
    }
}