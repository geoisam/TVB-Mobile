package com.pjs.tvbox.ui.view

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.footnotes.FootnotesExtension
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TableBlock
import org.commonmark.ext.gfm.tables.TableBody
import org.commonmark.ext.gfm.tables.TableCell
import org.commonmark.ext.gfm.tables.TableHead
import org.commonmark.ext.gfm.tables.TableRow
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.image.attributes.ImageAttributesExtension
import org.commonmark.ext.ins.InsExtension
import org.commonmark.ext.task.list.items.TaskListItemsExtension
import org.commonmark.node.*
import org.commonmark.parser.Parser
import java.io.File

@Composable
fun MarkdownView(
    markdown: String,
    modifier: Modifier = Modifier
) {
    val extensions = listOf(
        AutolinkExtension.create(),
        FootnotesExtension.create(),
        StrikethroughExtension.create(),
        TablesExtension.create(),
        ImageAttributesExtension.create(),
        InsExtension.create(),
        TaskListItemsExtension.create(),
    )

    val parser = remember {
        Parser.builder().extensions(extensions).build()
    }
    val document = parser.parse(markdown)

    MarkdownRenderer(document, modifier)
}

@Composable
private fun MarkdownRenderer(
    node: Node,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val blocks = remember(node) {
        generateSequence(node.firstChild) { it.next }.toList()
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(blocks) { block ->
            MarkdownNode(block)
        }
    }
}

@Composable
private fun MarkdownNode(node: Node) {
    when (node) {
        is Heading -> renderHeading(node)
        is Paragraph -> renderParagraph(node)
        is BulletList -> renderBulletList(node)
        is OrderedList -> renderOrderedList(node)
        is Image -> renderImage(node)
        is TableBlock -> renderTable(node)
        is Text -> Text(text = node.literal)
        else -> {
            var child = node.firstChild
            while (child != null) {
                MarkdownNode(child)
                child = child.next
            }
        }
    }
}

@Composable
private fun renderImage(node: Image) {
    val context = LocalContext.current
    val url = node.destination ?: return

    val imageRequest = when {
        url.startsWith("../") -> {
            val assetPath = url.removePrefix("../")
            ImageRequest.Builder(context)
                .data("file:///android_asset/$assetPath")
                .crossfade(true)
                .build()
        }

        url.startsWith("http://") || url.startsWith("https://") -> {
            ImageRequest.Builder(context)
                .data(url)
                .crossfade(true)
                .build()
        }

        else -> {
            ImageRequest.Builder(context)
                .data(File(url))
                .crossfade(true)
                .build()
        }
    }
    Box(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 10.dp)
    ) {
        SubcomposeAsyncImage(
            model = imageRequest,
            loading = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            },
            error = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "图片加载失败",
                        color = Color.Gray,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            },
            contentDescription = node.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun renderHeading(node: Heading) {
    val size = when (node.level) {
        1 -> 28.sp; 2 -> 24.sp; 3 -> 20.sp; 4 -> 18.sp; else -> 16.sp
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = node.getText(),
            fontSize = size,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun renderParagraph(node: Paragraph) {
    Column(
        modifier = Modifier.padding(vertical = 4.dp),
    ) {
        var child = node.firstChild
        while (child != null) {
            MarkdownNode(child)
            child = child.next
        }
    }
}

@Composable
private fun renderBulletList(node: BulletList) {
    Column(
        modifier = Modifier.padding(start = 16.dp),
    ) {
        var child = node.firstChild
        while (child != null) {
            if (child is ListItem) renderListItem(child, "•")
            child = child.next
        }
    }
}

@Composable
private fun renderOrderedList(node: OrderedList) {
    var index = node.markerStartNumber ?: 1
    Column(
        modifier = Modifier.padding(start = 16.dp),
    ) {
        var child = node.firstChild
        while (child != null) {
            if (child is ListItem) renderListItem(child, "${index++}.")
            child = child.next
        }
    }
}

@Composable
private fun renderListItem(node: ListItem, prefix: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "$prefix ", fontWeight = FontWeight.Bold)
        Column {
            var child = node.firstChild
            while (child != null) {
                MarkdownNode(child)
                child = child.next
            }
        }
    }
}

@Composable
private fun renderTable(table: TableBlock) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        var child = table.firstChild
        while (child != null) {
            when (child) {
                is TableHead -> renderTableHead(child)
                is TableBody -> {
                    var row = child.firstChild
                    while (row != null) {
                        if (row is TableRow) renderTableRow(row, isHeader = false)
                        row = row.next
                    }
                }
            }
            child = child.next
        }
    }
}

@Composable
private fun renderTableHead(head: TableHead) {
    var row = head.firstChild
    while (row != null) {
        if (row is TableRow) renderTableRow(row, isHeader = true)
        row = row.next
    }
}

@Composable
private fun renderTableRow(row: TableRow, isHeader: Boolean) {
    val cells = remember(row) {
        generateSequence(row.firstChild as? TableCell) { it.next as? TableCell }.toList()
    }

    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 4.dp)
    ) {
        cells.forEach { cell ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text(
                    text = cell.getText(),
                    fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(
                            align = when (cell.alignment) {
                                TableCell.Alignment.CENTER -> Alignment.CenterHorizontally
                                TableCell.Alignment.RIGHT -> Alignment.End
                                else -> Alignment.Start
                            }
                        )
                        .padding(vertical = 6.dp)
                )
            }
        }
    }

    if (isHeader) HorizontalDivider(thickness = 1.5.dp)
}

private fun Node.getText(): String = buildString {
    accept(object : AbstractVisitor() {
        override fun visit(text: Text) {
            append(text.literal)
        }

        override fun visit(softLineBreak: SoftLineBreak) {
            append(" ")
        }

        override fun visit(hardLineBreak: HardLineBreak) {
            append("\n")
        }
    })
}