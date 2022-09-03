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

//风闻
@Composable
internal fun Others(
    listStateList: MutableList<LazyListState>,
    page: Int,
    rightData: MutableList<Repo.RightData>,
    mainViewModel: MainViewModel,
    navController: NavHostController,
    imageLoader: ImageLoader,
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
        itemsIndexed(rightData) { i, right ->
            Card(
                shape = RoundedCornerShape(10.dp),
                elevation =5.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 10.dp)
                    .clickable {
                        mainViewModel.setNewsUrl(right.contentUrl)
                        navController.navigate(Screen.NewsView.route)
                    }
            ) {
                Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = right.title,
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
                                model = right.imgUrl,
                                contentDescription = null,
                                imageLoader = imageLoader,
                                filterQuality = FilterQuality.Low,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(
                        text = "${right.readCount}    ${right.commitCount}",
                        fontSize = 10.sp,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

    }
}
