package com.cyberfanta.desafiobetterfly.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Insets
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.cyberfanta.desafiobetterfly.R
import com.cyberfanta.desafiobetterfly.enumerator.AppState
import com.cyberfanta.desafiobetterfly.exceptions.ConnectionException
import com.cyberfanta.desafiobetterfly.presenters.QueryManager
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.*
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    @Suppress("PrivatePropertyName", "unused")
    private val TAG = this::class.java.simpleName

    //Firebase Variables
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    //UI Variables
    private var authorOpened: Boolean = false
    private var deviceWidth: Int = 0
    private var deviceHeight:Int = 0

    //Async Variables
    private lateinit var queryManager: QueryManager
    private var queriesManagerThread0 = Thread(InitializingQueryManager())
    private var queriesManagerThread1 = Thread(AsyncQueryManager())
    private var queriesManagerThread2 = Thread(AsyncQueryManager())
    private var queriesManagerThread3 = Thread(AsyncQueryManager())
    private var queriesManagerThread4 = Thread(AsyncBitmapQueryManager())
    private var queriesManagerThread5 = Thread(AsyncBitmapQueryManager())
    private var characterPagesLoaded = 1
    private var locationPagesLoaded = 1
    private var episodePagesLoaded = 1
    //currentTypeSearch:
    // 1 = character , 2 = location , 3 = episode,
    //4 = characterDetail, 5 = locationDetail, 6 = episodeDetail
    private var currentTypeSearch = 1
    private var currentIdSearch = 0
    private var currentBitmapSearch: Queue<Int?> = LinkedList()
    private var currentBitmapData: Queue<BitmapMessage?> = LinkedList()

    private var deleteMe = 0

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

        //Obtaining the MainActivity Presenter named QueryManager
        queryManager = QueryManager()

        //Binding onClick function for about menu option
        val author : ConstraintLayout = findViewById(R.id.author)
        author.setOnClickListener {
            authorSelected(author)
            authorOpened = false
        }
    }

    /**
     * Action to make when this app start
     */
    override fun onStart() {
        super.onStart()

        //Initial loading for recyclers view
        if (!queriesManagerThread0.isAlive)
            queriesManagerThread0.start()
    }

    /**
     * The finishing action of this app
     */
    override fun onDestroy() {
        if (queriesManagerThread1.isAlive)
            queriesManagerThread1.interrupt()
        if (queriesManagerThread2.isAlive)
            queriesManagerThread2.interrupt()
        if (queriesManagerThread3.isAlive)
            queriesManagerThread3.interrupt()

        super.onDestroy()

        //This way don't wait for Android Garbage collection. However, Android Studio work better with this.
        exitProcess(0)
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

    //Asynchronous Load data
    /**
     * This inner class will start the loading data for every recycler view on this app
     */
    private inner class InitializingQueryManager : Runnable {
        override fun run() {
            val message = handler.obtainMessage()
            try {
                queryManager.getCharacterPage(1)
                val message1 = handler.obtainMessage()
                message1.obj = AppState.Character_Page_Loaded
                handler.sendMessageAtFrontOfQueue(message1)

                queryManager.getLocationPage(1)
                val message2 = handler.obtainMessage()
                message2.obj = AppState.Location_Page_Loaded
                handler.sendMessageAtFrontOfQueue(message2)

                queryManager.getEpisodePage(1)
                val message3 = handler.obtainMessage()
                message3.obj = AppState.Episode_Page_Loaded
                handler.sendMessageAtFrontOfQueue(message3)
            } catch (e: ConnectionException) {
                message.obj = AppState.Load_Failed
                handler.sendMessageAtFrontOfQueue(message)
            }
        }
    }

    /**
     * Asynchronous Load data for QueryManager Class
     */
    private inner class AsyncQueryManager : Runnable {
        override fun run() {
            val message = handler.obtainMessage()

            try {
                when (currentTypeSearch) {
                    1 -> {
                        queryManager.getCharacterPage(characterPagesLoaded)
                        message.obj = AppState.Character_Page_Loaded
                    }
                    2 -> {
                        queryManager.getLocationPage(locationPagesLoaded)
                        message.obj = AppState.Location_Page_Loaded
                    }
                    3 -> {
                        queryManager.getEpisodePage(episodePagesLoaded)
                        message.obj = AppState.Episode_Page_Loaded
                    }
                    4 -> {
                        queryManager.getCharacterDetail(currentIdSearch)
                        message.obj = AppState.Character_Page_Loaded
                    }
                    5 -> {
                        queryManager.getLocationDetail(currentIdSearch)
                        message.obj = AppState.Location_Page_Loaded
                    }
                    6 -> {
                        queryManager.getEpisodeDetail(currentIdSearch)
                        message.obj = AppState.Episode_Page_Loaded
                    }
                }
            } catch (e: ConnectionException) {
                message.obj = AppState.Load_Failed
            }
            handler.sendMessageAtFrontOfQueue(message)
        }
    }

    /**
     * Asynchronous Load bitmaps for QueryManager Class
     */
    private inner class AsyncBitmapQueryManager : Runnable {
        override fun run() {
            val message = handler.obtainMessage()

            try {
                currentBitmapData.add(
                    currentBitmapSearch.poll()?.let { queryManager.getCharacterAvatar(it) }!!
                )
                message.obj = AppState.Character_Avatar_Loaded
            } catch (e: ConnectionException) {
                message.obj = AppState.Load_Failed
            }
            handler.sendMessageAtFrontOfQueue(message)
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
                    message.obj.equals(AppState.Character_Page_Loaded) -> {
                        val page = queryManager.getCharacterPage(characterPagesLoaded)
                        Log.i(TAG, page.toString())
                        currentBitmapSearch.add(page?.results?.get(0)?.id!!)

                        //Load an image
                        if (!queriesManagerThread4.isAlive)
                            queriesManagerThread4.start()

                        currentBitmapSearch.add(page.results.get(1)?.id!!)

                        //Load an image
                        if (!queriesManagerThread5.isAlive)
                            queriesManagerThread5.start()
                    }
                    message.obj.equals(AppState.Location_Page_Loaded) -> {
                    }
                    message.obj.equals(AppState.Episode_Page_Loaded) -> {
//                        loadJobData()

//                        val imageView = findViewById<ImageView>(R.id.loading)
//                        imageView.visibility = View.GONE
                    }
                    message.obj.equals(AppState.Character_Avatar_Loaded) -> {
                        if (deleteMe == 0) {
                            val tv: TextView = findViewById(R.id.textView)
                            val iv: ImageView = findViewById(R.id.imageView)
                            val data = currentBitmapData.poll()
                            tv.text = data?.id.toString()
                            iv.setImageBitmap(data?.bitmap)
                            deleteMe++
                        } else {
                            val tv: TextView = findViewById(R.id.textView2)
                            val iv: ImageView = findViewById(R.id.imageView2)
                            val data = currentBitmapData.poll()
                            tv.text = data?.id.toString()
                            iv.setImageBitmap(data?.bitmap)
                        }
                    }
                }
            }
        }
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

    //Utilities
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


}