package xyz.with.observe.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.LocalContext
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.blankj.utilcode.util.ToastUtils
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import xyz.with.observe.api.Repo
import xyz.with.observe.app.ObsApplication
import xyz.with.observe.theme.buttonColor
import xyz.with.observe.theme.colorBlue
import xyz.with.observe.theme.statusBarColor
import xyz.with.observe.viewmodel.MainViewModel

@OptIn(ExperimentalPagerApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainView(
    mainViewModel: MainViewModel,
    navController: NavHostController,
    context: Context = ObsApplication.context
) {
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(statusBarColor, false)
        systemUiController.setSystemBarsColor(statusBarColor, false)
    }
    //左边栏，中间，右边栏，头条
    val leftListData = mainViewModel.leftContent.collectAsState().value
    val centerData = mainViewModel.centerContent.collectAsState().value
    val rightData = mainViewModel.rightContent.collectAsState().value
    val contentHeadLine = mainViewModel.contentHeadLine.collectAsState().value
    val showImg by mainViewModel.showImg.collectAsState()
    val imgLoader = ImageLoader(context)
    val tabViewData = remember {
        mutableStateListOf("观点", "要闻", "风闻", "设置")
    }
    val state = rememberPagerState(initialPage = 1)
    val scope = rememberCoroutineScope()
    var swipe by remember {
        mutableStateOf(false)
    }
    val swipeState = rememberSwipeRefreshState(isRefreshing = swipe)
    val listStateList = mutableListOf(
        rememberLazyListState(),
        rememberLazyListState(),
        rememberLazyListState(),
        rememberLazyListState()
    )

    LaunchedEffect(key1 = swipe, block = {
        if (swipe) {
            if (state.currentPage == 3) {
                swipe = false
                return@LaunchedEffect
            }
            listStateList[state.currentPage].animateScrollToItem(0, 0)
            delay(2000)
            mainViewModel.getHtml()
            swipe = false
        }
    })
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(
            modifier = Modifier
                .fillMaxWidth(),
            backgroundColor = statusBarColor
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AnimatedVisibility(visible = !swipe) {
                    ScrollableTabRow(
                        selectedTabIndex = state.currentPage,
                        backgroundColor = statusBarColor,
                        contentColor = Color.White,
                        modifier = Modifier.fillMaxSize(),
                        indicator = {
                            val modifier = Modifier.tabIndicatorOffset(it[state.currentPage])
                            Canvas(modifier = modifier) {
                                drawCircle(Color.White, 15f)
                            }
                        },
                        divider = {
                            Divider(
                                modifier = Modifier
                                    .requiredHeight(0.dp)
                                    .fillMaxWidth()
                                    .background(Color.White)
                            )
                        }
                    ) {
                        tabViewData.forEachIndexed { i, d ->
                            Box(
                                modifier = Modifier
                                    .height(40.dp)
                                    .width(80.dp)
                                    .clickable {
                                        scope.launch { state.animateScrollToPage(i, 0f) }
                                    }, contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = d,
                                    color = if (state.currentPage == i) Color.White else Color.Unspecified,
                                    fontSize = if (state.currentPage == i) 20.sp else 15.sp,
                                    fontWeight = if (state.currentPage == i) FontWeight.ExtraBold else FontWeight.Light
                                )
                            }
                        }
                    }
                }
                AnimatedVisibility(visible = swipe) {
                    Text(
                        text = "刷新中...",
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }, floatingActionButtonPosition = FabPosition.End, floatingActionButton = {
        AnimatedVisibility(visible = state.currentPage != 3) {
            ExtendedFloatingActionButton(
                text = {
                    Text(text = "刷新", color = Color.White)
                },
                onClick = { swipe = true },
                backgroundColor = statusBarColor,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        tint = Color.White,
                        contentDescription = null
                    )
                })
        }
    }) {
        if (centerData.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = statusBarColor)
                Text(text = "加载中...")
            }
        } else {
            SwipeRefresh(
                state = swipeState,
                onRefresh = { swipe = true },
                indicator = { state, trigger ->
                    SwipeRefreshIndicator(
                        state = state,
                        refreshTriggerDistance = trigger,
                        scale = true,
                        arrowEnabled = true,
                        backgroundColor = statusBarColor,
                        shape = RoundedCornerShape(50.dp),
                        largeIndication = true,
                        elevation = 10.dp,
                        contentColor = Color.White
                    )
                }) {
                HorizontalPager(
                    state = state,
                    modifier = Modifier
                        .fillMaxSize(),
                    count = listStateList.size,
                    verticalAlignment = Alignment.Top
                ) { page: Int ->
                    when (page) {
                        //0代表左侧观察员栏
                        0 -> {

                            Point(
                                listStateList,
                                page,
                                leftListData,
                                mainViewModel,
                                navController,
                                imgLoader,
                                showImg
                            )


                        }
                        1 -> {
                            News(
                                listStateList,
                                page,
                                it,
                                mainViewModel,
                                contentHeadLine,
                                navController,
                                imgLoader,
                                centerData,
                                showImg
                            )
                        }
                        2 -> {
                            Others(
                                listStateList,
                                page,
                                rightData,
                                mainViewModel,
                                navController,
                                imgLoader,
                                showImg
                            )

                        }
                        3 -> {
                            Settings(
                                mainViewModel,
                            )
                        }
                    }
                }
            }


        }
    }
}

