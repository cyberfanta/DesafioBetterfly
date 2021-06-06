package com.cyberfanta.desafiobetterfly.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.cyberfanta.desafiobetterfly.R
import com.cyberfanta.desafiobetterfly.enumerator.AppState
import com.cyberfanta.desafiobetterfly.exceptions.ConnectionException
import com.cyberfanta.desafiobetterfly.models.location.LocationDetail
import com.cyberfanta.desafiobetterfly.presenters.QueryManager

class LocationActivity : AppCompatActivity() {
    @Suppress("PrivatePropertyName", "unused")
    private val TAG = this::class.java.simpleName

    //UI Variables
    private var authorOpened: Boolean = false
    private var deviceWidth: Int = 0
    private var deviceHeight:Int = 0

    //Current object to deploy
    private var currentIdSearch: Int = 0

    //Async Task Variables
    private val queryManager = QueryManager()
    private var queryDetailedThread = Thread(AsyncDetailedQueryManager())
    private val detailList = LinkedHashMap<Int, String>(0)
    private lateinit var query: LocationDetail

    /**
     * The initial point of this view
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

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
        try {
            query = queryManager.getLocationDetail(currentIdSearch)
        } catch (e: Exception) {
            return
        }

        var textView: TextView = findViewById(R.id.locationIdData)
        textView.text = query.id.toString()
        textView = findViewById(R.id.locationIdLabel)
        textView.text = getString(R.string.idLabel)

        textView = findViewById(R.id.locationNameData)
        textView.text = query.name
        textView = findViewById(R.id.locationNameLabel)
        textView.text = getString(R.string.nameLabel)

        textView = findViewById(R.id.locationTypeData)
        textView.text = query.type
        textView = findViewById(R.id.locationTypeLabel)
        textView.text = getString(R.string.typeLabel)

        textView = findViewById(R.id.locationDimensionData)
        textView.text = query.dimension
        textView = findViewById(R.id.locationDimensionLabel)
        textView.text = getString(R.string.dimensionLabel)

        textView = findViewById(R.id.locationCharactersData)
        var text = ""
        for (data in query.residents!!){
            val split = data?.split("https://rickandmortyapi.com/api/character/")
            text += split?.get(1) + "\n"
            detailList[split?.get(1)?.toInt()!!] = ""
        }
        textView.text = text.substring(0, text.length-1)
        textView = findViewById(R.id.locationCharactersLabel)
        textView.text = getString(R.string.residentsLabel)

        if (!queryDetailedThread.isAlive) {
            queryDetailedThread = Thread(AsyncDetailedQueryManager())
            queryDetailedThread.start()
        }
    }

    /**
     * Asynchronous Load Detailed Item for QueryManager Class
     */
    private inner class AsyncDetailedQueryManager : Runnable {
        override fun run() {
            try {
                for (detail in detailList) {
                    val message = handler.obtainMessage()
                    detailList[detail.key] = queryManager.getCharacterDetail(detail.key).name!!
                    message.obj = AppState.Character_Detail_Loaded
                    handler.sendMessageAtFrontOfQueue(message)
                }
            } catch (e: ConnectionException) {
                val message = handler.obtainMessage()
                message.obj = AppState.Load_Failed
                handler.sendMessageAtFrontOfQueue(message)
            }
        }
    }

    /**
     * Handle the actions when asynchronous task are ready to update the ui
     */
    @Suppress("DEPRECATION")
    @SuppressLint("HandlerLeak")
    private val handler: Handler = object : Handler() {
        override fun handleMessage(message: Message) {
            if (message.obj != null) {
                when {
                    message.obj.equals(AppState.Character_Detail_Loaded) -> {
                        var textView : TextView = findViewById(R.id.locationCharactersData)
                        textView.visibility = View.INVISIBLE
                        val linearLayout: LinearLayout = findViewById(R.id.locationCharactersDataList)
                        linearLayout.removeAllViewsInLayout()
                        System.gc()

                        for (episode in query.residents!!) {
                            textView = TextView (this@LocationActivity, null, R.style.characterDetailBottomList)
                            val split = episode?.split("https://rickandmortyapi.com/api/character/")?.get(1)
                            val text: String = split!! + ": " + detailList[split.toInt()]
                            textView.text = text
                            textView.layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            textView.setTextColor(resources.getColor(R.color.soft_black_37))
                            textView.setOnClickListener {
                                val intent = Intent(this@LocationActivity, CharacterActivity::class.java)
                                intent.putExtra("deviceWidth", deviceWidth.toString())
                                intent.putExtra("deviceHeight", deviceHeight.toString())
                                intent.putExtra("currentIdSearch", split)
                                startActivity(intent)
                            }
                            linearLayout.addView(textView)
                        }
                        textView = findViewById(R.id.locationCharactersLabel)
                        val params = textView.layoutParams as ConstraintLayout.LayoutParams
                        params.bottomToBottom = linearLayout.id
                        textView.requestLayout()
                    }
                }
            }
        }
    }
}