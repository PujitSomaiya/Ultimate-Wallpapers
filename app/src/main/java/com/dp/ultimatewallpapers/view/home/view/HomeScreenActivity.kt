package com.dp.ultimatewallpapers.view.home.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dp.ultimatewallpapers.R
import com.dp.ultimatewallpapers.api.APIClient
import com.dp.ultimatewallpapers.api.APIInterface
import com.dp.ultimatewallpapers.api.response.HitsItem
import com.dp.ultimatewallpapers.api.response.PhotoResponse
import com.dp.ultimatewallpapers.interfaces.AllClickListeners
import com.dp.ultimatewallpapers.utils.EndlessRecyclerViewScrollListener
import com.dp.ultimatewallpapers.utils.ProgressDialog
import com.dp.ultimatewallpapers.utils.closeKeyboard
import com.dp.ultimatewallpapers.utils.toast
import com.dp.ultimatewallpapers.view.fullscreen.view.FullScreenActivity
import com.dp.ultimatewallpapers.view.home.adapter.ImageAdapter
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class HomeScreenActivity : AppCompatActivity(), AllClickListeners.OnImageClick {

    private lateinit var apiInterface: APIInterface
    private lateinit var photoResponse: PhotoResponse
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var rvHome: RecyclerView
    private lateinit var scrollListener: EndlessRecyclerViewScrollListener
    private lateinit var gridLayoutManager: GridLayoutManager
    private var currentOffset = 2
    private var searchField = ""
    private var isLoadMore = false
    private var imageFilePath: String = ""
    private var imageType: String = "all"
    private var orientation: String = "all"
    private var isFromEditText = false
    private lateinit var progressbar: ProgressBar
    private lateinit var edSearchBox: EditText
    private lateinit var tvNoItem: TextView
    private lateinit var imgClear: ImageView
    private lateinit var imgFilter: ImageView
    lateinit var mAdView : AdView
    private var mInterstitialAd: InterstitialAd? = null
    private final var TAG = "MainActivity"
    lateinit var adRequest: AdRequest
    private var isForDownload:Boolean = true
    private lateinit var  item: HitsItem
    private var mAdIsLoading: Boolean = false

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

        callApi(searchField, currentOffset, isLoadMore,imageType,orientation)

        MobileAds.initialize(this) {}

        adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
        /*loadInterAdd()*/
        loadAd()
        /*adView.adUnitId = "ca-app-pub-6724890135457979/9395979140"*/
    }

    private fun initViews() {
        apiInterface = APIClient.getClient().create(APIInterface::class.java)
        imgFilter = findViewById(R.id.imgFilter)
        tvNoItem = findViewById(R.id.tvNoItem)
        progressbar = findViewById(R.id.progressbar)
        rvHome = findViewById(R.id.rvHome)
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
                callApi(searchField, currentOffset, isLoadMore,imageType,orientation)
            }

        }
        rvHome.addOnScrollListener(scrollListener)
        rvHome.adapter = imageAdapter
    }

    @SuppressLint("NewApi")
    fun statusBarColor(id: Int) {
        window.decorView.systemUiVisibility = 0
        window.statusBarColor = ContextCompat.getColor(this, id)
    }

    private fun callApi(key: String, page: Int, loadMore: Boolean,imageType:String,orientation:String){

        if (!loadMore && !isFromEditText)
            ProgressDialog.showProgress(this@HomeScreenActivity)
        else
            progressbar.visibility = View.VISIBLE

        val call: Call<PhotoResponse> = apiInterface.getPhotos(
            "22823628-343c53ced661a5f7a387a4eb3",
            key,
            imageType,
            page,
            orientation,
            true
        )

        call.enqueue(object : Callback<PhotoResponse?> {
            override fun onResponse(
                call: Call<PhotoResponse?>?,
                response: Response<PhotoResponse?>
            ) {
                ProgressDialog.dismissProgress()
                progressbar.visibility = View.GONE


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
                        tvNoItem.visibility = View.VISIBLE
                    } else {
                        rvHome.visibility = View.VISIBLE
                        tvNoItem.visibility = View.GONE
                    }
                } else {
                    rvHome.visibility = View.VISIBLE
                    tvNoItem.visibility = View.GONE
                }

                println(response.body())
            }

            override fun onFailure(call: Call<PhotoResponse?>, t: Throwable?) {
                call.cancel()
                ProgressDialog.dismissProgress()
                progressbar.visibility = View.GONE
                println(t?.message)
            }
        })
    }


    override fun onImageClick(
        position: Int,
        item: HitsItem,
        isForDownload: Boolean
    ) {
        this.item= item
        if (isForDownload){
            this.isForDownload =true
            showInterstitial()
        }else{
            this.isForDownload = false
            showInterstitial()
        }
        /*dialogForDownload(position, item)*/
    }

    private fun dialogForDownload(position: Int, item: HitsItem) {
        if (!isFinishing) {
            AlertDialog.Builder(this@HomeScreenActivity)
                .setCancelable(false)
                .setTitle("Info!!")
                .setMessage("Do you want to download??")
                .setCancelable(false)
                .setPositiveButton("Download"
                ) { dialog, which ->
                    isForDownload =true
                    showInterstitial()
                    /*if (!mAdIsLoading && mInterstitialAd == null) {
                        mAdIsLoading = true
                        loadAd()
                    }*/

                }
                .setNegativeButton("No"
                ) { dialog, which -> }
                .setNeutralButton("View"
                ) { dialog, which ->
                    isForDownload = false
                    showInterstitial()
                    /*if (!mAdIsLoading && mInterstitialAd == null) {
                        mAdIsLoading = true
                        loadAd()
                    }*/

                }.create().show()
        }
    }

    private fun persmissionCheckAndDownload(item: HitsItem) {
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

//        New-way to store image in gallery (not secured) ******
        val storagePath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString() + "/WallpaperHub"
        val NewStorageDir = File(storagePath)
        if (!NewStorageDir.exists()) {
            val wallpaperDirectory = File(storagePath)
            wallpaperDirectory.mkdirs()
        }
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            NewStorageDir /* directory */
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
            Toast.makeText(this, "Ad wasn't loaded.", Toast.LENGTH_SHORT).show()
            afterAdAndRedirect()
        }
    }

    private fun loadAd() {
        InterstitialAd.load(
            this, "ca-app-pub-3940256099942544/1033173712", adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError?.message)
                    mInterstitialAd = null
                    mAdIsLoading = false
                    val error = "domain: ${adError.domain}, code: ${adError.code}, " +
                            "message: ${adError.message}"
                    Toast.makeText(
                        this@HomeScreenActivity,
                        "onAdFailedToLoad() with error $error",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    mInterstitialAd = interstitialAd
                    mAdIsLoading = false
                    Toast.makeText(this@HomeScreenActivity, "onAdLoaded()", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun afterAdAndRedirect() {
        if (isForDownload) {
            toast("Download starting...")
            persmissionCheckAndDownload(item)

        } else {
            val intent = Intent(this@HomeScreenActivity, FullScreenActivity::class.java)
            intent.putExtra(
                "item",
                Gson().toJson(item)
            )
            startActivity(intent)
        }
    }
}