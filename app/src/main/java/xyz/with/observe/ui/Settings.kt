package xyz.with.observe.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blankj.utilcode.util.ToastUtils
import xyz.with.observe.theme.buttonColor
import xyz.with.observe.theme.colorBlue
import xyz.with.observe.theme.statusBarColor
import xyz.with.observe.viewmodel.MainViewModel

//设置
@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun Settings(
    mainViewModel: MainViewModel,
) {
    val enableScript by mainViewModel.enableScript.collectAsState()
    val jsUrlList by mainViewModel.jsUrlList.collectAsState()
    val showImg by mainViewModel.showImg.collectAsState()
    var showDeleteDialog by remember {
        mutableStateOf(false)
    }
    var showAddUrlDialog by remember {
        mutableStateOf(false)
    }
    var jsUrl by remember {
        mutableStateOf("")
    }
    var willDeleteUrl by remember {
        mutableStateOf("")
    }

    AnimatedVisibility(visible = showDeleteDialog) {
        AlertDialog(onDismissRequest = { showDeleteDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    mainViewModel.removeJsUrl(willDeleteUrl)
                    showDeleteDialog = false
                }) {
                    Text(text = "是", color = buttonColor)
                }
            }, dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = "否", color = statusBarColor)
                }
            }, title = {
                Text(text = "是否删除 $willDeleteUrl ？")
            })
    }
    AnimatedVisibility(visible = showAddUrlDialog) {
        AlertDialog(
            onDismissRequest = { showAddUrlDialog = false },
            title = {
                Text(
                    text = "输入Greasyfork脚本URL（可见Js代码的那一页）",
                    fontWeight = FontWeight.ExtraBold
                )
            },
            text = {
                OutlinedTextField(
                    value = jsUrl, onValueChange = { jsUrl = it }, colors =
                    TextFieldDefaults.outlinedTextFieldColors(
                        textColor = statusBarColor,
                        backgroundColor = Color.Transparent,
                        focusedBorderColor = statusBarColor,
                        cursorColor = statusBarColor,
                    ),
                    label = { Text("URL") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    mainViewModel.addJsUrl(jsUrl)
                    showAddUrlDialog = false
                }) {
                    Text(text = "完成", color = statusBarColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddUrlDialog = false }) {
                    Text(text = "取消")
                }
            }
        )
    }

    LazyColumn(state = rememberLazyListState()) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clickable {
                        mainViewModel.switchScript(!enableScript)
                    },
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = "针对WebView启用Javascript脚本")
                        Text(
                            text = "当前状态：${if (enableScript) "启用脚本" else "禁用脚本"}",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                    Switch(
                        checked = enableScript,
                        onCheckedChange = { mainViewModel.switchScript(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = statusBarColor
                        )
                    )
                }

            }
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clickable {
                        mainViewModel.clearCaches()
                    }
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(text = "清理WebView缓存")
                }
                Text(
                    text = "这将会清理留存的图片和WebView网页缓存",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(horizontal = 20.dp)
                    .clickable {
                        mainViewModel.showImg(!showImg)
                    },
                verticalArrangement = Arrangement.Center,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "无图模式")
                        Text(
                            text = "不加载列表图片，提高浏览性能。当前：${if (!showImg) "已启用" else "已禁用"}",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                    Switch(
                        checked = !showImg,
                        onCheckedChange = { mainViewModel.showImg(!it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = statusBarColor
                        )
                    )
                }


            }
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(horizontal = 20.dp)
                    .clickable {
                        statusBarColor = colorBlue
                    },
                verticalArrangement = Arrangement.Center,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(text = "更换强调色")
                }
                Text(
                    text = "娱乐功能，仅本次使用生效",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }
        }
        item {
            AnimatedVisibility(visible = enableScript) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clickable { showAddUrlDialog = true }
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text = "添加远程Js脚本URL")
                    Text(
                        text = "目前仅支持GreasyFork.org仓库上的脚本",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                }
            }
        }

        itemsIndexed(jsUrlList) { _, url ->
            AnimatedVisibility(visible = enableScript) {
                Column(modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .combinedClickable(
                        onClick = { ToastUtils.showShort("长按可删除") },
                        onLongClick = {
                            willDeleteUrl = url
                            showDeleteDialog = true
                        }
                    ), verticalArrangement = Arrangement.Center) {
                    Text(text = url)
                }
            }
        }

    }
}
