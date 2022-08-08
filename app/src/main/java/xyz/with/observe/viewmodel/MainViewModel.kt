package xyz.with.observe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import xyz.with.observe.api.Repo

class MainViewModel(private val repo: Repo = Repo) : ViewModel() {
    //左边栏
    private val _leftContent = MutableStateFlow(mutableListOf<Repo.LeftData>())
    val leftContent = _leftContent.asStateFlow()

    //头条
    private val _contentHeadLine = MutableStateFlow(Repo.ContentHeadLineData())
    val contentHeadLine = _contentHeadLine.asStateFlow()


    //中间正文部分
    private val _centerContent = MutableStateFlow(mutableListOf<Repo.CenterData>())
    val centerContent = _centerContent.asStateFlow()

    //右边
    private val _rightContent = MutableStateFlow(mutableListOf<Repo.RightData>())
    val rightContent = _rightContent.asStateFlow()

    //获取是否启用Script
    private val _enableScript = MutableStateFlow(false)
    val enableScript = _enableScript.asStateFlow()

    //传入Url
    private val _newsUrl = MutableStateFlow("")
    val newsUrl = _newsUrl.asStateFlow()

    //用于Nav跳转暂存数据
    fun setNewsUrl(url: String) {
        _newsUrl.value = url
    }

    private val _js = MutableStateFlow("")
    val js = _js.asStateFlow()
    private val _jsLoading = MutableStateFlow(false)
    val jsLoading = _jsLoading.asStateFlow()

    fun writeJsByUrl() {
        viewModelScope.launch {
            repo.writeJsByUrl().collect {
                _jsLoading.value = it
            }
        }
    }


    fun loadJs() {
        _js.value = repo.loadJsByFile()
    }

    //加载的UrlList
    private val _jsUrlList = MutableStateFlow(mutableListOf<String>())
    val jsUrlList = _jsUrlList.asStateFlow()

    init {
        getHtml()
        getJsUrl()
        writeJsByUrl()
    }

    //移除JsUrl
    fun removeJsUrl(url: String) {
        _jsUrlList.value = repo.removeScriptUrl(url)
        writeJsByUrl()
    }

    //添加Url
    fun addJsUrl(url: String) {
        _jsUrlList.value = repo.addScriptUrl(url)
        writeJsByUrl()
    }

    //读取Url
    private fun getJsUrl() {
        _jsUrlList.value = repo.loadScriptUrl()
    }

    //获取开关状态
    fun switchScript(v: Boolean) {
        viewModelScope.launch {
            repo.catchSwitch(true, v).collect {
                _enableScript.value = it
            }
        }
    }

    //Get观网首页
    fun getHtml() {
        viewModelScope.launch {
            repo.getHtmlDocument().collect { docu ->
                repo.getContent(docu).collect {
                    _contentHeadLine.value = it
                }
                repo.getCenterContent(docu).collect {
                    _centerContent.value = it
                }
                repo.getLeftContent(docu).collect {
                    _leftContent.value = it
                }
                repo.getRightContent(docu).collect {
                    _rightContent.value = it
                }
            }
            repo.catchSwitch().collect {
                _enableScript.value = it
            }
        }
    }
}