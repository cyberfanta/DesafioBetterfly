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
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.cyberfanta.desafiobetterfly.R
import com.cyberfanta.desafiobetterfly.enumerator.AppState
import com.cyberfanta.desafiobetterfly.exceptions.ConnectionException
import com.cyberfanta.desafiobetterfly.models.character.CharacterDetail
import com.cyberfanta.desafiobetterfly.presenters.AdsManager
import com.cyberfanta.desafiobetterfly.presenters.FirebaseManager
import com.cyberfanta.desafiobetterfly.presenters.QueryManager
import com.cyberfanta.desafiobetterfly.presenters.RateAppManager
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.LinkedHashMap

class CharacterActivity : AppCompatActivity() {
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
    private var queryDetailedThreadCharacter = Thread(AsyncDetailedQueryManager())
    private var avatarThreadCharacter = Thread(AsyncBitmapQueryManager())
    private val detailList = LinkedHashMap<Int, String>(0)
    private lateinit var query: CharacterDetail

    //UI
    private val map = mapOf<Int, TextView>()
    val sortedMap = map.toSortedMap()

    //Ad View Variable
    private lateinit var adView : FrameLayout

    /**
     * The initial point of this view
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character)

        getDeviceDimensions()
        loadObject()

        //Starting all loading circle animations
        val imageView : ImageView = findViewById(R.id.loadingCha)
        setAnimation(imageView, "rotation", 1000, true, 360f, 0f)

        //Binding all onClick functions
        bindingAllOnClickFunctions()

        //Load ads manager
        adView = findViewById(R.id.adViewCharacter)
        AdsManager.attachBannerAd (adView)

        //Load firebase manager
        FirebaseManager.logEvent("$TAG: Opened", "Activity_Opened")
    }

    /**
     * Load all onClick functions for all views on this Activity
     */
    private fun bindingAllOnClickFunctions (){
        //Binding onClick function for about menu option
        val author : ConstraintLayout = findViewById(R.id.author)
        author.setOnClickListener {
            authorSelected(author)
            authorOpened = false
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
            FirebaseManager.logEvent("Open website: API", "Open_Api")
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

        FirebaseManager.logEvent("$TAG: Closed", "Activity_Closed")
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
                RateAppManager.requestReview(applicationContext)
                return true
            }
            R.id.item_about -> {
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

                FirebaseManager.logEvent("Menu: Author", "Open_Menu")
                return true
            }
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
            query = queryManager.getCharacterDetail(currentIdSearch)
        } catch (e: Exception) {
            return
        }

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
        for (episode in query.episode!!) {
            val split = episode?.split("https://rickandmortyapi.com/api/episode/")
            text += split?.get(1) + "\n"
            detailList[split?.get(1)?.toInt()!!] = ""
        }
        text = text.substring(0, text.length - 1)
        textView.text = text
        textView = findViewById(R.id.characterEpisodeLabel)
        textView.text = getString(R.string.episodesLabel)

        //Activating Loading Arrow
        val imageView = findViewById<ImageView>(R.id.loadingCha)
        imageView.visibility = View.VISIBLE

        if (!queryDetailedThreadCharacter.isAlive) {
            queryDetailedThreadCharacter = Thread(AsyncDetailedQueryManager())
            queryDetailedThreadCharacter.start()
        }

