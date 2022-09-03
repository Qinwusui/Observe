package xyz.with.observe.api

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.core.content.edit
import com.blankj.utilcode.util.PathUtils
import com.google.gson.GsonBuilder
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import xyz.with.observe.app.ObsApplication
import java.io.File


object Repo {
    private const val URL_MAIN = "https://www.guancha.cn"
    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json()
        }
        install(HttpCookies)
    }

    //获取所有的组件
    suspend fun getHtmlDocument() = flow {
        val body = client.get(URL_MAIN)
        val jsoup = Jsoup.parse(body.bodyAsText())
        emit(jsoup)
    }.distinctUntilChanged().catch {
        return@catch
    }

    /**
     * 获取网页标题，[document] 非大报标题！
     * @return title 标题
     */
    fun getHtmlTitle(document: Document) = flow {
        emit(document.title())
    }

    /**
     * 获取大报标题
     */
    fun getContent(document: Document) = flow {
        document.allElements.filter { element -> element.className() == "content-headline" }
            .forEach { ele ->
                val contentHeadLineData = ContentHeadLineData()
                for (i in ele.allElements) {
                    if (i.attr("data-sensor") == "标题") {
                        contentHeadLineData.title = i.text()
                        contentHeadLineData.contentUrl = URL_MAIN + i.attr("href")
                    }
                    if (i.hasAttr("alt")) {
                        if (i.parent()?.attr("data-sensor") == "图片") {
                            contentHeadLineData.imgUrl = i.attr("src")
                        }
                    }
                }
                emit(contentHeadLineData)
            }
    }.distinctUntilChanged().flowOn(Dispatchers.IO)

    @Serializable
    data class ContentHeadLineData(
        var title: String = "",
        var contentUrl: String = "",
        var imgUrl: String = ""
    )

    @Serializable
    data class LeftData(
        var title: String = "",
        val authors: MutableList<String> = mutableListOf(),
        val authorsImgUrl: MutableList<String> = mutableListOf(),
        val authorsSubscription: MutableList<String> = mutableListOf(),
        var imgUrl: String = "",
        var content: String = "",
        var contentUrl: String = "",
        var readCount: String = "",
        var commitCount: String = "",
        var theme: String = ""
    )

    @Serializable
    data class CenterData(
        var title: String = "",
        var contentUrl: String = "",
        var imgUrl: String = "",
        var authorName: String = "",
        var theme: String = "",
        var readCount: String = "",
        var commitCount: String = "",
        var relationContent: MutableList<String> = mutableListOf(),
        var relationContentUrl: MutableList<String> = mutableListOf()
    )

    @Serializable
    data class RightData(
        var title: String = "",
        var contentUrl: String = "",
        var imgUrl: String = "",
        var authorName: String = "",
        var readCount: String = "",
        var commitCount: String = ""
    )

    //获取左边观察员列表
    fun getLeftContent(document: Document) = flow {
        val list = mutableListOf<LeftData>()
        document.allElements.filter { element ->
            element.className() == "Review-item"
        }.forEach { eleme ->
            val ls = eleme.allElements.filter { ele -> ele.tagName() == "li" }
            ls.forEach {
                val leftData = LeftData()
                for (i in it.allElements) {
                    if (i.attr("data-sensor") == "标题") {
                        leftData.title = i.text()
                    }
                    if (i.attr("data-sensor") == "作者名称") {
                        leftData.authors.add(i.text())
                    }
                    if (i.tagName() == "span") {
                        leftData.authorsSubscription.add(i.text())
                    }
                    if (i.hasAttr("data-original")) {
                        if (i.parent()?.attr("data-sensor") == "作者头像") {
                            leftData.authorsImgUrl.add(i.attr("data-original"))
                        } else {
                            leftData.imgUrl = i.attr("data-original")
                        }
                    }
                    if (i.className() == "module-artile") {
                        leftData.content = i.text().replace("[全文]", "")
                    }
                    if (i.attr("data-sensor") == "全文") {
                        leftData.contentUrl = "${URL_MAIN}${i.attr("href")}"
                    }
                    if (i.attr("data-sensor") == "阅读数") {
                        leftData.readCount = i.text()
                    }
                    if (i.attr("data-sensor") == "评论数") {
                        leftData.commitCount = i.text()
                    }
                    if (i.attr("data-sensor") == "专题") {
                        leftData.theme = i.text()
                    }
                }
                list.add(leftData)
            }


        }
        emit(list)
    }.distinctUntilChanged().flowOn(Dispatchers.IO)

    @SuppressLint("NewApi")
    fun getCenterContent(document: Document) = flow {
        val list = mutableListOf<CenterData>()
        document.allElements.filter { element ->
            element.className() == "img-List"
        }.forEach { element ->
            val ls = element.allElements.filter { el -> el.tagName() == "li" }
            ls.forEach { eleme ->
                val centerData = CenterData()
                for (i in eleme.allElements) {
                    if (i.attr("data-sensor") == "标题") {
                        centerData.title = i.text()
                        centerData.contentUrl = URL_MAIN + i.attr("href")
                    }
                    if (i.hasAttr("data-original")) {
                        if (i.parent()?.attr("data-sensor") == "图片") {
                            centerData.imgUrl = i.attr("data-original")
                        }
                    }
                    if (i.attr("data-sensor") == "阅读数") {
                        centerData.readCount = i.text()
                    }
                    if (i.attr("data-sensor") == "评论数") {
                        centerData.commitCount = i.text()
                    }
                    if (i.attr("data-sensor") == "专题") {
                        centerData.theme = i.text()
                    }
                    if (i.attr("data-sensor") == "关联新闻") {
                        centerData.relationContent.add(i.text())
                        centerData.relationContentUrl.add(URL_MAIN + i.attr("href"))
                    }
                    if (i.tagName() == "span" && i.className() == "module-interact-creator") {
                        centerData.authorName = i.text()
                    }
                }
                list.add(centerData)
            }
        }
        list.removeIf { it.title.isEmpty() }
        emit(list)
    }.distinctUntilChanged().flowOn(Dispatchers.IO)


    fun getRightContent(document: Document) = flow {
        val list = mutableListOf<RightData>()
        document.allElements.filter { element ->
            element.className() == "fengwen-list"
        }.forEach { element ->
            val ls = element.allElements.filter { el -> el.tagName() == "li" }
            ls.forEach { el ->
                val rightData = RightData()
                for (i in el.allElements) {
                    if (i.attr("data-sensor") == "标题") {
                        rightData.title = i.text()
                        rightData.contentUrl =
                            if (i.attr("href").startsWith("http") && !i.attr("href")
                                    .startsWith("https")
                            ) {
                                i.attr("href").replace("http", "https")
                            } else {
                                if (i.attr("href").startsWith("https")) {
                                    i.attr("href")
                                } else {
                                    URL_MAIN + i.attr("href")
                                }
                            }
                    }
                    if (i.hasAttr("data-original")) {
                        if (i.parent()?.attr("data-sensor") == "图片") {
                            rightData.imgUrl = i.attr("data-original")
                        }
                    }
                    if (i.className() == "module-interact-creator" && i.tagName() == "span") {
                        rightData.authorName = i.text()
                    }
                    if (i.attr("data-sensor") == "阅读数") {
                        rightData.readCount = i.text()
                    }
                    if (i.attr("data-sensor") == "评论数") {
                        rightData.commitCount = i.text()
                    }
                }
                list.add(rightData)
            }
        }
        emit(list)
    }.distinctUntilChanged().flowOn(Dispatchers.IO)


    //枚举
    enum class SwitchMode {
        Js, Img
    }

    //获取开关状态
    /**
     * @param isEdit 是否编辑该值
     * @param data 编辑值
     * @param mode 编辑对象
     * @return [Flow<Boolean>]
     */
    fun catchSwitch(isEdit: Boolean = false, data: Boolean = false, mode: SwitchMode) = flow {
        val sharedPreferences =
            ObsApplication.context.getSharedPreferences(
                when (mode) {
                    SwitchMode.Js -> {
                        "webView"
                    }
                    SwitchMode.Img -> {
                        "ImageManager"
                    }
                }, Context.MODE_PRIVATE
            )
        when (mode) {
            SwitchMode.Js -> {

                if (isEdit) {
                    sharedPreferences.edit(true) {
                        putBoolean("enableScript", data)
                    }
                    emit(data)
                } else {
                    val isEnabled = sharedPreferences.getBoolean("enableScript", false)
                    emit(isEnabled)
                }
            }
            SwitchMode.Img -> {
                if (isEdit) {
                    sharedPreferences.edit(true) {
                        putBoolean("showImage", data)
                    }
                    emit(data)
                } else {
                    val isEnabled = sharedPreferences.getBoolean("showImage", true)
                    emit(isEnabled)
                }
            }
        }

    }

    //加载JsUrl
    fun loadScriptUrl(): MutableList<String> {
        var list = mutableListOf<String>()
        val gson = GsonBuilder().setPrettyPrinting().create()
        val path = PathUtils.getExternalAppDataPath() + "/files/JsUrl.json"
        val file = File(path)
        if (!file.isFile) {
            file.createNewFile()
            writeToJsFile(list)
        }
        list = gson.fromJson(file.readText(), list::class.java)
        return list
    }

    //添加Js脚本
    fun addScriptUrl(url: String): MutableList<String> {
        val list = loadScriptUrl()
        if (!list.contains(url)) {
            list.add(url)
        }
        writeToJsFile(list)
        return list
    }

    //移除Js脚本
    fun removeScriptUrl(url: String): MutableList<String> {
        val list = loadScriptUrl()
        if (list.contains(url)) {
            list.remove(url)
        }
        writeToJsFile(list)
        return list
    }

    //更改后写入文件
    private fun writeToJsFile(list: MutableList<String>) {
        val path = PathUtils.getExternalAppDataPath() + "/files/JsUrl.json"
        val file = File(path)
        val gson = GsonBuilder().setPrettyPrinting().create()
        file.writeText(gson.toJson(list))
    }

    //读取Js
    fun loadJsByFile(): String {
        val file = File(PathUtils.getExternalAppDataPath() + "/files/js")
        if (!file.isFile) {
            return ""
        }
        return file.readText()
    }

    //写入Js
    suspend fun writeJsByUrl() = flow {
        val list = loadScriptUrl()
        val file = File(PathUtils.getExternalAppDataPath() + "/files/js")
        if (!file.isFile) {
            file.delete()
        }
        list.forEach { url ->
            if ("http(s)://".toRegex().containsMatchIn(url)) {
                withContext(Dispatchers.IO) {
                    try {
                        val jsoup = Jsoup.connect(url).get()
                        jsoup.body().allElements.first { it.tagName() == "pre" }.apply {
                            val text = this.text()
                            for (i in text.lines()) {
                                if (!i.startsWith("//")) {
                                    file.appendText(i + "\n")
                                }
                            }
                        }
                    } catch (e: Exception) {

                    }

                }
            }
        }
        emit(true)
    }

    //清理缓存
    fun clearCaches() {
        val pActivity = ObsApplication.context
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", pActivity.packageName, null)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.data = uri
        try {
            pActivity.startActivity(intent)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }


}

fun String.loge() = Log.e("TAG", this)