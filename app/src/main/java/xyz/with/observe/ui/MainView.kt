package xyz.with.observe.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import xyz.with.observe.app.ObsApplication
import xyz.with.observe.theme.cardColor
import xyz.with.observe.viewmodel.MainViewModel

@OptIn(ExperimentalPagerApi::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MainView(mainViewModel: MainViewModel) {
    val systemUiController = rememberSystemUiController()
    LaunchedEffect(key1 = systemUiController, block = {
        systemUiController.setStatusBarColor(cardColor, false)
        systemUiController.setSystemBarsColor(cardColor, false)
    })
    //左边栏，中间，右边栏，头条
    val leftListData = mainViewModel.leftContent.collectAsState().value
    val centerData = mainViewModel.centerContent.collectAsState().value
    val rightData = mainViewModel.rightContent.collectAsState().value
    val contentHeadLine = mainViewModel.contentHeadLine.collectAsState().value

//    centerData.shuffle()
    //左侧随机化
    leftListData.shuffle()
    //右侧随机化
    rightData.shuffle()
    val imgLoader = ImageLoader.Builder(ObsApplication.context).build()
    val tabViewData = remember {
        mutableStateListOf("观点", "要闻", "风闻")
    }
    val state = rememberPagerState(initialPage = 0)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
//    LogUtils.e(leftListData)
    Scaffold(modifier = Modifier.fillMaxSize(), topBar = {
        TopAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("观察者网")
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
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize(),
            ) {
                LazyColumn(
                    state = rememberLazyListState(),
                    modifier = Modifier.height(276.dp),
                    contentPadding = it
                ) {

                    item {
                        Column(modifier = Modifier
                            .fillMaxSize()
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
                        ScrollableTabRow(
                            selectedTabIndex = state.currentPage,
                            backgroundColor = Color.Transparent,
                            contentColor = cardColor,
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
                                    Text(text = d)
                                }
                            }
                        }

                    }

                }
                HorizontalPager(
                    state = state,
                    modifier = Modifier.fillMaxSize(),
                    count = 3,
                    verticalAlignment = Alignment.Top
                ) { page: Int ->

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(horizontal = 10.dp),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        when (page) {
                            //0代表左侧观察员栏
                            0 -> {
                                Spacer(modifier = Modifier.height(10.dp))
                                leftListData.forEach { leftData ->
                                    Card(
                                        shape = RoundedCornerShape(10.dp),
                                        elevation = 10.dp,
                                        modifier = Modifier
                                            .fillMaxWidth()
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

                            1 -> {
                                Spacer(modifier = Modifier.height(10.dp))
                                centerData.forEach { center ->
                                    Card(
                                        shape = RoundedCornerShape(10.dp),
                                        elevation = 10.dp,
                                        modifier = Modifier
                                            .fillMaxWidth()
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
                            2 -> {
                                Spacer(modifier = Modifier.height(10.dp))
                                rightData.forEach { right ->
                                    Card(
                                        shape = RoundedCornerShape(10.dp),
                                        elevation = 10.dp,
                                        modifier = Modifier
                                            .fillMaxWidth()
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