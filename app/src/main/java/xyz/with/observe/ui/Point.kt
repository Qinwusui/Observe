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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.ImageLoader
import coil.compose.AsyncImage
import xyz.with.observe.api.Repo
import xyz.with.observe.viewmodel.MainViewModel

//观点
@Composable
internal fun Point(
    listStateList: MutableList<LazyListState>,
    page: Int,
    leftListData: MutableList<Repo.LeftData>,
    mainViewModel: MainViewModel,
    navController: NavHostController,
    imgLoader: ImageLoader,
    showImg: Boolean
) {
    LazyColumn(
        state = listStateList[page],
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        itemsIndexed(leftListData) { _, leftData ->
            Card(
                shape = RoundedCornerShape(10.dp),
                elevation = 5.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp)
                    .clickable {
                        mainViewModel.setNewsUrl(leftData.contentUrl)
                        navController.navigate(Screen.NewsView.route)
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
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        leftData.authorsImgUrl.forEachIndexed { i, imgUrl ->
                            if (showImg) {
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
                    if (showImg) {
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            elevation = 0.dp
                        ) {
                            AsyncImage(
                                contentScale = ContentScale.Crop,
                                model = leftData.imgUrl,
                                contentDescription = null,
                                imageLoader = imgLoader,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                    }
                    Text(text = leftData.content)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "${leftData.readCount}    ${leftData.commitCount}",
                        fontSize = 10.sp,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }

            }
            Spacer(modifier = Modifier.height(10.dp))

        }
    }
}