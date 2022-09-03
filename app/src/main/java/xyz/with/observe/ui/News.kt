package xyz.with.observe.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import xyz.with.observe.api.Repo
import xyz.with.observe.app.ObsApplication
import xyz.with.observe.viewmodel.MainViewModel

//要闻
@Composable
internal fun News(
    listStateList: MutableList<LazyListState>,
    page: Int,
    it: PaddingValues,
    mainViewModel: MainViewModel,
    contentHeadLine: Repo.ContentHeadLineData,
    navController: NavHostController,
    imgLoader: ImageLoader,
    centerData: MutableList<Repo.CenterData>,
    showImg: Boolean
) {
    LazyColumn(
        state = listStateList[page],
        modifier = Modifier
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.Top,
        contentPadding = it,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
        item {
            Column(modifier = Modifier
                .clickable {
                    mainViewModel.setNewsUrl(contentHeadLine.contentUrl)
                    navController.navigate(Screen.NewsView.route)
                }) {
                Text(
                    text = contentHeadLine.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
                if (showImg) {
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
        }
        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
        itemsIndexed(centerData) { i, center ->
            Card(
                shape = RoundedCornerShape(10.dp),
                elevation = 5.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp)
                    .clickable {
                        mainViewModel.setNewsUrl(center.contentUrl)
                        navController.navigate(Screen.NewsView.route)
                    }
            ) {
                Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = center.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    if (showImg) {
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
                    }
                    Row {
                        Text(
                            text = "${center.readCount}    ${center.commitCount}",
                            fontSize = 10.sp,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = center.theme, fontSize = 10.sp)
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    if (center.relationContent.isNotEmpty()){
                        center.relationContent.forEachIndexed { i, relation ->
                            Column(modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    mainViewModel.setNewsUrl(center.relationContentUrl[i])
                                    navController.navigate(Screen.NewsView.route)
                                }) {
                                Text(
                                    text = relation,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Light,
                                    color = Color.Gray
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(5.dp))
                    }


                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

    }
}