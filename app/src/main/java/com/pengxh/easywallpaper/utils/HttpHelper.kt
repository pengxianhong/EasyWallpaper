package com.pengxh.easywallpaper.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import com.pengxh.app.multilib.widget.EasyToast
import com.pengxh.easywallpaper.BaseApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Connection
import org.jsoup.Jsoup
import java.util.*


/**
 * @description: TODO 多协程
 * @author: Pengxh
 * @email: 290677893@qq.com
 * @date: 2020/6/14 16:26
 */
class HttpHelper {
    companion object {
        private const val Tag: String = "HttpHelper"

        private fun createConnection(url: String): Connection = Jsoup.connect(url)
            .userAgent(obtainAgent())
            .timeout(30 * 1000)
            .ignoreHttpErrors(true)

        private fun obtainAgent(): String = Constant.UA[Random().nextInt(15)]

        private fun isNetworkAvailable(): Boolean {
            val connectivityManager = BaseApplication.instance()
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            // 获取所有NetworkInfo对象
            val networkInfo = connectivityManager.allNetworkInfo
            if (networkInfo.isNotEmpty()) {
                for (i in networkInfo.indices) if (networkInfo[i].state == NetworkInfo.State.CONNECTED) return true // 存在可用的网络连接
            }
            return false
        }

        /**
         * 获取最新壁纸
         * */
        fun getWallpaperUpdate(pageNumber: Int, listener: HttpListener) {
            val url = Constant.WallpaperUpdateURL.replace("index", pageNumber.toString(), true)
            Log.d(Tag, "抓取地址: $url")
            if (isNetworkAvailable()) {
                GlobalScope.launch(Dispatchers.Main) {
                    val status = withContext(Dispatchers.IO) {
                        createConnection(url).execute().statusCode()
                    }
                    if (status == 200) {
                        listener.onSuccess(withContext(Dispatchers.IO) {
                            createConnection(url).get()
                        })
                    } else {
                        listener.onFailure(IndexOutOfBoundsException("IndexOutOfBoundsException"))
                    }
                }
            } else {
                listener.onFailure(Exception("SocketTimeoutException"))
            }
        }

        /**
         * 加载更多壁纸数据
         * */
        fun loadMoreWallpaper(pageNumber: Int, firstLink: String, listener: HttpListener) {
            if (isNetworkAvailable()) {
                Log.d(Tag, "loadMoreWallpaper: $firstLink")
                val newLink = firstLink.replace("1.html", "$pageNumber.html", true)
                GlobalScope.launch(Dispatchers.Main) {
                    val status = withContext(Dispatchers.IO) {
                        createConnection(newLink).execute().statusCode()
                    }
                    if (status == 200) {
                        listener.onSuccess(withContext(Dispatchers.IO) {
                            createConnection(newLink).get()
                        })
                    } else {
                        listener.onFailure(IndexOutOfBoundsException("IndexOutOfBoundsException"))
                    }
                }
            } else {
                EasyToast.showToast("哎呀，网络似乎断开了~", EasyToast.ERROR)
            }
        }

        /**
         * 加载更多写真数据
         * */
        fun loadMoreStarWallpaper(pageNumber: Int, firstLink: String, listener: HttpListener) {
            if (isNetworkAvailable()) {
                val newLink = firstLink.replace(".html", "_$pageNumber.html", true)
                GlobalScope.launch(Dispatchers.Main) {
                    val status = withContext(Dispatchers.IO) {
                        createConnection(newLink).execute().statusCode()
                    }
                    if (status == 200) {
                        listener.onSuccess(withContext(Dispatchers.IO) {
                            createConnection(newLink).get()
                        })
                    } else {
                        listener.onFailure(IndexOutOfBoundsException("IndexOutOfBoundsException"))
                    }
                }
            } else {
                EasyToast.showToast("哎呀，网络似乎断开了~", EasyToast.ERROR)
            }
        }

        /**
         * 获取探索发现数据
         * */
        fun getDiscoverData(pageNumber: Int, listener: HttpListener) {
            if (isNetworkAvailable()) {
                val url: String = if (pageNumber == 1) {
                    Constant.DiscoverURL
                } else {
                    Constant.DiscoverURL.replace("index", "index$pageNumber", true)
                }
                Log.d(Tag, "探索发现: $url")
                GlobalScope.launch(Dispatchers.Main) {
                    val status = withContext(Dispatchers.IO) {
                        createConnection(url).execute().statusCode()
                    }
                    if (status == 200) {
                        listener.onSuccess(withContext(Dispatchers.IO) {
                            createConnection(url).get()
                        })
                    } else {
                        listener.onFailure(IndexOutOfBoundsException("IndexOutOfBoundsException"))
                    }
                }
            } else {
                listener.onFailure(Exception("SocketTimeoutException"))
            }
        }

        /**
         * 抓取精选头像列表源数据
         * */
        fun getHeadImageData(pageNumber: Int, listener: HttpListener) {
            if (isNetworkAvailable()) {
                val url: String = if (pageNumber == 1) {
                    Constant.HeadImageURL
                } else {
                    Constant.HeadImageURL + "index_" + pageNumber + ".html"
                }
                Log.d(Tag, "头像列表链接地址: $url")
                GlobalScope.launch(Dispatchers.Main) {
                    val status = withContext(Dispatchers.IO) {
                        createConnection(url).execute().statusCode()
                    }
                    if (status == 200) {
                        listener.onSuccess(withContext(Dispatchers.IO) {
                            createConnection(url).get()
                        })
                    } else {
                        listener.onFailure(IndexOutOfBoundsException("IndexOutOfBoundsException"))
                    }
                }
            } else {
                listener.onFailure(Exception("SocketTimeoutException"))
            }
        }

        fun getDocumentData(link: String, listener: HttpListener) {
            if (isNetworkAvailable()) {
                Log.d(Tag, "抓取地址: $link")
                GlobalScope.launch(Dispatchers.Main) {
                    val status = withContext(Dispatchers.IO) {
                        createConnection(link).execute().statusCode()
                    }
                    if (status == 200) {
                        listener.onSuccess(withContext(Dispatchers.IO) {
                            createConnection(link).get()
                        })
                    } else {
                        listener.onFailure(IndexOutOfBoundsException("IndexOutOfBoundsException"))
                    }
                }
            } else {
                EasyToast.showToast("哎呀，网络似乎断开了~", EasyToast.ERROR)
            }
        }
    }
}