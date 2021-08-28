package com.cyberfanta.desafiobetterfly.views

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cyberfanta.desafiobetterfly.R
import com.cyberfanta.desafiobetterfly.enumerator.AppState
import com.cyberfanta.desafiobetterfly.exceptions.ConnectionException
import com.cyberfanta.desafiobetterfly.models.character.Character
import com.cyberfanta.desafiobetterfly.models.episode.Episode
import com.cyberfanta.desafiobetterfly.models.location.Location
import com.cyberfanta.desafiobetterfly.presenters.AdsManager
import com.cyberfanta.desafiobetterfly.presenters.FirebaseManager
import com.cyberfanta.desafiobetterfly.presenters.QueryManager
import com.cyberfanta.desafiobetterfly.presenters.RateAppManager
import com.cyberfanta.desafiobetterfly.views.cards.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {
    @Suppress("PrivatePropertyName", "unused")
    private val TAG = this::class.java.simpleName

    //UI Variables
    private var authorOpened: Boolean = false
    private var deviceWidth: Int = 0
    private var deviceHeight:Int = 0

    //Async Task Variables
    private lateinit var queryManager: QueryManager
    private var queriesManagerThread = Thread(InitializingQueryManager())
    private var queryDetailedThread = Thread(AsyncDetailedQueryManager())
    private var characterPageThread1 = Thread(AsyncCharacterPage())
    private var characterPageThread2 = Thread(AsyncCharacterPage())
    private var characterPageThread3 = Thread(AsyncCharacterPage())
    private var locationPageThread1 = Thread(AsyncLocationPage())
    private var locationPageThread2 = Thread(AsyncLocationPage())
    private var locationPageThread3 = Thread(AsyncLocationPage())
    private var episodePageThread1 = Thread(AsyncEpisodePage())
    private var episodePageThread2 = Thread(AsyncEpisodePage())
    private var episodePageThread3 = Thread(AsyncEpisodePage())
    private var avatarThread1 = Thread(AsyncBitmapQueryManager())
    private var avatarThread2 = Thread(AsyncBitmapQueryManager())
    private var avatarThread3 = Thread(AsyncBitmapQueryManager())
    private var avatarThread4 = Thread(AsyncBitmapQueryManager())
    private var characterPagesLoaded = 1
    private var locationPagesLoaded = 1
    private var episodePagesLoaded = 1
    //currentTypeSearch: 4 = characterDetail, 5 = locationDetail, 6 = episodeDetail
    private var currentTypeSearch = 1
    private var currentIdSearch = 0
    private var currentBitmapSearch: Queue<Int?> = LinkedList()
    private var currentBitmapData: Queue<BitmapMessage?> = LinkedList()
    private val loadingItem = arrayOf(false, false, false, false, false, false, false)

    //Recyclers View Variables
    private lateinit var adapterCharacters: CardAdapterCharacters
    private var cardListCharacters: ArrayList<CardItemCharacters>
            = ArrayList<CardItemCharacters>(20)
    private lateinit var adapterLocations: CardAdapterLocations
    private var cardListLocations: ArrayList<CardItemLocations>
            = ArrayList<CardItemLocations>(20)
    private lateinit var adapterEpisodes: CardAdapterEpisodes
    private var cardListEpisodes: ArrayList<CardItemEpisodes>
            = ArrayList<CardItemEpisodes>(20)

    //Ad View Variable
    private lateinit var adView : FrameLayout

    /**
     * The initial point of this app
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Obtain the FirebaseAnalytics instance
        FirebaseManager(this)
        FirebaseManager.logEvent("$TAG: Opened", "App_Opened")

        //Obtain rate my app instance
        RateAppManager(this)

        //Calculate Current Device Size
        calculateDeviceDimensions()

        //Obtaining the MainActivity Presenter named QueryManager
        queryManager = QueryManager()

        //Binding all onClick functions
        bindingAllOnClickFunctions()

        //Initialize all recyclers view
        initializeAllRecyclersView()

        //Initial loading for recyclers view
        if (!queriesManagerThread.isAlive)
            queriesManagerThread.start()

        //Moving recycler views to its initial position
        val locationRW : RecyclerView = findViewById(R.id.locationsRV)
        val episodeRW : RecyclerView = findViewById(R.id.episodesRV)
        setAnimation(locationRW, "translationX", 300, false, 0f, -1f * deviceWidth)
        setAnimation(episodeRW, "translationX", 300, false, 0f, 1f * deviceWidth)

        //Starting all loading circle animations
        val charactersRVLoading : ImageView = findViewById(R.id.charactersRVLoading)
        setAnimation(charactersRVLoading, "rotation", 1000, true, 360f, 0f)
        val locationsRVLoading : ImageView = findViewById(R.id.locationsRVLoading)
        setAnimation(locationsRVLoading, "rotation", 1000, true, 360f, 0f)
        val episodesRVLoading : ImageView = findViewById(R.id.episodesRVLoading)
        setAnimation(episodesRVLoading, "rotation", 1000, true, 360f, 0f)

        //Loading ads manager
        adView = findViewById(R.id.adView)
        AdsManager.loadBannerAds(applicationContext, deviceWidth.toFloat())
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

        //Binding for authorMenu Button Send Email
        val constraintLayout1 : ConstraintLayout = findViewById(R.id.authorId)
        constraintLayout1.setOnClickListener {
            FirebaseManager.logEvent("Sending email: Author", "Send_Email")
            @Suppress("SpellCheckingInspection")
            @SuppressLint("SimpleDateFormat")
            val dateHour = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            sendAuthorEmail(
                "masterjulioleon@gmail.com",
                getString(R.string.app_name) + " --- " + getString(R.string.authorEmailSubject) + " --- " + dateHour,
                getString(R.string.authorEmailBody) + "",
                getString(R.string.authorEmailChooser) + ""
            )
        }

        //Binding for authorMenu Button Open Api Url
        val constraintLayout2 : ConstraintLayout = findViewById(R.id.poweredId)
        constraintLayout2.setOnClickListener {
            FirebaseManager.logEvent("Open website: API - " + getString(R.string.poweredByUrl), "Open_Api")
            openURL(getString(R.string.poweredByUrl))
        }
    }

    /**
     * Send Email to author via intent
     */
    @Suppress("SameParameterValue")
    private fun sendAuthorEmail(email: String, subject: String, body: String, chooserMessage: String){
        val intent = Intent(Intent.ACTION_SEND)
        intent.data = Uri.parse("mailto:")
        intent.type = "plain/text"
        intent.putExtra(Intent.EXTRA_TEXT, body)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        startActivity(Intent.createChooser(intent, chooserMessage))
    }

    /**
     * Open URL on intent
     */
    private fun openURL(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    /**
     * The finishing action of this app
     */
    override fun onDestroy() {
        if (characterPageThread1.isAlive)
            characterPageThread1.interrupt()
        if (characterPageThread2.isAlive)
            characterPageThread2.interrupt()
        if (characterPageThread3.isAlive)
            characterPageThread3.interrupt()
        if (avatarThread1.isAlive)
            avatarThread1.interrupt()
        if (avatarThread2.isAlive)
            avatarThread2.interrupt()

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

            FirebaseManager.logEvent("Device Button: Back", "Device_Button")
            return
        }

        FirebaseManager.logEvent("$TAG: Closed", "App_Closed")
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
        when (item.itemId) {
            R.id.item_policy -> {
                FirebaseManager.logEvent("Menu: Policy", "Open_Menu")
                val uri = Uri.parse(getString(R.string.item_policy_page))
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
                return true
            }
            R.id.item_rate -> {
                FirebaseManager.logEvent("Menu: Rate App", "Open_Menu")
                RateAppManager.requestReview(this)
                return true
            }
            R.id.item_about -> {
                FirebaseManager.logEvent("Menu: Author", "Open_Menu")
                val constraintLayout = findViewById<ConstraintLayout>(R.id.author)
                constraintLayout.visibility = View.VISIBLE
                setAnimation(
                    constraintLayout,
                    "translationX",
                    300,
                    false,
                    deviceWidth.toFloat(),
                    0f
                )
                authorOpened = true
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //Asynchronous Load data
    /**
     * This inner class will start the loading data for every recycler view on this app
     */
    private inner class InitializingQueryManager : Runnable {
        override fun run() {
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
                val message = handler.obtainMessage()
                message.obj = AppState.Load_Failed
                handler.sendMessageAtFrontOfQueue(message)
            }
        }
    }

    /**
     * Asynchronous Load Character Page for QueryManager Class
     */
    private inner class AsyncCharacterPage : Runnable {
        override fun run() {
            val message = handler.obtainMessage()

            try {
                queryManager.getCharacterPage(characterPagesLoaded)
                message.obj = AppState.Character_Page_Loaded
            } catch (e: ConnectionException) {
                message.obj = AppState.Load_Failed
            }
            handler.sendMessageAtFrontOfQueue(message)
        }
    }

    /**
     * Asynchronous Load Location Page for QueryManager Class
     */
    private inner class AsyncLocationPage : Runnable {
        override fun run() {
            val message = handler.obtainMessage()

            try {
                queryManager.getLocationPage(locationPagesLoaded)
                message.obj = AppState.Location_Page_Loaded
            } catch (e: ConnectionException) {
                message.obj = AppState.Load_Failed
            }
            handler.sendMessageAtFrontOfQueue(message)
        }
    }

    /**
     * Asynchronous Load Episode Page for QueryManager Class
     */
    private inner class AsyncEpisodePage : Runnable {
        override fun run() {
            val message = handler.obtainMessage()

            try {
                queryManager.getEpisodePage(episodePagesLoaded)
                message.obj = AppState.Episode_Page_Loaded
            } catch (e: ConnectionException) {
                message.obj = AppState.Load_Failed
            }
            handler.sendMessageAtFrontOfQueue(message)
        }
    }

    /**
     * Asynchronous Load Detailed Item for QueryManager Class
     */
    private inner class AsyncDetailedQueryManager : Runnable {
        override fun run() {
            val message = handler.obtainMessage()

            try {
                when (currentTypeSearch) {
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
            try {
                do{
                    val message = handler.obtainMessage()
                    if (currentBitmapSearch.size >  0) {
                        val currentSearch = currentBitmapSearch.poll()!!
                        FirebaseManager.logEvent("Search Avatar: $currentSearch - " + queryManager.getCharacterDetail(currentSearch).name, "Get_Avatar")
                        currentBitmapData.add(queryManager.getCharacterAvatar(currentSearch))
                    }
                    message.obj = AppState.Character_Avatar_Loaded
                    handler.sendMessageAtFrontOfQueue(message)
                } while (currentBitmapSearch.size > 0)
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
                            loadCharacterPage(page)
                            characterPagesLoaded++

                            for (bitmap in page.results!!)
                                currentBitmapSearch.add(bitmap?.id!!)
                            queryAvatar()

                        } catch (e: ConnectionException) {
                            //todo: Feedback to the user
//                            Toast.makeText(applicationContext, R.string.characterPageFail, Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            //todo: Feedback to the user
//                            Toast.makeText(applicationContext, R.string.loadingFail, Toast.LENGTH_SHORT).show()
                        }
                        if (loadingItem[0] && !loadingItem[3]) {
                            val imageView = findViewById<ImageView>(R.id.charactersRVLoading)
                            imageView.visibility = View.INVISIBLE
                        }
                        loadingItem[0] = false
                    }
                    message.obj.equals(AppState.Location_Page_Loaded) -> {
                        try {
                            val page = queryManager.getLocationPage(locationPagesLoaded)
                            loadLocationPage(page)
                            locationPagesLoaded++
                        } catch (e: Exception) {
                            //todo: Feedback to the user
//                            Toast.makeText(applicationContext, R.string.locationPageFail, Toast.LENGTH_SHORT).show()
                        }
                        if (loadingItem[1] && !loadingItem[4]) {
                            val imageView = findViewById<ImageView>(R.id.locationsRVLoading)
                            imageView.visibility = View.INVISIBLE
                        }
                        loadingItem[1] = false
                    }
                    message.obj.equals(AppState.Episode_Page_Loaded) -> {
                        try {
                            val page = queryManager.getEpisodePage(episodePagesLoaded)
                            loadEpisodePage(page)
                            episodePagesLoaded++
                        } catch (e: Exception) {
                            //todo: Feedback to the user
//                            Toast.makeText(applicationContext, R.string.episodePageFail, Toast.LENGTH_SHORT).show()
                        }
                        if (loadingItem[2] && !loadingItem[5]) {
                            val imageView = findViewById<ImageView>(R.id.episodesRVLoading)
                            imageView.visibility = View.INVISIBLE
                        }
                        loadingItem[2] = false
                    }
                    message.obj.equals(AppState.Character_Detail_Loaded) -> {
                        if (loadingItem[3] && !loadingItem[0]) {
                            val imageView = findViewById<ImageView>(R.id.charactersRVLoading)
                            imageView.visibility = View.INVISIBLE
                        }
                        loadingItem[3] = false

                        loadCharacterDetail()
                    }
                    message.obj.equals(AppState.Location_Detail_Loaded) -> {
                        if (loadingItem[4] && !loadingItem[1]) {
                            val imageView = findViewById<ImageView>(R.id.locationsRVLoading)
                            imageView.visibility = View.INVISIBLE
                        }
                        loadingItem[4] = false

                        loadLocationDetail()
                    }
                    message.obj.equals(AppState.Episode_Detail_Loaded) -> {
                        if (loadingItem[5] && !loadingItem[2]) {
                            val imageView = findViewById<ImageView>(R.id.episodesRVLoading)
                            imageView.visibility = View.INVISIBLE
                        }
                        loadingItem[5] = false

                        loadEpisodeDetail()
                    }
                    message.obj.equals(AppState.Character_Avatar_Loaded) -> {
                        currentBitmapData.poll()?.let { loadCharacterImage(it) }
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
        val recycler1: RecyclerView = findViewById(R.id.charactersRV)
        recycler1.setHasFixedSize(true)
        val layoutManagerCharactersRV: RecyclerView.LayoutManager = GridLayoutManager(this, 3)
        adapterCharacters = CardAdapterCharacters(cardListCharacters)
        recycler1.layoutManager = layoutManagerCharactersRV
        recycler1.adapter = adapterCharacters

        adapterCharacters.setOnItemClickListener(object:
            CardAdapterCharacters.OnItemClickListener {
            override fun onItemClick(position: Int) {
                FirebaseManager.logEvent("Character Detail: " + (position+1) + " - " + queryManager.getCharacterDetail(position+1).name, "Get_Character_Detail")

                //Activating Loading Arrow
                val imageView = findViewById<ImageView>(R.id.charactersRVLoading)
                imageView.visibility = View.VISIBLE
                loadingItem[3] = true

                currentIdSearch = position + 1
                currentTypeSearch = 4
                queryDetailed()
            }
        })

        adapterCharacters.setOnBottomReachedListener(object:
            CardAdapterCharacters.OnBottomReachedListener {
            override fun onBottomReached(position: Int) {
                //Activating Loading Arrow
                val imageView = findViewById<ImageView>(R.id.charactersRVLoading)
                imageView.visibility = View.VISIBLE
                loadingItem[0] = true

                FirebaseManager.logEvent("Character Page: $characterPagesLoaded", "Get_Character_Page")
                queryCharacterPage()
            }
        })

        recycler1.addOnScrollListener(object:
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    //Activating Loading Arrow
                    val imageView = findViewById<ImageView>(R.id.charactersRVLoading)
                    imageView.visibility = View.VISIBLE
                    loadingItem[0] = true

                    FirebaseManager.logEvent("Character Page: $characterPagesLoaded", "Get_Character_Page")
                    queryCharacterPage()
                }
            }
        })

        //LocationRV
        val recycler2: RecyclerView = findViewById(R.id.locationsRV)
        recycler2.setHasFixedSize(true)
        val layoutManagerLocationRV: RecyclerView.LayoutManager = GridLayoutManager(this, 2)
        adapterLocations = CardAdapterLocations(cardListLocations)
        recycler2.layoutManager = layoutManagerLocationRV
        recycler2.adapter = adapterLocations

        adapterLocations.setOnItemClickListener(object:
            CardAdapterLocations.OnItemClickListener {
            override fun onItemClick(position: Int) {
                FirebaseManager.logEvent("Location Detail: " + (position+1) + " - " + queryManager.getLocationDetail(position+1).name, "Get_Location_Detail")

                //Activating Loading Arrow
                val imageView = findViewById<ImageView>(R.id.locationsRVLoading)
                imageView.visibility = View.VISIBLE
                loadingItem[4] = true

                currentIdSearch = position + 1
                currentTypeSearch = 5
                queryDetailed()
            }
        })

        adapterLocations.setOnBottomReachedListener(object:
            CardAdapterLocations.OnBottomReachedListener {
            override fun onBottomReached(position: Int) {
                //Activating Loading Arrow
                val imageView = findViewById<ImageView>(R.id.locationsRVLoading)
                imageView.visibility = View.VISIBLE
                loadingItem[1] = true

                FirebaseManager.logEvent("Location Page: $locationPagesLoaded", "Get_Location_Page")
                queryLocationPage()
            }
        })

        recycler2.addOnScrollListener(object:
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    //Activating Loading Arrow
                    val imageView = findViewById<ImageView>(R.id.locationsRVLoading)
                    imageView.visibility = View.VISIBLE
                    loadingItem[1] = true

                    FirebaseManager.logEvent("Location Page: $locationPagesLoaded", "Get_Location_Page")
                    queryLocationPage()
                }
            }
        })

        //EpisodeRV
        val recycler3: RecyclerView = findViewById(R.id.episodesRV)
        recycler3.setHasFixedSize(true)
        val layoutManagerEpisodeRV: RecyclerView.LayoutManager = GridLayoutManager(this, 2)
        adapterEpisodes = CardAdapterEpisodes(cardListEpisodes)
        recycler3.layoutManager = layoutManagerEpisodeRV
        recycler3.adapter = adapterEpisodes

        adapterEpisodes.setOnItemClickListener(object:
            CardAdapterEpisodes.OnItemClickListener {
            override fun onItemClick(position: Int) {
                FirebaseManager.logEvent("Episode Detail: " + (position+1) + " - " + queryManager.getEpisodeDetail(position+1).name, "Get_Episode_Detail")

                //Activating Loading Arrow
                val imageView = findViewById<ImageView>(R.id.episodesRVLoading)
                imageView.visibility = View.VISIBLE
                loadingItem[5] = true

                currentIdSearch = position + 1
                currentTypeSearch = 6
                queryDetailed()
            }
        })

        adapterEpisodes.setOnBottomReachedListener(object:
            CardAdapterEpisodes.OnBottomReachedListener {
            override fun onBottomReached(position: Int) {
                //Activating Loading Arrow
                val imageView = findViewById<ImageView>(R.id.episodesRVLoading)
                imageView.visibility = View.VISIBLE
                loadingItem[2] = true

                FirebaseManager.logEvent("Episode Page: $episodePagesLoaded", "Get_Episode_Page")
                queryEpisodePage()
            }
        })

        recycler3.addOnScrollListener(object:
            RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    //Activating Loading Arrow
                    val imageView = findViewById<ImageView>(R.id.episodesRVLoading)
                    imageView.visibility = View.VISIBLE
                    loadingItem[2] = true

                    FirebaseManager.logEvent("Episode Page: $episodePagesLoaded", "Get_Episode_Page")
                    queryEpisodePage()
                }
            }
        })
    }

    /**
     * Running the async task to get a character page
     */
    fun queryCharacterPage() {
        if (!characterPageThread1.isAlive) {
            characterPageThread1 = Thread(AsyncCharacterPage())
            characterPageThread1.start()
        } else if (!characterPageThread2.isAlive) {
            characterPageThread2 = Thread(AsyncCharacterPage())
            characterPageThread2.start()
        } else if (!characterPageThread3.isAlive) {
            characterPageThread3 = Thread(AsyncCharacterPage())
            characterPageThread3.start()
        }
    }

    /**
     * Running the async task to get a location page
     */
    fun queryLocationPage() {
        if (!locationPageThread1.isAlive) {
            locationPageThread1 = Thread(AsyncLocationPage())
            locationPageThread1.start()
        } else if (!locationPageThread2.isAlive) {
            locationPageThread2 = Thread(AsyncLocationPage())
            locationPageThread2.start()
        } else if (!locationPageThread3.isAlive) {
            locationPageThread3 = Thread(AsyncLocationPage())
            locationPageThread3.start()
        }
    }

    /**
     * Running the async task to get a episode page
     */
    fun queryEpisodePage() {
        if (!episodePageThread1.isAlive) {
            episodePageThread1 = Thread(AsyncEpisodePage())
            episodePageThread1.start()
        } else if (!episodePageThread2.isAlive) {
            episodePageThread2 = Thread(AsyncEpisodePage())
            episodePageThread2.start()
        } else if (!episodePageThread3.isAlive) {
            episodePageThread3 = Thread(AsyncEpisodePage())
            episodePageThread3.start()
        }
    }

    /**
     * Running the async task to get a episode page
     */
    fun queryDetailed() {
        if (!queryDetailedThread.isAlive) {
            queryDetailedThread = Thread(AsyncDetailedQueryManager())
            queryDetailedThread.start()
        }
    }

    /**
     * Running the async task to get images
     */
    fun queryAvatar() {
        if (!avatarThread1.isAlive) {
            avatarThread1 = Thread(AsyncBitmapQueryManager())
            avatarThread1.start()
        } else if (!avatarThread2.isAlive) {
            avatarThread2 = Thread(AsyncBitmapQueryManager())
            avatarThread2.start()
        } else if (!avatarThread3.isAlive) {
            avatarThread3 = Thread(AsyncBitmapQueryManager())
            avatarThread3.start()
        } else if (!avatarThread4.isAlive) {
            avatarThread4 = Thread(AsyncBitmapQueryManager())
            avatarThread4.start()
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
     * Recycler view filler for characters
     */
    fun loadLocationPage(page: Location) {
        val size: Int = cardListLocations.size

        for (locationDetail in page.results!!) {
            val cardLocation = locationDetail?.let { CardItemLocations(it) }
            cardListLocations.add(cardLocation!!)
        }

        adapterLocations.notifyItemRangeInserted(size, 20)
    }

    /**
     * Recycler view filler for characters
     */
    fun loadEpisodePage(page: Episode) {
        val size: Int = cardListEpisodes.size

        for (episodeDetail in page.results!!) {
            val cardEpisode = episodeDetail?.let { CardItemEpisodes(it) }
            cardListEpisodes.add(cardEpisode!!)
        }

        adapterEpisodes.notifyItemRangeInserted(size, 20)
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
        intent.putExtra("currentIdSearch", currentIdSearch)
        startActivity(intent)
    }

    /**
     * Recycler view filler for location details
     */
    fun loadLocationDetail() {
        val intent = Intent(this, LocationActivity::class.java)
        intent.putExtra("currentIdSearch", currentIdSearch)
        startActivity(intent)
    }

    /**
     * Recycler view filler for episode details
     */
    fun loadEpisodeDetail() {
        val intent = Intent(this, EpisodeActivity::class.java)
        intent.putExtra("currentIdSearch", currentIdSearch)
        startActivity(intent)
    }

    //Author Menu
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
        val deviceSize = DeviceUtils.calculateDeviceDimensions(this)
        deviceWidth = deviceSize[0]
        deviceHeight = deviceSize[1]
    }

    //Footer Menu
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
        val characterRW : RecyclerView = findViewById(R.id.charactersRV)
        val locationRW : RecyclerView = findViewById(R.id.locationsRV)
        val episodeRW : RecyclerView = findViewById(R.id.episodesRV)

        when(view.id) {
            R.id.characterButton -> {
                //Recycler View Update
                if (locationIVColor.isVisible) {
                    setAnimation(locationRW, "translationX", 300, false, 0f, -1f * deviceWidth)
                    setAnimation(characterRW, "translationX", 300, false, 1f * deviceWidth, 0f)
                    setAnimation(episodeRW, "translationX", 300, false, 2f * deviceWidth, 1f * deviceWidth)
                } else if (episodeIVColor.isVisible) {
                    setAnimation(episodeRW, "translationX", 300, false, 0f, 1f * deviceWidth)
                    setAnimation(characterRW, "translationX", 300, false, -1f * deviceWidth, 0f)
                    setAnimation(locationRW, "translationX", 300, false, -2f * deviceWidth, -1f * deviceWidth)
                }

                //Button Color Update
                locationIVColor.visibility = View.INVISIBLE
                episodeIVColor.visibility = View.INVISIBLE
                locationIVBN.visibility = View.VISIBLE
                episodeIVBN.visibility = View.VISIBLE

                characterIVColor.visibility = View.VISIBLE
                characterIVBN.visibility = View.INVISIBLE

                //Firebase Data Collection
                FirebaseManager.logEvent("Footer Button: Character", "Footer_Button")
            }
            R.id.locationButton -> {
                //Recycler View Update
                if (characterIVColor.isVisible) {
                    setAnimation(characterRW, "translationX", 300, false, 0f, 1f * deviceWidth)
                    setAnimation(locationRW, "translationX", 300, false, -1f * deviceWidth, 0f)
                    setAnimation(episodeRW, "translationX", 300, false, 1f * deviceWidth, 2f * deviceWidth)
                } else if (episodeIVColor.isVisible) {
                    setAnimation(episodeRW, "translationX", 600, false, 0f, 2f * deviceWidth)
                    setAnimation(locationRW, "translationX", 600, false, -2f * deviceWidth, 0f)
                    setAnimation(characterRW, "translationX", 600, false, -1f * deviceWidth, 1f * deviceWidth)
                }

                //Button Color
                characterIVColor.visibility = View.INVISIBLE
                episodeIVColor.visibility = View.INVISIBLE
                characterIVBN.visibility = View.VISIBLE
                episodeIVBN.visibility = View.VISIBLE

                locationIVColor.visibility = View.VISIBLE
                locationIVBN.visibility = View.INVISIBLE

                //Firebase Data Collection
                FirebaseManager.logEvent("Footer Button: Location", "Footer_Button")
            }
            R.id.episodeButton -> {
                //Recycler View Update
                if (characterIVColor.isVisible) {
                    setAnimation(characterRW, "translationX", 300, false, 0f, -1f * deviceWidth)
                    setAnimation(episodeRW, "translationX", 300, false, 1f * deviceWidth, 0f)
                    setAnimation(locationRW, "translationX", 300, false, -1f * deviceWidth, -2f * deviceWidth)
                } else if (locationIVColor.isVisible) {
                    setAnimation(locationRW, "translationX", 600, false, 0f, -2f * deviceWidth)
                    setAnimation(episodeRW, "translationX", 600, false, 2f * deviceWidth, 0f)
                    setAnimation(characterRW, "translationX", 600, false, 1f * deviceWidth, -1f * deviceWidth)
                }

                //Button Color Update
                characterIVColor.visibility = View.INVISIBLE
                locationIVColor.visibility = View.INVISIBLE
                characterIVBN.visibility = View.VISIBLE
                locationIVBN.visibility = View.VISIBLE

                episodeIVColor.visibility = View.VISIBLE
                episodeIVBN.visibility = View.INVISIBLE

                //Firebase Data Collection
                FirebaseManager.logEvent("Footer Button: Episode", "Footer_Button")
            }
        }
    }

    /**
     * Actions made when app start
     */
    override fun onStart() {
        AdsManager.attachBannerAd(adView)
        super.onStart()
    }
}