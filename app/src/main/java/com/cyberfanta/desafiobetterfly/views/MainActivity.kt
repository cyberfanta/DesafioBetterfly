package com.cyberfanta.desafiobetterfly.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Insets
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cyberfanta.desafiobetterfly.R
import com.cyberfanta.desafiobetterfly.enumerator.AppState
import com.cyberfanta.desafiobetterfly.exceptions.ConnectionException
import com.cyberfanta.desafiobetterfly.models.character.Character
import com.cyberfanta.desafiobetterfly.presenters.QueryManager
import com.cyberfanta.desafiobetterfly.views.cards.CardAdapterCharacters
import com.cyberfanta.desafiobetterfly.views.cards.CardItemCharacters
import com.google.firebase.analytics.FirebaseAnalytics
import java.lang.Exception
import java.text.FieldPosition
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

    //Async Task Variables
    private lateinit var queryManager: QueryManager
    private var queriesManagerThread0 = Thread(InitializingQueryManager())
    private var queriesManagerThread1 = Thread(AsyncQueryManager())
    private var queriesManagerThread2 = Thread(AsyncQueryManager())
    private var queriesManagerThread3 = Thread(AsyncQueryManager())
    private var queriesManagerThread4 = Thread(AsyncBitmapQueryManager())
    private var queriesManagerThread5 = Thread(AsyncBitmapQueryManager())
    private var queriesManagerThread6 = Thread(AsyncBitmapQueryManager())
    private var queriesManagerThread7 = Thread(AsyncBitmapQueryManager())
    private var characterPagesLoaded = 1
    private var locationPagesLoaded = 1
    private var episodePagesLoaded = 1
    //currentTypeSearch:
    // 1 = character , 2 = location , 3 = episode,
    // 4 = characterDetail, 5 = locationDetail, 6 = episodeDetail
    private var currentTypeSearch = 1
    private var currentIdSearch = 0
    private var currentBitmapSearch: Queue<Int?> = LinkedList()
    private var currentBitmapData: Queue<BitmapMessage?> = LinkedList()

    //Recyclers View Variables
    private lateinit var adapterCharacters: CardAdapterCharacters
    private var cardListCharacters: ArrayList<CardItemCharacters>
            = ArrayList<CardItemCharacters>(20)


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

        //Binding all onClick functions
        bindingAllOnClickFunctions()

        //Initialize all recyclers view
        initializeAllRecyclersView()

        //Initial loading for recyclers view
        if (!queriesManagerThread0.isAlive)
            queriesManagerThread0.start()
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
     * Action to make when this app start
     */
    override fun onStart() {
        super.onStart()
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
        if (queriesManagerThread4.isAlive)
            queriesManagerThread4.interrupt()
        if (queriesManagerThread5.isAlive)
            queriesManagerThread5.interrupt()

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
                        queryManager.getCharacterAvatar(currentIdSearch)
                        message.obj = AppState.Character_Detail_Loaded
                    }
                    5 -> {
                        queryManager.getLocationDetail(currentIdSearch)
                        message.obj = AppState.Location_Detail_Loaded
                    }
                    6 -> {
                        queryManager.getEpisodeDetail(currentIdSearch)
                        message.obj = AppState.Episode_Detail_Loaded
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

//            currentBitmapSearch.add(bitmap?.id!!)
//            currentBitmapData.poll()?.let { loadCharacterImage(it) }
//            val bitmapSearch = currentBitmapSearch
//            : Queue<Int?> = LinkedList()

            try {
                do{
                    val message = handler.obtainMessage()
//                    currentBitmapData.add(
//                        currentBitmapSearch.poll().let { it?.let { it1 ->
//                            queryManager.getCharacterAvatar(
//                                it1
//                            )
//                        } }
//                    )

                    currentBitmapData.add(queryManager.getCharacterAvatar(currentBitmapSearch.poll()!!))

                    message.obj = AppState.Character_Avatar_Loaded
                    handler.sendMessageAtFrontOfQueue(message)
                } while (currentBitmapSearch.size > 0)
//                for (bitmap in bitmapSearch) {
//                    val message = handler.obtainMessage()
//                    currentBitmapData.add(
//                        bitmapSearch.poll().let { it?.let { it1 ->
//                            queryManager.getCharacterAvatar(
//                                it1
//                            )
//                        } }
//                    )
//                    message.obj = AppState.Character_Avatar_Loaded
//                    handler.sendMessageAtFrontOfQueue(message)
//                }
            } catch (e: ConnectionException) {
                val message = handler.obtainMessage()
                message.obj = AppState.Load_Failed
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
                    message.obj.equals(AppState.Character_Page_Loaded) -> {
                        try {
                            val page = queryManager.getCharacterPage(characterPagesLoaded)
                            Log.i(TAG, page.toString())
                            loadCharacterPage(page)
                            characterPagesLoaded++

                            for (bitmap in page.results!!)
                                currentBitmapSearch.add(bitmap?.id!!)

                            queryFromApi2()

                        } catch (e: Exception) {
                            //todo: Feedback to the user
                        }
//                        currentBitmapSearch.add(page?.results?.get(0)?.id!!)
//
//                        //Load an image
//                        if (!queriesManagerThread4.isAlive)
//                            queriesManagerThread4.start()
//
//                        currentBitmapSearch.add(page.results.get(1)?.id!!)
//
//                        //Load an image
//                        if (!queriesManagerThread5.isAlive)
//                            queriesManagerThread5.start()

                    }
                    message.obj.equals(AppState.Location_Page_Loaded) -> {
                    }
                    message.obj.equals(AppState.Episode_Page_Loaded) -> {
//                        loadJobData()

//                        val imageView = findViewById<ImageView>(R.id.loading)
//                        imageView.visibility = View.GONE
                    }
                    message.obj.equals(AppState.Character_Detail_Loaded) -> {
                        loadCharacterDetail()
                    }
                    message.obj.equals(AppState.Location_Detail_Loaded) -> {
                    }
                    message.obj.equals(AppState.Episode_Detail_Loaded) -> {
                    }
                    message.obj.equals(AppState.Character_Avatar_Loaded) -> {
                        currentBitmapData.poll()?.let { loadCharacterImage(it) }

//                        if (deleteMe == 0) {
//                            val tv: TextView = findViewById(R.id.textView)
//                            val iv: ImageView = findViewById(R.id.imageView)
//                            val data = currentBitmapData.poll()
//                            tv.text = data?.id.toString()
//                            iv.setImageBitmap(data?.bitmap)
//                            deleteMe++
//                        } else {
//                            val tv: TextView = findViewById(R.id.textView2)
//                            val iv: ImageView = findViewById(R.id.imageView2)
//                            val data = currentBitmapData.poll()
//                            tv.text = data?.id.toString()
//                            iv.setImageBitmap(data?.bitmap)
//                        }
                    }
                }
            }
        }
    }

    //RecyclerView UI
    /**
     * Initialize all the recyclerView
     */
    private fun initializeAllRecyclersView() {
        //CharactersRV
        var recycler: RecyclerView = findViewById(R.id.charactersRV)
        recycler.setHasFixedSize(true)
        val layoutManagerCharactersRV: RecyclerView.LayoutManager = GridLayoutManager(this, 3)
        adapterCharacters = CardAdapterCharacters(cardListCharacters)
        recycler.layoutManager = layoutManagerCharactersRV
        recycler.adapter = adapterCharacters

        adapterCharacters.setOnItemClickListener(object:
            CardAdapterCharacters.OnItemClickListener {
            override fun onItemClick(position: Int) {
//                val imageView = findViewById<ImageView>(R.id.loading)
//                imageView.visibility = View.VISIBLE

                currentIdSearch = position + 1
//                currentIdSearch = cardListCharacters[position].id?.toInt()!!
                currentTypeSearch = 4
                Log.i(TAG, "currentIdSearch: $currentIdSearch")
                Log.i(TAG, "currentTypeSearch: $currentTypeSearch")
                queryFromApi()
            }
        })

        adapterCharacters.setOnBottomReachedListener(object:
            CardAdapterCharacters.OnBottomReachedListener {
            override fun onBottomReached(position: Int) {
//                val imageView = findViewById<ImageView>(R.id.loading)
//                imageView.visibility = View.VISIBLE

                currentTypeSearch = 1
                queryFromApi()
            }
        })

        recycler.addOnScrollListener(object:
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    currentTypeSearch = 1
                    queryFromApi()
                }
            }
        })
    }

    /**
     * Running the async task to get data
     */
    fun queryFromApi() {
        if (!queriesManagerThread1.isAlive) {
            queriesManagerThread1 = Thread(AsyncQueryManager())
            queriesManagerThread1.start()
        } else if (!queriesManagerThread2.isAlive) {
            queriesManagerThread2 = Thread(AsyncQueryManager())
            queriesManagerThread2.start()
        } else if (!queriesManagerThread3.isAlive) {
            queriesManagerThread3 = Thread(AsyncQueryManager())
            queriesManagerThread3.start()
        }
    }

    /**
     * Running the async task to get images
     */
    fun queryFromApi2() {
        if (!queriesManagerThread4.isAlive) {
            queriesManagerThread4 = Thread(AsyncBitmapQueryManager())
            queriesManagerThread4.start()
        } else if (!queriesManagerThread5.isAlive) {
            queriesManagerThread5 = Thread(AsyncBitmapQueryManager())
            queriesManagerThread5.start()
        } else if (!queriesManagerThread6.isAlive) {
            queriesManagerThread6 = Thread(AsyncBitmapQueryManager())
            queriesManagerThread6.start()
        } else if (!queriesManagerThread7.isAlive) {
            queriesManagerThread7 = Thread(AsyncBitmapQueryManager())
            queriesManagerThread7.start()
        }
    }

    /**
     * Recycler view filler for characters
     */
    fun loadCharacterPage(page: Character) {
        val size: Int = cardListCharacters.size

        for (characterDetail in page.results!!) {
            val cardCharacter = characterDetail?.let { CardItemCharacters(it) }
            cardListCharacters.add(cardCharacter!!)
        }

        adapterCharacters.notifyItemRangeInserted(size, 20)
    }

    /**
     * Load images for characters cards
     */
    fun loadCharacterImage(bitmapMessage: BitmapMessage) {
        cardListCharacters[bitmapMessage.id-1].image = bitmapMessage.bitmap!!
        adapterCharacters.notifyItemChanged(bitmapMessage.id-1)
    }

    /**
     * Recycler view filler for character details
     */
    fun loadCharacterDetail() {
        val intent = Intent(this, CharacterActivity::class.java)
        intent.putExtra("deviceWidth", deviceWidth.toString())
        intent.putExtra("deviceHeight", deviceHeight.toString())
        intent.putExtra("currentIdSearch", currentIdSearch.toString())
        startActivity(intent)
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

    // Footer Menu
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