        if (!avatarThreadCharacter.isAlive) {
            avatarThreadCharacter = Thread(AsyncBitmapQueryManager())
            avatarThreadCharacter.start()
        }
    }

    /**
     * Asynchronous Load Detailed Item for QueryManager Class
     */
    private inner class AsyncDetailedQueryManager : Runnable {
        override fun run() {
            try {
                val message1 = handler.obtainMessage()
                if (query.origin2?.url != "") {
                    val number: Int = query.origin2?.url?.split("https://rickandmortyapi.com/api/location/")?.get(1)?.toInt()!!
                    val name: String = queryManager.getLocationDetail(number).name!!
                    FirebaseManager.logEvent("Location Detail: $number - $name", "Get_Location_Detail")
                }
                if (query.location2?.url != "") {
                    val number: Int = query.location2?.url?.split("https://rickandmortyapi.com/api/location/")?.get(1)?.toInt()!!
                    val name: String = queryManager.getLocationDetail(number).name!!
                    FirebaseManager.logEvent("Location Detail: $number - $name", "Get_Location_Detail")
                }
                message1.obj = AppState.Location_Detail_Loaded
                handler.sendMessageAtFrontOfQueue(message1)

                for (detail in detailList) {
                    val message2 = handler.obtainMessage()
                    val number: Int = detail.key
                    val name: String = queryManager.getEpisodeDetail(number).name!!
                    detailList[number] = name
                    message2.obj = AppState.Episode_Detail_Loaded
                    message2.arg1 = number
                    FirebaseManager.logEvent("Episode Detail: $number - $name", "Get_Episode_Detail")
                    handler.sendMessageAtFrontOfQueue(message2)
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
                    message.obj.equals(AppState.Episode_Detail_Loaded) -> {
                        var textView: TextView = findViewById(R.id.characterEpisodeData)
                        textView.visibility = View.GONE

                        val obj = message.arg1
                        textView = TextView(this@CharacterActivity, null, R.style.characterDetailBottomList)
                        val text = obj.toString() + ": " + queryManager.getEpisodeDetail(obj).name!!
                        textView.text = text
                        textView.layoutParams = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                        textView.setTextColor(resources.getColor(R.color.soft_black_37))
                        textView.setOnClickListener {
                            FirebaseManager.logEvent("Episode Detail: $obj - " + queryManager.getEpisodeDetail(obj).name, "Get_Episode_Detail")

                            val intent = Intent(this@CharacterActivity, EpisodeActivity::class.java)
                            intent.putExtra("deviceWidth", deviceWidth.toString())
                            intent.putExtra("deviceHeight", deviceHeight.toString())
                            intent.putExtra("currentIdSearch", obj.toString())
                            startActivity(intent)
                        }
                        sortedMap[obj] = textView

                        val linearLayout: LinearLayout = findViewById(R.id.characterEpisodeDataList)
                        linearLayout.removeAllViewsInLayout()

                        for (textViews in sortedMap)
                            linearLayout.addView(textViews.value)

                        System.gc()
                    }
                    message.obj.equals(AppState.Location_Detail_Loaded) -> {
                        if (query.origin2?.url != "") {
                            val textView: TextView = findViewById(R.id.characterOriginData)
                            val number: Int =
                                query.origin2?.url?.split("https://rickandmortyapi.com/api/location/")
                                    ?.get(1)?.toInt()!!
                            val text =
                                number.toString() + ": " + queryManager.getLocationDetail(number).name!!
                            textView.text = text
                            textView.setOnClickListener {
                                FirebaseManager.logEvent("Location Detail: $number - " + queryManager.getLocationDetail(number).name, "Get_Location_Detail")

                                val intent =
                                    Intent(this@CharacterActivity, LocationActivity::class.java)
                                intent.putExtra("deviceWidth", deviceWidth.toString())
                                intent.putExtra("deviceHeight", deviceHeight.toString())
                                intent.putExtra("currentIdSearch", number.toString())
                                startActivity(intent)
                            }
                        }
                        if (query.location2?.url != "") {
                            val textView: TextView = findViewById(R.id.characterLocationData)
                            val number2: Int =
                                query.location2?.url?.split("https://rickandmortyapi.com/api/location/")
                                    ?.get(1)?.toInt()!!
                            val text = "$number2: " + queryManager.getLocationDetail(number2).name!!
                            textView.text = text
                            textView.setOnClickListener {
                                FirebaseManager.logEvent("Location Detail: $number2 - " + queryManager.getLocationDetail(number2).name, "Get_Location_Detail")

                                val intent =
                                    Intent(this@CharacterActivity, LocationActivity::class.java)
                                intent.putExtra("deviceWidth", deviceWidth.toString())
                                intent.putExtra("deviceHeight", deviceHeight.toString())
                                intent.putExtra("currentIdSearch", number2.toString())
                                startActivity(intent)
                            }
                        }
                    }
                    message.obj.equals(AppState.Character_Avatar_Loaded) -> {
                        //Deactivating Loading Arrow
                        var imageView = findViewById<ImageView>(R.id.loadingCha)
                        imageView.visibility = View.INVISIBLE

                        imageView = findViewById(R.id.characterImageView)

                        try {
                            imageView.setImageBitmap(query.id?.let { queryManager.getCharacterAvatar(it).bitmap })
                        } catch (e: Exception) {
                            return
                        }
                    }
                }
            }
        }
    }

    /**
     * Asynchronous Load bitmaps for QueryManager Class
     */
    private inner class AsyncBitmapQueryManager : Runnable {
        override fun run() {
            val message = handler.obtainMessage()
            try {
                @Suppress("SpellCheckingInspection")
                FirebaseManager.logEvent("Search Avatar: $currentIdSearch - " + queryManager.getCharacterDetail(currentIdSearch).name, "Get_Avatar")
                queryManager.getCharacterAvatar(currentIdSearch)
                message.obj = AppState.Character_Avatar_Loaded
            } catch (e: ConnectionException) {
                message.obj = AppState.Load_Failed
            }
            handler.sendMessageAtFrontOfQueue(message)
        }
    }

    /**
     * Actions made when activity start
     */
    override fun onStart() {
        AdsManager.attachBannerAd(adView)
        super.onStart()
    }
}