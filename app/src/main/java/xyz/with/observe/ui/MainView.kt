package xyz.with.observe.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import xyz.with.observe.app.ObsApplication
import xyz.with.observe.theme.statusBarColor
import xyz.with.observe.viewmodel.MainViewModel

@OptIn(ExperimentalPagerApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainView(mainViewModel: MainViewModel) {
    val systemUiController = rememberSystemUiController()
    LaunchedEffect(key1 = systemUiController, block = {
        systemUiController.setStatusBarColor(statusBarColor, false)
        systemUiController.setSystemBarsColor(statusBarColor, false)
    })
    //左边栏，中间，右边栏，头条
    val leftListData = mainViewModel.leftContent.collectAsState().value
    val centerData = mainViewModel.centerContent.collectAsState().value
    val rightData = mainViewModel.rightContent.collectAsState().value
    val contentHeadLine = mainViewModel.contentHeadLine.collectAsState().value

    val imgLoader = ImageLoader.Builder(ObsApplication.context).build()
    val tabViewData = remember {
        mutableStateListOf("观点", "要闻", "风闻")
    }
    val state = rememberPagerState(initialPage = 1)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var swipe by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = swipe, block = {
        if (swipe) {
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
                        modifier = Modifier.fillMaxSize()
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
    }) {
        if (centerData.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Text(text = "加载中...")
            }
        } else {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = swipe),
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
                        elevation = 20.dp,
                        contentColor = Color.White
                    )
                }) {
                HorizontalPager(
                    state = state,
                    modifier = Modifier
                        .fillMaxSize(),
                    count = 3,
                    verticalAlignment = Alignment.Top
                ) { page: Int ->
                    when (page) {
                        //0代表左侧观察员栏
                        0 -> {
                            LazyColumn(
                                state = rememberLazyListState(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp),
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                itemsIndexed(leftListData) { i, leftData ->
                                    Card(
                                        shape = RoundedCornerShape(10.dp),
                                        elevation = 20.dp,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 10.dp)
                                            .clickable {
                                                val intent = Intent(Intent.ACTION_VIEW)
                                                intent.data = Uri.parse(leftData.contentUrl)
                                                context.startActivity(intent)
                                            }
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(
                                                start = 5.dp,
                                                end = 5.dp
                                            )
                                        ) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = leftData.title,
                                                fontSize = 18.sp,
                                                fontStyle = FontStyle.Italic,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(
                                                horizontalArrangement = Arrangement.Start,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                leftData.authorsImgUrl.forEachIndexed { i, imgUrl ->
                                                    Card(
                                                        shape = RoundedCornerShape(10.dp),
                                                        elevation = 0.dp
                                                    ) {
                                                        AsyncImage(
                                                            model = imgUrl,
                                                            contentDescription = null,
                                                            imageLoader = imgLoader,
                                                            contentScale = ContentScale.Crop,
                                                            modifier = Modifier.size(20.dp),
                                                            filterQuality = FilterQuality.Low
                                                        )
                                                    }
                                                    Column {
                                                        Text(
                                                            text = leftData.authors[i],
                                                            fontSize = 10.sp
                                                        )
                                                        Text(
                                                            text = leftData.authorsSubscription[i],
                                                            fontSize = 6.sp,
                                                            color = Color.Gray
                                                        )
                                                    }
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Text(text = leftData.content)
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Text(
                                                text = "${leftData.readCount}    ${leftData.commitCount}",
                                                fontSize = 10.sp,
                                                modifier = Modifier.padding(bottom = 10.dp)
                                            )
                                        }

                                    }
                                    Spacer(modifier = Modifier.height(20.dp))

                                }
                            }

                        }
                        1 -> {
                            LazyColumn(
                                state = rememberLazyListState(),
                                modifier = Modifier
                                    .padding(horizontal = 10.dp),
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                item {
                                    Column(modifier = Modifier
                                        .clickable {
                                            val intent = Intent(Intent.ACTION_VIEW)
                                            intent.data = Uri.parse(contentHeadLine.contentUrl)
                                            context.startActivity(intent)
                                        }) {
                                        Text(
                                            text = contentHeadLine.title,
                                            fontSize = 30.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            textAlign = TextAlign.Center,
                                        )
                                        Card(shape = RoundedCornerShape(10.dp), elevation = 0.dp) {
                                            SubcomposeAsyncImage(
                                                model = contentHeadLine.imgUrl,
                                                contentDescription = null,
                                                imageLoader = imgLoader,
                                                modifier = Modifier.height(150.dp)
                                            )
                                        }
                                    }
                                }
                                item {
                                    Spacer(modifier = Modifier.height(20.dp))
                                }
                                itemsIndexed(centerData) { i, center ->
                                    Card(
                                        shape = RoundedCornerShape(10.dp),
                                        elevation = 20.dp,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 10.dp)
                                            .clickable {
                                                val intent = Intent(Intent.ACTION_VIEW)
                                                intent.data = Uri.parse(center.contentUrl)
                                                context.startActivity(intent)
                                            }
                                    ) {
                                        Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = center.title,
                                                fontSize = 18.sp,
                                                fontStyle = FontStyle.Italic,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Card(
                                                shape = RoundedCornerShape(10.dp),
                                                elevation = 0.dp
                                            ) {
                                                AsyncImage(
                                                    model = center.imgUrl,
                                                    contentDescription = null,
                                                    imageLoader = ImageLoader(ObsApplication.context),
                                                    filterQuality = FilterQuality.Low,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Row {
                                                Text(
                                                    text = "${center.readCount}    ${center.commitCount}",
                                                    fontSize = 10.sp,
                                                    modifier = Modifier.padding(bottom = 10.dp)
                                                )
                                                Spacer(modifier = Modifier.width(20.dp))
                                                Text(text = center.theme, fontSize = 10.sp)
                                            }
                                            Spacer(modifier = Modifier.height(10.dp))
                                            center.relationContent.forEachIndexed { i, relation ->
                                                Column(modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        val intent = Intent(Intent.ACTION_VIEW)
                                                        intent.data =
                                                            Uri.parse(center.relationContentUrl[i])
                                                        context.startActivity(intent)
                                                    }) {
                                                    Text(
                                                        text = relation,
                                                        fontSize = 15.sp,
                                                        fontWeight = FontWeight.Light,
                                                        color = Color.Gray
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(20.dp))
                                }
                            }

                        }
                        2 -> {
                            LazyColumn(
                                state = rememberLazyListState(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp),
                                verticalArrangement = Arrangement.Top,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                itemsIndexed(rightData) { i, right ->
                                    Card(
                                        shape = RoundedCornerShape(10.dp),
                                        elevation = 20.dp,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 10.dp, horizontal = 10.dp)
                                            .clickable {
                                                val intent = Intent(Intent.ACTION_VIEW)
                                                intent.data = Uri.parse(right.contentUrl)
                                                context.startActivity(intent)
                                            }
                                    ) {
                                        Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                                            Spacer(modifier = Modifier.height(10.dp))
                                            Text(
                                                text = right.title,
                                                fontSize = 18.sp,
                                                fontStyle = FontStyle.Italic,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Card(
                                                shape = RoundedCornerShape(10.dp),
                                                elevation = 0.dp
                                            ) {
                                                AsyncImage(
                                                    model = right.imgUrl,
                                                    contentDescription = null,
                                                    imageLoader = ImageLoader(ObsApplication.context),
                                                    filterQuality = FilterQuality.Low,
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(5.dp))
                                            Text(
                                                text = "${right.readCount}    ${right.commitCount}",
                                                fontSize = 10.sp,
                                                modifier = Modifier.padding(bottom = 10.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(20.dp))
                                }

                            }

                        }
                    }
                }
            }


        }
    }
}