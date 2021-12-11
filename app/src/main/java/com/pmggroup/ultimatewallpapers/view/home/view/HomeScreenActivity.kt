package com.pmggroup.ultimatewallpapers.view.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Editable
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.pmggroup.ultimatewallpapers.R
import com.pmggroup.ultimatewallpapers.api.APIClient
import com.pmggroup.ultimatewallpapers.api.APIInterface
import com.pmggroup.ultimatewallpapers.api.response.HitsItem
import com.pmggroup.ultimatewallpapers.api.response.PhotoResponse
import com.pmggroup.ultimatewallpapers.api.response.SuggestionItem
import com.pmggroup.ultimatewallpapers.interfaces.AllClickListeners
import com.pmggroup.ultimatewallpapers.utils.*
import com.pmggroup.ultimatewallpapers.view.fullscreen.view.FullScreenActivity
import com.pmggroup.ultimatewallpapers.view.home.adapter.ImageAdapter
import com.pmggroup.ultimatewallpapers.view.home.adapter.SuggestionsAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class HomeScreenActivity : AppCompatActivity(), AllClickListeners.OnImageClick,
    AllClickListeners.OnSuggestionClick, AllClickListeners.SetOnBottomDialogButtonClick {

    private lateinit var apiInterface: APIInterface
    private lateinit var photoResponse: PhotoResponse
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var suggestionsAdapter: SuggestionsAdapter
    private lateinit var rvHome: RecyclerView
    private lateinit var rvSuggetions: RecyclerView
    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private lateinit var gridLayoutManager: GridLayoutManager
    private var currentOffset = 2
    private var addCount = 0
    private var searchField = ""
    private var category = ""
    private var isLoadMore = false
    private var imageFilePath: String = ""
    private var imageType: String = "all"
    private var orientation: String = "all"
    private var isFromEditText = false
    private lateinit var progressbar: ProgressBar
    private lateinit var edSearchBox: EditText
    private lateinit var tvNoItem: TextView
    private lateinit var imgClear: ImageView
    private lateinit var imgMenu: ImageView
    private lateinit var imgFilter: ImageView
    private lateinit var imgSearch: ImageView
    private lateinit var imgSearchEdittext: ImageView
    private lateinit var llEdittext: LinearLayout
    lateinit var mAdView: AdView
    private var mInterstitialAd: InterstitialAd? = null
    private var TAG = "MainActivity"
    lateinit var adRequest: AdRequest
    private var isForDownload: Boolean = true
    private var isEdittextVisible: Boolean = false
    private lateinit var item: HitsItem
    private var mAdIsLoading: Boolean = false
    private lateinit var dialog: BottomDialog
    private var pixybyApiKey="23046411-9a876cbf36c9dce4bd1c44893"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)
        statusBarColor(R.color.color_main_background)

        initControls()
        closeKeyboard(this@HomeScreenActivity)
        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

    }

    private fun initControls() {
        initViews()
        initRecyclerView()
        callApi(searchField, currentOffset, isLoadMore, imageType, orientation, category)
        MobileAds.initialize(this) {}
        adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        /*loadInterAdd()*/
        loadAd()
        /*adView.adUnitId = "ca-app-pub-6724890135457979/9395979140"*/
        onClicks()
    }

    private fun onClicks() {

        edSearchBox.doAfterTextChanged { text: Editable? ->
            if (text.toString().trim().length > 1) {
                searchField = text.toString().trim()
                currentOffset = 1
                isLoadMore = false
                isFromEditText = true
                callApi(searchField, currentOffset, isLoadMore, imageType, orientation, category)
            } else {
                searchField = ""
                currentOffset = 1
                isLoadMore = false
                isFromEditText = true
                callApi(searchField, currentOffset, isLoadMore, imageType, orientation, category)
            }
        }

        imgSearch.setOnClickListener {
            edSearchBox.text.clear()
            isEdittextVisible = if (isEdittextVisible) {
                imgSearch.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@HomeScreenActivity,
                        R.drawable.ic_search
                    )
                )
                showHide(this@HomeScreenActivity, llEdittext, false)
                searchField = ""
                currentOffset = 1
                isLoadMore = false
                isFromEditText = true
                callApi(searchField, currentOffset, isLoadMore, imageType, orientation, category)
                closeKeyboard(this@HomeScreenActivity)
                false
            } else {
                imgSearch.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@HomeScreenActivity,
                        R.drawable.ic_cancel
                    )
                )
                showHide(this@HomeScreenActivity, llEdittext, true)
                edSearchBox.requestFocus()
                true
            }


        }

        imgFilter.setOnClickListener {
            if (!dialog.isHidden) {
                dialog.show(this.supportFragmentManager, "dialogFilter")
                dialog.isCancelable = false
            } else {
                dialog.dismiss()
            }
        }

        edSearchBox.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            actionId == EditorInfo.IME_ACTION_SEARCH
        })

        imgMenu.setOnClickListener {
            display()
        }

    }

    fun display(): AboutUsDialog? {
        val exampleDialog = AboutUsDialog(
            supportFragmentManager
        )
        exampleDialog.show(supportFragmentManager, TAG)
        return exampleDialog
    }

    private fun initViews() {
        apiInterface = APIClient.getClient().create(APIInterface::class.java)
        dialog = BottomDialog(this)
        imgSearch = findViewById(R.id.imgSearch)
        imgMenu = findViewById(R.id.imgMenu)
        edSearchBox = findViewById(R.id.edSearchBox)
        imgSearchEdittext = findViewById(R.id.imgSearchEdittext)
        llEdittext = findViewById(R.id.llEdittext)
        imgFilter = findViewById(R.id.imgFilter)
        tvNoItem = findViewById(R.id.tvNoItem)
        progressbar = findViewById(R.id.progressbar)
        rvHome = findViewById(R.id.rvHome)
        rvSuggetions = findViewById(R.id.rvSuggetions)
        mAdView = findViewById(R.id.adView)
    }

    private fun initRecyclerView() {
        imageAdapter = ImageAdapter(this, this)
        gridLayoutManager = GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false)
        rvHome.layoutManager = gridLayoutManager
        scrollListener = object : EndlessRecyclerViewScrollListener(gridLayoutManager) {
            override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
                println("Load more")
                isLoadMore = true
                currentOffset += 1
                callApi(searchField, currentOffset, isLoadMore, imageType, orientation, category)
            }

        }
        rvHome.addOnScrollListener(scrollListener)
        rvHome.adapter = imageAdapter

        suggestionsAdapter = SuggestionsAdapter(this, this)
        rvSuggetions.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        rvSuggetions.adapter = suggestionsAdapter

        val suggestionString: ArrayList<SuggestionItem> = arrayListOf()
        initCategaury(suggestionString)

        suggestionsAdapter.refresh(suggestionString)
        category = "Backgrounds"

    }

    private fun initCategaury(suggestionString: ArrayList<SuggestionItem>) {

        val suggestionItem = SuggestionItem()
        suggestionItem.suggestion = "Backgrounds"
        suggestionItem.isSelected = true
        suggestionString.add(suggestionItem)

        val suggestionItem2 = SuggestionItem()
        suggestionItem2.suggestion = "Fashion"
        suggestionItem2.isSelected = false
        suggestionString.add(suggestionItem2)

        val suggestionItem3 = SuggestionItem()
        suggestionItem3.suggestion = "Nature"
        suggestionItem3.isSelected = false
        suggestionString.add(suggestionItem3)

        val suggestionItem4 = SuggestionItem()
        suggestionItem4.suggestion = "Science"
        suggestionItem4.isSelected = false
        suggestionString.add(suggestionItem4)

        val suggestionItem5 = SuggestionItem()
        suggestionItem5.suggestion = "Education"
        suggestionItem5.isSelected = false
        suggestionString.add(suggestionItem5)

        val suggestionItem6 = SuggestionItem()
        suggestionItem6.suggestion = "Feelings"
        suggestionItem6.isSelected = false
        suggestionString.add(suggestionItem6)

        val suggestionItem7 = SuggestionItem()
        suggestionItem7.suggestion = "Health"
        suggestionItem7.isSelected = false
        suggestionString.add(suggestionItem7)

        val suggestionItem8 = SuggestionItem()
        suggestionItem8.suggestion = "People"
        suggestionItem8.isSelected = false
        suggestionString.add(suggestionItem8)

        val suggestionItem9 = SuggestionItem()
        suggestionItem9.suggestion = "Religion"
        suggestionItem9.isSelected = false
        suggestionString.add(suggestionItem9)

        val suggestionItem10 = SuggestionItem()
        suggestionItem10.suggestion = "Places"
        suggestionItem10.isSelected = false
        suggestionString.add(suggestionItem10)


        val suggestionItem11 = SuggestionItem()
        suggestionItem11.suggestion = "Animals"
        suggestionItem11.isSelected = false
        suggestionString.add(suggestionItem11)

        val suggestionItem12 = SuggestionItem()
        suggestionItem12.suggestion = "Industry"
        suggestionItem12.isSelected = false
        suggestionString.add(suggestionItem12)

        val suggestionItem13 = SuggestionItem()
        suggestionItem13.suggestion = "Computer"
        suggestionItem13.isSelected = false
        suggestionString.add(suggestionItem13)

        val suggestionItem14 = SuggestionItem()
        suggestionItem14.suggestion = "Food"
        suggestionItem14.isSelected = false
        suggestionString.add(suggestionItem14)

        val suggestionItem15 = SuggestionItem()
        suggestionItem15.suggestion = "Sports"
        suggestionItem15.isSelected = false
        suggestionString.add(suggestionItem15)

        val suggestionItem16 = SuggestionItem()
        suggestionItem16.suggestion = "Transportation"
        suggestionItem16.isSelected = false
        suggestionString.add(suggestionItem16)

        val suggestionItem17 = SuggestionItem()
        suggestionItem17.suggestion = "Travel"
        suggestionItem17.isSelected = false
        suggestionString.add(suggestionItem17)

        val suggestionItem18 = SuggestionItem()
        suggestionItem18.suggestion = "Buildings"
        suggestionItem18.isSelected = false
        suggestionString.add(suggestionItem18)

        val suggestionItem19 = SuggestionItem()
        suggestionItem19.suggestion = "Business"
        suggestionItem19.isSelected = false
        suggestionString.add(suggestionItem19)

        val suggestionItem20 = SuggestionItem()
        suggestionItem20.suggestion = "Music"
        suggestionItem20.isSelected = false
        suggestionString.add(suggestionItem20)
    }

    @SuppressLint("NewApi")
    fun statusBarColor(id: Int) {
        window.decorView.systemUiVisibility = 0
        window.statusBarColor = ContextCompat.getColor(this, id)
    }

    private fun callApi(
        key: String,
        page: Int,
        loadMore: Boolean,
        imageType: String,
        orientation: String,
        category: String
    ) {

        if (page == 1) {
            scrollListener.resetState()
        }

        if (!loadMore && !isFromEditText)
            ProgressDialog.showProgress(this@HomeScreenActivity)
        else
            progressbar.visibility = View.VISIBLE

        val call: Call<PhotoResponse> = apiInterface.getPhotos(
            pixybyApiKey,
            key,
            imageType,
            page,
            orientation,
            true,
            true,
            category
        )

        call.enqueue(object : Callback<PhotoResponse?> {
            override fun onResponse(
                call: Call<PhotoResponse?>?,
                response: Response<PhotoResponse?>
            ) {
                ProgressDialog.dismissProgress()
                progressbar.visibility = View.GONE

                if (response.body() != null) {
                    photoResponse = PhotoResponse()
                    photoResponse = response.body() as PhotoResponse
                    if (isLoadMore) {
                        imageAdapter.add(photoResponse.hits)
                    } else {
                        imageAdapter.refresh(photoResponse.hits)
                    }

                    if (!isLoadMore) {
                        if (photoResponse.hits.size == 0) {
                            rvHome.visibility = View.GONE
                            rvSuggetions.visibility = View.GONE
                            tvNoItem.visibility = View.VISIBLE
                        } else {
                            rvHome.visibility = View.VISIBLE
                            rvSuggetions.visibility = View.VISIBLE
                            tvNoItem.visibility = View.GONE
                        }
                    } else {
                        rvHome.visibility = View.VISIBLE
                        rvSuggetions.visibility = View.VISIBLE
                        tvNoItem.visibility = View.GONE
                    }

                    println(response.body())
                } else {
                    rvHome.visibility = View.GONE
                    tvNoItem.visibility = View.VISIBLE
                }


            }

            override fun onFailure(call: Call<PhotoResponse?>, t: Throwable?) {
                call.cancel()
                ProgressDialog.dismissProgress()
                progressbar.visibility = View.GONE
                println(t?.message)
                rvHome.visibility = View.GONE
                tvNoItem.visibility = View.VISIBLE
            }
        })
    }


    override fun onImageClick(
        position: Int,
        item: HitsItem,
        isForDownload: Boolean
    ) {
        this.item = item
        addCount += 1
        if (addCount % 3 == 0) {
            if (isForDownload) {
                this.isForDownload = true
                showInterstitial()
            } else {
                this.isForDownload = false
                showInterstitial()
            }
        } else {
            if (isForDownload) {
                this.isForDownload = true
                afterAdAndRedirect()
            } else {
                this.isForDownload = false
                afterAdAndRedirect()
            }
        }
    }


    private fun permissionCheckAndDownload(item: HitsItem) {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    // check if all permissions are granted
                    if (report.areAllPermissionsGranted()) {
                        downloadFile(item.largeImageURL)
                    }

                    // check for permanent denial of any permission
                    if (report.isAnyPermissionPermanentlyDenied) {
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }


            })
            .onSameThread()
            .check()
    }

    fun downloadFile(uRl: String?) {
        val direct = createImageFile()
        if (!direct?.exists()!!) {
            direct.mkdirs()
        }

        val mgr =
            this@HomeScreenActivity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri: Uri = Uri.parse(uRl)
        val request = DownloadManager.Request(
            downloadUri
        )
        request.setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_WIFI
                    or DownloadManager.Request.NETWORK_MOBILE
        )
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setAllowedOverRoaming(true).setTitle("Image")
            .setDescription("Downloading!!!")

        mgr.enqueue(request)
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {

        val timeStamp: String = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())
        val imageFileName = "IMG_" + timeStamp + "_"

        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            this@HomeScreenActivity.filesDir /* directory */
        )
        imageFilePath = image.absolutePath
        return image
    }

    private fun showSettingsDialog() {
        val builder =
            AlertDialog.Builder(this@HomeScreenActivity)
        builder.setTitle("Need Permissions")
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.")
        builder.setPositiveButton("GOTO SETTINGS", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                dialog.cancel()
                openSettings()
            }
        })
        builder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                dialog.cancel()
            }
        })
        builder.show()
    }

    // navigating user to app settings
    private fun openSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivityForResult(intent, 101)
    }


    private fun showInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Ad was dismissed.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null
                    loadAd()
                    afterAdAndRedirect()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    Log.d(TAG, "Ad failed to show.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Ad showed fullscreen content.")
                    // Called when ad is dismissed.
                }
            }
            mInterstitialAd?.show(this)
        } else {
            loadAd()
            /*Toast.makeText(this, "Ad wasn't loaded.", Toast.LENGTH_SHORT).show()*/
            afterAdAndRedirect()
        }
    }

    private fun loadAd() {
        /*test ca-app-pub-3940256099942544/8691691433*/
        /*live ca-app-pub-6491242549381158/4486883924*/
        InterstitialAd.load(
            this, "ca-app-pub-6491242549381158/4486883924", adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.message)
                    mInterstitialAd = null
                    mAdIsLoading = false
                    val error = "domain: ${adError.domain}, code: ${adError.code}, " +
                            "message: ${adError.message}"
                    /*Toast.makeText(
                        this@HomeScreenActivity,
                        "onAdFailedToLoad() with error $error",
                        Toast.LENGTH_SHORT
                    ).show()*/
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                    mAdIsLoading = false
                    /*Toast.makeText(this@HomeScreenActivity, "onAdLoaded()", Toast.LENGTH_SHORT).show()*/
                }
            }
        )
    }

    private fun afterAdAndRedirect() {
        if (isForDownload) {
            toast("Download starting...")
            permissionCheckAndDownload(item)

        } else {
            val intent = Intent(this@HomeScreenActivity, FullScreenActivity::class.java)
            intent.putExtra(
                "item",
                Gson().toJson(item)
            )
            startActivity(intent)
        }
    }

    override fun onSuggestionClick(position: Int, text: String?) {
        searchField = text!!
        category = text
        currentOffset = 1
        isLoadMore = false
        isFromEditText = true
        callApi(searchField, currentOffset, isLoadMore, imageType, orientation, category)
    }

    override fun setFilter(number: Int, rbImageType: RadioButton?, rbOrientation: RadioButton?) {
        if (number == 1) {
            searchField = edSearchBox.text.toString().trim()
            currentOffset = 1
            isLoadMore = false
            isFromEditText = true
            imageType = "all"
            orientation = "all"
            callApi(searchField, currentOffset, isLoadMore, imageType, orientation, category)
        } else if (number == 2) {
            searchField = edSearchBox.text.toString().trim()
            currentOffset = 1
            isLoadMore = false
            isFromEditText = true
            imageType = rbImageType!!.text.trim().toString()
            orientation = rbOrientation!!.text.trim().toString()
            callApi(searchField, currentOffset, isLoadMore, imageType, orientation, category)
        }
    }
}