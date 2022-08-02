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

    init {
        getHtml()
    }

    private fun getHtml() {
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
        }
    }
}