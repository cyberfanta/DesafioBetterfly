package com.cyberfanta.desafiobetterfly.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.cyberfanta.desafiobetterfly.R
import com.cyberfanta.desafiobetterfly.models.character.CharacterDetail
import com.cyberfanta.desafiobetterfly.presenters.QueryManager

class CharacterActivity : AppCompatActivity() {
    @Suppress("PrivatePropertyName", "unused")
    private val TAG = this::class.java.simpleName

    //UI Variables
    private var authorOpened: Boolean = false
    private var deviceWidth: Int = 0
    private var deviceHeight:Int = 0

    //Current object to deploy
    private var currentIdSearch: Int = 0

    /**
     * The initial point of this app
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character)

        getDeviceDimensions()
        loadObject()
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

    //Utilities
    /**
     * Get the device dimension
     */
    private fun getDeviceDimensions(){
        deviceWidth = intent.getStringExtra("deviceWidth")!!.toInt()
        deviceHeight = intent.getStringExtra("deviceHeight")!!.toInt()
        currentIdSearch = intent.getStringExtra("currentIdSearch")!!.toInt()

        Log.i(TAG, "currentIdSearch: $currentIdSearch")
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
    @Suppress("SameParameterValue")
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

    // Loading data
    /**
     * Load all data in the view
     */
    private fun loadObject() {
        val queryManager = QueryManager()
        val query: CharacterDetail
        try {
            query = queryManager.getCharacterDetail(currentIdSearch)
        } catch (e: Exception) {
            return
        }
        Log.i(TAG, query.toString())

        var textView: TextView = findViewById(R.id.characterIdData)
        textView.text = query.id.toString()
        textView = findViewById(R.id.characterIdLabel)
        textView.text = getString(R.string.idLabel)

        textView = findViewById(R.id.characterNameData)
        textView.text = query.name
        textView = findViewById(R.id.characterNameLabel)
        textView.text = getString(R.string.nameLabel)

        textView = findViewById(R.id.characterStatusData)
        textView.text = query.status
        textView = findViewById(R.id.characterStatusLabel)
        textView.text = getString(R.string.statusLabel)

        textView = findViewById(R.id.characterSpeciesData)
        textView.text = query.species
        textView = findViewById(R.id.characterSpeciesLabel)
        textView.text = getString(R.string.specieLabel)

        textView = findViewById(R.id.characterTypeData)
        textView.text = query.type
        textView = findViewById(R.id.characterTypeLabel)
        textView.text = getString(R.string.typeLabel)

        textView = findViewById(R.id.characterGenderData)
        textView.text = query.gender
        textView = findViewById(R.id.characterGenderLabel)
        textView.text = getString(R.string.genderLabel)

        textView = findViewById(R.id.characterOriginData)
        textView.text = query.origin2?.name
        textView = findViewById(R.id.characterOriginLabel)
        textView.text = getString(R.string.originLabel)

        textView = findViewById(R.id.characterLocationData)
        textView.text = query.location2?.name
        textView = findViewById(R.id.characterLocationLabel)
        textView.text = getString(R.string.locationLabel)

        textView = findViewById(R.id.characterEpisodeData)
        var text = ""
        for (episode in query.episode!!)
            text += episode?.split("https://rickandmortyapi.com/api/episode/")?.get(1) + ", "
        text = text.substring(0, text.length - 2)
        textView.text = text
        textView = findViewById(R.id.characterEpisodeLabel)
        textView.text = getString(R.string.episodeLabel)

        val imageView: ImageView = findViewById(R.id.imageView)
        try {
            imageView.setImageBitmap(query.id?.let { queryManager.getCharacterAvatar(it).bitmap })
        } catch (e: Exception) {
            return
        }
    }
}